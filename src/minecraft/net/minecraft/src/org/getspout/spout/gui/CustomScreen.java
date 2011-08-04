package org.getspout.spout.gui;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.getspout.spout.packet.*;
import net.minecraft.src.*;
import java.util.ArrayList;

public class CustomScreen extends GuiScreen {
	protected PopupScreen screen;
	public boolean waiting = false;
	private int mouseX = 0, mouseY = 0;
	public CustomScreen(PopupScreen screen) {
		update(screen);
		this.setWorldAndResolution(Spout.getGameInstance(), screen.getWidth(), screen.getHeight());
	}
	
	public void update(PopupScreen screen) {
		this.screen = screen;
	}
	
	public void testScreenClose() {
		if (waiting) {
			return;
		}	
		if (this.mc.thePlayer instanceof EntityClientPlayerMP) {
			waiting = true;
			((EntityClientPlayerMP)this.mc.thePlayer).sendQueue.addToSendQueue(new CustomPacket(new PacketScreenAction(ScreenAction.ScreenClose)));
		}
	}
	
	public void closeScreen() {
		if (!waiting){
			testScreenClose();
			return;
		}
		this.mc.displayGuiScreen(null);
		this.mc.setIngameFocus();
	}
	
	public void failedCloseScreen() {
		waiting = false;
	}
	
	@Override
	public void actionPerformed(GuiButton button){
		if (button instanceof CustomGuiButton){
			((EntityClientPlayerMP)this.mc.thePlayer).sendQueue.addToSendQueue(new CustomPacket(new PacketControlAction(screen, ((CustomGuiButton)button).getWidget(), 1)));
		}
		else if (button instanceof CustomGuiSlider) {
			//This fires before the new position is set, so no good
		}	
	}
	
	@Override
	public void handleKeyboardInput() {
		boolean handled = false;
		if(Keyboard.getEventKeyState()) {
			if(Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
				handled = true;
				testScreenClose();
			}
			else {
				for (GuiButton control : getControlList()) {
					if (control instanceof CustomTextField) {
						if (((CustomTextField)control).isFocused()) {
							((CustomTextField)control).textboxKeyTyped(Keyboard.getEventCharacter(), Keyboard.getEventKey());
							handled = true;
							break;
						}
					}
				}
			}
		}
		if (!handled) {
			super.handleKeyboardInput();
		}
	}
		
	public ArrayList<GuiButton> getControlList() {
		return (ArrayList<GuiButton>)this.controlList;
	}
	
	public void drawScreen(int x, int y, float z) {
		if (!screen.isTransparent()) {
			this.drawDefaultBackground();
		}
		for (Widget widget : screen.getAttachedWidgets()) {
			if (widget instanceof GenericButton) {
				((GenericButton)widget).setup(x, y);
			}
			else if (widget instanceof GenericTextField) {
				((GenericTextField)widget).setup(x, y);
			}
			else if (widget instanceof GenericSlider) {
				((GenericSlider)widget).setup(x, y);
			}
		}
		screen.render();
		//Draw the tooltip!
		String tooltip = "";
		for(Widget w : screen.getAttachedWidgets()) {
			if(w.isVisible() && isInBoundingRect(w, x, y) && !w.getTooltip().equals("")) {
				tooltip = w.getTooltip();
				break;
			}
		}
		
		if(!tooltip.equals("")) {
			GL11.glPushMatrix();
			int tooltipWidth = this.fontRenderer.getStringWidth(tooltip);
			this.drawGradientRect(x - 3, y - 3, x + tooltipWidth + 3, y + 8 + 3, -1073741824, -1073741824);
			this.fontRenderer.drawStringWithShadow(tooltip, x, y, -1);
			GL11.glPopMatrix();
		}
	}
	
	private boolean isInBoundingRect(Widget widget, int x, int y) {
		int left = widget.getX();
		int top = widget.getY();
		int height = widget.getHeight();
		int width = widget.getWidth();
		int right = left+width;
		int bottom = top+height;
		if(left < x && x < right && top < y && y < bottom){
			return true;
		}
		return false;
	}
}