package acid.api.events.world;

import net.minecraft.entity.Entity;
import acid.api.Event;

public class EventAttack extends Event
{
    public Entity entity;
    private boolean preAttack;
    
    public EventAttack(final Entity targetEntity, final boolean preAttack) {
        this.entity = targetEntity;
        this.preAttack = preAttack;
    }
    
    public Entity getEntity() {
        return this.entity;
    }
    
    public boolean isPreAttack() {
        return this.preAttack;
    }
    
    public boolean isPostAttack() {
        return !this.preAttack;
    }
}
