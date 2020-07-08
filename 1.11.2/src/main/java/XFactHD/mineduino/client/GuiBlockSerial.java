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

package XFactHD.mineduino.client;

import XFactHD.mineduino.MineDuino;
import XFactHD.mineduino.common.blocks.TileEntitySerial;
import XFactHD.mineduino.common.blocks.TileEntitySerialReceiver;
import XFactHD.mineduino.common.gui.ContainerBlockSerial;
import XFactHD.mineduino.common.net.PacketUpdateSerialParams;
import XFactHD.mineduino.common.utils.serial.SerialHandler;
import XFactHD.mineduino.common.utils.serial.ThreadCommHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;

import static XFactHD.mineduino.common.utils.ConfigHandler.modo;

public class GuiBlockSerial extends GuiContainer
{
    private TileEntitySerial te;
    private SerialHandler.PinMode mode;
    private GuiTextField pinField;

    public GuiBlockSerial(TileEntitySerial te)
    {
        super(new ContainerBlockSerial(te));
        this.te = te;
        this.mode = te.getMode();
        setGuiSize(220, 110);
        mc = Minecraft.getMinecraft();
    }

    @Override
    public void initGui()
    {
        super.initGui();

        ScaledResolution res = new ScaledResolution(mc);
        guiLeft = (res.getScaledWidth() / 2) - 110;
        guiTop = (res.getScaledHeight() /2) - 55;

        this.buttonList.add(new GuiButton(0, guiLeft + 10,   guiTop + 40, 40, 20, "Prior"));
        this.buttonList.add(new GuiButton(1, guiLeft + 170, guiTop + 40, 40, 20, "Next"));
        this.buttonList.add(new GuiButton(2, guiLeft + 90,  guiTop + 80, 40, 20, "Apply"));
        if(modo.equals("serial")){

            pinField = new GuiFilteredTextField(3, this.fontRenderer, guiLeft + 115, guiTop + 10, 20, 14,"123456789A");

        }else{
            pinField = new GuiTextField(3, this.fontRenderer, guiLeft + 115, guiTop + 10, 50, 14);


        }
        pinField.setCanLoseFocus(false);
        pinField.setFocused(true);
        //pinField.setMaxStringLength(2);
        pinField.setText(te.getPin());
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        mc.getRenderManager().renderEngine.bindTexture(new ResourceLocation("mineduino:textures/gui/gui_block_serial.png"));
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, 220, 110);
        drawString(fontRenderer, TextFormatting.WHITE + "Pin:", guiLeft + 91, guiTop + 13, 16);
        pinField.drawTextBox();
        drawCenteredString(fontRenderer, TextFormatting.WHITE + I18n.format("gui.mineduino:mode.name"), guiLeft + 110, guiTop + 40, 16);
        drawCenteredString(fontRenderer, TextFormatting.WHITE + I18n.format(mode.getName()), guiLeft + 110, guiTop + 50, 16);
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();
        pinField.updateCursorCounter();
        buttonList.get(0).enabled = mode != SerialHandler.PinMode.NONE;
        buttonList.get(1).enabled = mode != SerialHandler.PinMode.getLastValue(isReceiver());
       if(modo.equals("serial")){

           buttonList.get(2).enabled = pinField.getText().length() > 0 && ThreadCommHandler.isValidPin(pinField.getText(), mode) && isValidMode() && canUseMode() && mode != SerialHandler.PinMode.NONE;

       }else{

           buttonList.get(2).enabled = true;
       }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 0) { mode = isReceiver() ? mode.getPriorReceiver() : mode.getPriorSender(); }
        else if (button.id == 1) { mode = isReceiver() ? mode.getNextReceiver() : mode.getNextSender(); }
        else if (button.id == 2)
        {
            MineDuino.MD_NET_WRAPPER.sendToServer(new PacketUpdateSerialParams(te.getPos(), pinField.getText(), mode));
            mc.player.closeScreen();
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        super.keyTyped(typedChar, keyCode);
        pinField.textboxKeyTyped(typedChar, keyCode);
    }

    @SuppressWarnings("SimplifiableIfStatement")
    private boolean canUseMode()
    {
        if (ThreadCommHandler.getPosOnPin(pinField.getText()).size() == 1)
        {
            return true;
        }
        return ThreadCommHandler.getPinMode(pinField.getText()) == mode || ThreadCommHandler.getPinMode(pinField.getText()) == SerialHandler.PinMode.NONE;
    }

    private boolean isValidMode()
    {
        return (te instanceof TileEntitySerialReceiver) == mode.isReceiver() && (te.getMode() != mode || !pinField.getText().equals(te.getPin()));
    }

    private boolean isReceiver()
    {
        return te instanceof TileEntitySerialReceiver;
    }
}