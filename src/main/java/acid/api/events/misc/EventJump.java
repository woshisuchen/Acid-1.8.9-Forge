package acid.api.events.misc;

import acid.api.Event;

public class EventJump extends Event
{
    private double motionY;
    private boolean pre;
    
    public EventJump(final double motionY, final boolean pre) {
        this.motionY = motionY;
        this.pre = pre;
    }
    
    public double getMotionY() {
        return this.motionY;
    }
    
    public void setMotionY(final double motiony) {
        this.motionY = motiony;
    }
    
    public boolean isPre() {
        return this.pre;
    }
    
    public boolean isPost() {
        return !this.pre;
    }
}
