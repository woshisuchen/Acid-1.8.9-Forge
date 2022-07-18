package acid.api.events.rendering;

import net.minecraft.client.gui.ScaledResolution;
import acid.api.Event;

public class EventRender2D extends Event
{
    public ScaledResolution sr;
    private float pt;
    
    public EventRender2D(final ScaledResolution sr, final float pt) {
        this.sr = sr;
        this.pt = pt;
    }
    
    public ScaledResolution getSR() {
        return this.sr;
    }
    
    public float getPartialTicks() {
        return this.pt;
    }
    
    public float getPT() {
        return this.pt;
    }
}
