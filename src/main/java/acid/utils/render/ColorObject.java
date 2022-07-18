package acid.utils.render;

public class ColorObject
{
    public static int red;
    public static int green;
    public static int blue;
    public int alpha;
    
    public ColorObject(final int red, final int green, final int blue, final int alpha) {
        ColorObject.red = red;
        ColorObject.green = green;
        ColorObject.blue = blue;
        this.alpha = alpha;
    }
    
    public static int getRed() {
        return ColorObject.red;
    }
    
    public static int getGreen() {
        return ColorObject.green;
    }
    
    public static int getBlue() {
        return ColorObject.blue;
    }
    
    public int getAlpha() {
        return this.alpha;
    }
    
    public void setRed(final int red) {
        ColorObject.red = red;
    }
    
    public void setGreen(final int green) {
        ColorObject.green = green;
    }
    
    public void setBlue(final int blue) {
        ColorObject.blue = blue;
    }
    
    public void setAlpha(final int alpha) {
        this.alpha = alpha;
    }
    
    public void updateColors(final int red, final int green, final int blue, final int alpha) {
        ColorObject.red = red;
        ColorObject.green = green;
        ColorObject.blue = blue;
        this.alpha = alpha;
    }
}
