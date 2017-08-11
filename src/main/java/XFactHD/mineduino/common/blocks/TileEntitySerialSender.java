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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileEntitySerialSender extends TileEntitySerial implements ITickable
{
    private DimBlockPos dimPos = null;
    private boolean initialized = false;
    private int lastPower = 0;
    private String pin = "";
    private SerialHandler.PinMode mode = SerialHandler.PinMode.NONE;

    @Override
    public void update()
    {
        if (dimPos == null) { dimPos = new DimBlockPos(world.provider.getDimension(), pos); }
        if (!world.isRemote)
        {
            initialize();
            if (initialized && SerialHandler.getSerialHandler().isInitialized())
            {
                int power = getHighestPower();
                if (power != lastPower)
                {
                    lastPower = power;
                    ThreadCommHandler.sendData(dimPos, pin, mode, power);
                }
            }
        }
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
        if (ThreadCommHandler.getPinMode(pin) == SerialHandler.PinMode.NONE)
        {
            this.mode = mode;
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

    private int getHighestPower()
    {
        int power = 0;
        for (EnumFacing side : EnumFacing.HORIZONTALS)
        {
            int val = world.getRedstonePower(pos.offset(side), side);
            if (val > power) { power = val; }
        }
        return power;
    }

    @Override
    public void invalidate()
    {
        super.invalidate();
        ThreadCommHandler.sendData(dimPos, pin, mode, 0);
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
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt)
    {
        pin = nbt.getString("pin");
        mode = SerialHandler.PinMode.values()[nbt.getInteger("mode")];
    }
}