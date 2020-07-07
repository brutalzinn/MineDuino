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

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class ConfigHandler
{
    public static Configuration configuration;

    public static String port;
    public static ArrayList<String> digitalPins = new ArrayList<>(); //All digital pins
    public static ArrayList<String> pwmPins = new ArrayList<>(); //All digital pins with PWM functionality
    public static ArrayList<String> irPins = new ArrayList<>(); //All pins wiht interrupt capability
    public static ArrayList<String> analogPins = new ArrayList<>(); //All pins with ADCs (voltage level reading)
public static String modo;
    public static void init(File configFile)
    {
        if (configuration == null)
        {
            configuration = new Configuration(configFile);
            loadConfiguration();
        }
    }

    @SubscribeEvent
    public void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equalsIgnoreCase(Reference.MOD_ID))
        {
            loadConfiguration();
        }
    }

    private static void loadConfiguration()
    {

        modo = configuration.getString("modo", "General", "serial", "This needs to be set to the COM port your Arduino is connected to!");

        port = configuration.getString("comPort", "General", "COM3", "This needs to be set to the COM port your Arduino is connected to!");
        String digitalPins = configuration.getString("digitalPins", "Pins",   "2;3;4;5;6;7;8;9;10;11;12;13", "Put all the digital pins of your Arduino here, sperated by semicolons. " +
                "Note: These most not contain the RX and TX pins used by the USB-to-Serial-Adapter (for example pin 0 and 1 on the Arduino UNO)!!!");
        String pwmPins =     configuration.getString("pwmPins", "Pins",       "3;5;6;9;10;11", "Put all the pwm capable pins of your Arduino here, sperated by semicolons.");
        String irPins =      configuration.getString("interruptPins", "Pins", "2;3", "Put all the interrupt capable pins of your Arduino here, sperated by semicolons.");
        String analogPins =  configuration.getString("analogPins", "Pins",    "A0;A1;A2;A3;A4;A5", "Put all the analog pins of your Arduino here, sperated by semicolons.");

        ConfigHandler.digitalPins.addAll(Arrays.asList(digitalPins.split(";")));
        ConfigHandler.pwmPins.addAll(Arrays.asList(pwmPins.split(";")));
        ConfigHandler.irPins.addAll(Arrays.asList(irPins.split(";")));
        ConfigHandler.analogPins.addAll(Arrays.asList(analogPins.split(";")));

        if (configuration.hasChanged())
        {
            configuration.save();
        }
    }
}