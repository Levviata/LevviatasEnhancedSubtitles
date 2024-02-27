package levviatasenhancedsubtitles.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.Vec3d;

public class SubtitleOverlayHandler
{

    public static class Subtitle
    {
        final String subtitle;
        long startTime;
        Vec3d location;
        String text;
        int x, y;
        int width, height;
        boolean isDragging;

        public Subtitle(String subtitleIn, Vec3d locationIn)
        {
            this.subtitle = subtitleIn;
            this.location = locationIn;
            this.startTime = Minecraft.getSystemTime();
        }
        public void setPosition(int x, int y) {
            this.x = x;
            this.y = y;
        }
        public void setText(String textIn) {
            this.text = textIn;
        }

        public int getHeight() {
            return height;
        }

        public int getWidth() {
            return width;
        }

        public boolean isMouseOver(int mouseX, int mouseY) {
            return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
        }

        // Getters and setters for other fields...
        public String getText() {
            return text;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public void setDragging(boolean dragging) {
            isDragging = dragging;
        }

        public boolean isDragging() {
            return isDragging;
        }

        public String getString()
        {
            return this.subtitle;
        }

        public long getStartTime()
        {
            return this.startTime;
        }

        public Vec3d getLocation()
        {
            return this.location;
        }

        public void refresh(Vec3d locationIn)
        {
            this.location = locationIn;
            this.startTime = Minecraft.getSystemTime();
        }
    }
}
