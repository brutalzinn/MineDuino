/*  Copyright (C) <2015>  <XFactHD>

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

import net.minecraftforge.fml.common.FMLLog;
import org.apache.logging.log4j.Level;

public class LogHelper
{
    private static void log(Level logLevel, Object object, Object... data)
    {
        FMLLog.log(Reference.MOD_ID, logLevel, String.valueOf(object), object, data);
    }

    public static void all(Object object, Object... data)
    {
        log(Level.ALL, object, data);
    }

    public static void debug(Object object, Object... data)
    {
        log(Level.DEBUG, object, data);
    }

    public static void error(Object object, Object... data)
    {
        log(Level.ERROR, object, data);
    }

    public static void fatal(Object object, Object... data)
    {
        log(Level.FATAL, object, data);
    }

    public static void info(Object object, Object... data)
    {
        log(Level.INFO, object, data);
    }

    public static void off(Object object, Object... data)
    {
        log(Level.OFF, object, data);
    }

    public static void trace(Object object, Object... data)
    {
        log(Level.TRACE, object, data);
    }

    public static void warn(Object object, Object... data)
    {
        log(Level.WARN, object);
    }
}
