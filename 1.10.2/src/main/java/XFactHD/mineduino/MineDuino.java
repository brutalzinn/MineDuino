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

package XFactHD.mineduino;

import XFactHD.mineduino.common.CommonProxy;
import XFactHD.mineduino.common.utils.LogHelper;
import XFactHD.mineduino.common.utils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;

@Mod(modid = Reference.MOD_ID, name = Reference.NAME, version = Reference.VERSION)
public class MineDuino
{
    @Mod.Instance
    public static MineDuino INSTANCE;

    @SidedProxy(serverSide = Reference.SERVER_PROXY, clientSide = Reference.CLIENT_PROXY)
    public static CommonProxy proxy;

    public static SimpleNetworkWrapper MD_NET_WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID);

    @Mod.EventHandler
    public void construct(FMLConstructionEvent event)
    {
        if (getServer() != null && getServer().isDedicatedServer()) { throw new UnsupportedOperationException("This mod cannot be run on a dedicated server!"); }

        File mcDir = Minecraft.getMinecraft().mcDataDir;
        int index = mcDir.getAbsolutePath().lastIndexOf(".");
        String mcDirAbs = index == -1 ? mcDir.getAbsolutePath() : mcDir.getAbsolutePath().substring(0, index);
        System.setProperty("java.library.path", System.getProperty("java.library.path") + ";.;" + mcDirAbs);
        try
        {
            final Field sysPathsField = ClassLoader.class.getDeclaredField("sys_paths");
            sysPathsField.setAccessible(true);
            sysPathsField.set(null, null);
        }
        catch (NoSuchFieldException | IllegalAccessException e)
        {
            LogHelper.info("Failed to reset field sys_paths in ClassLoader!");
            throw new RuntimeException(e);
        }

        if ((Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment")) { return; }

        File parallel = new File(mcDirAbs + "/rxtxParallel.dll");
        if (!parallel.exists())
        {
            URL inputUrl = getClass().getResource("/rxtxParallel.dll");
            try { FileUtils.copyURLToFile(inputUrl, parallel); }
            catch (IOException e)
            {
                LogHelper.error("Failed to copy DLLs from the jar!");
                e.printStackTrace();
            }
        }

        File serial = new File(mcDirAbs + "/rxtxSerial.dll");
        if (!serial.exists())
        {
            URL inputUrl = getClass().getResource("/rxtxSerial.dll");
            try { FileUtils.copyURLToFile(inputUrl, serial); }
            catch (IOException e)
            {
                LogHelper.error("Failed to copy DLLs from the jar!");
                e.printStackTrace();
            }
        }
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        proxy.postInit(event);
    }

    private static MinecraftServer getServer()
    {
        return FMLCommonHandler.instance().getMinecraftServerInstance();
    }
}