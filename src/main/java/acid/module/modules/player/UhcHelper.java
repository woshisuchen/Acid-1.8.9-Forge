package acid.module.modules.player;

import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.block.BlockHopper;
import net.minecraft.world.World;
import net.minecraft.block.BlockAir;
import net.minecraft.util.MathHelper;
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity;
import acid.api.events.world.EventPacketReceive;
import org.lwjgl.input.Keyboard;
import acid.utils.PlayerUtils;
import net.minecraft.network.play.client.C03PacketPlayer;
import acid.api.events.world.EventPacketSend;
import acid.api.EventHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.world.WorldSettings;
import acid.utils.Helper;
import net.minecraft.util.EnumFacing;
import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import acid.api.events.world.EventPreUpdate;
import acid.module.ModuleType;
import acid.utils.timer.TimeHelper;
import acid.api.value.Option;
import acid.module.Module;

public class UhcHelper extends Module
{
    public Option<Boolean> sandbreak;
    public Option<Boolean> LightningCheck;
    public Option<Boolean> autoSneak;
    public static int movement;
    public Option<Boolean> noSandDamage;
    public Option<Boolean> noWaterDamage;
    public Option<Boolean> lessPacket;
    boolean sneak;
    public static int x;
    public static int y;
    public static int z;
    TimeHelper sneakTimer;
    int clock;
    int delay;
    
    public UhcHelper() {
        super("UhcHelper", new String[] { "UhcHelper" }, ModuleType.Player);
        this.sandbreak = new Option<Boolean>("SandBreaker", Boolean.valueOf(false));
        this.LightningCheck = new Option<Boolean>("LightningCheck", Boolean.valueOf(false));
        this.autoSneak = new Option<Boolean>("AutoSneak", Boolean.valueOf(false));
        this.noSandDamage = new Option<Boolean>("noSandDamage", Boolean.valueOf(false));
        this.noWaterDamage = new Option<Boolean>("noWaterDamage", Boolean.valueOf(false));
        this.lessPacket = new Option<Boolean>("lessPacket", Boolean.valueOf(false));
        this.sneak = false;
        this.sneakTimer = new TimeHelper();
        this.addValues(this.sandbreak, this.LightningCheck, this.autoSneak, this.noSandDamage, this.noWaterDamage, this.lessPacket);
    }
    
    @EventHandler
    public void OnUpdate(final EventPreUpdate e) {
        final BlockPos sand = new BlockPos(new Vec3(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 3.0, this.mc.thePlayer.posZ));
        final Block sandblock = this.mc.theWorld.getBlockState(sand).getBlock();
        final BlockPos forge = new BlockPos(new Vec3(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 2.0, this.mc.thePlayer.posZ));
        final Block forgeblock = this.mc.theWorld.getBlockState(forge).getBlock();
        final BlockPos obsidianpos = new BlockPos(new Vec3(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 1.0, this.mc.thePlayer.posZ));
        final Block obsidianblock = this.mc.theWorld.getBlockState(obsidianpos).getBlock();
        if (obsidianblock == Block.getBlockById(49)) {
            this.bestTool(this.mc.objectMouseOver.getBlockPos().getX(), this.mc.objectMouseOver.getBlockPos().getY(), this.mc.objectMouseOver.getBlockPos().getZ());
            final BlockPos downpos = new BlockPos(new Vec3(this.mc.thePlayer.posX, this.mc.thePlayer.posY - 1.0, this.mc.thePlayer.posZ));
            this.mc.playerController.onPlayerDamageBlock(downpos, EnumFacing.UP);
        }
        if (forgeblock == Block.getBlockById(61)) {
            this.bestTool(this.mc.objectMouseOver.getBlockPos().getX(), this.mc.objectMouseOver.getBlockPos().getY(), this.mc.objectMouseOver.getBlockPos().getZ());
            final BlockPos downpos = new BlockPos(new Vec3(this.mc.thePlayer.posX, this.mc.thePlayer.posY - 1.0, this.mc.thePlayer.posZ));
            this.mc.playerController.onPlayerDamageBlock(downpos, EnumFacing.UP);
        }
        if (this.sandbreak.getValue() && (sandblock == Block.getBlockById(12) || sandblock == Block.getBlockById(13))) {
            this.bestTool(this.mc.objectMouseOver.getBlockPos().getX(), this.mc.objectMouseOver.getBlockPos().getY(), this.mc.objectMouseOver.getBlockPos().getZ());
            final BlockPos downpos = new BlockPos(new Vec3(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 3.0, this.mc.thePlayer.posZ));
            Helper.sendMessage("Sand On your Head. Care for it :D");
            this.mc.playerController.onPlayerDamageBlock(downpos, EnumFacing.UP);
        }
        if (this.autoSneak.getValue() && !this.sneak && this.sneakTimer.isDelayComplete(2000L)) {
            this.sneak = true;
        }
        if (this.sneak) {}
        if (this.lessPacket.getValue()) {
            this.mc.thePlayer.setGameType(WorldSettings.GameType.SURVIVAL);
            this.mc.thePlayer.setGameType(WorldSettings.GameType.CREATIVE);
            if (this.mc.thePlayer.onGround) {
                final EntityPlayerSP thePlayer = this.mc.thePlayer;
                thePlayer.motionX *= 1.0049999952316284;
                final EntityPlayerSP thePlayer2 = this.mc.thePlayer;
                thePlayer2.motionZ *= 1.0049999952316284;
            }
        }
    }
    
    @EventHandler
    public void onPacketReceive(final EventPacketSend packetEvent) {
        if (this.noSandDamage.getValue() && EventPacketSend.packet instanceof C03PacketPlayer && this.isInsideBlock()) {
            packetEvent.setCancelled(true);
        }
        if (this.noWaterDamage.getValue() && EventPacketSend.packet instanceof C03PacketPlayer && PlayerUtils.isInWater() && Keyboard.isKeyDown(42)) {
            packetEvent.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPacketReceive(final EventPacketReceive packetEvent) {
        if (this.LightningCheck.getValue() && EventPacketReceive.packet instanceof S2CPacketSpawnGlobalEntity) {
            final S2CPacketSpawnGlobalEntity packetIn = (S2CPacketSpawnGlobalEntity)EventPacketReceive.packet;
            if (packetIn.func_149053_g() == 1) {
                UhcHelper.x = (int)(packetIn.func_149051_d() / 32.0);
                UhcHelper.y = (int)(packetIn.func_149050_e() / 32.0);
                UhcHelper.z = (int)(packetIn.func_149049_f() / 32.0);
                PlayerUtils.tellPlayer("Found Lightning X:" + UhcHelper.x + "-Y:" + UhcHelper.y + "-Z:" + UhcHelper.z);
            }
        }
    }
    
    @Override
    public void onEnable() {
        if (this.noWaterDamage.getValue()) {
            PlayerUtils.tellPlayer("Lshift In Water To Enable Water GodMode");
        }
        this.sneakTimer.reset();
        super.onEnable();
    }
    
    @Override
    public void onDisable() {
        if (this.lessPacket.getValue()) {
            if (this.mc.thePlayer.capabilities.isCreativeMode) {
                this.mc.thePlayer.setGameType(WorldSettings.GameType.CREATIVE);
            }
            else {
                this.mc.thePlayer.setGameType(WorldSettings.GameType.SURVIVAL);
            }
        }
        this.sneakTimer.reset();
        super.onDisable();
    }
    
    private boolean isInsideBlock() {
        for (int x = MathHelper.floor_double(this.mc.thePlayer.getEntityBoundingBox().minX); x < MathHelper.floor_double(this.mc.thePlayer.getEntityBoundingBox().maxX) + 1; ++x) {
            for (int y = MathHelper.floor_double(this.mc.thePlayer.getEntityBoundingBox().minY); y < MathHelper.floor_double(this.mc.thePlayer.getEntityBoundingBox().maxY) + 1; ++y) {
                for (int z = MathHelper.floor_double(this.mc.thePlayer.getEntityBoundingBox().minZ); z < MathHelper.floor_double(this.mc.thePlayer.getEntityBoundingBox().maxZ) + 1; ++z) {
                    final Block block = this.mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
                    if (block != null && !(block instanceof BlockAir) && block.isFullBlock()) {
                        AxisAlignedBB boundingBox = block.getCollisionBoundingBox((World)this.mc.theWorld, new BlockPos(x, y, z), this.mc.theWorld.getBlockState(new BlockPos(x, y, z)));
                        if (block instanceof BlockHopper) {
                            boundingBox = new AxisAlignedBB((double)x, (double)y, (double)z, (double)(x + 1), (double)(y + 1), (double)(z + 1));
                        }
                        if (boundingBox != null && this.mc.thePlayer.getEntityBoundingBox().intersectsWith(boundingBox)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    public void bestTool(final int x, final int y, final int z) {
        final int blockId = Block.getIdFromBlock(this.mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock());
        int bestSlot = 0;
        float f = -1.0f;
        for (int i1 = 36; i1 < 45; ++i1) {
            try {
                final ItemStack curSlot = this.mc.thePlayer.inventoryContainer.getSlot(i1).getStack();
                if ((curSlot.getItem() instanceof ItemTool || curSlot.getItem() instanceof ItemSword || curSlot.getItem() instanceof ItemShears) && curSlot.getStrVsBlock(Block.getBlockById(blockId)) > f) {
                    bestSlot = i1 - 36;
                    f = curSlot.getStrVsBlock(Block.getBlockById(blockId));
                }
            }
            catch (Exception ex) {}
        }
        if (f != -1.0f) {
            this.mc.thePlayer.inventory.currentItem = bestSlot;
            this.mc.playerController.updateController();
        }
    }
}
