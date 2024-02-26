package levviatasenhancedsubtitles.unused;

import com.google.common.collect.Lists;
import com.lukflug.panelstudio.component.IComponent;
import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.base.IInterface;
import com.lukflug.panelstudio.component.IComponentProxy;
import com.lukflug.panelstudio.component.IFixedComponent;
import levviatasenhancedsubtitles.config.LESConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.ISoundEventListener;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.Iterator;
import java.util.List;

public abstract class DraggableButton<T extends IFixedComponent> implements IComponentProxy<T>,IFixedComponent, ISoundEventListener {
    private static final Minecraft mc = Minecraft.getMinecraft();
    static final List<Subtitle> subtitles = Lists.newArrayList();
    private boolean isListening;
    private static String Caption1;
    private static int xTranslate, yTranslate;
    private static int width = mc.fontRenderer.getStringWidth(Caption1);
    private final int height = mc.fontRenderer.FONT_HEIGHT;
    private Point position;
    private boolean dragging;
    private int dragOffsetX, dragOffsetY;
    private String title;

    public DraggableButton(String title, Point position) {
        this.title = title;
        this.position = position;
        this.dragging = false;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public void render(Context context) {
        ScaledResolution resolution = new ScaledResolution(mc);

        if (!this.isListening && mc.gameSettings.showSubtitles)
        {
            mc.getSoundHandler().addListener(this);
            this.isListening = true;
        }
        else if (this.isListening && !mc.gameSettings.showSubtitles)
        {
            mc.getSoundHandler().removeListener(this);
            this.isListening = false;
        }

        if (this.isListening && !subtitles.isEmpty())
        {
            Vec3d playerPosition = new Vec3d(mc.player.posX, mc.player.posY + (double)mc.player.getEyeHeight(), mc.player.posZ);
            Vec3d zPlayerDirection = (new Vec3d(0.0D, 0.0D, -1.0D)).rotatePitch(-mc.player.rotationPitch * 0.017453292F).rotateYaw(-mc.player.rotationYaw * 0.017453292F);
            Vec3d yPlayerDirection = (new Vec3d(0.0D, 1.0D, 0.0D)).rotatePitch(-mc.player.rotationPitch * 0.017453292F).rotateYaw(-mc.player.rotationYaw * 0.017453292F);
            Vec3d vec3d3 = zPlayerDirection.crossProduct(yPlayerDirection);

            int maxSubtitleLength = 0;
            Iterator<Subtitle> iterator = subtitles.iterator();

            while (iterator.hasNext())
            {
                Subtitle caption = iterator.next();

                if (caption.getStartTime() + 3000L <= Minecraft.getSystemTime())
                {
                    iterator.remove();
                }
                else
                {
                    maxSubtitleLength = Math.max(maxSubtitleLength,
                            mc.fontRenderer.getStringWidth(caption.getString()));
                }
            }

            maxSubtitleLength = maxSubtitleLength +
                    mc.fontRenderer.getStringWidth("<") +
                    mc.fontRenderer.getStringWidth(" ") +
                    mc.fontRenderer.getStringWidth(">") +
                    mc.fontRenderer.getStringWidth(" ");


            int captionIndex = 0;
            for (Subtitle caption : subtitles)
            {
                Caption1 = caption.getString();

                Vec3d vec3d4 = caption.getLocation().subtract(playerPosition).normalize();
                double d0 = -vec3d3.dotProduct(vec3d4);
                double d1 = -zPlayerDirection.dotProduct(vec3d4);
                boolean flag = d1 > 0.5D;

                int halfMaxLength = maxSubtitleLength / 2;

                int subtitleHeight = mc.fontRenderer.FONT_HEIGHT;
                int subtitleWidth = mc.fontRenderer.getStringWidth(Caption1);

                /*
                + Calculates the alpha value of the current caption based on the time it
                + has been on screen
                */
                int fadeAwayCalculation = MathHelper.floor(MathHelper.clampedLerp(255.0D, 75.0D, (float)(Minecraft.getSystemTime() - caption.getStartTime()) / 3000.0F));

                // Assuming caption.getStartTime() returns the time when the subtitle was first displayed
                long elapsedTime = Minecraft.getSystemTime() - caption.getStartTime();

                // User-defined starting and ending alpha values
                int startAlpha = 255; // Fully opaque
                int endAlpha = 75; // Fully transparent

                // Calculate the new alpha value based on the elapsed time
                int newAlpha = MathHelper.floor(MathHelper.clampedLerp(startAlpha, endAlpha, (float)elapsedTime / 3000.0F));

                // Ensure the new alpha value is clamped between 0 and 255
                newAlpha = MathHelper.clamp(newAlpha, 0, 255);

                // User-defined RGB color values
                int fontRed = LESConfiguration.propFontRed.getInt();
                int fontGreen = LESConfiguration.propFontGreen.getInt();
                int fontBlue = LESConfiguration.propFontBlue.getInt();

                int backgroundRed = LESConfiguration.propBackgroundRed.getInt();
                int backgroundGreen = LESConfiguration.propBackgroundGreen.getInt();
                int backgroundBlue = LESConfiguration.propBackgroundBlue.getInt();

                // Combine the new alpha value with the RGB color values
                int fadedColor = (newAlpha << 24) | (fontRed << 16) | (fontGreen << 8) | fontBlue;

                // Now you can use fadedColor as the color for your subtitles

                int fadeAway = fadeAwayCalculation << 16 | fadeAwayCalculation << 8 | fadeAwayCalculation;

                //int fadeAwayWithColor = getFadeAwayWithColor(fadeAwayCalculation);

                //Change happens here

                String positionSwitch = LESConfiguration.propOverlayPosition.getString();

                int verticalSpacing = 1;
                int horizontalSpacing = 2;
                int subtitleSpacing = 10;

                switch (positionSwitch) {
                    case "BOTTOM_CENTER":
                        xTranslate = resolution.getScaledWidth() / 2;
                        yTranslate = resolution.getScaledHeight() - 75 - (captionIndex * subtitleSpacing);
                        break;
                    case "BOTTOM_LEFT":
                        xTranslate = halfMaxLength + horizontalSpacing;
                        yTranslate = resolution.getScaledHeight() - 30 - (captionIndex * subtitleSpacing);
                        break;
                    case "CENTER_LEFT":
                        xTranslate = halfMaxLength + horizontalSpacing;
                        yTranslate = resolution.getScaledHeight() / 2 -
                                (((subtitles.size() - 1) / 2) - captionIndex) * subtitleSpacing;
                        break;
                    case "TOP_LEFT":
                        xTranslate = halfMaxLength + horizontalSpacing;
                        yTranslate = captionIndex * subtitleSpacing + 5 + verticalSpacing;
                        break;
                    case "TOP_CENTER":
                        xTranslate = resolution.getScaledWidth() / 2;
                        yTranslate = captionIndex * subtitleSpacing + 5 + verticalSpacing;
                        break;
                    case "TOP_RIGHT":
                        xTranslate = resolution.getScaledWidth() - halfMaxLength - 2;
                        yTranslate = captionIndex * subtitleSpacing + 5 + verticalSpacing;
                        break;
                    case "CENTER_RIGHT":
                        xTranslate = resolution.getScaledWidth() -
                                resolution.getScaledWidth() - halfMaxLength - horizontalSpacing;
                        yTranslate = resolution.getScaledHeight() / 2 -
                                (((subtitles.size() - 1) / 2) - captionIndex) * subtitleSpacing;
                        break;
                    default: //if there's any invalid input just show it in the bottom right
                        xTranslate = resolution.getScaledWidth() - halfMaxLength - 2;
                        yTranslate = resolution.getScaledHeight() - 30 - (captionIndex * subtitleSpacing);
                        break;
                }
                position = new Point(xTranslate, yTranslate);
                Color color1 = new Color(255, 255, 255, 255);
                Color color2 = new Color(0, 0, 0, 255);
                Color color3 = new Color(0, 0, 0, 255);
                Color color4 = new Color(255, 255, 255, 255);
                Rectangle rect = new Rectangle(context.getPos().x, context.getPos().y,
                        subtitleWidth, subtitleHeight);
                //drawRect(-halfMaxLength - 1, -subtitleHeight / 2 - 1, halfMaxLength + 1, subtitleHeight / 2 + 1, backgroundSubtitleColor);
                context.getInterface().drawRect(rect, color1, color2, color3, color4);
                Color textColor = new Color(fontRed, fontGreen, fontBlue, newAlpha);
                if (!flag) {
                    if (d0 > 0.00D)
                    {
                        Point left = new Point(context.getPos().x - halfMaxLength, context.getPos().y);
                        context.getInterface().drawString(left, -halfMaxLength, ">", textColor);
                    }
                    else if (d0 < -0.00D)
                    {
                        Point right = new Point(context.getPos().x - halfMaxLength, context.getPos().y);
                        context.getInterface().drawString(right, -subtitleHeight / 2, "<", textColor);
                    }
                }

                Point textPos = new Point(context.getPos().x -subtitleWidth / 2, context.getPos().y);
                context.getInterface().drawString(textPos, -subtitleHeight / 2, Caption1, textColor);
                ++captionIndex;
            }
        }
        if (dragging) {
            position = new Point(context.getInterface().getMouse().x - dragOffsetX, context.getInterface().getMouse().y - dragOffsetY);
        }
    }

    @Override
    public void handleButton(Context context, int button) {
        if (button == IInterface.LBUTTON) {
            if (context.isClicked(button)) {
                dragging = true;
                dragOffsetX = context.getInterface().getMouse().x - position.x;
                dragOffsetY = context.getInterface().getMouse().y - position.y;
            } else if (!context.isHovered()) {
                dragging = false;
            }
        }
    }
    @Override
    public void soundPlay(ISound soundIn, SoundEventAccessor accessor) {
        // Your custom implementation here
        if (accessor.getSubtitle() != null) {
            String subtitleText = accessor.getSubtitle().getFormattedText();

            if (!subtitles.isEmpty()) {
                for (Subtitle caption : subtitles) {
                    if (caption.getString().equals(subtitleText)) {
                        caption.refresh(new Vec3d(soundIn.getXPosF(), soundIn.getYPosF(), soundIn.getZPosF()));
                        return;
                    }
                }
            }
            subtitles.add(new Subtitle(subtitleText, new Vec3d(soundIn.getXPosF(), soundIn.getYPosF(), soundIn.getZPosF())));
        }
    }

    @Override
    public void handleKey(Context context, int i) {

    }

    @Override
    public void handleChar(Context context, char c) {

    }

    @Override
    public void handleScroll(Context context, int i) {

    }

    @Override
    public void getHeight(Context context) {

    }

    @Override
    public void enter() {

    }

    @Override
    public void exit() {

    }

    @Override
    public void releaseFocus() {

    }

    @Override
    public boolean isVisible() {
        return false;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    private static class Subtitle
    {
        private final String subtitle;
        private long startTime;
        private Vec3d location;

        public Subtitle(String subtitleIn, Vec3d locationIn)
        {
            this.subtitle = subtitleIn;
            this.location = locationIn;
            this.startTime = Minecraft.getSystemTime();
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