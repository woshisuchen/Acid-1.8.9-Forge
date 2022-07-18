package acid.module.modules.player;

import acid.module.ModuleType;
import acid.module.Module;

public class NoJumpDelay extends Module
{
    private boolean down;
    
    public NoJumpDelay() {
        super("NoJumpDelay", new String[] { "NoJumpDelay", "NoJumpDelay" }, ModuleType.Player);
    }
    
    @Override
    public void onEnable() {
    }
}
