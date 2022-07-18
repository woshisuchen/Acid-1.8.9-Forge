package acid.module.modules.combat;

import acid.management.ModuleManager;
import acid.api.value.Value;
import acid.module.ModuleType;
import acid.api.value.Numbers;
import acid.module.Module;

public class Hitbox extends Module
{
    public static Numbers<Double> size;
    
    public Hitbox() {
        super("Hitbox", new String[] { "Hitbox" }, ModuleType.Combat);
        this.addValues(Hitbox.size);
    }
    
    public static float getSize() {
        return ModuleManager.getModuleByClass(Hitbox.class).isEnabled() ? ((float)(Object)Hitbox.size.getValue()) : 0.1f;
    }
    
    static {
        Hitbox.size = new Numbers<Double>("Size", 0.25, 0.1, 1.0, 0.05);
    }
}
