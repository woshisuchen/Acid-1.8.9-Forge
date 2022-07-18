package acid.api.value;

public class Mode<V extends Enum> extends Value<V>
{
    private V[] modes;
    
    public Mode(final String displayName, final String name, final V[] modes, final V value) {
        super(displayName, name);
        this.modes = modes;
        this.setValue(value);
    }
    
    public Mode(final String displayName, final V[] modes, final V value) {
        super(displayName, displayName);
        this.modes = modes;
        this.setValue(value);
    }
    
    public V[] getModes() {
        return (V[])this.modes;
    }
    
    public String getModeAsString() {
        return this.getValue().name();
    }
    
    public void setMode(final String mode) {
        for (final V e : this.modes) {
            if (e.name().equalsIgnoreCase(mode)) {
                this.setValue(e);
            }
        }
    }
    
    public boolean isValid(final String name) {
        for (final V e : this.modes) {
            if (e.name().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
}
