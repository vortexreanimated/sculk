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
package dev.vortex.sculk.item.oddities;

import dev.vortex.sculk.command.BatphoneCommand;
import dev.vortex.sculk.item.*;
import dev.vortex.sculk.sequence.SoundSequenceType;
import dev.vortex.sculk.util.SUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MaddoxBatphone implements SkullStatistics, MaterialFunction, Ability {
	private static final List<String> SUCCESSFUL_RESPONSES = Arrays.asList("Hello?", "Someone answers!",
			"How does a lobster answer? Shello!", "Hey what do you need?", "You hear the line pick up...",
			"You again? What do you want this time?");
	private static final List<String> FAILED_RESPONSES = Arrays.asList("Please leave your message after the beep.",
			"How can you tell if a bee is on the phone? You get a buzzy signal!",
			"The phone keeps ringing, is it broken?", "The phone picks up but it immediately hands up!",
			"What did the cat say on the phone? Can you hear meow?", "No answer.", "Seems like it's not picking up!",
			"\"Your call is important to us, please stay on the line\", so you hang up.");
	public static final List<UUID> RING_COOLDOWN = new ArrayList<>();
	public static final List<UUID> CALL_COOLDOWN = new ArrayList<>();

	@Override
	public String getURL() {
		return "9336d7cc95cbf6689f5e8c954294ec8d1efc494a4031325bb427bc81d56a484d";
	}

	@Override
	public String getDisplayName() {
		return "Maddox Batphone";
	}

	@Override
	public Rarity getRarity() {
		return Rarity.UNCOMMON;
	}

	@Override
	public GenericItemType getType() {
		return GenericItemType.ITEM;
	}

	@Override
	public String getAbilityName() {
		return "Whassup?";
	}

	@Override
	public String getAbilityDescription() {
		return "Lets you call Maddox, when he's not busy.";
	}

	@Override
	public int getAbilityCooldownTicks() {
		return 0;
	}

	@Override
	public int getManaCost() {
		return 0;
	}

	@Override
	public boolean displayUsage() {
		return false;
	}

	@Override
	public void onAbilityUse(Player player, SItem sItem) {
		if (RING_COOLDOWN.contains(player.getUniqueId())) {
			return;
		}
		RING_COOLDOWN.add(player.getUniqueId());
		SUtil.delay(() -> RING_COOLDOWN.remove(player.getUniqueId()), 52);
		SoundSequenceType.MADDOX_BATPHONE.play(player);
		player.sendMessage(ChatColor.YELLOW + "✆ Ring...");
		SUtil.delay(() -> player.sendMessage(ChatColor.YELLOW + "✆ Ring... Ring..."), 18);
		SUtil.delay(() -> player.sendMessage(ChatColor.YELLOW + "✆ Ring... Ring... Ring..."), 35);
		if (CALL_COOLDOWN.contains(player.getUniqueId())) {
			SUtil.delay(() -> player.sendMessage(ChatColor.RED + "✆ " + SUtil.getRandom(FAILED_RESPONSES)), 52);
			return;
		}
		TextComponent message = new TextComponent(ChatColor.DARK_GREEN + " " + ChatColor.BOLD + "[OPEN MENU]");
		message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				new TextComponent[]{new TextComponent(ChatColor.YELLOW + "Click!")}));
		String key = UUID.randomUUID().toString();
		BatphoneCommand.KEYS.add(key);
		SUtil.delay(() -> BatphoneCommand.KEYS.remove(key), 20 * 23);
		message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
				"/batphone " + BatphoneCommand.ACCESS_KEY.toString() + " " + key));
		SUtil.delay(
				() -> player.spigot().sendMessage(new TextComponent(
						new TextComponent(ChatColor.GREEN + "✆ " + SUtil.getRandom(SUCCESSFUL_RESPONSES)), message)),
				52);
	}
}
