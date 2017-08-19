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

package XFactHD.mineduino.common.utils;

import XFactHD.mineduino.common.utils.serial.SerialHandler;

public class SerialTask
{
    private String pin;
    private SerialHandler.PinMode mode;
    private int value;

    public SerialTask(String pin, SerialHandler.PinMode mode, int value)
    {
        this.pin = pin;
        this.mode = mode;
        this.value = value;
    }

    public void call()
    {
        Exception exception;

        if (mode == SerialHandler.PinMode.IR)
        {
            exception = SerialHandler.getSerialHandler().setInterrupt(pin, value == 1);
        }
        else if (mode.isReceiver())
        {
            exception = SerialHandler.getSerialHandler().requestValue(pin, mode);
        }
        else
        {
            exception = SerialHandler.getSerialHandler().sendMessage(pin, mode, value);
        }

        if (exception != null)
        {
            LogHelper.info("An error occured while sending serial data!");
            exception.printStackTrace();
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj == this || (obj instanceof SerialTask && ((SerialTask)obj).pin.equals(pin));
    }

    @Override
    public int hashCode()
    {
        return pin.hashCode() * (mode.ordinal() + 40) * value;
    }
}