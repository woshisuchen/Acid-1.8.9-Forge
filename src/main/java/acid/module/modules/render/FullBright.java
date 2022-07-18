package acid.module.modules.render;

import acid.api.EventHandler;
import acid.api.events.world.EventTick;
import java.awt.Color;
import acid.module.ModuleType;
import acid.module.Module;

public class FullBright extends Module
{
    private float old;
    
    public FullBright() {
        super("FullBright", new String[] { "fbright", "brightness", "bright" }, ModuleType.Render);
        this.setColor(new Color(244, 255, 149).getRGB());
    }
    
    @Override
    public void onEnable() {
        this.old = this.mc.gameSettings.gammaSetting;
    }
    
    @EventHandler
    private void onTick(final EventTick e) {
        this.mc.gameSettings.gammaSetting = 1.5999999E7f;
    }
    
    @Override
    public void onDisable() {
        this.mc.gameSettings.gammaSetting = this.old;
    }
}
