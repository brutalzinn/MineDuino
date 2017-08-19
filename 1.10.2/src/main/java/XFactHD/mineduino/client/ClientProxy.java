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

package XFactHD.mineduino.client;

import XFactHD.mineduino.common.CommonProxy;
import XFactHD.mineduino.common.Content;
import XFactHD.mineduino.common.blocks.TileEntitySerial;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy
{
    @Override
    @SuppressWarnings("ConstantConditions")
    public void preInit(FMLPreInitializationEvent event)
    {
        super.preInit(event);
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(Content.blockSerial), 0, new ModelResourceLocation(Content.blockSerial.getRegistryName(), "type=tx"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(Content.blockSerial), 1, new ModelResourceLocation(Content.blockSerial.getRegistryName(), "type=rx"));
    }

    @Override
    public void init(FMLInitializationEvent event)
    {
        super.init(event);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event)
    {

    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if (ID == 0)
        {
            TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
            if (te instanceof TileEntitySerial)
            {
                return new GuiBlockSerial((TileEntitySerial)te);
            }
        }
        return null;
    }
}