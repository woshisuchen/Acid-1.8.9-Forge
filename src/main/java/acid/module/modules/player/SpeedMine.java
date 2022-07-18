package acid.module.modules.player;

import acid.api.events.world.EventPacketSend;
import acid.api.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;
import buzz.gaoyusense.injection.interfaces.IPlayerControllerMP;
import acid.api.events.world.EventPostUpdate;
import acid.api.value.Value;
import acid.module.ModuleType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.BlockPos;
import acid.api.value.Numbers;
import acid.api.value.Mode;
import acid.module.Module;

public class SpeedMine extends Module
{
    private Mode<modeEnums> modeValue;
    public Numbers<Number> Speed;
    private BlockPos blockPos;
    private EnumFacing facing;
    private boolean digging;
    private float damage;
    
    public SpeedMine() {
        super("Fast Mine", new String[] { "SpeedMine", "fastmine" }, ModuleType.Player);
        this.modeValue = new Mode<modeEnums>("Mode", "Fucking mode", modeEnums.values(), modeEnums.Packet);
        this.Speed = new Numbers<Number>("SpeedMine", 1.4, 0.0, 3.0, 0.1);
        this.addValues(this.modeValue, this.Speed);
    }
    
    @EventHandler
    void onUpdate(final EventPostUpdate event) {
        if (this.modeValue.getValue() == modeEnums.Legit) {
            final IPlayerControllerMP controller = (IPlayerControllerMP)this.mc.playerController;
            controller.setBlockHitDelay(0);
            if (controller.getCurBlockDamageMP() >= 0.65f) {
                controller.setCurBlockDamageMP(1.0f);
            }
        }
        if (this.modeValue.getValue() == modeEnums.Packet) {
            final IPlayerControllerMP controller = (IPlayerControllerMP)this.mc.playerController;
            controller.setBlockHitDelay(0);
            if (this.digging && !this.mc.playerController.isInCreativeMode()) {
                final Block block = this.mc.theWorld.getBlockState(this.blockPos).getBlock();
                this.damage += block.getPlayerRelativeBlockHardness((EntityPlayer)this.mc.thePlayer, (World)this.mc.theWorld, this.blockPos) * this.Speed.getValue().floatValue();
                if (this.damage >= 1.0f) {
                    this.mc.theWorld.setBlockState(this.blockPos, Blocks.air.getDefaultState(), 11);
                    this.mc.thePlayer.sendQueue.getNetworkManager().sendPacket((Packet)new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.blockPos, this.facing));
                    this.damage = 0.0f;
                    this.digging = false;
                }
            }
        }
    }
    
    @EventHandler
    void onPacket(final EventPacketSend event) {
        final Packet<?> p = (Packet<?>)EventPacketSend.getPacket();
        if (this.modeValue.getValue() == modeEnums.Packet && p instanceof C07PacketPlayerDigging && !this.mc.playerController.isInCreativeMode()) {
            final C07PacketPlayerDigging c07PacketPlayerDigging = (C07PacketPlayerDigging)p;
            if (c07PacketPlayerDigging.getStatus() == C07PacketPlayerDigging.Action.START_DESTROY_BLOCK) {
                this.digging = true;
                this.blockPos = c07PacketPlayerDigging.getPosition();
                this.facing = c07PacketPlayerDigging.getFacing();
                this.damage = 0.0f;
            }
            else if (c07PacketPlayerDigging.getStatus() == C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK || c07PacketPlayerDigging.getStatus() == C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK) {
                this.digging = false;
                this.blockPos = null;
                this.facing = null;
            }
        }
    }
    
    private enum modeEnums
    {
        Legit, 
        Packet;
    }
}
