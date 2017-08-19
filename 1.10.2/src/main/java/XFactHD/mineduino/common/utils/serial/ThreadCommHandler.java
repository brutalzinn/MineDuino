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

package XFactHD.mineduino.common.utils.serial;

import XFactHD.mineduino.common.blocks.TileEntitySerialReceiver;
import XFactHD.mineduino.common.utils.ConfigHandler;
import XFactHD.mineduino.common.utils.DimBlockPos;
import XFactHD.mineduino.common.utils.SerialTask;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ThreadCommHandler
{
    private static final HashMap<String, ArrayList<DimBlockPos>> positionsOnPin = new HashMap<>();
    private static final HashMap<String, SerialHandler.PinMode> assignedModes = new HashMap<>();
    private static final HashMap<String, ArrayList<DimBlockPos>> posWaitingFor = new HashMap<>();
    private static final Queue<SerialTask> tasks = new ConcurrentLinkedQueue<>();

    public static void sendData(DimBlockPos pos, String pin, SerialHandler.PinMode mode, int power)
    {
        if (!posWaitingFor.containsKey(pin)) { posWaitingFor.put(pin, new ArrayList<>()); }
        if (mode.isReceiver()) { posWaitingFor.get(pin).add(pos); }
        synchronized (tasks)
        {
            SerialTask task = new SerialTask(pin, mode, map(power, mode, true));
            if (!tasks.contains(task))
            {
                tasks.add(task);
            }
        }
    }

    public static void receiveData(String pin, SerialHandler.PinMode mode, int value)
    {
        FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(new Runnable()
        {
            @Override
            public void run()
            {
                if (!posWaitingFor.containsKey(pin)) { posWaitingFor.put(pin, new ArrayList<>()); }
                for (DimBlockPos pos : posWaitingFor.get(pin))
                {
                    World world = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(pos.getDim());
                    TileEntity te = world.getTileEntity(pos.getPos());
                    if (te instanceof TileEntitySerialReceiver)
                    {
                        ((TileEntitySerialReceiver)te).updateValue(map(value, mode, false));
                    }
                }
                if (mode != SerialHandler.PinMode.IR)
                {
                    posWaitingFor.get(pin).clear();
                }
            }
        });
    }

    public static void executeQueuedTasks()
    {
        synchronized (tasks)
        {
            while (!tasks.isEmpty())
            {
                tasks.poll().call();
                //try { Thread.sleep(10); } //TODO: check if this is necessary
                //catch (InterruptedException e)
                //{
                //    LogHelper.error("Thread '" + Thread.currentThread().getName() + "' was interrupted!");
                //    e.printStackTrace();
                //}
            }
        }
    }

    public static SerialHandler.PinMode addPosToPin(String pin, DimBlockPos pos)
    {
        if (!positionsOnPin.containsKey(pin)) { positionsOnPin.put(pin, new ArrayList<>()); }
        if (!positionsOnPin.get(pin).contains(pos)) { positionsOnPin.get(pin).add(pos); }
        if (!assignedModes.containsKey(pin)) { assignedModes.put(pin, SerialHandler.PinMode.NONE); }
        return assignedModes.get(pin);
    }

    public static void removePosFromPin(String pin, DimBlockPos pos)
    {
        if (!positionsOnPin.containsKey(pin)) { positionsOnPin.put(pin, new ArrayList<>()); }
        if (!assignedModes.containsKey(pin)) { assignedModes.put(pin, SerialHandler.PinMode.NONE); }
        positionsOnPin.get(pin).remove(pos);
        if (positionsOnPin.get(pin).isEmpty())
        {
            if (assignedModes.get(pin) == SerialHandler.PinMode.IR)
            {
                sendData(pos, pin, SerialHandler.PinMode.IR, 0);
            }
            assignedModes.replace(pin, SerialHandler.PinMode.NONE);
        }
    }

    public static void setPinMode(String pin, SerialHandler.PinMode mode)
    {
        if (!assignedModes.containsKey(pin)) { assignedModes.put(pin, mode); }
        else { assignedModes.replace(pin, mode); }
    }

    public static SerialHandler.PinMode getPinMode(String pin)
    {
        SerialHandler.PinMode checkMode = assignedModes.getOrDefault(pin, SerialHandler.PinMode.NONE);
        if (!isValidPin(pin, checkMode)) { return null; }
        if (!assignedModes.containsKey(pin)) { assignedModes.put(pin, SerialHandler.PinMode.NONE); }
        return assignedModes.get(pin);
    }

    private static int map(int value, SerialHandler.PinMode mode, boolean asking)
    {
        if (mode.isDigital()) { return mode.isReceiver() && !asking ? (value == 1 ? 15 : 0) : (value > 0 ? 1 : 0); }

        int valueRange = mode.isReceiver() ?  1023 : 15;
        int outputRange = mode.isReceiver() ? 15 :  255;
        float part = ((float)value) / ((float)valueRange);
        return (int) (part * ((float)outputRange));
    }

    public static boolean isValidPin(String pin, SerialHandler.PinMode mode)
    {
        switch (mode)
        {
            case NONE: return ConfigHandler.digitalPins.contains(pin) || ConfigHandler.analogPins.contains(pin);
            case DR:   return ConfigHandler.digitalPins.contains(pin) || ConfigHandler.analogPins.contains(pin);
            case DRP:  return ConfigHandler.digitalPins.contains(pin) || ConfigHandler.analogPins.contains(pin);
            case DW:   return ConfigHandler.digitalPins.contains(pin) || ConfigHandler.analogPins.contains(pin);
            case AR:   return ConfigHandler.analogPins.contains(pin);
            case AW:   return ConfigHandler.pwmPins.contains(pin);
            case IR:   return ConfigHandler.irPins.contains(pin);
            default: return false;
        }
    }

    public static void doCleanup()
    {
        positionsOnPin.clear();
        assignedModes.clear();
        posWaitingFor.clear();
        tasks.clear();
    }

    public static ArrayList<DimBlockPos> getPosOnPin(String pin)
    {
        if (!positionsOnPin.containsKey(pin)) { positionsOnPin.put(pin, new ArrayList<>()); }
        return positionsOnPin.get(pin);
    }
}