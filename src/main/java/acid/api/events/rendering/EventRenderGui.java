package acid.api.events.rendering;

import net.minecraft.client.gui.ScaledResolution;
import acid.api.Event;

public class EventRenderGui extends Event
{
    public ScaledResolution sr;
    
    public EventRenderGui(final ScaledResolution sr2) {
        this.sr = sr2;
    }
    
    public ScaledResolution getResolution() {
        return this.sr;
    }
}
