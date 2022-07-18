package acid.module.modules.render;

import acid.module.ModuleType;
import acid.api.value.Numbers;
import acid.module.Module;

public class NoFov extends Module
{
    public static Numbers<Double> fovspoof;
    
    public NoFov() {
        super("NoFov", new String[] { "NoFov" }, ModuleType.Render);
    }
    
    static {
        NoFov.fovspoof = new Numbers<Double>("NoFov_Fov", 1.0, 0.1, 1.5, 0.01);
    }
}
