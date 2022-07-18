package acid.api.value;

public class Option<V>
extends Value<V> {
    public int anim;
    
    public Option(final String displayName, final String name, final V enabled) {
        super(displayName, name);
        this.setValue(enabled);
    }
    
    public Option(final String displayName, final V enabled) {
        super(displayName, displayName);
        this.setValue(enabled);
    }
}
