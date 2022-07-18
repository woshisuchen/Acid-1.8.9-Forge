package acid.api.events.misc;

import acid.api.Event;

public class EventKey extends Event
{
    private int key;
    
    public EventKey(final int key) {
        this.key = key;
    }
    
    public int getKey() {
        return this.key;
    }
    
    public void setKey(final int key) {
        this.key = key;
    }
}
