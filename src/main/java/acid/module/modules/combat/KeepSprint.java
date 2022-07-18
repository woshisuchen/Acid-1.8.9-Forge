package acid.module.modules.combat;

import java.awt.Color;
import acid.module.ModuleType;
import acid.module.Module;

public class KeepSprint extends Module
{
    public KeepSprint() {
        super("KeepSprint", new String[] { "KeepSprint" }, ModuleType.Combat);
        this.setColor(new Color(208, 30, 142).getRGB());
    }
}
