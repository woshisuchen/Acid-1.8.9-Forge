package acid.module.modules.movement;

import java.util.Iterator;
import java.util.List;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import java.util.Arrays;
import acid.utils.Helper;
import acid.api.events.world.StepEvent;
import acid.api.EventHandler;
import acid.utils.PlayerUtils;
import acid.api.events.world.EventPreUpdate;
import acid.api.value.Value;
import acid.module.ModuleType;
import acid.utils.timer.TimerUtil;
import acid.api.value.Numbers;
import acid.api.value.Option;
import acid.module.Module;

public class Step extends Module
{
    private final Option<Boolean> smoothValue;
    private final Option<Boolean> packetValue;
    private final Numbers<Number> heightValue;
    private final Numbers<Number> delayValue;
    private final TimerUtil timer;
    private boolean resetTimer;
    int groundTicks;
    
    public Step() {
        super("Step", new String[] { "Step" }, ModuleType.Movement);
        this.smoothValue = new Option<Boolean>("Smooth", "Step smooth", true);
        this.packetValue = new Option<Boolean>("Packet", "Packet step", true);
        this.heightValue = new Numbers<Number>("Height", "Just height", 1.0, 1.0, 5.0, 0.1);
        this.delayValue = new Numbers<Number>("Delay", "Just delay", 1.0, 1.0, 15.0, 0.1);
        this.timer = new TimerUtil();
        this.addValues(this.heightValue, this.delayValue, this.smoothValue, this.packetValue);
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
        this.timer.reset();
        this.resetTimer = false;
        this.groundTicks = 0;
    }
    
    @Override
    public void onDisable() {
        super.onDisable();
    }
    
    @EventHandler
    void onUpdate(final EventPreUpdate event) {
        this.setSuffix(this.packetValue.getValue() ? "Packet" : "");
        if (PlayerUtils.isOnGround(0.01)) {
            ++this.groundTicks;
        }
        else {
            this.groundTicks = 0;
        }
        if (this.groundTicks > 20) {
            this.groundTicks = 20;
        }
    }
    
    @EventHandler
    void onStep(final StepEvent event) {
        if (this.resetTimer) {
            this.resetTimer = false;
            Helper.getTimer().timerSpeed = 1.0f;
        }
        if (event.isPre() && this.canStep() && this.timer.hasPassed(this.delayValue.getValue().intValue() * 100L)) {
            event.setStepHeight(((boolean)this.packetValue.getValue()) ? Math.min(this.heightValue.getValue().doubleValue(), 2.5) : this.heightValue.getValue().doubleValue());
        }
        if (event.isPost()) {
            final Number realHeight = this.mc.thePlayer.getEntityBoundingBox().minY - this.mc.thePlayer.posY;
            if (realHeight.doubleValue() < 0.625) {
                return;
            }
            this.timer.reset();
            if (this.smoothValue.getValue()) {
                Helper.getTimer().timerSpeed = 0.4f - ((realHeight.floatValue() >= 1.0f) ? (Math.abs(1.0f - realHeight.floatValue()) * 0.2f) : 0.0f);
                if (Helper.getTimer().timerSpeed <= 0.1f) {
                    Helper.getTimer().timerSpeed = 0.1f;
                }
                this.resetTimer = true;
            }
            if (this.packetValue.getValue()) {
                final List<Double> offset = Arrays.asList(0.42, 0.333, 0.248, 0.083, -0.078);
                final double posX = this.mc.thePlayer.posX;
                final double posZ = this.mc.thePlayer.posZ;
                double y = this.mc.thePlayer.posY;
                if (realHeight.doubleValue() < 1.1) {
                    double first = 0.42;
                    double second = 0.75;
                    if (realHeight.doubleValue() != 1.0) {
                        first *= realHeight.doubleValue();
                        second *= realHeight.doubleValue();
                        if (first > 0.425) {
                            first = 0.425;
                        }
                        if (second > 0.78) {
                            second = 0.78;
                        }
                        if (second < 0.49) {
                            second = 0.49;
                        }
                    }
                    if (first == 0.42) {
                        first = 0.41999998688698;
                    }
                    this.mc.thePlayer.sendQueue.addToSendQueue((Packet)new C03PacketPlayer.C04PacketPlayerPosition(posX, y + first, posZ, false));
                    if (y + second < y + realHeight.doubleValue()) {
                        this.mc.thePlayer.sendQueue.addToSendQueue((Packet)new C03PacketPlayer.C04PacketPlayerPosition(posX, y + second, posZ, false));
                    }
                }
                else if (realHeight.doubleValue() < 1.6) {
                    for (final double off : offset) {
                        y += off;
                        this.mc.thePlayer.sendQueue.addToSendQueue((Packet)new C03PacketPlayer.C04PacketPlayerPosition(posX, y, posZ, false));
                    }
                }
                else if (realHeight.doubleValue() < 2.1) {
                    final double[] array;
                    final double[] heights = array = new double[] { 0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869 };
                    for (final double off2 : array) {
                        this.mc.thePlayer.sendQueue.addToSendQueue((Packet)new C03PacketPlayer.C04PacketPlayerPosition(posX, y + off2, posZ, false));
                    }
                }
                else {
                    final double[] array2;
                    final double[] heights = array2 = new double[] { 0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869, 2.019, 1.907 };
                    for (final double off2 : array2) {
                        this.mc.thePlayer.sendQueue.addToSendQueue((Packet)new C03PacketPlayer.C04PacketPlayerPosition(posX, y + off2, posZ, false));
                    }
                }
            }
        }
    }
    
    private boolean canStep() {
        return this.groundTicks > 3 && this.mc.thePlayer.isCollidedVertically && !this.mc.gameSettings.keyBindJump.isPressed() && !PlayerUtils.isInLiquid();
    }
}
