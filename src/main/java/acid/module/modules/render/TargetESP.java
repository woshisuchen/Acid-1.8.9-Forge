package acid.module.modules.render;

import acid.module.modules.combat.KillAura;
import acid.api.EventHandler;
import java.util.Iterator;
import net.minecraft.entity.player.EntityPlayer;
import acid.api.events.rendering.EventRenderGui;
import acid.module.ModuleType;
import acid.module.Module;

public class TargetESP extends Module
{
    public TargetESP() {
        super("TargetESP", new String[] { "TargetESP" }, ModuleType.Render);
        super.setRemoved(true);
    }
    
    @EventHandler
    public void Event(final EventRenderGui e) {
        for (EntityPlayer var3 : this.mc.theWorld.playerEntities) {}
    }
    
    public static boolean isPriority(final EntityPlayer player) {
        return player.equals((Object)KillAura.vip) || player.getDisplayName().getFormattedText().contains(" ¡ì6¡ìl");
    }
}
