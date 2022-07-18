package acid.api.events.world;

import acid.api.Event;

public class SafeWalkEvent extends Event
{
    public SafeWalkEvent(final boolean safeWalking) {
        this.setCancelled(safeWalking);
    }
}
