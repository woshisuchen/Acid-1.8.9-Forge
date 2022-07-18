package acid.utils.render;

import org.lwjgl.opengl.GL11;
import java.awt.Color;

public class ColorUtil
{
    public static Color getRainbow(final float second, final float sat, final float bright) {
        final float hue = System.currentTimeMillis() % (int)(second * 1000.0f) / (second * 1000.0f);
        return new Color(Color.HSBtoRGB(hue, sat, bright));
    }
    
    public static Color getRainbow(final float second, final float sat, final float bright, final long index) {
        final float hue = (System.currentTimeMillis() + index) % (int)(second * 1000.0f) / (second * 1000.0f);
        return new Color(Color.HSBtoRGB(hue, sat, bright));
    }
    
    public static Color getFadeRainbow(final Color color, final int index, final int count) {
        final float[] hsb = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
        float brightness = Math.abs((System.currentTimeMillis() % 2000L / 1000.0f + index / count * 2.0f) % 2.0f - 1.0f);
        brightness = 0.5f + 0.5f * brightness;
        hsb[2] = brightness % 2.0f;
        return new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
    }
    
    public static Color getBlendColor(final double current, final double max) {
        final long base = Math.round(max / 5.0);
        if (current >= base * 5L) {
            return new Color(15, 255, 15);
        }
        if (current >= base * 4L) {
            return new Color(166, 255, 0);
        }
        if (current >= base * 3L) {
            return new Color(255, 191, 0);
        }
        if (current >= base * 2L) {
            return new Color(255, 89, 0);
        }
        return new Color(255, 0, 0);
    }
    
    public static Color getDarker(final Color before, final int dark, final int alpha) {
        final int rDank = Math.max(before.getRed() - dark, 0);
        final int gDank = Math.max(before.getGreen() - dark, 0);
        final int bDank = Math.max(before.getBlue() - dark, 0);
        return new Color(rDank, gDank, bDank, alpha);
    }
    
    public static Color getLighter(final Color before, final int light, final int alpha) {
        final int rDank = Math.min(before.getRed() + light, 255);
        final int gDank = Math.min(before.getGreen() + light, 255);
        final int bDank = Math.min(before.getBlue() + light, 255);
        return new Color(rDank, gDank, bDank, alpha);
    }
    
    public static void glColor(final int color) {
        final float f = (color >> 24 & 0xFF) / 255.0f;
        final float f2 = (color >> 16 & 0xFF) / 255.0f;
        final float f3 = (color >> 8 & 0xFF) / 255.0f;
        final float f4 = (color & 0xFF) / 255.0f;
        GL11.glColor4f(f2, f3, f4, f);
    }
    
    public static Color reAlpha(final Color before, final int alpha) {
        return new Color(before.getRed(), before.getGreen(), before.getBlue(), alpha);
    }
    
    public static int getColor(final int red, final int green, final int blue, final int alpha) {
        int color = 0;
        color |= alpha << 24;
        color |= red << 16;
        color |= green << 8;
        return color |= blue;
    }
    
    public static int getColor(final int brightness) {
        return getColor(brightness, brightness, brightness, 255);
    }
    
    public static Color rainbow(final long time, final float count, final float fade) {
        final float hue = (time + (1.0f + count) * 2.0E8f) / 1.0E10f % 1.0f;
        final long color = Long.parseLong(Integer.toHexString(Color.HSBtoRGB(hue, 0.7f, 1.0f)), 16);
        final Color c = new Color((int)color);
        return new Color(c.getRed() / 255.0f * fade, c.getGreen() / 255.0f * fade, c.getBlue() / 255.0f * fade, c.getAlpha() / 255.0f);
    }
}
