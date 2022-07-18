package acid.api.events.world;

import acid.api.Event;

public class StepEvent extends Event
{
    private double stepHeight;
    private boolean pre;
    
    public StepEvent(final double stepHeight, final boolean pre) {
        this.stepHeight = stepHeight;
        this.pre = pre;
    }
    
    public double getStepHeight() {
        return this.stepHeight;
    }
    
    public void setStepHeight(final double stepHeight) {
        this.stepHeight = stepHeight;
    }
    
    public boolean isPre() {
        return this.pre;
    }
    
    public boolean isPost() {
        return !this.pre;
    }
}
