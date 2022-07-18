package acid.module.modules.combat;

import java.util.Random;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import buzz.gaoyusense.injection.interfaces.INetworkManager;
import io.netty.util.internal.ThreadLocalRandom;
import acid.utils.Helper;
import acid.management.ModuleManager;
import acid.module.modules.movement.Speed;
import acid.Client;
import net.minecraft.entity.EntityLivingBase;
import acid.api.EventHandler;
import acid.api.events.world.EventPreUpdate;
import acid.api.value.Value;
import java.awt.Color;
import acid.module.ModuleType;
import acid.api.value.Option;
import acid.api.value.Numbers;
import acid.utils.timer.TimeHelper;
import acid.api.value.Mode;
import acid.module.Module;

public class Criticals extends Module
{
    private Mode mode;
    private final TimeHelper timer;
    private final Numbers<Number> hurtTimeValue;
    private static final Option<Boolean> Speed;
    private static final Option<Boolean> debug;
    
    public Criticals() {
        super("Criticals", new String[] { "crits", "crit" }, ModuleType.Combat);
        this.mode = new Mode("Mode", "mode", (Enum[])CritMode.values(), (Enum)CritMode.Packet);
        this.timer = new TimeHelper();
        this.hurtTimeValue = new Numbers<Number>("Hurt Time", "Hurt Time", 15.0, 0.0, 20.0, 1.0);
        this.setColor(new Color(235, 194, 138).getRGB());
        this.addValues(this.mode, this.hurtTimeValue, Criticals.Speed, Criticals.debug);
    }
    
    @Override
    public void onEnable() {
        this.timer.reset();
        super.onEnable();
    }
    
    @EventHandler
    private void onUpdate(final EventPreUpdate e) {
        this.setSuffix("Tiny");
    }
    
    void packetCrit(final EntityLivingBase target) {
        if (this.mc.thePlayer.onGround && !this.mc.thePlayer.isOnLadder() && !this.mc.thePlayer.isInWater() && !this.mc.thePlayer.isInLava() && this.mc.thePlayer.ridingEntity == null && target.hurtResistantTime > this.hurtTimeValue.getValue().doubleValue()) {
            if (!Criticals.Speed.getValue()) {
                Client.getModuleManager();
                if (ModuleManager.getModuleByClass(Speed.class).isEnabled()) {
                    return;
                }
            }
            if (this.timer.isDelayComplete(500L)) {
                if (this.mode.getValue() == CritMode.Packet) {
                    final Double x = Helper.mc.thePlayer.posX;
                    final Double y = Helper.mc.thePlayer.posY;
                    final Double z = Helper.mc.thePlayer.posZ;
                    if (Criticals.debug.getValue()) {
                        Helper.sendMessage("¡ì" + randomNumber(1.0, 9.0) + "Try Hits");
                    }
                    final double var13 = 0.01125;
                    final double var14 = ThreadLocalRandom.current().nextDouble(0.001, 0.0011);
                    final int var15 = 0;
                    if (var15 < 1) {
                        ((INetworkManager)this.mc.thePlayer.sendQueue.getNetworkManager()).sendPacketNoEvent((Packet)new C03PacketPlayer.C04PacketPlayerPosition((double)x, y + var13 + var14, (double)z, false));
                        ((INetworkManager)this.mc.thePlayer.sendQueue.getNetworkManager()).sendPacketNoEvent((Packet)new C03PacketPlayer.C04PacketPlayerPosition((double)x, y + var14, (double)z, false));
                    }
                    this.timer.reset();
                }
            }
        }
    }
    
    private static int randomNumber(final double min, final double max) {
        final Random random = new Random();
        return (int)(min + random.nextDouble() * (max - min));
    }
    
    static {
        Speed = new Option<Boolean>("SPeed", "SPeed", true);
        debug = new Option<Boolean>("Debug", "Debug", true);
    }
    
    enum CritMode
    {
        Packet;
    }
}
