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

import XFactHD.mineduino.common.utils.serial.SerialHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public abstract class TileEntitySerial extends TileEntity
{
    public abstract void writeCustomNBT(NBTTagCompound nbt);

    public abstract void readCustomNBT(NBTTagCompound nbt);

    public abstract void setPin(String pin);

    public abstract void setMode(SerialHandler.PinMode mode);

    public abstract String getPin();

    public abstract SerialHandler.PinMode getMode();

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        writeCustomNBT(compound);
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        readCustomNBT(compound);
        if (world != null)
        {
            world.markBlockRangeForRenderUpdate(pos, pos);
        }
    }

    @Override
    public NBTTagCompound getUpdateTag()
    {
        NBTTagCompound nbt = super.getUpdateTag();
        writeCustomNBT(nbt);
        return nbt;
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag)
    {
        super.handleUpdateTag(tag);
        if (world != null)
        {
            world.markBlockRangeForRenderUpdate(pos, pos);
        }
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        writeCustomNBT(nbt);
        return new SPacketUpdateTileEntity(pos, getBlockMetadata(), nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
    {
        readCustomNBT(pkt.getNbtCompound());
        if (world != null)
        {
            world.markBlockRangeForRenderUpdate(pos, pos);
        }
    }

    public void notifyBlockUpdate()
    {
        markDirty();
        if (world != null)
        {
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        }
    }

    public void notifyNeighbors()
    {
        if (world != null)
        {
            world.notifyNeighborsOfStateChange(pos, world.getBlockState(pos).getBlock());
        }
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
    {
        return oldState != newSate;
    }
}