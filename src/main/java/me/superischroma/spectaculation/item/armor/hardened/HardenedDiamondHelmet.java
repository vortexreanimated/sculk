package me.superischroma.spectaculation.item.armor.hardened;

import me.superischroma.spectaculation.item.*;

import java.util.List;

public class HardenedDiamondHelmet implements ToolStatistics, MaterialFunction
{
    @Override
    public String getDisplayName()
    {
        return "Hardened Diamond Helmet";
    }

    @Override
    public Rarity getRarity()
    {
        return Rarity.RARE;
    }

    @Override
    public GenericItemType getType()
    {
        return GenericItemType.ARMOR;
    }

    @Override
    public SpecificItemType getSpecificType()
    {
        return SpecificItemType.HELMET;
    }

    @Override
    public int getBaseDefense()
    {
        return 60;
    }
}