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

import XFactHD.mineduino.common.utils.ConfigHandler;
import XFactHD.mineduino.common.utils.LogHelper;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import jdk.jfr.events.SocketReadEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import scala.actors.Debug;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Locale;

import static XFactHD.mineduino.common.utils.ConfigHandler.modo;

public class SerialHandler implements SerialPortEventListener
{
    private static final SerialHandler INSTANCE = new SerialHandler();

    private boolean initialized = false;
    private SerialPort port;

    private Thread senderThread;

    private BufferedReader input;
    private OutputStream output;
    private static final int TIME_OUT = 5;
    private static final int DATA_RATE = 38400;
    public static   ServerSocket socket_server;
    private static volatile boolean portReady = true;
public static Socket socket_cliente;
    public static SerialHandler getSerialHandler()
    {
        return INSTANCE;
    }

    public void initialize()
    {
        CommPortIdentifier portId = null;
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
        if(modo.equals("serial")){
        while (portEnum.hasMoreElements())
        {
            CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
            if (currPortId.getName().equals(ConfigHandler.port))
            {
                portId = currPortId;
                break;
            }
        }
        if (portId == null)
        {
            LogHelper.error("Could not find COM port! Is your Arduino connected?");
            return;
        }

        try
        {

            port = (SerialPort) portId.open("MineDuino", TIME_OUT);
            port.setSerialPortParams(DATA_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);





            input = new BufferedReader(new InputStreamReader(port.getInputStream()));
            output = port.getOutputStream();

            port.addEventListener(this);
            port.notifyOnDataAvailable(true);

            initialized = true;

        }
        catch (Exception e)
        {
            LogHelper.error("Failed to open the serial port!");
            e.printStackTrace();
        }

        senderThread = new Thread("MineDuinoSerialSender")
        {
            private long time = 0;

            @Override
            @SuppressWarnings("InfiniteLoopStatement")
            public void run()
            {
                while (true)
                {
                    time = System.currentTimeMillis();
                    ThreadCommHandler.executeQueuedTasks();
                    try { sleep(50 - (System.currentTimeMillis() - time)); }
                    catch (InterruptedException e)
                    {
                        LogHelper.error("Thread '" + Thread.currentThread().getName() + "' was interrupted!");
                        e.printStackTrace();
                    }
                }
            }
        };
        if (initialized) { senderThread.start();



        }
        }else{

             socket_server = null;
            try {
                socket_server = new ServerSocket(8888);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                socket_cliente = socket_server.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                output = socket_cliente.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                input = new BufferedReader(new InputStreamReader(socket_cliente.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            initialized = true;
            senderThread = new Thread("MineDuinoSerialSender")
            {
                private long time = 0;

                @Override
                @SuppressWarnings("InfiniteLoopStatement")
                public void run()
                {
                    while (true)
                    {
                        time = System.currentTimeMillis();
                        ThreadCommHandler.executeQueuedTasks();
                        try { sleep(50 - (System.currentTimeMillis() - time));






                        }
                        catch (InterruptedException e)
                        {
                            LogHelper.error("Thread '" + Thread.currentThread().getName() + "' was interrupted!");
                            e.printStackTrace();
                        }
                    }
                }
            };

            if (initialized) {
                senderThread.start();
            }
        }
        }
    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof EntityPlayer && !event.getEntity().world.isRemote) {


            Debug.warning("Debugando.. player entrou no mundo.");

        }
    }


    public synchronized void serialEvent(SocketReadEvent event){

        try
        {
            if (input.ready())
            {
                portReady = false;
                String[] vals = new String[] {"none","dr","drp","dw","ar","aw","ir"};
                String data = input.readLine();
                System.out.println("DataIn: " + data);
                String[] parts = data.split(";");
                if (Arrays.asList(vals).contains(parts[1]) && parts.length == 3)
                {
                    ThreadCommHandler.receiveData(parts[0], PinMode.fromString(parts[1]), Integer.valueOf(parts[2]));
                }
                portReady = true;
            }
        }
        catch (Exception e)
        {
            System.err.println(e.toString());
            e.printStackTrace();
        }
    }
    @Override
    public synchronized void serialEvent(SerialPortEvent event)
    {
if(modo.equals("socket")){





}else{



        if (event.getEventType() == SerialPortEvent.DATA_AVAILABLE)
        {
            try
            {
                if (input.ready())
                {
                    portReady = false;
                    String[] vals = new String[] {"none","dr","drp","dw","ar","aw","ir"};
                    String data = input.readLine();
                  System.out.println("DataIn: " + data);
                    String[] parts = data.split(";");
                    if (Arrays.asList(vals).contains(parts[1]) && parts.length == 3)
                    {
                        ThreadCommHandler.receiveData(parts[0], PinMode.fromString(parts[1]), Integer.valueOf(parts[2]));
                    }
                    portReady = true;
                }
            }
            catch (Exception e)
            {
                System.err.println(e.toString());
                e.printStackTrace();
            }

        }
        }
    }

    @SuppressWarnings("deprecation")
    public synchronized void close()
    {
        if (port != null)
        {
            senderThread.stop();
            port.removeEventListener();
            port.close();
        }
    }

    public Exception sendMessage(String pin, PinMode mode, int value)
    {
        Exception exception = null;

        if(modo.equals("socket")){

            try {
                String out = pin + ";" + mode.toString() + ";" + Integer.toString(value);

                output.write(out.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else {


            checkPortReady();
            try {
                if (mode.isReceiver()) {
                    portReady = false;
                }
                String out = pin + ";" + mode.toString() + ";" + Integer.toString(value);
               System.out.println("DataOut: " + out);
                output.write(out.getBytes());
            } catch (IOException e) {
                exception = e;
            }
        }
            return exception;

    }

    public Exception requestValue(String pin, PinMode mode)
    {
        Exception exception = null;
        if(modo.equals("socket")){

            try {
                String out = pin + ";" + mode.toString() + ";-1";

                output.write(out.getBytes());

            } catch (IOException e) {
                e.printStackTrace();
            }

        }else {




        checkPortReady();
        try
        {
            String out = pin + ";" + mode.toString() + ";-1";
            output.write(out.getBytes());
        }
        catch (IOException e)
        {
            exception = e;
        }
        }
        return exception;
    }

    public Exception setInterrupt(String pin, boolean active) {
        Exception exception = null;
        if (modo.equals("socket")) {


            try {
                String out = pin + ";ir;" + (active ? "1" : "0");
                output.write(out.getBytes());

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {

        }
        checkPortReady();
        try {
            String out = pin + ";ir;" + (active ? "1" : "0");
            output.write(out.getBytes());
        } catch (IOException e) {
            exception = e;
        }

        return exception;
    }

    public boolean isInitialized()
    {
        return initialized;
    }

    private void checkPortReady()
    {
        while (!portReady)
        {
            try
            {
                Thread.sleep(1);
            }
            catch (InterruptedException e)
            {
                LogHelper.info("Thread " + Thread.currentThread().getName() + " was interrupted!");
                e.printStackTrace();
            }
        }
    }

    public enum PinMode
    {
        NONE("mode.mineduino:none.name"),//Pin not used
        DR  ("mode.mineduino:dr.name"),  //digitalRead
        DRP ("mode.mineduino:drp.name"), //digitalRead with internal pullup
        DW  ("mode.mineduino:dw.name"),  //digitalWrite
        AR  ("mode.mineduino:ar.name"),  //analogRead
        AW  ("mode.mineduino:aw.name"),  //analogWrite
        IR  ("mode.mineduino:ir.name");  //Interrupt attached

        private String name;

        PinMode(String name)
        {
            this.name = name;
        }

        @Override
        public String toString()
        {
            return super.toString().toLowerCase(Locale.ENGLISH);
        }

        public static PinMode fromString(String name)
        {
            return valueOf(name.toUpperCase(Locale.ENGLISH));
        }

        public boolean isReceiver()
        {
            return this == DR || this == DRP || this == AR || this == IR;
        }

        public boolean isDigital()
        {
            return this == DR || this == DRP || this == DW || this == IR;
        }

        public PinMode getNextReceiver()
        {
            switch (this)
            {
                case NONE: return DR;
                case DR: return DRP;
                case DRP: return AR;
                case AR: return IR;
                case IR: return this;
            }
            return this;
        }

        public PinMode getPriorReceiver()
        {
            switch (this)
            {
                case NONE: return this;
                case DR: return NONE;
                case DRP: return DR;
                case AR: return DRP;
                case IR: return AR;
            }
            return this;
        }

        public PinMode getNextSender()
        {
            switch (this)
            {
                case NONE: return DW;
                case DW: return AW;
                case AW: return this;
            }
            return this;
        }

        public PinMode getPriorSender()
        {
            switch (this)
            {
                case NONE: return this;
                case DW: return NONE;
                case AW: return DW;
            }
            return this;
        }

        public String getName()
        {
            return name;
        }

        public static PinMode getLastValue(boolean receiver)
        {
            return receiver ? IR : AW;
        }
    }
}