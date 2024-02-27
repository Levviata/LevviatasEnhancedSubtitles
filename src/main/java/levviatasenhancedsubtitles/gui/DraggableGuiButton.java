package levviatasenhancedsubtitles.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.Minecraft;

public class DraggableGuiButton extends GuiButton {
    private boolean dragging;
    private int lastMouseX;
    private int lastMouseY;

    public DraggableGuiButton(int buttonId, int x, int y, String buttonText) {
        super(buttonId, x, y, buttonText);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {

    }

    @Override
    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            if (this.dragging) {
                this.x += mouseX - this.lastMouseX;
                this.y += mouseY - this.lastMouseY;
            }
            this.lastMouseX = mouseX;
            this.lastMouseY = mouseY;
        }
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (super.mousePressed(mc, mouseX, mouseY)) {
            this.dragging = true;
            this.lastMouseX = mouseX;
            this.lastMouseY = mouseY;
            return true;
        }
        return false;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        this.dragging = false;
    }
}