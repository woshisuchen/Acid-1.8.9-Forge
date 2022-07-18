package acid.module.modules.render;

import acid.api.value.Value;
import acid.module.ModuleType;
import acid.api.value.Numbers;
import acid.module.Module;

public class ViewClip extends Module
{
    public static Numbers<Double> N;
    
    public ViewClip() {
        super("ViewClip", new String[] { "ViewClip" }, ModuleType.Render);
        this.addValues(ViewClip.N);
    }
    
    static {
        ViewClip.N = new Numbers<Double>("3rdPersonDistance", 4.0, 1.0, 10.0, 0.01);
    }
}
