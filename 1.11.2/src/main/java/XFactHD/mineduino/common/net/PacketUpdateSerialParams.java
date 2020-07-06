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

package XFactHD.mineduino.common.net;

import XFactHD.mineduino.common.blocks.TileEntitySerial;
import XFactHD.mineduino.common.utils.serial.SerialHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketUpdateSerialParams implements IMessage
{
    private BlockPos pos;
    private String pin;
    private SerialHandler.PinMode mode;

    public PacketUpdateSerialParams(){}

    public PacketUpdateSerialParams(BlockPos pos, String pin, SerialHandler.PinMode mode)
    {
        this.pos = pos;
        this.pin = pin;
        this.mode = mode;
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setLong("pos", pos.toLong());
        nbt.setString("pin", pin);
        nbt.setInteger("mode", mode.ordinal());
        ByteBufUtils.writeTag(buf, nbt);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        NBTTagCompound nbt = ByteBufUtils.readTag(buf);
        pos = BlockPos.fromLong(nbt.getLong("pos"));
        pin = nbt.getString("pin");
        mode = SerialHandler.PinMode.values()[nbt.getInteger("mode")];
    }

    public static class Handler implements IMessageHandler<PacketUpdateSerialParams, IMessage>
    {
        @Override
        public IMessage onMessage(PacketUpdateSerialParams message, MessageContext ctx)
        {
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(new Runnable()
            {
                @Override
                public void run()
                {
                    World world = ctx.getServerHandler().player.world;
                    TileEntity te = world.getTileEntity(message.pos);
                    if (te instanceof TileEntitySerial)
                    {
                        ((TileEntitySerial)te).setPin(message.pin);
                        ((TileEntitySerial)te).setMode(message.mode);
                    }
                }
            });
            return null;
        }
    }
}