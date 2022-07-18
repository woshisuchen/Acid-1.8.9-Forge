package acid.module.modules.player;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.entity.Entity;
import acid.utils.Helper;
import net.minecraft.client.entity.EntityPlayerSP;
import acid.api.events.world.EventPreUpdate;
import acid.api.EventHandler;
import net.minecraft.network.Packet;
import buzz.gaoyusense.injection.interfaces.INetworkManager;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0APacketAnimation;
import acid.api.events.world.EventPacketSend;
import java.awt.Color;
import acid.module.ModuleType;
import acid.module.Module;

public class NoFall extends Module
{
    private double z;
    private double A;
    
    public NoFall() {
        super("NoFall", new String[] { "Nofalldamage" }, ModuleType.Player);
        this.setColor(new Color(242, 137, 73).getRGB());
    }
    
    @EventHandler
    public final void onPacket(final EventPacketSend event) {
        if (this.mc.thePlayer.fallDistance > 2.2 && this.mc.thePlayer.motionY != 0.0 && this.mc.thePlayer.posY > 0.0) {
            if (EventPacketSend.getPacket() instanceof C0APacketAnimation || EventPacketSend.getPacket() instanceof C02PacketUseEntity) {
                event.setCancelled(true);
            }
            if (EventPacketSend.getPacket() instanceof C03PacketPlayer) {
                final C03PacketPlayer var4 = (C03PacketPlayer)EventPacketSend.getPacket();
                if (var4.isMoving() && var4.getRotating()) {
                    ((INetworkManager)this.mc.thePlayer.sendQueue.getNetworkManager()).sendPacketNoEvent((Packet)new C03PacketPlayer.C04PacketPlayerPosition(var4.getPositionX(), var4.getPositionY(), var4.getPositionZ(), var4.isOnGround()));
                    event.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler
    public void update(final EventPreUpdate e) {
        if (this.mc.thePlayer.fallDistance > 3.0f && this.mc.thePlayer.motionY != 0.0 && this.mc.thePlayer.posY > 0.0) {
            if (this.A + this.mc.thePlayer.fallDistance - this.z > 3.0 || (this.mc.thePlayer.fallDistance > 3.0f && this.A == 0.0)) {
                ((INetworkManager)this.mc.thePlayer.sendQueue.getNetworkManager()).sendPacketNoEvent((Packet)new C03PacketPlayer(true));
                this.A = 0.0;
            }
            this.A += this.mc.thePlayer.fallDistance - this.z;
            this.z = this.mc.thePlayer.fallDistance;
            if (this.mc.thePlayer.fallDistance <= 120.0f) {
                return;
            }
            final EntityPlayerSP thePlayer = this.mc.thePlayer;
            thePlayer.motionY += 1.0E-4 * this.mc.thePlayer.fallDistance;
        }
        this.A = 0.0;
        this.z = 0.0;
    }
    
    public static boolean isBlockUnder() {
        for (int offset = 0; offset < Helper.mc.thePlayer.posY + Helper.mc.thePlayer.getEyeHeight(); offset += 2) {
            final AxisAlignedBB boundingBox = Helper.mc.thePlayer.getEntityBoundingBox().offset(0.0, (double)(-offset), 0.0);
            if (!Helper.mc.theWorld.getCollidingBoundingBoxes((Entity)Helper.mc.thePlayer, boundingBox).isEmpty()) {
                return true;
            }
        }
        return false;
    }
}
