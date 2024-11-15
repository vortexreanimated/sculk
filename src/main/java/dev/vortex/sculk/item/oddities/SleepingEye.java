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

import dev.vortex.sculk.item.GenericItemType;
import dev.vortex.sculk.item.MaterialFunction;
import dev.vortex.sculk.item.Rarity;
import dev.vortex.sculk.item.SkullStatistics;

public class SleepingEye implements SkullStatistics, MaterialFunction {
	@Override
	public String getDisplayName() {
		return "Sleeping Eye";
	}

	@Override
	public Rarity getRarity() {
		return Rarity.EPIC;
	}

	@Override
	public String getLore() {
		return "Keep this item in your inventory to recover your placed Summoning Eye when you leave"
				+ " or when you click the Ender Altar. This item becomes imbued with the magic of the Dragon"
				+ " when it spawns, turning it into a Remnant of the Eye.";
	}

	@Override
	public GenericItemType getType() {
		return GenericItemType.ITEM;
	}

	@Override
	public boolean isStackable() {
		return false;
	}

	@Override
	public String getURL() {
		return "37c0d010dd0e512ffea108d7c5fe69d576c31ec266c884b51ec0b28cc457";
	}
}
