/*  Copyright (C) <2017>  <XFactHD>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see http://www.gnu.org/licenses. */

package XFactHD.mineduino.common;

import XFactHD.mineduino.common.blocks.BlockSerial;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class Content
{
    public static Block blockSerial;

    public static void preInit()
    {
        blockSerial = new BlockSerial();
    }

    public static void init()
    {
        GameRegistry.addShapedRecipe(new ItemStack(blockSerial, 1, 0), "ITI", "RCR", "III", 'I', Items.IRON_INGOT, 'T', Blocks.REDSTONE_TORCH, 'R', Items.REDSTONE, 'C', Items.COMPARATOR);
        GameRegistry.addShapedRecipe(new ItemStack(blockSerial, 1, 1), "ITI", "RLR", "III", 'I', Items.IRON_INGOT, 'T', Blocks.REDSTONE_TORCH, 'R', Items.REDSTONE, 'L', Blocks.LEVER);
    }
}