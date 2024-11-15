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

import dev.vortex.sculk.item.*;

public class Recombobulator3000 implements SkullStatistics, MaterialFunction {
	@Override
	public String getURL() {
		return "57ccd36dc8f72adcb1f8c8e61ee82cd96ead140cf2a16a1366be9b5a8e3cc3fc";
	}

	@Override
	public String getDisplayName() {
		return "Recombobulator 3000";
	}

	@Override
	public Rarity getRarity() {
		return Rarity.LEGENDARY;
	}

	@Override
	public GenericItemType getType() {
		return GenericItemType.ITEM;
	}

	@Override
	public SpecificItemType getSpecificType() {
		return SpecificItemType.DUNGEON_ITEM;
	}

	@Override
	public boolean isStackable() {
		return false;
	}
}
