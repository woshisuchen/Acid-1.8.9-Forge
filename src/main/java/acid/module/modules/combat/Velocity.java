package acid.module.modules.combat;

import java.awt.Color;

import acid.api.EventHandler;
import acid.api.events.world.EventPacketReceive;
import acid.api.value.Option;
import acid.module.Module;
import acid.module.ModuleType;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

public class Velocity extends Module
{
    private static final Option<Boolean> Suffix;
    
    public Velocity() {
        super("Velocity", new String[] { "velocity" }, ModuleType.Combat);
        this.setColor(new Color(191, 191, 191).getRGB());
        this.addValues(Velocity.Suffix);
    }
    
    @EventHandler
    private void onPacket(final EventPacketReceive event) {
        if (Velocity.Suffix.getValue()) {
            this.setSuffix("Cancel");
        }
        else {
            this.setSuffix("");
        }
        final EntityPlayerSP player = this.mc.thePlayer;
        if (EventPacketReceive.getPacket() instanceof S12PacketEntityVelocity) {
            final S12PacketEntityVelocity s12 = (S12PacketEntityVelocity)EventPacketReceive.getPacket();
            if (player.getEntityId() == s12.getEntityID()) {
                event.setCancelled(true);
            }
        }
        else if (EventPacketReceive.getPacket() instanceof S27PacketExplosion) {
            event.setCancelled(true);
        }
    }
    
    static {
        Suffix = new Option<Boolean>("Set Suffix", "Set Suffix", true);
    }
}
