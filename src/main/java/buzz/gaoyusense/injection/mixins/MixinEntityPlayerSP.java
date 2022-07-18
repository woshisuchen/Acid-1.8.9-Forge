package buzz.gaoyusense.injection.mixins;

import net.minecraft.crash.CrashReportCategory;
import net.minecraft.block.Block;
import java.util.Iterator;
import java.util.List;
import net.minecraft.util.ReportedException;
import net.minecraft.crash.CrashReport;
import net.minecraft.init.Blocks;
import buzz.gaoyusense.injection.interfaces.IEntity;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockWall;
import net.minecraft.block.BlockFence;
import net.minecraft.block.material.Material;
import net.minecraft.util.BlockPos;
import acid.api.events.world.StepEvent;
import net.minecraft.util.AxisAlignedBB;
import acid.api.events.world.SafeWalkEvent;
import acid.api.events.world.EventMove;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.AchievementList;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.entity.player.EntityPlayerMP;
import acid.module.modules.combat.KeepSprint;
import net.minecraft.util.MathHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.Packet;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import acid.api.events.world.EventPostUpdate;
import acid.api.events.world.EventPreUpdate;
import org.spongepowered.asm.mixin.Overwrite;
import acid.module.modules.movement.Sprint;
import acid.Client;
import acid.module.Module;
import acid.management.ModuleManager;
import acid.module.modules.movement.NoSlow;
import net.minecraft.potion.Potion;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.gui.GuiScreen;
import acid.api.events.world.LivingUpdateEvent;
import acid.api.events.misc.EventChat;
import com.mojang.authlib.GameProfile;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import acid.api.EventBus;
import acid.api.events.world.EventUpdate;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.Final;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.util.MovementInput;
import net.minecraft.client.entity.EntityPlayerSP;
import org.spongepowered.asm.mixin.Mixin;
import buzz.gaoyusense.injection.interfaces.IEntityPlayerSP;
import net.minecraft.client.entity.AbstractClientPlayer;

@Mixin({ EntityPlayerSP.class })
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer implements IEntityPlayerSP
{
    private double cachedX;
    private double cachedY;
    private double cachedZ;
    private float cachedRotationPitch;
    private float cachedRotationYaw;
    private final int cacheSprintToggleTimer = 0;
    private final float cacheYaw = 0.0f;
    private final float cachePitch = 0.0f;
    private float cacheStrafe;
    private float cacheForward;
    @Shadow
    public MovementInput movementInput;
    @Shadow
    public int sprintingTicksLeft;
    @Shadow
    protected int sprintToggleTimer;
    @Shadow
    public float timeInPortal;
    @Shadow
    public float prevTimeInPortal;
    @Shadow
    public float horseJumpPower;
    @Shadow
    public int horseJumpPowerCounter;
    @Shadow
    protected Minecraft mc;
    @Shadow
    private boolean serverSprintState;
    @Shadow
    @Final
    public NetHandlerPlayClient sendQueue;
    @Shadow
    private boolean serverSneakState;
    @Shadow
    private double lastReportedPosX;
    @Shadow
    private double lastReportedPosY;
    @Shadow
    private double lastReportedPosZ;
    @Shadow
    private float lastReportedYaw;
    @Shadow
    private float lastReportedPitch;
    @Shadow
    private int positionUpdateTicks;
    
    @Inject(method = "onUpdate", at = { @At("HEAD") })
    public void eventUpdate(final CallbackInfo info) {
        if (Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().theWorld != null) {
            final EventUpdate event = new EventUpdate();
            EventBus.getInstance().call(event);
        }
    }
    
    public MixinEntityPlayerSP() {
        super((World)null, (GameProfile)null);
        this.cacheStrafe = 0.0f;
        this.cacheForward = 0.0f;
    }
    
    @Shadow
    protected abstract void sendHorseJump();
    
    @Shadow
    public abstract boolean isRidingHorse();
    
    @Inject(method = "sendChatMessage", at = { @At("HEAD") }, cancellable = true)
    public void sendChatMessage(final String message, final CallbackInfo callbackInfo) {
        final EventChat event = new EventChat(message);
        EventBus.getInstance().call(event);
        if (event.isCancelled()) {
            callbackInfo.cancel();
        }
    }
    
    @Overwrite
    public void onLivingUpdate() {
        EventBus.getInstance().register(new LivingUpdateEvent());
        if (this.sprintingTicksLeft > 0) {
            --this.sprintingTicksLeft;
            if (this.sprintingTicksLeft == 0) {
                this.setSprinting(false);
            }
        }
        if (this.sprintToggleTimer > 0) {
            --this.sprintToggleTimer;
        }
        this.prevTimeInPortal = this.timeInPortal;
        if (this.inPortal) {
            if (this.mc.currentScreen != null && !this.mc.currentScreen.doesGuiPauseGame()) {
                this.mc.displayGuiScreen((GuiScreen)null);
            }
            if (this.timeInPortal == 0.0f) {
                this.mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.create(new ResourceLocation("portal.trigger"), this.rand.nextFloat() * 0.4f + 0.8f));
            }
            this.timeInPortal += 0.0125f;
            if (this.timeInPortal >= 1.0f) {
                this.timeInPortal = 1.0f;
            }
            this.inPortal = false;
        }
        else if (this.isPotionActive(Potion.confusion) && this.getActivePotionEffect(Potion.confusion).getDuration() > 60) {
            this.timeInPortal += 0.006666667f;
            if (this.timeInPortal > 1.0f) {
                this.timeInPortal = 1.0f;
            }
        }
        else {
            if (this.timeInPortal > 0.0f) {
                this.timeInPortal -= 0.05f;
            }
            if (this.timeInPortal < 0.0f) {
                this.timeInPortal = 0.0f;
            }
        }
        if (this.timeUntilPortal > 0) {
            --this.timeUntilPortal;
        }
        final boolean flag = this.movementInput.jump;
        final boolean flag2 = this.movementInput.sneak;
        final float f = 0.8f;
        final boolean flag3 = this.movementInput.moveForward >= f;
        this.movementInput.updatePlayerMoveState();
        final NoSlow ns = (NoSlow)ModuleManager.getModuleByClass(NoSlow.class);
        if (this.isUsingItem() && !this.isRiding() && ns.isEnabled()) {
            this.movementInput.moveStrafe *= (float)((double)NoSlow.Reduceslow.getValue() / 100.0);
            this.movementInput.moveForward *= (float)((double)NoSlow.Reduceslow.getValue() / 100.0);
        }
        else if (this.isUsingItem() && !this.isRiding()) {
            this.movementInput.moveStrafe *= 0.2;
            this.movementInput.moveForward *= 0.2;
            this.sprintToggleTimer = 0;
        }
        this.pushOutOfBlocks(this.posX - this.width * 0.35, this.getEntityBoundingBox().minY + 0.5, this.posZ + this.width * 0.35);
        this.pushOutOfBlocks(this.posX - this.width * 0.35, this.getEntityBoundingBox().minY + 0.5, this.posZ - this.width * 0.35);
        this.pushOutOfBlocks(this.posX + this.width * 0.35, this.getEntityBoundingBox().minY + 0.5, this.posZ - this.width * 0.35);
        this.pushOutOfBlocks(this.posX + this.width * 0.35, this.getEntityBoundingBox().minY + 0.5, this.posZ + this.width * 0.35);
        final boolean flag4 = this.getFoodStats().getFoodLevel() > 6.0f || this.capabilities.allowFlying;
        if (this.onGround && !flag2 && !flag3 && this.movementInput.moveForward >= f && !this.isSprinting() && flag4 && !this.isUsingItem() && !this.isPotionActive(Potion.blindness)) {
            if (this.sprintToggleTimer <= 0 && !this.mc.gameSettings.keyBindSprint.isKeyDown()) {
                this.sprintToggleTimer = 7;
            }
            else {
                this.setSprinting(true);
            }
        }
        Label_0916: {
            if (!this.isSprinting() && this.movementInput.moveForward >= f && flag4) {
                if (this.isUsingItem()) {
                    Client.getModuleManager();
                    if (!ModuleManager.getModuleByClass(NoSlow.class).isEnabled()) {
                        break Label_0916;
                    }
                }
                if (!this.isPotionActive(Potion.blindness)) {
                    if (!this.mc.gameSettings.keyBindSprint.isKeyDown()) {
                        Client.getModuleManager();
                        if (!ModuleManager.getModuleByClass(Sprint.class).isEnabled()) {
                            break Label_0916;
                        }
                    }
                    this.setSprinting(true);
                }
            }
        }
        if (this.isSprinting() && (this.movementInput.moveForward < f || this.isCollidedHorizontally || !flag4)) {
            this.setSprinting(false);
        }
        if (this.capabilities.allowFlying) {
            if (this.mc.playerController.isSpectatorMode()) {
                if (!this.capabilities.isFlying) {
                    this.capabilities.isFlying = true;
                    this.sendPlayerAbilities();
                }
            }
            else if (!flag && this.movementInput.jump) {
                if (this.flyToggleTimer == 0) {
                    this.flyToggleTimer = 7;
                }
                else {
                    this.capabilities.isFlying = !this.capabilities.isFlying;
                    this.sendPlayerAbilities();
                    this.flyToggleTimer = 0;
                }
            }
        }
        if (this.capabilities.isFlying && this.isCurrentViewEntity()) {
            if (this.movementInput.sneak) {
                this.motionY -= this.capabilities.getFlySpeed() * 3.0f;
            }
            if (this.movementInput.jump) {
                this.motionY += this.capabilities.getFlySpeed() * 3.0f;
            }
        }
        if (this.isRidingHorse()) {
            if (this.horseJumpPowerCounter < 0) {
                ++this.horseJumpPowerCounter;
                if (this.horseJumpPowerCounter == 0) {
                    this.horseJumpPower = 0.0f;
                }
            }
            if (flag && !this.movementInput.jump) {
                this.horseJumpPowerCounter = -10;
                this.sendHorseJump();
            }
            else if (!flag && this.movementInput.jump) {
                this.horseJumpPowerCounter = 0;
                this.horseJumpPower = 0.0f;
            }
            else if (flag) {
                ++this.horseJumpPowerCounter;
                if (this.horseJumpPowerCounter < 10) {
                    this.horseJumpPower = this.horseJumpPowerCounter * 0.1f;
                }
                else {
                    this.horseJumpPower = 0.8f + 2.0f / (this.horseJumpPowerCounter - 9) * 0.1f;
                }
            }
        }
        else {
            this.horseJumpPower = 0.0f;
        }
        super.onLivingUpdate();
        if (this.onGround && this.capabilities.isFlying && !this.mc.playerController.isSpectatorMode()) {
            this.capabilities.isFlying = false;
            this.sendPlayerAbilities();
        }
    }
    
    @Overwrite
    public void onUpdateWalkingPlayer() {
        try {
            final boolean flag = this.isSprinting();
            final EventPreUpdate pre = new EventPreUpdate(this.lastReportedYaw, this.lastReportedPitch, this.rotationYaw, this.rotationPitch, this.posX, this.posY, this.posZ, this.onGround);
            EventBus.getInstance().call(pre);
            final EventPostUpdate post = new EventPostUpdate(this.rotationYaw, this.rotationPitch);
            if (pre.isCancelled()) {
                EventBus.getInstance().call(post);
                return;
            }
            if (flag != this.serverSprintState) {
                if (flag) {
                    this.sendQueue.addToSendQueue((Packet)new C0BPacketEntityAction((Entity)this, C0BPacketEntityAction.Action.START_SPRINTING));
                }
                else {
                    this.sendQueue.addToSendQueue((Packet)new C0BPacketEntityAction((Entity)this, C0BPacketEntityAction.Action.STOP_SPRINTING));
                }
                this.serverSprintState = flag;
            }
            final boolean flag2 = this.isSneaking();
            if (flag2 != this.serverSneakState) {
                if (flag2) {
                    this.sendQueue.addToSendQueue((Packet)new C0BPacketEntityAction((Entity)this, C0BPacketEntityAction.Action.START_SNEAKING));
                }
                else {
                    this.sendQueue.addToSendQueue((Packet)new C0BPacketEntityAction((Entity)this, C0BPacketEntityAction.Action.STOP_SNEAKING));
                }
                this.serverSneakState = flag2;
            }
            if (this.isCurrentViewEntity()) {
                final double d0 = pre.getX() - this.lastReportedPosX;
                final double d2 = pre.getY() - this.lastReportedPosY;
                final double d3 = pre.getZ() - this.lastReportedPosZ;
                final double d4 = pre.getYaw() - this.lastReportedYaw;
                final double d5 = pre.getPitch() - this.lastReportedPitch;
                boolean flag3 = d0 * d0 + d2 * d2 + d3 * d3 > 9.0E-4 || this.positionUpdateTicks >= 20;
                final boolean flag4 = d4 != 0.0 || d5 != 0.0;
                if (this.ridingEntity == null) {
                    if (flag3 && flag4) {
                        this.sendQueue.getNetworkManager().sendPacket((Packet)new C03PacketPlayer.C06PacketPlayerPosLook(pre.getX(), pre.getY(), pre.getZ(), pre.getYaw(), pre.getPitch(), pre.isOnground()));
                    }
                    else if (flag3) {
                        this.sendQueue.getNetworkManager().sendPacket((Packet)new C03PacketPlayer.C04PacketPlayerPosition(pre.getX(), pre.getY(), pre.getZ(), pre.isOnground()));
                    }
                    else if (flag4) {
                        this.sendQueue.getNetworkManager().sendPacket((Packet)new C03PacketPlayer.C05PacketPlayerLook(pre.getYaw(), pre.getPitch(), pre.isOnground()));
                    }
                    else {
                        this.sendQueue.getNetworkManager().sendPacket((Packet)new C03PacketPlayer(pre.isOnground()));
                    }
                }
                else {
                    this.sendQueue.getNetworkManager().sendPacket((Packet)new C03PacketPlayer.C06PacketPlayerPosLook(this.motionX, -999.0, this.motionZ, pre.getYaw(), pre.getPitch(), pre.isOnground()));
                    flag3 = false;
                }
                ++this.positionUpdateTicks;
                if (flag3) {
                    this.lastReportedPosX = pre.getX();
                    this.lastReportedPosY = pre.getY();
                    this.lastReportedPosZ = pre.getZ();
                    this.positionUpdateTicks = 0;
                }
                if (flag4) {
                    this.lastReportedYaw = pre.getYaw();
                    this.lastReportedPitch = pre.getPitch();
                }
            }
            EventBus.getInstance().call(post);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void attackTargetEntityWithCurrentItem(final Entity targetEntity) {
        if (targetEntity.canAttackWithItem() && !targetEntity.hitByEntity((Entity)this)) {
            float f = (float)this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
            int i = 0;
            float f2 = 0.0f;
            if (targetEntity instanceof EntityLivingBase) {
                f2 = EnchantmentHelper.getModifierForCreature(this.getHeldItem(), ((EntityLivingBase)targetEntity).getCreatureAttribute());
            }
            else {
                f2 = EnchantmentHelper.getModifierForCreature(this.getHeldItem(), EnumCreatureAttribute.UNDEFINED);
            }
            i += EnchantmentHelper.getKnockbackModifier((EntityLivingBase)this);
            if (this.isSprinting()) {
                ++i;
            }
            if (f > 0.0f || f2 > 0.0f) {
                final boolean flag = this.fallDistance > 0.0f && !this.onGround && !this.isOnLadder() && !this.isInWater() && !this.isPotionActive(Potion.blindness) && this.ridingEntity == null && targetEntity instanceof EntityLivingBase;
                if (flag && f > 0.0f) {
                    f *= 1.5f;
                }
                f += f2;
                boolean flag2 = false;
                final int j = EnchantmentHelper.getFireAspectModifier((EntityLivingBase)this);
                if (targetEntity instanceof EntityLivingBase && j > 0 && !targetEntity.isBurning()) {
                    flag2 = true;
                    targetEntity.setFire(1);
                }
                final double d0 = targetEntity.motionX;
                final double d2 = targetEntity.motionY;
                final double d3 = targetEntity.motionZ;
                final boolean flag3 = targetEntity.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)this), f);
                if (flag3) {
                    if (i > 0) {
                        targetEntity.addVelocity((double)(-MathHelper.sin(this.rotationYaw * 3.1415927f / 180.0f) * i * 0.5f), 0.1, (double)(MathHelper.cos(this.rotationYaw * 3.1415927f / 180.0f) * i * 0.5f));
                        if (!ModuleManager.getModuleByClass(KeepSprint.class).isEnabled()) {
                            this.motionX *= 0.6;
                            this.motionZ *= 0.6;
                            this.setSprinting(false);
                        }
                    }
                    if (targetEntity instanceof EntityPlayerMP && targetEntity.velocityChanged) {
                        ((EntityPlayerMP)targetEntity).playerNetServerHandler.sendPacket((Packet)new S12PacketEntityVelocity(targetEntity));
                        targetEntity.velocityChanged = false;
                        targetEntity.motionX = d0;
                        targetEntity.motionY = d2;
                        targetEntity.motionZ = d3;
                    }
                    if (flag) {
                        this.onCriticalHit(targetEntity);
                    }
                    if (f2 > 0.0f) {
                        this.onEnchantmentCritical(targetEntity);
                    }
                    if (f >= 18.0f) {
                        this.triggerAchievement((StatBase)AchievementList.overkill);
                    }
                    this.setLastAttacker(targetEntity);
                    if (targetEntity instanceof EntityLivingBase) {
                        EnchantmentHelper.applyThornEnchantments((EntityLivingBase)targetEntity, (Entity)this);
                    }
                    EnchantmentHelper.applyArthropodEnchantments((EntityLivingBase)this, targetEntity);
                    final ItemStack itemstack = this.getCurrentEquippedItem();
                    Entity entity = targetEntity;
                    if (targetEntity instanceof EntityDragonPart) {
                        final IEntityMultiPart ientitymultipart = ((EntityDragonPart)targetEntity).entityDragonObj;
                        if (ientitymultipart instanceof EntityLivingBase) {
                            entity = (Entity)ientitymultipart;
                        }
                    }
                    if (itemstack != null && entity instanceof EntityLivingBase) {
                        itemstack.hitEntity((EntityLivingBase)entity, (EntityPlayer)this);
                        if (itemstack.stackSize <= 0) {
                            this.destroyCurrentEquippedItem();
                        }
                    }
                    if (targetEntity instanceof EntityLivingBase) {
                        this.addStat(StatList.damageDealtStat, Math.round(f * 10.0f));
                        if (j > 0) {
                            targetEntity.setFire(j * 4);
                        }
                    }
                    this.addExhaustion(0.3f);
                }
                else if (flag2) {
                    targetEntity.extinguish();
                }
            }
        }
    }
    
    @Shadow
    public boolean isCurrentViewEntity() {
        return false;
    }
    
    public void moveEntity(double x, double y, double z) {
        final EventMove moveEvent = new EventMove(x, y, z);
        EventBus.getInstance().call(moveEvent);
        if (moveEvent.isCancelled()) {
            return;
        }
        x = moveEvent.getX();
        y = moveEvent.getY();
        z = moveEvent.getZ();
        if (this.noClip) {
            this.setEntityBoundingBox(this.getEntityBoundingBox().offset(x, y, z));
            this.posX = (this.getEntityBoundingBox().minX + this.getEntityBoundingBox().maxX) / 2.0;
            this.posY = this.getEntityBoundingBox().minY;
            this.posZ = (this.getEntityBoundingBox().minZ + this.getEntityBoundingBox().maxZ) / 2.0;
        }
        else {
            this.worldObj.theProfiler.startSection("move");
            final double d0 = this.posX;
            final double d2 = this.posY;
            final double d3 = this.posZ;
            if (this.isInWeb) {
                this.isInWeb = false;
                x *= 0.25;
                y *= 0.05000000074505806;
                z *= 0.25;
                this.motionX = 0.0;
                this.motionY = 0.0;
                this.motionZ = 0.0;
            }
            double d4 = x;
            final double d5 = y;
            double d6 = z;
            final boolean ff = this.onGround && this.isSneaking() && this instanceof EntityPlayer;
            final SafeWalkEvent safeWalkEvent = new SafeWalkEvent(ff);
            EventBus.getInstance().call(safeWalkEvent);
            final boolean flag = safeWalkEvent.isCancelled();
            if (flag) {
                final double d7 = 0.05;
                while (x != 0.0 && this.worldObj.getCollidingBoundingBoxes((Entity)this, this.getEntityBoundingBox().offset(x, -1.0, 0.0)).isEmpty()) {
                    if (x < d7 && x >= -d7) {
                        x = 0.0;
                    }
                    else if (x > 0.0) {
                        x -= d7;
                    }
                    else {
                        x += d7;
                    }
                    d4 = x;
                }
                while (z != 0.0 && this.worldObj.getCollidingBoundingBoxes((Entity)this, this.getEntityBoundingBox().offset(0.0, -1.0, z)).isEmpty()) {
                    if (z < d7 && z >= -d7) {
                        z = 0.0;
                    }
                    else if (z > 0.0) {
                        z -= d7;
                    }
                    else {
                        z += d7;
                    }
                    d6 = z;
                }
                while (x != 0.0 && z != 0.0 && this.worldObj.getCollidingBoundingBoxes((Entity)this, this.getEntityBoundingBox().offset(x, -1.0, z)).isEmpty()) {
                    if (x < d7 && x >= -d7) {
                        x = 0.0;
                    }
                    else if (x > 0.0) {
                        x -= d7;
                    }
                    else {
                        x += d7;
                    }
                    d4 = x;
                    if (z < d7 && z >= -d7) {
                        z = 0.0;
                    }
                    else if (z > 0.0) {
                        z -= d7;
                    }
                    else {
                        z += d7;
                    }
                    d6 = z;
                }
            }
            final List<AxisAlignedBB> list1 = (List<AxisAlignedBB>)this.worldObj.getCollidingBoundingBoxes((Entity)this, this.getEntityBoundingBox().addCoord(x, y, z));
            final AxisAlignedBB axisalignedbb = this.getEntityBoundingBox();
            for (final AxisAlignedBB axisalignedbb2 : list1) {
                y = axisalignedbb2.calculateYOffset(this.getEntityBoundingBox(), y);
            }
            this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0, y, 0.0));
            final boolean flag2 = this.onGround || (d5 != y && d5 < 0.0);
            for (final AxisAlignedBB axisalignedbb3 : list1) {
                x = axisalignedbb3.calculateXOffset(this.getEntityBoundingBox(), x);
            }
            this.setEntityBoundingBox(this.getEntityBoundingBox().offset(x, 0.0, 0.0));
            for (final AxisAlignedBB axisalignedbb4 : list1) {
                z = axisalignedbb4.calculateZOffset(this.getEntityBoundingBox(), z);
            }
            this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0, 0.0, z));
            if (this.stepHeight > 0.0f && flag2 && (d4 != x || d6 != z)) {
                final StepEvent stepEvent = new StepEvent(this.stepHeight, true);
                EventBus.getInstance().call(stepEvent);
                final double d8 = x;
                final double d9 = y;
                final double d10 = z;
                final AxisAlignedBB axisalignedbb5 = this.getEntityBoundingBox();
                this.setEntityBoundingBox(axisalignedbb);
                y = stepEvent.getStepHeight();
                final List<AxisAlignedBB> list2 = (List<AxisAlignedBB>)this.worldObj.getCollidingBoundingBoxes((Entity)this, this.getEntityBoundingBox().addCoord(d4, y, d6));
                AxisAlignedBB axisalignedbb6 = this.getEntityBoundingBox();
                final AxisAlignedBB axisalignedbb7 = axisalignedbb6.addCoord(d4, 0.0, d6);
                double d11 = y;
                for (final AxisAlignedBB axisalignedbb8 : list2) {
                    d11 = axisalignedbb8.calculateYOffset(axisalignedbb7, d11);
                }
                axisalignedbb6 = axisalignedbb6.offset(0.0, d11, 0.0);
                double d12 = d4;
                for (final AxisAlignedBB axisalignedbb9 : list2) {
                    d12 = axisalignedbb9.calculateXOffset(axisalignedbb6, d12);
                }
                axisalignedbb6 = axisalignedbb6.offset(d12, 0.0, 0.0);
                double d13 = d6;
                for (final AxisAlignedBB axisalignedbb10 : list2) {
                    d13 = axisalignedbb10.calculateZOffset(axisalignedbb6, d13);
                }
                axisalignedbb6 = axisalignedbb6.offset(0.0, 0.0, d13);
                AxisAlignedBB axisalignedbb11 = this.getEntityBoundingBox();
                double d14 = y;
                for (final AxisAlignedBB axisalignedbb12 : list2) {
                    d14 = axisalignedbb12.calculateYOffset(axisalignedbb11, d14);
                }
                axisalignedbb11 = axisalignedbb11.offset(0.0, d14, 0.0);
                double d15 = d4;
                for (final AxisAlignedBB axisalignedbb13 : list2) {
                    d15 = axisalignedbb13.calculateXOffset(axisalignedbb11, d15);
                }
                axisalignedbb11 = axisalignedbb11.offset(d15, 0.0, 0.0);
                double d16 = d6;
                for (final AxisAlignedBB axisalignedbb14 : list2) {
                    d16 = axisalignedbb14.calculateZOffset(axisalignedbb11, d16);
                }
                axisalignedbb11 = axisalignedbb11.offset(0.0, 0.0, d16);
                final double d17 = d12 * d12 + d13 * d13;
                final double d18 = d15 * d15 + d16 * d16;
                if (d17 > d18) {
                    x = d12;
                    z = d13;
                    y = -d11;
                    this.setEntityBoundingBox(axisalignedbb6);
                }
                else {
                    x = d15;
                    z = d16;
                    y = -d14;
                    this.setEntityBoundingBox(axisalignedbb11);
                }
                for (final AxisAlignedBB axisalignedbb15 : list2) {
                    y = axisalignedbb15.calculateYOffset(this.getEntityBoundingBox(), y);
                }
                this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0, y, 0.0));
                if (d8 * d8 + d10 * d10 >= x * x + z * z) {
                    x = d8;
                    y = d9;
                    z = d10;
                    this.setEntityBoundingBox(axisalignedbb5);
                }
                else {
                    EventBus.getInstance().call(new StepEvent(-1.0, false));
                }
            }
            this.worldObj.theProfiler.endSection();
            this.worldObj.theProfiler.startSection("rest");
            this.posX = (this.getEntityBoundingBox().minX + this.getEntityBoundingBox().maxX) / 2.0;
            this.posY = this.getEntityBoundingBox().minY;
            this.posZ = (this.getEntityBoundingBox().minZ + this.getEntityBoundingBox().maxZ) / 2.0;
            this.isCollidedHorizontally = (d4 != x || d6 != z);
            this.isCollidedVertically = (d5 != y);
            this.onGround = (this.isCollidedVertically && d5 < 0.0);
            this.isCollided = (this.isCollidedHorizontally || this.isCollidedVertically);
            final int i = MathHelper.floor_double(this.posX);
            final int j = MathHelper.floor_double(this.posY - 0.20000000298023224);
            final int k = MathHelper.floor_double(this.posZ);
            BlockPos blockpos = new BlockPos(i, j, k);
            Block block1 = this.worldObj.getBlockState(blockpos).getBlock();
            if (block1.getMaterial() == Material.air) {
                final Block block2 = this.worldObj.getBlockState(blockpos.down()).getBlock();
                if (block2 instanceof BlockFence || block2 instanceof BlockWall || block2 instanceof BlockFenceGate) {
                    block1 = block2;
                    blockpos = blockpos.down();
                }
            }
            this.updateFallState(y, this.onGround, block1, blockpos);
            if (d4 != x) {
                this.motionX = 0.0;
            }
            if (d6 != z) {
                this.motionZ = 0.0;
            }
            if (d5 != y) {
                block1.onLanded(this.worldObj, (Entity)this);
            }
            final IEntity ent = (IEntity)this;
            if (this.canTriggerWalking() && !flag && this.ridingEntity == null) {
                final double d19 = this.posX - d0;
                double d20 = this.posY - d2;
                final double d21 = this.posZ - d3;
                if (block1 != Blocks.ladder) {
                    d20 = 0.0;
                }
                if (block1 != null && this.onGround) {
                    block1.onEntityCollidedWithBlock(this.worldObj, blockpos, (Entity)this);
                }
                this.distanceWalkedModified += (float)(MathHelper.sqrt_double(d19 * d19 + d21 * d21) * 0.6);
                this.distanceWalkedOnStepModified += (float)(MathHelper.sqrt_double(d19 * d19 + d20 * d20 + d21 * d21) * 0.6);
                if (this.distanceWalkedOnStepModified > ent.getNextStepDistance() && block1.getMaterial() != Material.air) {
                    ent.setNextStepDistance((int)this.distanceWalkedOnStepModified + 1);
                    if (this.isInWater()) {
                        float f = MathHelper.sqrt_double(this.motionX * this.motionX * 0.20000000298023224 + this.motionY * this.motionY + this.motionZ * this.motionZ * 0.20000000298023224) * 0.35f;
                        if (f > 1.0f) {
                            f = 1.0f;
                        }
                        this.playSound(this.getSwimSound(), f, 1.0f + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4f);
                    }
                    this.playStepSound(blockpos, block1);
                }
            }
            try {
                this.doBlockCollisions();
            }
            catch (Throwable throwable) {
                final CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Checking entity block collision");
                final CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being checked for collision");
                this.addEntityCrashInfo(crashreportcategory);
                throw new ReportedException(crashreport);
            }
            final boolean flag3 = this.isWet();
            if (this.worldObj.isFlammableWithin(this.getEntityBoundingBox().contract(0.001, 0.001, 0.001))) {
                this.dealFireDamage(1);
                if (!flag3) {
                    this.setFire(ent.getFire() + 1);
                    if (ent.getFire() == 0) {
                        this.setFire(8);
                    }
                }
            }
            else if (ent.getFire() <= 0) {
                this.setFire(-this.fireResistance);
            }
            if (flag3 && ent.getFire() > 0) {
                this.playSound("random.fizz", 0.7f, 1.6f + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4f);
                this.setFire(-this.fireResistance);
            }
            this.worldObj.theProfiler.endSection();
        }
    }
    
    public boolean moving() {
        return this.moveForward > 0.0 | this.moveStrafing > 0.0;
    }
    
    public float getSpeed() {
        final float vel = (float)Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
        return vel;
    }
    
    public void setSpeed(final double speed) {
        this.motionX = -MathHelper.sin(this.getDirection()) * speed;
        this.motionZ = MathHelper.cos(this.getDirection()) * speed;
    }
    
    public float getDirection() {
        float yaw = this.rotationYaw;
        final float forward = this.moveForward;
        final float strafe = this.moveStrafing;
        yaw += ((forward < 0.0f) ? 180 : 0);
        if (strafe < 0.0f) {
            yaw += ((forward < 0.0f) ? -45.0f : ((forward == 0.0f) ? 90.0f : 45.0f));
        }
        if (strafe > 0.0f) {
            yaw -= ((forward < 0.0f) ? -45.0f : ((forward == 0.0f) ? 90.0f : 45.0f));
        }
        return yaw * 0.017453292f;
    }
    
    public void setYaw(final double yaw) {
        this.rotationYaw = (float)yaw;
    }
    
    public void setPitch(final double pitch) {
        this.rotationPitch = (float)pitch;
    }
    
    public void setMoveSpeed(final EventMove event, final double speed) {
        double forward = this.movementInput.moveForward;
        double strafe = this.movementInput.moveStrafe;
        float yaw = this.rotationYaw;
        if (forward == 0.0 && strafe == 0.0) {
            event.setX(0.0);
            event.setZ(0.0);
        }
        else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += ((forward > 0.0) ? -45 : 45);
                }
                else if (strafe < 0.0) {
                    yaw += ((forward > 0.0) ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                }
                else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            event.setX(forward * speed * Math.cos(Math.toRadians(yaw + 90.0f)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0f)));
            event.setZ(forward * speed * Math.sin(Math.toRadians(yaw + 90.0f)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0f)));
        }
    }
    
    public void setLastReportedPosY(final double f) {
        this.lastReportedPosY = f;
    }
}
