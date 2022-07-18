package acid.module.modules.movement;

import acid.api.EventHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import acid.module.modules.combat.KillAura;
import acid.utils.MovementUtils;
import acid.api.events.world.EventPostUpdate;
import acid.api.value.Value;
import java.awt.Color;
import acid.module.ModuleType;
import acid.api.value.Numbers;
import acid.module.Module;

public class NoSlow extends Module
{
    public static final Numbers<Number> Reduceslow;
    
    public NoSlow() {
        super("NoSlow", new String[] { "noslowdown" }, ModuleType.Movement);
        this.setColor(new Color(216, 253, 100).getRGB());
        this.addValues(NoSlow.Reduceslow);
    }
    
    @EventHandler
    private void onUpdate(final EventPostUpdate e) {
        if (!MovementUtils.isMoving()) {
            return;
        }
        if (this.mc.thePlayer.isUsingItem() || this.mc.thePlayer.isBlocking() || KillAura.blocking) {
            this.mc.getNetHandler().addToSendQueue((Packet)new C08PacketPlayerBlockPlacement(this.mc.thePlayer.getHeldItem()));
        }
    }
    
    static {
        Reduceslow = new Numbers<Number>("Reduceslow", 100.0, 0.0, 100.0, 5.0);
    }
}
