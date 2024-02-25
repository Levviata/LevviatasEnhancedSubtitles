package levviatasenhancedsubtitles.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PositionOverlayHandler extends GuiScreen {

    private Logger logger = Logger.getLogger("levviatasenhancedsubtitles");
    private GuiButton buttonXPlus, buttonXMinus, buttonYPlus, buttonYMinus, buttonPosition;
    private GuiTextField subtitleTextField;
    private int subtitleX, subtitleY;
    private boolean isDragging;
    private int lastMouseX, lastMouseY;
    private int textWidth;
    private int textHeight;
    private String[] positionChoices = {
            "BOTTOM_RIGHT", "BOTTOM_LEFT", "BOTTOM_CENTER",
            "CENTER_LEFT", "TOP_LEFT", "TOP_CENTER",
            "TOP_RIGHT", "CENTER_RIGHT"
    };
    private String currentOverlayPosition = "BOTTOM_RIGHT";
    private int currentPositionIndex = 0;

    @Override
    public void initGui() {
        ScaledResolution scaledResolution = new ScaledResolution(this.mc);
        int scaledWidth = scaledResolution.getScaledWidth();
        int scaledHeight = scaledResolution.getScaledHeight();
        this.textHeight = this.fontRenderer.FONT_HEIGHT;

        // Initialize the subtitle text field
        this.subtitleTextField = new GuiTextField(5, this.fontRenderer, scaledWidth / 2 - 150, scaledHeight / 2 - 10, 300, 20);
        this.subtitleTextField.setMaxStringLength(200);
        this.subtitleTextField.setText("Subtitle Text Here");

        // Initialize textWidth after creating the subtitleTextField
        this.textWidth = this.fontRenderer.getStringWidth(subtitleTextField.getText());

        // Initialize your buttons and text fields here
        this.buttonList.add(this.buttonXPlus = new GuiButton(0, scaledWidth / 2 + 50, scaledHeight / 2, 20, 20, "+"));
        this.buttonList.add(this.buttonXMinus = new GuiButton(1, scaledWidth / 2 - 70, scaledHeight / 2, 20, 20, "-"));
        this.buttonList.add(this.buttonYPlus = new GuiButton(2, scaledWidth / 2 + 50, scaledHeight / 2 + 30, 20, 20, "+"));
        this.buttonList.add(this.buttonYMinus = new GuiButton(3, scaledWidth / 2 - 70, scaledHeight / 2 + 30, 20, 20, "-"));
        this.buttonList.add(this.buttonPosition = new GuiButton(4, scaledWidth / 2 - 100, scaledHeight - 30, 200, 20, "Position: " + currentOverlayPosition));

        // Set initial subtitle position
        updateSubtitlePosition();

        drawScreen(0, 0, 20);
        logger.log(Level.INFO, "Initialized gui on the screen.");
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button == this.buttonXPlus) {
            subtitleX += 5;
        } else if (button == this.buttonXMinus) {
            subtitleX -= 5;
        }
        if (button == this.buttonYPlus) {
            subtitleY += 5;
        } else if (button == this.buttonYMinus) {
            subtitleY -= 5;
        }

        if (button == this.buttonPosition) {
            currentPositionIndex = (currentPositionIndex + 1) % positionChoices.length;
            currentOverlayPosition = positionChoices[currentPositionIndex];
            button.displayString = "Position: " + currentOverlayPosition;
            updateSubtitlePosition();
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }

    private void updateSubtitlePosition() {
        // Update the subtitle position based on the currentOverlayPosition
        ScaledResolution scaledResolution = new ScaledResolution(this.mc);
        int scaledWidth = scaledResolution.getScaledWidth();
        int scaledHeight = scaledResolution.getScaledHeight();

        // Update the subtitle position based on the currentOverlayPosition
        switch (currentOverlayPosition) {
            case "BOTTOM_RIGHT":
                subtitleX = scaledWidth - textWidth - 10;
                subtitleY = scaledHeight - textHeight - 10;
                break;
            case "BOTTOM_LEFT":
                subtitleX = 10;
                subtitleY = scaledHeight - textHeight - 10;
                break;
            case "BOTTOM_CENTER":
                subtitleX = scaledWidth / 2 - textWidth / 2;
                subtitleY = scaledHeight - textHeight - 10;
                break;
            case "CENTER_LEFT":
                subtitleX = 10;
                subtitleY = scaledHeight / 2 - textHeight / 2;
                break;
            case "TOP_LEFT":
                subtitleX = 10;
                subtitleY = 10;
                break;
            case "TOP_CENTER":
                subtitleX = scaledWidth / 2 - textWidth / 2;
                subtitleY = 10;
                break;
            case "TOP_RIGHT":
                subtitleX = scaledWidth - textWidth - 10;
                subtitleY = 10;
                break;
            case "CENTER_RIGHT":
                subtitleX = scaledWidth - textWidth - 10;
                subtitleY = scaledHeight / 2 - textHeight / 2;
                break;
            // Add additional cases if needed
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (subtitleTextField.mouseClicked(mouseX, mouseY, mouseButton)) {
            this.isDragging = true;
            this.lastMouseX = mouseX;
            this.lastMouseY = mouseY;
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (this.isDragging) {
            subtitleX += mouseX - lastMouseX;
            subtitleY += mouseY - lastMouseY;
            this.lastMouseX = mouseX;
            this.lastMouseY = mouseY;
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        this.isDragging = false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        subtitleTextField.x = subtitleX;
        subtitleTextField.y = subtitleY;
        subtitleTextField.drawTextBox();
        logger.log(Level.INFO, "Drawn subtitle overlay on the screen.");
    }

    @Override
    public void onResize(Minecraft mc, int w, int h) {
        super.onResize(mc, w, h);
        initGui();
    }
}