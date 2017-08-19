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

import XFactHD.mineduino.MineDuino;
import XFactHD.mineduino.common.blocks.TileEntitySerial;
import XFactHD.mineduino.common.gui.ContainerBlockSerial;
import XFactHD.mineduino.common.net.PacketUpdateSerialParams;
import XFactHD.mineduino.common.utils.ConfigHandler;
import XFactHD.mineduino.common.utils.serial.SerialHandler;
import XFactHD.mineduino.common.utils.serial.ThreadCommHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class CommonProxy implements IGuiHandler
{
    public void preInit(FMLPreInitializationEvent event)
    {
        ConfigHandler.init(event.getSuggestedConfigurationFile());
        Content.preInit();
        NetworkRegistry.INSTANCE.registerGuiHandler(MineDuino.INSTANCE, MineDuino.proxy);
        MineDuino.MD_NET_WRAPPER.registerMessage(PacketUpdateSerialParams.Handler.class, PacketUpdateSerialParams.class, 0, Side.SERVER);
    }

    public void init(FMLInitializationEvent event)
    {
        Content.init();
        SerialHandler.getSerialHandler().initialize();
    }

    public void postInit(FMLPostInitializationEvent event) {}

    @SubscribeEvent
    public void serverStopped(FMLServerStoppedEvent event)
    {
        SerialHandler.getSerialHandler().close();
        ThreadCommHandler.doCleanup();
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if (ID == 0)
        {
            TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
            if (te instanceof TileEntitySerial)
            {
                return new ContainerBlockSerial(((TileEntitySerial)te));
            }
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        return null;
    }
}