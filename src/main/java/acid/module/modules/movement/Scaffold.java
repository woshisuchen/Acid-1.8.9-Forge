package acid.module.modules.movement;

import java.util.Arrays;
import net.minecraft.init.Blocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.block.BlockEnchantmentTable;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.BlockLadder;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.Vec3;
import net.minecraft.util.EnumFacing;
import net.minecraft.block.BlockSnow;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import acid.api.events.world.EventPostUpdate;
import net.minecraft.client.entity.EntityPlayerSP;
import acid.utils.PlayerUtils;
import net.minecraft.entity.EntityLivingBase;
import acid.utils.math.RotationUtil;
import net.minecraft.world.World;
import net.minecraft.entity.passive.EntityPig;
import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.util.BlockPos;
import acid.api.events.world.EventPreUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.potion.Potion;
import acid.management.ModuleManager;
import acid.Client;
import acid.utils.MovementUtils;
import acid.api.events.world.EventMove;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemBlock;
import acid.api.EventHandler;
import acid.api.events.world.SafeWalkEvent;
import acid.api.value.Value;
import acid.module.ModuleType;
import net.minecraft.block.Block;
import java.util.List;
import acid.utils.render.TranslateUtil;
import acid.api.value.Option;
import acid.module.Module;

public class Scaffold extends Module
{
    private Option tower;
    private Option ab;
    private Option swing;
    public static Option<Boolean> safeWalkValue;
    public static Option<Boolean> towerMove;
    private double xDist;
    private BlockData blockData;
    private int slot;
    private float yaw;
    private float pitch;
    private TranslateUtil translate;
    private TranslateUtil position;
    public static List<Block> blacklisted;
    
    public Scaffold() {
        super("Scaffold", new String[] { "magiccarpet", "blockplacer", "airwalk" }, ModuleType.Movement);
        this.tower = new Option("Tower", (Object)true);
        this.ab = new Option("Silent", (Object)true);
        this.swing = new Option("Swing", (Object)false);
        this.addValues(Scaffold.safeWalkValue, this.tower, Scaffold.towerMove, this.ab, this.swing);
        this.translate = new TranslateUtil(0.0f, 0.0f);
        this.position = new TranslateUtil(0.0f, 0.0f);
    }
    
    @EventHandler
    void onSafeWalk(final SafeWalkEvent event) {
        event.setCancelled(!this.mc.gameSettings.keyBindSprint.isPressed() && Scaffold.safeWalkValue.getValue() && this.mc.thePlayer.onGround);
    }
    
    public int getBlockCount() {
        int blockCount = 0;
        for (int i = 0; i < 45; ++i) {
            if (this.mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack is = this.mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                final Item item = is.getItem();
                if (is.getItem() instanceof ItemBlock && is.stackSize > 0 && !Scaffold.blacklisted.contains(((ItemBlock)item).getBlock()) && !is.getUnlocalizedName().contains("fence")) {
                    if (!is.getUnlocalizedName().contains("pane")) {
                        blockCount += is.stackSize;
                    }
                }
            }
        }
        return blockCount;
    }
    
    @EventHandler
    public void onmove(final EventMove e) {
        if (MovementUtils.isMoving()) {
            Client.getModuleManager();
            if (!ModuleManager.getModuleByClass(Speed.class).isEnabled() && (!this.mc.gameSettings.keyBindJump.isPressed() || !this.mc.gameSettings.keyBindLeft.isPressed()) && (!this.mc.gameSettings.keyBindJump.isPressed() || !this.mc.gameSettings.keyBindRight.isPressed())) {
                MovementUtils.setSpeed(e, 0.22);
            }
        }
    }
    
    public float getBaseMoveSpeed3() {
        float baseSpeed = 1.299f;
        if (this.mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            baseSpeed *= (float)(1.0 + 0.2 * (this.mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1));
        }
        return baseSpeed;
    }
    
    @EventHandler
    public void c(final EventMove var1) {
        if (!MovementUtils.isMoving() && this.mc.gameSettings.keyBindJump.isKeyDown() && (boolean)this.tower.getValue() && (this.mc.thePlayer.onGround || this.blockData != null)) {
            this.mc.thePlayer.setPosition(MathHelper.floor_double(this.mc.thePlayer.posX) + 0.5, this.mc.thePlayer.posY, MathHelper.floor_double(this.mc.thePlayer.posZ) + 0.5);
            var1.setY(this.mc.thePlayer.motionY = 0.41999998688698);
        }
    }
    
    @EventHandler
    public void onPreMotion(final EventPreUpdate e) {
        this.setSuffix("Hypixel");
        this.blockData = this.getBlockData(new BlockPos(this.mc.thePlayer.posX, this.mc.thePlayer.posY - 1.0, this.mc.thePlayer.posZ));
        if (!MovementUtils.isMoving() && this.getHotBar() == -1) {
            this.getBlocksFromInventory();
        }
        if (!MovementUtils.isMoving() && this.mc.gameSettings.keyBindJump.isKeyDown() && (boolean)this.tower.getValue()) {
            final BlockPos underPos = new BlockPos(this.mc.thePlayer.posX, this.mc.thePlayer.posY - 1.0, this.mc.thePlayer.posZ);
            this.mc.thePlayer.setPosition(underPos.getX() + 0.5, this.mc.thePlayer.posY, underPos.getZ() + 0.5);
            final EntityPlayerSP thePlayer = this.mc.thePlayer;
            thePlayer.motionX *= 0.0;
            final EntityPlayerSP thePlayer2 = this.mc.thePlayer;
            thePlayer2.motionZ *= 0.0;
            e.x += ((this.mc.thePlayer.ticksExisted % 2 == 0) ? ThreadLocalRandom.current().nextDouble(0.06, 0.0625) : (-ThreadLocalRandom.current().nextDouble(0.06, 0.0625)));
            e.z += ((this.mc.thePlayer.ticksExisted % 2 != 0) ? ThreadLocalRandom.current().nextDouble(0.06, 0.0625) : (-ThreadLocalRandom.current().nextDouble(0.06, 0.0625)));
        }
        if (this.blockData != null) {
            final EntityPig entity = new EntityPig((World)this.mc.theWorld);
            entity.posX = this.blockData.position.getX() + 0.5;
            entity.posY = this.blockData.position.getY() + 0.5;
            entity.posZ = this.blockData.position.getZ() + 0.5;
            final float[] rots = RotationUtil.getAngles((EntityLivingBase)entity);
            this.yaw = rots[0];
            this.pitch = rots[1];
        }
        e.setYaw(PlayerUtils.isMoving() ? this.getYawBackward() : this.yaw);
        e.setPitch(PlayerUtils.isMoving() ? this.pitch : 90.0f);
    }
    
    public float getYawBackward() {
        float yaw = MathHelper.wrapAngleTo180_float(this.mc.thePlayer.rotationYaw);
        final double strafe = this.mc.thePlayer.movementInput.moveStrafe;
        final double forward = this.mc.thePlayer.movementInput.moveForward;
        if (forward != 0.0) {
            if (strafe < 0.0) {
                yaw += ((forward < 0.0) ? 135.0f : 45.0f);
            }
            else if (strafe > 0.0) {
                yaw -= ((forward < 0.0) ? 135.0f : 45.0f);
            }
            else if (strafe == 0.0 && forward < 0.0) {
                yaw -= 180.0f;
            }
        }
        else if (strafe < 0.0) {
            yaw += 90.0f;
        }
        else if (strafe > 0.0) {
            yaw -= 90.0f;
        }
        return MathHelper.wrapAngleTo180_float(yaw - 180.0f);
    }
    
    public static int Down(final double n) {
        final int n2 = (int)n;
        try {
            if (n < n2) {
                return n2 - 1;
            }
        }
        catch (IllegalArgumentException ex) {}
        return n2;
    }
    
    @EventHandler
    public void onPost(final EventPostUpdate e) {
        if (this.blockData != null) {
            final int block = this.getHotBar();
            if (block != -1 && this.mc.gameSettings.keyBindJump.isKeyDown() && (boolean)this.tower.getValue()) {
                final BlockPos underPos = new BlockPos(this.mc.thePlayer.posX, this.mc.thePlayer.posY - 1.0, this.mc.thePlayer.posZ);
                final Block underBlock = this.mc.theWorld.getBlockState(underPos).getBlock();
                if (MovementUtils.isMoving() && Scaffold.towerMove.getValue()) {
                    if (PlayerUtils.isOnGround(0.76) && !PlayerUtils.isOnGround(0.75) && this.mc.thePlayer.motionY > 0.23 && this.mc.thePlayer.motionY < 0.25) {
                        this.mc.thePlayer.motionY = Math.round(this.mc.thePlayer.posY) - this.mc.thePlayer.posY;
                    }
                    if (PlayerUtils.isOnGround(1.0E-4)) {
                        this.mc.thePlayer.motionY = 0.41999998688698;
                    }
                    else if (this.mc.thePlayer.posY >= Math.round(this.mc.thePlayer.posY) - 1.0E-4 && this.mc.thePlayer.posY <= Math.round(this.mc.thePlayer.posY) + 1.0E-4) {
                        this.mc.thePlayer.motionY = 0.0;
                    }
                }
            }
            if ((block != -1 && this.mc.thePlayer.getCurrentEquippedItem() != null && this.mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemBlock) || (block != -1 && (boolean)this.ab.getValue())) {
                this.mc.thePlayer.inventory.currentItem = block;
                if ((Boolean)this.ab.getValue()) {
                    this.placeBlock(this.blockData.position, this.blockData.face, block);
                }
                else {
                    if ((Boolean)this.ab.getValue()) {
                        this.mc.thePlayer.sendQueue.addToSendQueue((Packet)new C09PacketHeldItemChange(this.mc.thePlayer.inventory.currentItem));
                    }
                    this.mc.playerController.updateController();
                }
            }
        }
    }
    
    public boolean isBlockAccessible(final Block nameBlock) {
        return nameBlock.getMaterial().isReplaceable() && (!(nameBlock instanceof BlockSnow) || nameBlock.getBlockBoundsMaxY() <= 0.125);
    }
    
    public void eventMotion(final EventPostUpdate nameEventMotion) {
        final BlockPos localBlockPos = new BlockPos(this.mc.thePlayer.posX, this.mc.thePlayer.posY - 1.0, this.mc.thePlayer.posZ);
        final Block localBlock = this.mc.theWorld.getBlockState(localBlockPos).getBlock();
        final BlockData localBlockData = this.getBlockData(localBlockPos);
        if (this.isBlockAccessible(localBlock) && localBlockData != null) {
            this.mc.thePlayer.motionY = 0.4196;
            final EntityPlayerSP thePlayer = this.mc.thePlayer;
            thePlayer.motionX *= 0.0;
            final EntityPlayerSP thePlayer2 = this.mc.thePlayer;
            thePlayer2.motionZ *= 0.0;
        }
    }
    
    @Override
    public void onEnable() {
        this.slot = this.mc.thePlayer.inventory.currentItem;
    }
    
    @Override
    public void onDisable() {
        this.mc.thePlayer.inventory.currentItem = this.slot;
        if ((Boolean)this.ab.getValue()) {
            this.mc.thePlayer.sendQueue.addToSendQueue((Packet)new C09PacketHeldItemChange(this.mc.thePlayer.inventory.currentItem));
        }
    }
    
    private boolean placeBlock(final BlockPos pos, final EnumFacing facing, final int slotWithBlockInIt) {
        final Vec3 eyesPos = new Vec3(this.mc.thePlayer.posX, this.mc.thePlayer.posY + this.mc.thePlayer.getEyeHeight(), this.mc.thePlayer.posZ);
        final ItemStack itemstack = this.mc.thePlayer.inventory.mainInventory[slotWithBlockInIt];
        if (this.mc.playerController.onPlayerRightClick(this.mc.thePlayer, this.mc.theWorld, itemstack, this.blockData.getPosition(), this.blockData.getFacing(), getVec3(this.blockData.getPosition(), this.blockData.getFacing()))) {
            if ((Boolean)this.swing.getValue()) {
                this.mc.thePlayer.swingItem();
            }
            else {
                this.mc.thePlayer.sendQueue.addToSendQueue((Packet)new C0APacketAnimation());
            }
            return true;
        }
        return false;
    }
    
    public static double randomNumber(final double max, final double min) {
        return Math.random() * (max - min) + min;
    }
    
    public static Vec3 getVec3(final BlockPos pos, final EnumFacing face) {
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;
        if (face == EnumFacing.UP || face == EnumFacing.DOWN) {
            x += randomNumber(0.3, -0.3);
            z += randomNumber(0.3, -0.3);
        }
        else {
            y += randomNumber(0.3, -0.3);
        }
        if (face == EnumFacing.WEST || face == EnumFacing.EAST) {
            z += randomNumber(0.3, -0.3);
        }
        if (face == EnumFacing.SOUTH || face == EnumFacing.NORTH) {
            x += randomNumber(0.3, -0.3);
        }
        return new Vec3(x, y, z);
    }
    
    private BlockData getBlockData(final BlockPos pos) {
        if (this.mc.gameSettings.keyBindSprint.isPressed()) {
            final BlockPos pos2 = pos.add(0, -1, 0);
            if (this.canPush(pos2.add(0, -1, 0))) {
                return new BlockData(pos2.add(0, -1, 0), EnumFacing.UP);
            }
            if (this.canPush(pos2.add(-1, 0, 0))) {
                return new BlockData(pos2.add(-1, 0, 0), EnumFacing.EAST);
            }
            if (this.canPush(pos2.add(1, 0, 0))) {
                return new BlockData(pos2.add(1, 0, 0), EnumFacing.WEST);
            }
            if (this.canPush(pos2.add(0, 0, 1))) {
                return new BlockData(pos2.add(0, 0, 1), EnumFacing.NORTH);
            }
            if (this.canPush(pos2.add(0, 0, -1))) {
                return new BlockData(pos2.add(0, 0, -1), EnumFacing.SOUTH);
            }
            return new BlockData(pos.add(0, 0, 0), EnumFacing.DOWN);
        }
        else {
            if (this.canPush(pos.add(0, -1, 0))) {
                return new BlockData(pos.add(0, -1, 0), EnumFacing.UP);
            }
            if (this.canPush(pos.add(-1, 0, 0))) {
                return new BlockData(pos.add(-1, 0, 0), EnumFacing.EAST);
            }
            if (this.canPush(pos.add(1, 0, 0))) {
                return new BlockData(pos.add(1, 0, 0), EnumFacing.WEST);
            }
            if (this.canPush(pos.add(0, 0, 1))) {
                return new BlockData(pos.add(0, 0, 1), EnumFacing.NORTH);
            }
            if (this.canPush(pos.add(0, 0, -1))) {
                return new BlockData(pos.add(0, 0, -1), EnumFacing.SOUTH);
            }
            final BlockPos pos2 = pos.add(-1, 0, 0);
            if (this.canPush(pos2.add(0, -1, 0))) {
                return new BlockData(pos2.add(0, -1, 0), EnumFacing.UP);
            }
            if (this.canPush(pos2.add(-1, 0, 0))) {
                return new BlockData(pos2.add(-1, 0, 0), EnumFacing.EAST);
            }
            if (this.canPush(pos2.add(1, 0, 0))) {
                return new BlockData(pos2.add(1, 0, 0), EnumFacing.WEST);
            }
            if (this.canPush(pos2.add(0, 0, 1))) {
                return new BlockData(pos2.add(0, 0, 1), EnumFacing.NORTH);
            }
            if (this.canPush(pos2.add(0, 0, -1))) {
                return new BlockData(pos2.add(0, 0, -1), EnumFacing.SOUTH);
            }
            final BlockPos pos3 = pos.add(1, 0, 0);
            if (this.canPush(pos3.add(0, -1, 0))) {
                return new BlockData(pos3.add(0, -1, 0), EnumFacing.UP);
            }
            if (this.canPush(pos3.add(-1, 0, 0))) {
                return new BlockData(pos3.add(-1, 0, 0), EnumFacing.EAST);
            }
            if (this.canPush(pos3.add(1, 0, 0))) {
                return new BlockData(pos3.add(1, 0, 0), EnumFacing.WEST);
            }
            if (this.canPush(pos3.add(0, 0, 1))) {
                return new BlockData(pos3.add(0, 0, 1), EnumFacing.NORTH);
            }
            if (this.canPush(pos3.add(0, 0, -1))) {
                return new BlockData(pos3.add(0, 0, -1), EnumFacing.SOUTH);
            }
            final BlockPos pos4 = pos.add(0, 0, 1);
            if (this.canPush(pos4.add(0, -1, 0))) {
                return new BlockData(pos4.add(0, -1, 0), EnumFacing.UP);
            }
            if (this.canPush(pos4.add(-1, 0, 0))) {
                return new BlockData(pos4.add(-1, 0, 0), EnumFacing.EAST);
            }
            if (this.canPush(pos4.add(1, 0, 0))) {
                return new BlockData(pos4.add(1, 0, 0), EnumFacing.WEST);
            }
            if (this.canPush(pos4.add(0, 0, 1))) {
                return new BlockData(pos4.add(0, 0, 1), EnumFacing.NORTH);
            }
            if (this.canPush(pos4.add(0, 0, -1))) {
                return new BlockData(pos4.add(0, 0, -1), EnumFacing.SOUTH);
            }
            final BlockPos pos5 = pos.add(0, 0, -1);
            if (this.canPush(pos5.add(0, -1, 0))) {
                return new BlockData(pos5.add(0, -1, 0), EnumFacing.UP);
            }
            if (this.canPush(pos5.add(-1, 0, 0))) {
                return new BlockData(pos5.add(-1, 0, 0), EnumFacing.EAST);
            }
            if (this.canPush(pos5.add(1, 0, 0))) {
                return new BlockData(pos5.add(1, 0, 0), EnumFacing.WEST);
            }
            if (this.canPush(pos5.add(0, 0, 1))) {
                return new BlockData(pos5.add(0, 0, 1), EnumFacing.NORTH);
            }
            if (this.canPush(pos5.add(0, 0, -1))) {
                return new BlockData(pos5.add(0, 0, -1), EnumFacing.SOUTH);
            }
            final BlockPos pos6 = pos.add(-2, 0, 0);
            if (this.canPush(pos2.add(0, -1, 0))) {
                return new BlockData(pos2.add(0, -1, 0), EnumFacing.UP);
            }
            if (this.canPush(pos2.add(-1, 0, 0))) {
                return new BlockData(pos2.add(-1, 0, 0), EnumFacing.EAST);
            }
            if (this.canPush(pos2.add(1, 0, 0))) {
                return new BlockData(pos2.add(1, 0, 0), EnumFacing.WEST);
            }
            if (this.canPush(pos2.add(0, 0, 1))) {
                return new BlockData(pos2.add(0, 0, 1), EnumFacing.NORTH);
            }
            if (this.canPush(pos2.add(0, 0, -1))) {
                return new BlockData(pos2.add(0, 0, -1), EnumFacing.SOUTH);
            }
            final BlockPos pos7 = pos.add(2, 0, 0);
            if (this.canPush(pos3.add(0, -1, 0))) {
                return new BlockData(pos3.add(0, -1, 0), EnumFacing.UP);
            }
            if (this.canPush(pos3.add(-1, 0, 0))) {
                return new BlockData(pos3.add(-1, 0, 0), EnumFacing.EAST);
            }
            if (this.canPush(pos3.add(1, 0, 0))) {
                return new BlockData(pos3.add(1, 0, 0), EnumFacing.WEST);
            }
            if (this.canPush(pos3.add(0, 0, 1))) {
                return new BlockData(pos3.add(0, 0, 1), EnumFacing.NORTH);
            }
            if (this.canPush(pos3.add(0, 0, -1))) {
                return new BlockData(pos3.add(0, 0, -1), EnumFacing.SOUTH);
            }
            final BlockPos pos8 = pos.add(0, 0, 2);
            if (this.canPush(pos4.add(0, -1, 0))) {
                return new BlockData(pos4.add(0, -1, 0), EnumFacing.UP);
            }
            if (this.canPush(pos4.add(-1, 0, 0))) {
                return new BlockData(pos4.add(-1, 0, 0), EnumFacing.EAST);
            }
            if (this.canPush(pos4.add(1, 0, 0))) {
                return new BlockData(pos4.add(1, 0, 0), EnumFacing.WEST);
            }
            if (this.canPush(pos4.add(0, 0, 1))) {
                return new BlockData(pos4.add(0, 0, 1), EnumFacing.NORTH);
            }
            if (this.canPush(pos4.add(0, 0, -1))) {
                return new BlockData(pos4.add(0, 0, -1), EnumFacing.SOUTH);
            }
            final BlockPos pos9 = pos.add(0, 0, -2);
            if (this.canPush(pos5.add(0, -1, 0))) {
                return new BlockData(pos5.add(0, -1, 0), EnumFacing.UP);
            }
            if (this.canPush(pos5.add(-1, 0, 0))) {
                return new BlockData(pos5.add(-1, 0, 0), EnumFacing.EAST);
            }
            if (this.canPush(pos5.add(1, 0, 0))) {
                return new BlockData(pos5.add(1, 0, 0), EnumFacing.WEST);
            }
            if (this.canPush(pos5.add(0, 0, 1))) {
                return new BlockData(pos5.add(0, 0, 1), EnumFacing.NORTH);
            }
            if (this.canPush(pos5.add(0, 0, -1))) {
                return new BlockData(pos5.add(0, 0, -1), EnumFacing.SOUTH);
            }
            final BlockPos pos10 = pos.add(0, -1, 0);
            if (this.canPush(pos10.add(0, -1, 0))) {
                return new BlockData(pos10.add(0, -1, 0), EnumFacing.UP);
            }
            if (this.canPush(pos10.add(-1, 0, 0))) {
                return new BlockData(pos10.add(-1, 0, 0), EnumFacing.EAST);
            }
            if (this.canPush(pos10.add(1, 0, 0))) {
                return new BlockData(pos10.add(1, 0, 0), EnumFacing.WEST);
            }
            if (this.canPush(pos10.add(0, 0, 1))) {
                return new BlockData(pos10.add(0, 0, 1), EnumFacing.NORTH);
            }
            if (this.canPush(pos10.add(0, 0, -1))) {
                return new BlockData(pos10.add(0, 0, -1), EnumFacing.SOUTH);
            }
            final BlockPos pos11 = pos10.add(1, 0, 0);
            if (this.canPush(pos11.add(0, -1, 0))) {
                return new BlockData(pos11.add(0, -1, 0), EnumFacing.UP);
            }
            if (this.canPush(pos11.add(-1, 0, 0))) {
                return new BlockData(pos11.add(-1, 0, 0), EnumFacing.EAST);
            }
            if (this.canPush(pos11.add(1, 0, 0))) {
                return new BlockData(pos11.add(1, 0, 0), EnumFacing.WEST);
            }
            if (this.canPush(pos11.add(0, 0, 1))) {
                return new BlockData(pos11.add(0, 0, 1), EnumFacing.NORTH);
            }
            if (this.canPush(pos11.add(0, 0, -1))) {
                return new BlockData(pos11.add(0, 0, -1), EnumFacing.SOUTH);
            }
            final BlockPos pos12 = pos10.add(-1, 0, 0);
            if (this.canPush(pos12.add(0, -1, 0))) {
                return new BlockData(pos12.add(0, -1, 0), EnumFacing.UP);
            }
            if (this.canPush(pos12.add(-1, 0, 0))) {
                return new BlockData(pos12.add(-1, 0, 0), EnumFacing.EAST);
            }
            if (this.canPush(pos12.add(1, 0, 0))) {
                return new BlockData(pos12.add(1, 0, 0), EnumFacing.WEST);
            }
            if (this.canPush(pos12.add(0, 0, 1))) {
                return new BlockData(pos12.add(0, 0, 1), EnumFacing.NORTH);
            }
            if (this.canPush(pos12.add(0, 0, -1))) {
                return new BlockData(pos12.add(0, 0, -1), EnumFacing.SOUTH);
            }
            final BlockPos pos13 = pos10.add(0, 0, 1);
            if (this.canPush(pos13.add(0, -1, 0))) {
                return new BlockData(pos13.add(0, -1, 0), EnumFacing.UP);
            }
            if (this.canPush(pos13.add(-1, 0, 0))) {
                return new BlockData(pos13.add(-1, 0, 0), EnumFacing.EAST);
            }
            if (this.canPush(pos13.add(1, 0, 0))) {
                return new BlockData(pos13.add(1, 0, 0), EnumFacing.WEST);
            }
            if (this.canPush(pos13.add(0, 0, 1))) {
                return new BlockData(pos13.add(0, 0, 1), EnumFacing.NORTH);
            }
            if (this.canPush(pos13.add(0, 0, -1))) {
                return new BlockData(pos13.add(0, 0, -1), EnumFacing.SOUTH);
            }
            final BlockPos pos14 = pos10.add(0, 0, -1);
            if (this.canPush(pos14.add(0, -1, 0))) {
                return new BlockData(pos14.add(0, -1, 0), EnumFacing.UP);
            }
            if (this.canPush(pos14.add(-1, 0, 0))) {
                return new BlockData(pos14.add(-1, 0, 0), EnumFacing.EAST);
            }
            if (this.canPush(pos14.add(1, 0, 0))) {
                return new BlockData(pos14.add(1, 0, 0), EnumFacing.WEST);
            }
            if (this.canPush(pos14.add(0, 0, 1))) {
                return new BlockData(pos14.add(0, 0, 1), EnumFacing.NORTH);
            }
            if (this.canPush(pos14.add(0, 0, -1))) {
                return new BlockData(pos14.add(0, 0, -1), EnumFacing.SOUTH);
            }
            return null;
        }
    }
    
    private boolean canPush(final BlockPos pos) {
        final Block block = this.mc.theWorld.getBlockState(pos).getBlock();
        return (block.getMaterial().isSolid() || !block.isTranslucent() || block instanceof BlockLadder || block instanceof BlockCarpet || block instanceof BlockSnow || block instanceof BlockSkull) && !block.getMaterial().isLiquid() && !(block instanceof BlockContainer) && !(block instanceof BlockEnchantmentTable);
    }
    
    private void getBlocksFromInventory() {
        if (this.mc.currentScreen instanceof GuiChest) {
            return;
        }
        for (int index = 9; index < 36; ++index) {
            final ItemStack stack = this.mc.thePlayer.inventoryContainer.getSlot(index).getStack();
            if (stack != null) {
                if (this.isValid(stack.getItem())) {
                    this.mc.playerController.windowClick(0, index, 6, 2, (EntityPlayer)this.mc.thePlayer);
                    break;
                }
            }
        }
    }
    
    private int getHotBar() {
        for (int i = 36; i < 45; ++i) {
            if (this.mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack stack = this.mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (this.isValid(stack.getItem())) {
                    return i - 36;
                }
            }
        }
        return -1;
    }
    
    private boolean isValid(final Item item) {
        if (!(item instanceof ItemBlock)) {
            return false;
        }
        final ItemBlock iBlock = (ItemBlock)item;
        final Block block = iBlock.getBlock();
        return !Scaffold.blacklisted.contains(block);
    }
    
    public float getYaw() {
        return this.yaw;
    }
    
    public float getPitch() {
        return this.pitch;
    }
    
    static {
        Scaffold.safeWalkValue = new Option<Boolean>("Safe Walk", Boolean.valueOf(false));
        Scaffold.towerMove = new Option<Boolean>("Tower Move", Boolean.valueOf(false));
        Scaffold.blacklisted = Arrays.asList(Blocks.air, Blocks.tnt, Blocks.water, Blocks.flowing_water, Blocks.lava, Blocks.flowing_lava, Blocks.enchanting_table, Blocks.ender_chest, Blocks.yellow_flower, Blocks.carpet, Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.crafting_table, Blocks.snow_layer, Blocks.packed_ice, Blocks.chest, Blocks.torch, Blocks.anvil, Blocks.trapped_chest, Blocks.noteblock, Blocks.gold_ore, Blocks.lapis_ore, Blocks.coal_ore, Blocks.diamond_ore, Blocks.emerald_ore, Blocks.lit_redstone_ore, Blocks.redstone_ore, Blocks.wooden_pressure_plate, Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate, Blocks.heavy_weighted_pressure_plate, Blocks.stone_button, Blocks.wooden_button, Blocks.cactus, Blocks.lever, Blocks.activator_rail, Blocks.rail, Blocks.detector_rail, Blocks.golden_rail, Blocks.furnace, Blocks.ladder, Blocks.oak_fence, Blocks.redstone_torch, Blocks.iron_trapdoor, Blocks.trapdoor, Blocks.tripwire_hook, Blocks.hopper, Blocks.acacia_fence_gate, Blocks.birch_fence_gate, Blocks.dark_oak_fence_gate, Blocks.jungle_fence_gate, Blocks.spruce_fence_gate, Blocks.oak_fence_gate, Blocks.dispenser, Blocks.sapling, Blocks.tallgrass, Blocks.deadbush, Blocks.web, Blocks.red_flower, Blocks.red_mushroom, Blocks.brown_mushroom, Blocks.nether_brick_fence, Blocks.vine, Blocks.double_plant, Blocks.flower_pot, Blocks.beacon, Blocks.pumpkin, Blocks.lit_pumpkin);
    }
    
    private static class BlockData
    {
        public BlockPos position;
        public EnumFacing face;
        
        private BlockData(final BlockPos position, final EnumFacing face) {
            this.position = position;
            this.face = face;
        }
        
        private EnumFacing getFacing() {
            return this.face;
        }
        
        private BlockPos getPosition() {
            return this.position;
        }
    }
}
