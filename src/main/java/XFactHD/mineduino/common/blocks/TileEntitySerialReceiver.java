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

package XFactHD.mineduino.common.blocks;

import XFactHD.mineduino.common.utils.DimBlockPos;
import XFactHD.mineduino.common.utils.serial.SerialHandler;
import XFactHD.mineduino.common.utils.serial.ThreadCommHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileEntitySerialReceiver extends TileEntitySerial implements ITickable
{
    private DimBlockPos dimPos = null;
    private boolean initialized = false;
    private int ticks = 0;
    private String pin = "";
    private SerialHandler.PinMode mode = SerialHandler.PinMode.NONE;
    private int power = 0;

    @Override
    public void update()
    {
        if (dimPos == null) { dimPos = new DimBlockPos(world.provider.getDimension(), pos); }
        if (!world.isRemote)
        {
            initialize();
            if (initialized && mode != SerialHandler.PinMode.NONE && mode != SerialHandler.PinMode.IR && SerialHandler.getSerialHandler().isInitialized())
            {
                if (ticks == 10)
                {
                    ticks = 0;
                    ThreadCommHandler.sendData(dimPos, pin, mode, 0);
                }
                ticks += 1;
            }
        }
    }

    public void updateValue(int value)
    {
        if (this.power != value)
        {
            this.power = value;
            notifyBlockUpdate();
            notifyNeighbors();
        }
    }

    public int getPower()
    {
        return this.power;
    }

    @Override
    public void setPin(String pin)
    {
        this.pin = pin;
        notifyBlockUpdate();
    }

    @Override
    public void setMode(SerialHandler.PinMode mode)
    {
        if (ThreadCommHandler.getPinMode(pin) == SerialHandler.PinMode.NONE || ThreadCommHandler.getPosOnPin(pin).size() <= 1)
        {
            boolean wasIR = this.mode == SerialHandler.PinMode.IR;
            this.mode = mode;
            if (mode == SerialHandler.PinMode.IR)
            {
                ThreadCommHandler.sendData(dimPos, pin, mode, 1);
            }
            else if (wasIR)
            {
                ThreadCommHandler.sendData(dimPos, pin, SerialHandler.PinMode.IR, 0);
            }
            ThreadCommHandler.setPinMode(pin, mode);
            notifyBlockUpdate();
        }
    }

    @Override
    public String getPin()
    {
        return pin;
    }

    @Override
    public SerialHandler.PinMode getMode()
    {
        return mode;
    }

    @Override
    public void invalidate()
    {
        super.invalidate();
        ThreadCommHandler.removePosFromPin(pin, dimPos);
    }

    private void initialize()
    {
        if (!isInvalid() && !initialized && !pin.equals(""))
        {
            initialized = true;
            SerialHandler.PinMode pinMode = ThreadCommHandler.addPosToPin(pin, dimPos);
            if (pinMode == SerialHandler.PinMode.NONE)
            {
                ThreadCommHandler.setPinMode(pin, mode);
            }
            else
            {
                this.mode = pinMode;
                notifyBlockUpdate();
            }
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt)
    {
        nbt.setString("pin", pin);
        nbt.setInteger("mode", mode.ordinal());
        nbt.setInteger("power", power);
        notifyNeighbors();
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt)
    {
        pin = nbt.getString("pin");
        mode = SerialHandler.PinMode.values()[nbt.getInteger("mode")];
        power = nbt.getInteger("power");

    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState)
    {
        return oldState != newState;
    }
}