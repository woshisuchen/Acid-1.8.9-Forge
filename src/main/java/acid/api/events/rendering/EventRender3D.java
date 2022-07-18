package acid.api.events.rendering;

import acid.api.Event;

public class EventRender3D extends Event
{
    private float ticks;
    
    public EventRender3D() {
    }
    
    public EventRender3D(final float ticks) {
        this.ticks = ticks;
    }
    
    public float getPartialTicks() {
        return this.ticks;
    }
}
