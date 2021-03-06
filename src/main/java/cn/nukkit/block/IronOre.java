package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class IronOre extends Solid {

    protected int id = IRON_ORE;

    public IronOre() {
        super(IRON_ORE);
    }

    public IronOre(int meta) {
        super(IRON_ORE, 0);
    }

    @Override
    public double getHardness() {
        return 3;
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_PICKAXE;
    }

    @Override
    public String getName() {
        return "Iron Ore";
    }

    @Override
    public int[][] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= Tool.TIER_STONE) {
            return new int[][]{new int[]{Item.IRON_ORE, 0, 1}};
        } else {
            return new int[0][];
        }
    }
}
