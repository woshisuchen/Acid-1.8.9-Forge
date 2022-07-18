package acid.api.value;

public abstract class Value<V>
{
    private String displayName;
    private String name;
    public V value;
    
    public Value(final String displayName, final String name) {
        this.displayName = displayName;
        this.name = name;
    }
    
    public String getDisplayName() {
        return this.displayName;
    }
    
    public String getName() {
        return this.name;
    }
    
    public V getValue() {
        return this.value;
    }
    
    public void setValue(final V value) {
        this.value = value;
    }
}
