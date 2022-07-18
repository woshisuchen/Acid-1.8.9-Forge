package acid.module.modules.combat;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import acid.api.events.world.EventPreUpdate;
import acid.api.EventHandler;
import acid.api.events.world.EventPacketReceive;
import java.awt.Color;
import acid.api.value.Value;
import acid.module.ModuleType;
import acid.api.value.Option;
import acid.api.value.Numbers;
import acid.module.Module;

public class AntiKB extends Module
{
    public Numbers<Number> Vertical;
    public Numbers<Number> Horizontal;
    public Numbers<Number> changce;
    int z;
    private static final Option<Boolean> alerts;
    
    public AntiKB() {
        super("AntiKB", new String[] { "LegitVelocity" }, ModuleType.Combat);
        this.Vertical = new Numbers<Number>("V-Motion", 0.0, 0.0, 100.0, 5.0);
        this.Horizontal = new Numbers<Number>("H-Motion", 0.0, 0.0, 100.0, 5.0);
        this.changce = new Numbers<Number>("C-Hangce", 100.0, 0.0, 100.0, 1.0);
        this.addValues(this.Vertical, this.Horizontal, this.changce, AntiKB.alerts);
        this.setColor(new Color(191, 191, 191).getRGB());
    }
    
    @EventHandler
    private void b(final EventPacketReceive var1) {
        if (Math.random() <= this.changce.getValue().intValue() / 100) {
            this.a(var1, this.Horizontal.getValue().intValue(), this.Vertical.getValue().intValue());
        }
    }
    
    @EventHandler
    public void update(final EventPreUpdate e) {
        this.setSuffix(this.Horizontal.getValue() + "% " + this.Vertical.getValue() + "%");
        if (this.mc.thePlayer.ticksExisted <= 1) {
            this.z = 0;
        }
    }
    
    @Override
    public void onDisable() {
        this.z = 0;
    }
    
    public void a(final EventPacketReceive var1, final int var2, final int var3) {
        if (EventPacketReceive.getPacket() instanceof S12PacketEntityVelocity) {
            final S12PacketEntityVelocity var4 = (S12PacketEntityVelocity)EventPacketReceive.getPacket();
            if (var4.getEntityID() == this.mc.thePlayer.getEntityId()) {
                ++this.z;
                if (!AntiKB.alerts.getValue() || this.z % 2 == 0) {
                    final double var5 = var4.getMotionX() * var2 / 100;
                    final double var6 = var4.getMotionY() * var3 / 100;
                    final double var7 = var4.getMotionZ() * var2 / 100;
                    if (var2 != 0) {
                        this.mc.thePlayer.motionX = var5 / 8000.0;
                        this.mc.thePlayer.motionZ = var7 / 8000.0;
                    }
                    if (var3 != 0) {
                        this.mc.thePlayer.motionY = var6 / 8000.0;
                    }
                    var1.setCancelled(true);
                }
            }
        }
        if (EventPacketReceive.getPacket() instanceof S27PacketExplosion) {
            final S27PacketExplosion var8 = (S27PacketExplosion)EventPacketReceive.getPacket();
            if (!AntiKB.alerts.getValue() || this.z % 2 == 0) {
                final double var9 = var8.getX() * var2 / 100.0;
                final double var10 = var8.getY() * var3 / 100.0;
                final double var11 = var8.getZ() * var2 / 100.0;
                if (var2 != 0) {
                    final EntityPlayerSP thePlayer = this.mc.thePlayer;
                    thePlayer.motionX += var9;
                    final EntityPlayerSP thePlayer2 = this.mc.thePlayer;
                    thePlayer2.motionZ += var11;
                }
                if (var3 != 0) {
                    final EntityPlayerSP thePlayer3 = this.mc.thePlayer;
                    thePlayer3.motionY += var10;
                }
                var1.setCancelled(true);
            }
        }
    }
    
    static {
        alerts = new Option<Boolean>("A_SKIP", Boolean.valueOf(true));
    }
}
