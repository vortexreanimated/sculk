package me.superischroma.spectaculation.item.shovel.vanilla;

import me.superischroma.spectaculation.item.*;

public class GoldenShovel implements ToolStatistics, MaterialFunction
{
    @Override
    public String getDisplayName()
    {
        return "Golden Shovel";
    }

    @Override
    public Rarity getRarity()
    {
        return Rarity.COMMON;
    }

    @Override
    public int getBaseDamage()
    {
        return 15;
    }

    @Override
    public GenericItemType getType()
    {
        return GenericItemType.TOOL;
    }

    @Override
    public SpecificItemType getSpecificType()
    {
        return SpecificItemType.SHOVEL;
    }
}