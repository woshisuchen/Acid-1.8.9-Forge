package acid.api.events.misc;

import acid.api.Event;

public class EventTitle extends Event
{
    private final String message;
    
    public EventTitle(final String message) {
        this.message = message;
    }
    
    public String getMessage() {
        return this.message;
    }
}
