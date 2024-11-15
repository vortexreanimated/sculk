/*
 * This file belongs to Sculk, a Hypixel Skyblock recreation.
 * Copyright (c) 2024 VortexReanimated
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, see <https://www.gnu.org/licenses/>.
 * 
 */
package dev.vortex.sculk;

import dev.vortex.sculk.auction.AuctionBid;
import dev.vortex.sculk.auction.AuctionEscrow;
import dev.vortex.sculk.auction.AuctionItem;
import dev.vortex.sculk.command.*;
import dev.vortex.sculk.config.Config;
import dev.vortex.sculk.entity.EntitySpawner;
import dev.vortex.sculk.entity.StaticDragonManager;
import dev.vortex.sculk.gui.GUIListener;
import dev.vortex.sculk.item.*;
import dev.vortex.sculk.item.pet.Pet;
import dev.vortex.sculk.listener.BlockListener;
import dev.vortex.sculk.listener.PlayerListener;
import dev.vortex.sculk.listener.ServerPingListener;
import dev.vortex.sculk.listener.WorldListener;
import dev.vortex.sculk.region.Region;
import dev.vortex.sculk.slayer.SlayerQuest;
import dev.vortex.sculk.sql.SQLDatabase;
import dev.vortex.sculk.sql.SQLRegionData;
import dev.vortex.sculk.sql.SQLWorldData;
import dev.vortex.sculk.user.AuctionSettings;
import dev.vortex.sculk.user.User;
import dev.vortex.sculk.util.Groups;
import dev.vortex.sculk.util.SLog;
import dev.vortex.sculk.util.SerialNBTTagCompound;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandMap;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public final class Spectaculation extends JavaPlugin {
	private static Spectaculation plugin;

	public static Spectaculation getPlugin() {
		return plugin;
	}

	public Config config;
	public Config heads;
	public Config blocks;
	public Config spawners;
	public CommandMap commandMap;
	public SQLDatabase sql;
	public SQLRegionData regionData;
	public SQLWorldData worldData;
	public CommandLoader cl;
	public Repeater repeater;

	@Override
	public void onLoad() {
		SLog.info("Loading Bukkit-serializable classes...");
		loadSerializableClasses();
	}

	@SneakyThrows
	@Override
	public void onEnable() {
		plugin = this;
		SLog.info("Loading YAML data...");
		config = new Config("config.yml");
		heads = new Config("heads.yml");
		blocks = new Config("blocks.yml");
		spawners = new Config("spawners.yml");
		SLog.info("Loading command map...");
		try {
			Field f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
			f.setAccessible(true);
			commandMap = (CommandMap) f.get(Bukkit.getServer());
		} catch (IllegalAccessException | NoSuchFieldException e) {
			SLog.severe("Couldn't load command map: ");
			e.printStackTrace();
		}
		SLog.info("Loading SQL database...");
		sql = new SQLDatabase();
		regionData = new SQLRegionData();
		worldData = new SQLWorldData();
		cl = new CommandLoader();
		SLog.info("Starting server loop...");
		repeater = new Repeater();
		SLog.info("Loading commands...");
		loadCommands();
		SLog.info("Loading listeners...");
		loadListeners();
		SLog.info("Registering Citizens traits...");
		registerTraits();
		SLog.info("Starting entity spawners...");
		EntitySpawner.startSpawnerTask();
		SLog.info("Establishing player regions...");
		Region.cacheRegions();
		SLog.info("Loading auction items from disk...");
		AuctionItem.loadAuctionsFromDisk();
		SkyBlockCalendar.ELAPSED = plugin.config.getLong("timeElapsed");
		SLog.info("Synchronizing world time with calendar time and removing world entities...");
		for (World world : Bukkit.getWorlds()) {
			for (Entity entity : world.getEntities()) {
				if (entity instanceof HumanEntity) {
					continue;
				}
				entity.remove();
			}
			// Time Validator
			int time = (int) ((SkyBlockCalendar.ELAPSED % 24000) - 6000);
			if (time < 0) {
				time += 24000;
			}
			world.setTime(time);
		}
		SLog.info("Loading items...");
		Class.forName("dev.vortex.sculk.item.SMaterial"); // ensuring materials are loaded prior to this
		for (SMaterial material : SMaterial.values()) {
			if (material.hasClass()) {
				material.getStatistics().load();
			}
		}
		SLog.info("Converting craft recipes into Spectaculation recipes...");
		for (Iterator<Recipe> iter = Bukkit.recipeIterator(); iter.hasNext();) {
			Recipe recipe = iter.next();
			if (recipe.getResult() == null) {
				continue;
			}
			Material result = recipe.getResult().getType();
			if (recipe instanceof ShapedRecipe shaped) {
				dev.vortex.sculk.item.ShapedRecipe specShaped = new dev.vortex.sculk.item.ShapedRecipe(
						SItem.convert(shaped.getResult()), Groups.EXCHANGEABLE_RECIPE_RESULTS.contains(result))
						.shape(shaped.getShape());
				for (Map.Entry<Character, ItemStack> entry : shaped.getIngredientMap().entrySet()) {
					if (entry.getValue() == null) {
						continue;
					}
					ItemStack stack = entry.getValue();
					specShaped.set(entry.getKey(), SMaterial.getSpecEquivalent(stack.getType(), stack.getDurability()),
							stack.getAmount());
				}
			}
			if (recipe instanceof ShapelessRecipe shapeless) {
				dev.vortex.sculk.item.ShapelessRecipe specShapeless = new dev.vortex.sculk.item.ShapelessRecipe(
						SItem.convert(shapeless.getResult()), Groups.EXCHANGEABLE_RECIPE_RESULTS.contains(result));
				for (ItemStack stack : shapeless.getIngredientList())
					specShapeless.add(SMaterial.getSpecEquivalent(stack.getType(), stack.getDurability()),
							stack.getAmount());
			}
		}
		SLog.info("Enabled " + this.getDescription().getFullName());
	}

	@Override
	public void onDisable() {
		SLog.info("Killing all non-human entities...");
		for (World world : Bukkit.getWorlds()) {
			for (Entity entity : world.getEntities()) {
				if (entity instanceof HumanEntity) {
					continue;
				}
				entity.remove();
			}
		}
		SLog.info("Stopping server loop...");
		repeater.stop();
		SLog.info("Stopping entity spawners...");
		EntitySpawner.stopSpawnerTask();
		SLog.info("Ending dragon fight... (if one is currently active)");
		StaticDragonManager.endFight();
		SLog.info("Saving calendar time...");
		SkyBlockCalendar.saveElapsed();
		SLog.info("Saving user data...");
		for (User user : User.getCachedUsers())
			user.save();
		SLog.info("Saving auction data...");
		for (AuctionItem item : AuctionItem.getAuctions())
			item.save();
		plugin = null;
		SLog.info("Disabled " + this.getDescription().getFullName());
	}

	private void loadCommands() {
		cl.register(new SpectaculationCommand());
		cl.register(new RegionCommand());
		cl.register(new PlayEnumSoundCommand());
		cl.register(new PlayEnumEffectCommand());
		cl.register(new SpawnSpecCommand());
		cl.register(new ItemCommand());
		cl.register(new SpecEnchantmentCommand());
		cl.register(new SpecPotionCommand());
		cl.register(new SpecEffectsCommand());
		cl.register(new SpecReforgeCommand());
		cl.register(new ManaCommand());
		cl.register(new CoinsCommand());
		cl.register(new GUICommand());
		cl.register(new ItemBrowseCommand());
		cl.register(new SpecRarityCommand());
		cl.register(new RecombobulateCommand());
		cl.register(new NBTCommand());
		cl.register(new IslandCommand());
		cl.register(new DataCommand());
		cl.register(new SpecTestCommand());
		cl.register(new SoundSequenceCommand());
		cl.register(new BatphoneCommand());
		cl.register(new AbsorptionCommand());
		cl.register(new SkillsCommand());
		cl.register(new CollectionsCommand());
		cl.register(new MaterialDataCommand());
		cl.register(new EntitySpawnersCommand());
		cl.register(new AuctionHouseCommand());
	}

	private void loadListeners() {
		new BlockListener();
		new PlayerListener();
		new ServerPingListener();
		new ItemListener();
		new GUIListener();
		new WorldListener();
	}

	private void registerTraits() {
	}

	private void loadSerializableClasses() {
		ConfigurationSerialization.registerClass(SlayerQuest.class, "SlayerQuest");
		ConfigurationSerialization.registerClass(Pet.PetItem.class, "PetItem");
		ConfigurationSerialization.registerClass(SItem.class, "SItem");
		ConfigurationSerialization.registerClass(AuctionSettings.class, "AuctionSettings");
		ConfigurationSerialization.registerClass(AuctionEscrow.class, "AuctionEscrow");
		ConfigurationSerialization.registerClass(SerialNBTTagCompound.class, "SerialNBTTagCompound");
		ConfigurationSerialization.registerClass(AuctionBid.class, "AuctionBid");
	}
}
