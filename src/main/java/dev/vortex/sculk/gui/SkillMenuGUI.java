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
package dev.vortex.sculk.gui;

import dev.vortex.sculk.collection.ItemCollection;
import dev.vortex.sculk.skill.*;
import dev.vortex.sculk.user.User;
import dev.vortex.sculk.util.SUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class SkillMenuGUI extends GUI {
	public SkillMenuGUI() {
		super("Your Skills", 54);
	}

	@Override
	public void onOpen(GUIOpenEvent e) {
		fill(BLACK_STAINED_GLASS_PANE);
		Player player = e.getPlayer();
		User user = User.getUser(player.getUniqueId());
		set(GUIClickableItem.getCloseItem(49));
		AtomicInteger found = new AtomicInteger();
		Collection<ItemCollection> collections = ItemCollection.getCollections();
		for (ItemCollection collection : collections) {
			if (user.getCollection(collection) > 0) {
				found.incrementAndGet();
			}
		}
		set(GUIClickableItem.createGUIOpenerItem(GUIType.SKYBLOCK_MENU, player, ChatColor.GREEN + "Go Back", 48,
				Material.ARROW, ChatColor.GRAY + "To SkyBlock Menu"));
		set(4, SUtil.getStack(ChatColor.GREEN + "Your Skills", Material.DIAMOND_SWORD, (short) 0, 1,
				ChatColor.GRAY + "View your Skill progression and", ChatColor.GRAY + "rewards."));
		set(createCollectionClickable(null, FarmingSkill.INSTANCE, Material.GOLD_HOE, 19, player));
		set(createCollectionClickable(null, MiningSkill.INSTANCE, Material.STONE_PICKAXE, 20, player));
		set(createCollectionClickable(null, CombatSkill.INSTANCE, Material.STONE_SWORD, 21, player));
		set(createCollectionClickable(null, ForagingSkill.INSTANCE, Material.SAPLING, (short) 3, 22, player));
		set(createCollectionClickable(null, null, Material.FISHING_ROD, 23, player));
		set(createCollectionClickable(null, null, Material.ENCHANTMENT_TABLE, 24, player));
		set(createCollectionClickable(null, null, Material.BREWING_STAND_ITEM, 25, player));
	}

	private static GUIClickableItem createCollectionClickable(GUI gui, Skill skill, Material icon, short data, int slot,
			Player player) {
		User user = User.getUser(player.getUniqueId());
		List<String> l = new ArrayList<>();
		if (skill != null) {
			for (String line : skill.getDescription())
				l.add(ChatColor.GRAY + line);
		}
		if (l.size() == 0) {
			l.add(ChatColor.GRAY + "Come back soon!");
		} else {
			l.add(" ");
		}
		double xp = skill != null ? user.getSkillXP(skill) : 0.0;
		int level = skill != null ? Skill.getLevel(xp, skill.hasSixtyLevels()) : 0;
		String name = ChatColor.RED + "Not Finished";
		if (skill != null && ((level < 50 && !skill.hasSixtyLevels()) || (level < 60 && skill.hasSixtyLevels()))) {
			name = skill.getName();
			int nextLevel = level + 1;
			String numeral = SUtil.toRomanNumeral(nextLevel);
			double nextXP = Skill.getNextOverallXPGoal(xp, skill.hasSixtyLevels());
			l.add(SUtil.createProgressText("Progress to Level " + numeral, xp, nextXP));
			l.add(SUtil.createLineProgressBar(20, ChatColor.DARK_GREEN, xp, nextXP));
			l.add(" ");
			l.add(ChatColor.GRAY + "Level " + numeral + " Rewards:");
			for (String line : skill.getRewardLore(nextLevel, nextLevel, false))
				l.add("  " + line);
			l.add(" ");
		}
		if (skill != null) {
			l.add(ChatColor.YELLOW + "Click to view!");
		}
		return GUIClickableItem.createGUIOpenerItem(gui, player,
				ChatColor.GREEN + name + (level != 0 ? " " + SUtil.toRomanNumeral(level) : ""), slot, icon, data,
				SUtil.toArray(l, String.class));
	}

	private static GUIClickableItem createCollectionClickable(GUI gui, Skill skill, Material icon, int slot,
			Player player) {
		return createCollectionClickable(gui, skill, icon, (short) 0, slot, player);
	}
}
