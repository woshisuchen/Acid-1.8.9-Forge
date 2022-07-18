package acid.module;

public class AutoReconnect
{
    public static final double MAX = 60000.0;
    public static final double MIN = 1000.0;
    public static boolean isEnabled;
    public static float delay;
    
    public AutoReconnect(final float value) {
        AutoReconnect.isEnabled = (value < 60000.0);
        AutoReconnect.delay = value;
    }
    
    public static float getDelay() {
        return AutoReconnect.delay;
    }
    
    public static void setDelay(final float packet) {
        AutoReconnect.delay = packet;
    }
    
    public static boolean isEnabled() {
        return AutoReconnect.isEnabled;
    }
    
    static {
        AutoReconnect.isEnabled = true;
        AutoReconnect.delay = 5000.0f;
    }
}
