package acid.api.events.misc;

import acid.api.Event;

public class EventChat extends Event
{
    private String message;
    
    public EventChat(final String message) {
        this.message = message;
        this.setType((byte)0);
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public void setMessage(final String message) {
        this.message = message;
    }
}
