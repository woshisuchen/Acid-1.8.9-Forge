package acid.api.events.misc;

import net.minecraft.entity.player.EntityPlayer;
import acid.api.Event;

public class EventInventory extends Event
{
    private final EntityPlayer player;
    
    public EventInventory(final EntityPlayer player) {
        this.player = player;
    }
    
    public EntityPlayer getPlayer() {
        return this.player;
    }
}
