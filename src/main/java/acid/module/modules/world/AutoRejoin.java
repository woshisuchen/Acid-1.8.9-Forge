package acid.module.modules.world;

import acid.module.ModuleType;
import acid.module.Module;

public class AutoRejoin extends Module
{
    private boolean down;
    
    public AutoRejoin() {
        super("Logout", new String[] { "Logout", "Logout" }, ModuleType.World);
    }
    
    @Override
    public void onEnable() {
        this.setEnabled(false);
        if (this.mc.thePlayer == null) {
            return;
        }
        this.mc.thePlayer.sendChatMessage("/lobby");
        new Thread(() -> {
            try {
                Thread.sleep(200L);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.mc.thePlayer.sendChatMessage("/back");
        }).start();
    }
}
