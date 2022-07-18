package acid.api.events.world;

import acid.api.Event;

public class EventPreUpdate extends Event
{
    public static float yaw;
    public static float pitch;
    public double x;
    public static double y;
    public double z;
    public static boolean ground;
    private boolean alwaysSend;
    private boolean isPre;
    public float prevYaw;
    public static float prevPitch;
    
    public EventPreUpdate(final float prevYaw, final float prevPitch, final float yaw, final float pitch, final double x, final double y, final double z, final boolean ground) {
        this.prevYaw = prevYaw;
        EventPreUpdate.prevPitch = prevPitch;
        this.isPre = true;
        EventPreUpdate.yaw = yaw;
        EventPreUpdate.pitch = pitch;
        this.x = x;
        EventPreUpdate.y = y;
        this.z = z;
        EventPreUpdate.ground = ground;
    }
    
    public float getYaw() {
        return EventPreUpdate.yaw;
    }
    
    public void setYaw(final float yaw) {
        EventPreUpdate.yaw = yaw;
    }
    
    public float getPitch() {
        return EventPreUpdate.pitch;
    }
    
    public float getPrevPitch() {
        return EventPreUpdate.prevPitch;
    }
    
    public float getPrevYaw() {
        return this.prevYaw;
    }
    
    public void setPitch(final float pitch) {
        EventPreUpdate.pitch = pitch;
    }
    
    public double getZ() {
        return this.z;
    }
    
    public void setZ(final double z) {
        this.z = z;
    }
    
    public double getX() {
        return this.x;
    }
    
    public void setX(final double x) {
        this.x = x;
    }
    
    public double getY() {
        return EventPreUpdate.y;
    }
    
    public static void setY(final double y2) {
        EventPreUpdate.y = y2;
    }
    
    public boolean isOnground() {
        return EventPreUpdate.ground;
    }
    
    public static void setOnground(final boolean ground2) {
        EventPreUpdate.ground = ground2;
    }
}
