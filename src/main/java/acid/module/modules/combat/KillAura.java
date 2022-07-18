package acid.module.modules.combat;

import org.lwjgl.opengl.GL11;
import acid.utils.render.RenderUtils;
import acid.utils.render.RenderUtil;
import buzz.gaoyusense.injection.interfaces.IRenderManager;
import acid.api.events.rendering.EventRender3D;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IChatComponent;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import acid.management.FriendManager;
import acid.module.modules.player.Teams;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.potion.Potion;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.network.play.client.C02PacketUseEntity;
import acid.api.events.world.EventPostUpdate;
import java.util.concurrent.ThreadLocalRandom;
import acid.utils.PlayerUtils;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.BlockPos;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import buzz.gaoyusense.injection.interfaces.IEntityPlayer;
import net.minecraft.util.MathHelper;
import acid.module.modules.movement.Scaffold;
import java.util.Iterator;
import net.minecraft.entity.boss.EntityWither;
import acid.utils.Helper;
import acid.module.modules.movement.Speed;
import acid.management.ModuleManager;
import acid.Client;
import java.util.List;
import acid.api.EventHandler;
import acid.api.events.world.EventPreUpdate;
import net.minecraft.item.ItemSword;
import net.minecraft.entity.player.EntityPlayer;
import acid.api.value.Value;
import java.awt.Color;
import acid.utils.math.RotationUtil;
import acid.module.ModuleType;
import java.util.Random;
import acid.api.value.Numbers;
import acid.api.value.Option;
import acid.api.value.Mode;
import net.minecraft.entity.Entity;
import java.util.Comparator;
import acid.utils.timer.TimerUtil;
import java.util.ArrayList;
import net.minecraft.entity.EntityLivingBase;
import acid.utils.timer.TimeHelper;
import acid.module.Module;

public class KillAura extends Module
{
    private final TimeHelper attackTimer;
    public static EntityLivingBase curTarget;
    public static EntityLivingBase blockTarget;
    public ArrayList<EntityLivingBase> targetList;
    private int index;
    private final TimerUtil blockPreventFlagTimer;
    public static EntityLivingBase vip;
    public static boolean blocking;
    private Comparator<Entity> angleComparator;
    private final TimeHelper switchTimer;
    private Mode<Enum> Priority;
    private float yaw;
    private float pitch;
    private float iyaw;
    private float ipitch;
    private float prevIYaw;
    private float prevIPitch;
    private float alpha;
    private Mode<Enum> mode;
    private Mode<Enum> mode2;
    public static final Option<Boolean> playerValue;
    public static final Option<Boolean> animalValue;
    public static final Option<Boolean> monsterValue;
    public static final Option<Boolean> neutralValue;
    public static final Option<Boolean> attackValue;
    public static final Option<Boolean> preferValue;
    public static final Option<Boolean> deathValue;
    public static final Option<Boolean> multi;
    public static final Option<Boolean> invisibleValue;
    public static final Option<Boolean> throughWallValue;
    public static final Option<Boolean> RotValue;
    public static final Option<Boolean> preventFlagValue;
    public static final Option<Boolean> autoBlockValue;
    public final Numbers<Number> switchDelayValue;
    public final Numbers<Number> rangeValue;
    public final Numbers<Number> maxApsValue;
    public final Numbers<Number> minApsValue;
    public final Numbers<Number> smoothValue;
    public final Numbers<Number> blockRangeValue;
    public final Numbers<Number> wallRangeValue;
    public final Numbers<Number> fovValue;
    public final Numbers<Number> MAXT;
    static float c;
    static float b;
    private Random V;
    
    public KillAura() {
        super("Kill Aura", new String[] { "ka", "aura", "killa" }, ModuleType.Combat);
        this.attackTimer = new TimeHelper();
        this.targetList = new ArrayList<EntityLivingBase>();
        this.blockPreventFlagTimer = new TimerUtil();
        this.angleComparator = Comparator.comparingDouble(e2 -> RotationUtil.getRotations(e2)[0]);
        this.switchTimer = new TimeHelper();
        this.Priority = new Mode<Enum>("Priority", priority.values(), priority.Health);
        this.mode = new Mode<Enum>("Mode", AuraMode.values(), AuraMode.Switch);
        this.mode2 = new Mode<Enum>("ESP", ESPMode.values(), ESPMode.Vape);
        this.switchDelayValue = new Numbers<Number>("SwitchDelay", "SwitchDelay", 15.0, 0.0, 20.0, 0.5);
        this.rangeValue = new Numbers<Number>("Range", "Range", 4.2, 1.0, 8.0, 0.05);
        this.maxApsValue = new Numbers<Number>("MaxCPS", "MaxCPS", 14.5, 0.0, 20.0, 0.5);
        this.minApsValue = new Numbers<Number>("MinCPS", "MinCPS", 11.5, 0.0, 20.0, 0.5);
        this.smoothValue = new Numbers<Number>("Smoothness", "Smoothness", 30.0, 1.0, 100.0, 5.0);
        this.blockRangeValue = new Numbers<Number>("BlockRange", "BlockRange", 5.0, 1.0, 15.0, 0.05);
        this.wallRangeValue = new Numbers<Number>("WallRange", "WallRange", 4.2, 1.0, 5.0, 0.05);
        this.fovValue = new Numbers<Number>("Fov", "Fov", 180.0, 10.0, 360.0, 10.0);
        this.MAXT = new Numbers<Number>("MaxTarget", 1.0, 1.0, 50.0, 1.0);
        this.V = new Random();
        this.setColor(new Color(226, 54, 30).getRGB());
        this.addValues(this.mode, this.mode2, this.Priority, this.MAXT, this.maxApsValue, this.minApsValue, this.fovValue, this.smoothValue, this.switchDelayValue, this.wallRangeValue, this.blockRangeValue, this.rangeValue, KillAura.attackValue, KillAura.multi, KillAura.playerValue, KillAura.animalValue, KillAura.monsterValue, KillAura.neutralValue, KillAura.preferValue, KillAura.deathValue, KillAura.invisibleValue, KillAura.preventFlagValue, KillAura.RotValue, KillAura.throughWallValue, KillAura.autoBlockValue);
    }
    
    public boolean isBlocking2() {
        return KillAura.blocking;
    }
    
    @Override
    public void onDisable() {
        super.onDisable();
        final float rotationYaw = this.mc.thePlayer.rotationYaw;
        this.iyaw = rotationYaw;
        this.yaw = rotationYaw;
        final float rotationPitch = this.mc.thePlayer.rotationPitch;
        this.ipitch = rotationPitch;
        this.pitch = rotationPitch;
        this.targetList.clear();
        KillAura.curTarget = null;
        KillAura.blockTarget = null;
        if (KillAura.blocking) {
            this.unblock(true);
        }
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
        this.iyaw = this.mc.thePlayer.rotationYaw;
        this.ipitch = this.mc.thePlayer.rotationPitch;
        this.attackTimer.reset();
        this.targetList.clear();
        KillAura.curTarget = null;
        KillAura.blockTarget = null;
        KillAura.blocking = this.mc.thePlayer.isBlocking();
        this.blockPreventFlagTimer.reset();
        this.index = 0;
        this.switchTimer.reset();
    }
    
    private boolean hasSword(final EntityPlayer playerIn) {
        return playerIn.inventory.getCurrentItem() != null && playerIn.inventory.getCurrentItem().getItem() instanceof ItemSword;
    }
    
    private static int randomNumber(final double min, final double max) {
        final Random random = new Random();
        return (int)(min + random.nextDouble() * (max - min));
    }
    
    @EventHandler
    public final void On(final EventPreUpdate event) {
        if (KillAura.multi.getValue()) {
            this.setSuffix("Multi");
        }
        else {
            this.setSuffix(this.mode.getValue());
        }
    }
    
    private void updateTargets() {
        this.targetList.clear();
        final List entities = this.mc.theWorld.loadedEntityList;
        for (int entitiesSize = entities.size(), i = 0; i < entitiesSize; ++i) {
            final Entity entity = (Entity) entities.get(i);
            final EntityLivingBase entityLivingBase;
            if (entity instanceof EntityLivingBase && this.getEntityValid(entityLivingBase = (EntityLivingBase)entity)) {
                this.targetList.add(entityLivingBase);
            }
        }
    }
    
    @EventHandler
    private void onUpdate(final EventPreUpdate event) {
        Client.getModuleManager();
        boolean b = false;
        Label_0037: {
            if (ModuleManager.getModuleByClass(Criticals.class).isEnabled()) {
                Client.getModuleManager();
                if (!ModuleManager.getModuleByClass(Speed.class).isEnabled()) {
                    b = true;
                    break Label_0037;
                }
            }
            b = false;
        }
        final boolean crits = b;
        KillAura.blockTarget = null;
        this.updateTargets();
        if (this.mc.thePlayer.ticksExisted <= 1 && KillAura.deathValue.getValue()) {
            Helper.sendMessage("Disabled aura due to death");
            this.setEnabled(false);
            return;
        }
        KillAura.blockTarget = null;
        for (final Entity entity : this.mc.theWorld.getLoadedEntityList()) {
            if (entity instanceof EntityLivingBase) {
                final EntityLivingBase livingBase = (EntityLivingBase)entity;
                if (!this.getEntityValidBlock(livingBase)) {
                    continue;
                }
                KillAura.blockTarget = livingBase;
            }
        }
        KillAura.curTarget = null;
        this.targetList.clear();
        this.clearcurTargets();
        for (final Entity entity : this.mc.theWorld.getLoadedEntityList()) {
            if (entity instanceof EntityLivingBase) {
                final EntityLivingBase livingBase = (EntityLivingBase)entity;
                if (!this.getEntityValid(livingBase)) {
                    continue;
                }
                if (KillAura.vip == livingBase) {
                    this.targetList.clear();
                    this.targetList.add(livingBase);
                    break;
                }
                this.targetList.add(livingBase);
            }
        }
        this.sortList(this.targetList);
        if (KillAura.preferValue.getValue()) {
            for (final EntityLivingBase entityLivingBase : this.targetList) {
                if (entityLivingBase instanceof EntityWither) {
                    this.targetList.clear();
                    this.targetList.add(entityLivingBase);
                    break;
                }
            }
        }
        if (this.switchTimer.isDelayComplete(this.switchDelayValue.getValue().intValue() * 100L) && this.targetList.size() > 1) {
            this.switchTimer.reset();
            ++this.index;
        }
        if (this.index >= this.targetList.size()) {
            this.index = 0;
        }
        if (!this.targetList.isEmpty()) {
            KillAura.curTarget = this.targetList.get((this.mode.getValue() == AuraMode.Switch) ? this.index : 0);
        }
        this.prevIPitch = this.ipitch;
        this.prevIYaw = this.iyaw;
    }
    
    public float getPrevIPitch() {
        return this.prevIPitch;
    }
    
    public float getPrevIYaw() {
        return this.prevIYaw;
    }
    
    public float[] Rotate() {
        return new float[] { RotationUtil.getRotations1(KillAura.curTarget)[0], RotationUtil.getRotations1(KillAura.curTarget)[1] };
    }
    
    @EventHandler
    public void rot(final EventPreUpdate e) {
        if (KillAura.RotValue.getValue() && KillAura.curTarget != null) {
            Client.getModuleManager();
            if (!ModuleManager.getModuleByClass(Scaffold.class).isEnabled() && !this.mc.playerController.getIsHittingBlock()) {
                final float frac = MathHelper.clamp_float(1.0f - this.smoothValue.getValue().floatValue() / 100.0f, 0.1f, 1.0f);
                final float[] rotations = RotationUtil.getAngles(KillAura.curTarget);
                this.iyaw += (rotations[0] - this.iyaw) * frac;
                this.ipitch += (rotations[1] - this.ipitch) * frac;
                e.setYaw(this.yaw = this.iyaw);
                e.setPitch(this.pitch = this.ipitch);
            }
            else {
                this.iyaw = this.mc.thePlayer.rotationYaw;
                this.ipitch = this.mc.thePlayer.rotationPitch;
            }
        }
        if (KillAura.autoBlockValue.getValue()) {
            if (KillAura.blockTarget != null) {
                if (this.hasSword((EntityPlayer)this.mc.thePlayer)) {
                    if (!KillAura.blocking) {
                        this.blockPreventFlagTimer.reset();
                    }
                    else if (this.blockPreventFlagTimer.hasPassed(30L) && KillAura.preventFlagValue.getValue()) {
                        this.unblock2(false);
                        this.blockPreventFlagTimer.reset();
                    }
                }
                else if (KillAura.blocking) {
                    this.unblock2(true);
                }
            }
            else if (KillAura.blocking) {
                this.unblock2(true);
            }
        }
    }
    
    public static float[] a(final float[] var0, final int var1) {
        final float var2 = MathHelper.clamp_float(1.0f - var1 / 100.0f, 0.1f, 1.0f);
        KillAura.c += (var0[0] - KillAura.c) * var2;
        KillAura.b += (var0[1] - KillAura.b) * var2;
        return new float[] { MathHelper.wrapAngleTo180_float(KillAura.c), KillAura.b };
    }
    
    private void unblock2(final boolean setItemUseInCount) {
        if (setItemUseInCount) {
            ((IEntityPlayer)this.mc.thePlayer).setItemInUseCount(0);
        }
        KillAura.blocking = false;
    }
    
    private float randomfloat(final float max, final float min) {
        return min + (float)Math.random() * (max - min);
    }
    
    private void block(final boolean setItemUseInCount) {
        if (setItemUseInCount) {
            ((IEntityPlayer)this.mc.thePlayer).setItemInUseCount(this.mc.thePlayer.getHeldItem().getMaxItemUseDuration());
        }
        this.mc.getNetHandler().addToSendQueue((Packet)new C08PacketPlayerBlockPlacement(this.mc.thePlayer.getHeldItem()));
        KillAura.blocking = true;
    }
    
    private void unblock(final boolean setItemUseInCount) {
        if (setItemUseInCount) {
            ((IEntityPlayer)this.mc.thePlayer).setItemInUseCount(0);
        }
        this.mc.thePlayer.sendQueue.addToSendQueue((Packet)new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(-1, -1, -1), EnumFacing.DOWN));
        KillAura.blocking = false;
    }
    
    public void clearcurTargets() {
        KillAura.curTarget = null;
        this.targetList.clear();
        for (final EntityLivingBase ent : this.targetList) {
            if (!this.getEntityValidBlock(ent)) {
                this.targetList.remove(ent);
            }
        }
    }
    
    private boolean isBlocking() {
        return PlayerUtils.isHoldingSword() && this.mc.thePlayer.isBlocking() && !KillAura.blocking;
    }
    
    public static long randomClickDelay(final double minCPS, final double maxCPS) {
        return (long)(Math.random() * (1000.0 / minCPS - 1000.0 / maxCPS + 1.0) + 1000.0 / maxCPS);
    }
    
    public boolean shouldAttack() {
        if (KillAura.attackValue.getValue()) {
            return this.attackTimer.isDelayComplete(randomClickDelay(this.minApsValue.getValue().intValue() - 1, this.maxApsValue.getValue().intValue() + 1));
        }
        return this.attackTimer.isDelayComplete(1000L / ThreadLocalRandom.current().nextLong(this.minApsValue.getValue().longValue(), this.maxApsValue.getValue().longValue() + 1L));
    }
    
    @EventHandler
    private void onUpdatePost(final EventPostUpdate e) {
        if (KillAura.autoBlockValue.getValue().equals(false) && this.hasSword((EntityPlayer)this.mc.thePlayer) && this.mc.thePlayer.isBlocking()) {
            this.mc.getNetHandler().addToSendQueue((Packet)new C08PacketPlayerBlockPlacement(this.mc.thePlayer.getHeldItem()));
        }
        if (KillAura.curTarget != null) {
            Client.getModuleManager();
            if (ModuleManager.getModuleByClass(Scaffold.class).isEnabled()) {
                this.attackTimer.reset();
                return;
            }
            if (this.shouldAttack()) {
                Client.getModuleManager();
                final Criticals cri = (Criticals)ModuleManager.getModuleByClass(Criticals.class);
                if (cri.isEnabled()) {
                    cri.packetCrit(KillAura.curTarget);
                }
                if (KillAura.multi.getValue()) {
                    int targets = 0;
                    for (Iterator<EntityLivingBase> iterator = this.targetList.iterator(); iterator.hasNext() && targets < this.MAXT.getValue().intValue(); ++targets) {
                        final Entity entity = (Entity)iterator.next();
                        if (this.getEntityValid((EntityLivingBase)entity)) {
                            this.mc.thePlayer.swingItem();
                            this.mc.thePlayer.sendQueue.addToSendQueue((Packet)new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
                        }
                    }
                }
                else {
                    this.mc.thePlayer.swingItem();
                    this.mc.thePlayer.sendQueue.addToSendQueue((Packet)new C02PacketUseEntity((Entity)KillAura.curTarget, C02PacketUseEntity.Action.ATTACK));
                }
                if (EnchantmentHelper.getModifierForCreature(this.mc.thePlayer.getHeldItem(), KillAura.curTarget.getCreatureAttribute()) > 0.0f) {
                    this.mc.thePlayer.onEnchantmentCritical((Entity)KillAura.curTarget);
                }
                if (this.mc.thePlayer.fallDistance > 0.0f && !this.mc.thePlayer.onGround && !this.mc.thePlayer.isOnLadder() && !this.mc.thePlayer.isInWater() && !this.mc.thePlayer.isPotionActive(Potion.blindness) && this.mc.thePlayer.ridingEntity == null) {
                    this.mc.thePlayer.onCriticalHit((Entity)KillAura.curTarget);
                }
                this.attackTimer.reset();
            }
        }
        if (this.shouldAttack()) {
            if (KillAura.autoBlockValue.getValue().equals(false) && KillAura.blockTarget != null) {
                this.mc.thePlayer.swingItem();
            }
            this.attackTimer.reset();
        }
        if (KillAura.autoBlockValue.getValue() && KillAura.blockTarget != null && this.hasSword((EntityPlayer)this.mc.thePlayer) && !this.mc.thePlayer.isBlocking() && !KillAura.blocking) {
            this.block(true);
        }
    }
    
    public boolean hitbox(final Entity entity) {
        Client.getModuleManager();
        final AntiBot ab = (AntiBot)ModuleManager.getModuleByClass(AntiBot.class);
        if (!this.mc.thePlayer.isEntityAlive() || this.mc.thePlayer.isPlayerSleeping() || this.mc.thePlayer.isDead || this.mc.thePlayer.getHealth() <= 0.0f || !entity.isEntityAlive() || entity.isDead || entity instanceof EntityArmorStand || Teams.isOnSameTeam(entity) || ab.isBot(entity) || this.isAutismShopKeeperCheck(entity) || entity == this.mc.thePlayer) {
            return false;
        }
        if (entity instanceof EntityPlayer) {
            final EntityPlayer player = (EntityPlayer)entity;
            if (FriendManager.isFriend(player.getName())) {
                return false;
            }
            if (!KillAura.playerValue.getValue()) {
                return false;
            }
            if (player.isPlayerSleeping()) {
                return false;
            }
            if (player.isPotionActive(Potion.invisibility) && !KillAura.invisibleValue.getValue()) {
                return false;
            }
        }
        if (entity instanceof EntityAnimal) {
            return KillAura.animalValue.getValue();
        }
        if (entity instanceof EntityMob) {
            return KillAura.monsterValue.getValue();
        }
        return (!(entity instanceof EntityVillager) && !(entity instanceof EntityIronGolem) && !(entity instanceof EntitySnowman)) || KillAura.neutralValue.getValue();
    }
    
    private boolean isAutismShopKeeperCheck(final Entity entity) {
        final IChatComponent displayName = entity.getDisplayName();
        final String formattedText = displayName.getFormattedText();
        displayName.getUnformattedText();
        final boolean b = !formattedText.substring(0, formattedText.length() - 2).contains("¡ì");
        final boolean contains = formattedText.substring(formattedText.length() - 2).contains("¡ì");
        return b && contains;
    }
    
    public boolean getEntityValid(final EntityLivingBase entity) {
        Client.getModuleManager();
        final AntiBot ab = (AntiBot)ModuleManager.getModuleByClass(AntiBot.class);
        if (!this.mc.thePlayer.isEntityAlive() || this.mc.thePlayer.isPlayerSleeping() || this.mc.thePlayer.isDead || this.mc.thePlayer.getHealth() <= 0.0f || this.mc.thePlayer.getDistanceToEntity((Entity)entity) >= this.rangeValue.getValue().floatValue() || !entity.isEntityAlive() || entity.isDead || entity.getHealth() <= 0.0f || entity instanceof EntityArmorStand || Teams.isOnSameTeam((Entity)entity) || ab.isBot(entity) || this.isAutismShopKeeperCheck((Entity)entity) || this.notInFov((Entity)entity) || entity == this.mc.thePlayer) {
            return false;
        }
        if (entity instanceof EntityPlayer) {
            final EntityPlayer player = (EntityPlayer)entity;
            if (FriendManager.isFriend(player.getName())) {
                return false;
            }
            if (!KillAura.playerValue.getValue()) {
                return false;
            }
            if (player.isPlayerSleeping()) {
                return false;
            }
            final boolean wallChecks = !KillAura.throughWallValue.getValue() || this.mc.thePlayer.getDistanceToEntity((Entity)player) >= this.wallRangeValue.getValue().floatValue();
            if (!RotationUtil.canEntityBeSeen((Entity)player) && wallChecks) {
                return false;
            }
            if (player.isPotionActive(Potion.invisibility) && !KillAura.invisibleValue.getValue()) {
                return false;
            }
        }
        if (entity instanceof EntityAnimal) {
            return KillAura.animalValue.getValue();
        }
        if (entity instanceof EntityMob) {
            return KillAura.monsterValue.getValue();
        }
        return (!(entity instanceof EntityVillager) && !(entity instanceof EntityIronGolem) && !(entity instanceof EntitySnowman)) || KillAura.neutralValue.getValue();
    }
    
    private boolean getEntityValidBlock(final EntityLivingBase entity) {
        Client.getModuleManager();
        final AntiBot ab = (AntiBot)ModuleManager.getModuleByClass(AntiBot.class);
        if (!this.mc.thePlayer.isEntityAlive() || this.mc.thePlayer.isPlayerSleeping() || this.mc.thePlayer.isDead || this.mc.thePlayer.getHealth() <= 0.0f || this.mc.thePlayer.getDistanceToEntity((Entity)entity) >= this.blockRangeValue.getValue().floatValue() || !entity.isEntityAlive() || entity.isDead || entity.getHealth() <= 0.0f || entity instanceof EntityArmorStand || Teams.isOnSameTeam((Entity)entity) || ab.isBot(entity) || this.isAutismShopKeeperCheck((Entity)entity) || this.notInFov((Entity)entity) || entity == this.mc.thePlayer) {
            return false;
        }
        if (entity instanceof EntityPlayer) {
            final EntityPlayer player = (EntityPlayer)entity;
            if (FriendManager.isFriend(player.getName())) {
                return false;
            }
            if (!KillAura.playerValue.getValue()) {
                return false;
            }
            if (player.isPlayerSleeping()) {
                return false;
            }
            if (player.isPotionActive(Potion.invisibility) && !KillAura.invisibleValue.getValue()) {
                return false;
            }
        }
        if (entity instanceof EntityAnimal) {
            return KillAura.animalValue.getValue();
        }
        if (entity instanceof EntityMob) {
            return KillAura.monsterValue.getValue();
        }
        return (!(entity instanceof EntityVillager) && !(entity instanceof EntityIronGolem) && !(entity instanceof EntitySnowman)) || KillAura.neutralValue.getValue();
    }
    
    private boolean notInFov(final Entity entity) {
        return Math.abs(RotationUtil.getYawDifference(this.mc.thePlayer.rotationYaw, entity.posX, entity.posY, entity.posZ)) > this.fovValue.getValue().floatValue();
    }
    
    private void sortList(final List<EntityLivingBase> weed) {
        if (this.Priority.getValue() == priority.Health) {
            weed.sort(Comparator.comparingDouble(EntityLivingBase::getHealth));
        }
        if (this.Priority.getValue() == priority.Armor) {
            weed.sort(Comparator.comparingInt(o -> (o instanceof EntityPlayer) ? ((EntityPlayer)o).inventory.getTotalArmorValue() : ((int)((EntityLivingBase)o).getHealth())));
        }
        if (this.Priority.getValue() == priority.Range) {
            weed.sort((o1, o2) -> (int)(o1.getDistanceToEntity((Entity)this.mc.thePlayer) - o2.getDistanceToEntity((Entity)this.mc.thePlayer)));
        }
        if (this.Priority.getValue() == priority.Fov) {
            weed.sort(Comparator.comparingDouble(o -> RotationUtil.getDistanceBetweenAngles(this.mc.thePlayer.rotationPitch, RotationUtil.getRotationToEntity(o)[0])));
        }
        if (this.Priority.getValue() == priority.Angle) {
            weed.sort((o1, o2) -> {
            	float[] rot1 = this.getRotationToEntity((Entity)o1);
                float[] rot2 = this.getRotationToEntity((Entity)o2);
                return (int)(this.mc.thePlayer.rotationYaw - rot1[0] - (this.mc.thePlayer.rotationYaw - rot2[0]));
            });
        }
        if (this.Priority.getValue() == priority.Slowly) {
            weed.sort((ent1, ent2) -> {
            	 float f2 = 0.0f;
                 float e1 = RotationUtil.getRotations((Entity)ent1)[0];
                 float e2 = RotationUtil.getRotations((Entity)ent2)[0];
                 return e1 < f2 ? 1 : (e1 == e2 ? 0 : -2);
            });
        }
    }
    
    public float[] getRotationToEntity(final Entity target) {
        Minecraft.getMinecraft();
        final double xDiff = target.posX - this.mc.thePlayer.posX;
        Minecraft.getMinecraft();
        final double yDiff = target.posY - this.mc.thePlayer.posY;
        Minecraft.getMinecraft();
        final double zDiff = target.posZ - this.mc.thePlayer.posZ;
        final float yaw = (float)(Math.atan2(zDiff, xDiff) * 180.0 / 3.141592653589793) - 90.0f;
        Minecraft.getMinecraft();
        Minecraft.getMinecraft();
        float pitch = (float)(-Math.atan2(target.posY + target.getEyeHeight() / 0.0 - (this.mc.thePlayer.posY + this.mc.thePlayer.getEyeHeight()), Math.hypot(xDiff, zDiff)) * 180.0 / 3.141592653589793);
        if (yDiff > -0.2 && yDiff < 0.2) {
            Minecraft.getMinecraft();
            Minecraft.getMinecraft();
            pitch = (float)(-Math.atan2(target.posY + target.getEyeHeight() / HitLocation.CHEST.getOffset() - (this.mc.thePlayer.posY + this.mc.thePlayer.getEyeHeight()), Math.hypot(xDiff, zDiff)) * 180.0 / 3.141592653589793);
        }
        else if (yDiff > -0.2) {
            Minecraft.getMinecraft();
            Minecraft.getMinecraft();
            pitch = (float)(-Math.atan2(target.posY + target.getEyeHeight() / HitLocation.FEET.getOffset() - (this.mc.thePlayer.posY + this.mc.thePlayer.getEyeHeight()), Math.hypot(xDiff, zDiff)) * 180.0 / 3.141592653589793);
        }
        else if (yDiff < 0.3) {
            Minecraft.getMinecraft();
            Minecraft.getMinecraft();
            pitch = (float)(-Math.atan2(target.posY + target.getEyeHeight() / HitLocation.HEAD.getOffset() - (this.mc.thePlayer.posY + this.mc.thePlayer.getEyeHeight()), Math.hypot(xDiff, zDiff)) * 180.0 / 3.141592653589793);
        }
        return new float[] { yaw, pitch };
    }
    
    @EventHandler
    public void onRender3D2(final EventRender3D event) {
        if (this.mode2.getValue() == ESPMode.Vape) {
            final EntityLivingBase entity = KillAura.curTarget;
            this.mc.getRenderManager();
            final double x1 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * Helper.getTimer().renderPartialTicks - ((IRenderManager)this.mc.getRenderManager()).getRenderPosX();
            this.mc.getRenderManager();
            final double y1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * Helper.getTimer().renderPartialTicks - ((IRenderManager)this.mc.getRenderManager()).getRenderPosY();
            this.mc.getRenderManager();
            final double z1 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * Helper.getTimer().renderPartialTicks - ((IRenderManager)this.mc.getRenderManager()).getRenderPosZ();
            final double width = entity.getEntityBoundingBox().maxX - entity.getEntityBoundingBox().minX - 0.1;
            final double height = entity.getEntityBoundingBox().maxY - entity.getEntityBoundingBox().minY + 0.05;
            final float red = 220.0f - entity.hurtTime * 5 / 255.0f;
            final float green = entity.hurtTime * 10 / 255.0f;
            final float blue = entity.hurtTime * 2 / 255.0f;
            final float alpha = (80 + entity.hurtTime * 10) / 255.0f;
            final float red2 = 220.0f - entity.hurtTime * 5 / 255.0f;
            final float green2 = entity.hurtTime * 10 / 255.0f;
            final float blue2 = entity.hurtTime * 2 / 255.0f;
            final float alpha2 = (80 + entity.hurtTime * 10) / 500.0f;
            if (entity.hurtTime >= 1) {
                RenderUtil.drawEntityESP(x1, y1, z1, width, height, red, green, blue, alpha, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f);
            }
            else {
                RenderUtil.drawEntityESP(x1, y1, z1, width, height, red, green, blue, alpha2, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f);
            }
        }
        if (this.mode2.getValue() == ESPMode.Novoline) {
            RenderUtils.pre3D();
            GL11.glLineWidth(6.0f);
            GL11.glBegin(3);
            GL11.glColor4f(0.0f, 0.0f, 0.0f, this.alpha);
            for (double d = 0.0; d < 6.283185307179586; d += 0.12319971190548208) {
                final double x2 = this.mc.thePlayer.lastTickPosX + (this.mc.thePlayer.posX - this.mc.thePlayer.lastTickPosX) * event.getPartialTicks() + Math.sin(d) * this.rangeValue.getValue().doubleValue() - ((IRenderManager)this.mc.getRenderManager()).getRenderPosX();
                final double y2 = this.mc.thePlayer.lastTickPosY + (this.mc.thePlayer.posY - this.mc.thePlayer.lastTickPosY) * event.getPartialTicks() - ((IRenderManager)this.mc.getRenderManager()).getRenderPosY();
                final double z2 = this.mc.thePlayer.lastTickPosZ + (this.mc.thePlayer.posZ - this.mc.thePlayer.lastTickPosZ) * event.getPartialTicks() + Math.cos(d) * this.rangeValue.getValue().doubleValue() - ((IRenderManager)this.mc.getRenderManager()).getRenderPosZ();
                GL11.glVertex3d(x2, y2, z2);
            }
            GL11.glEnd();
            GL11.glLineWidth(3.0f);
            GL11.glBegin(3);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            for (double d = 0.0; d < 6.283185307179586; d += 0.12319971190548208) {
                final double x2 = this.mc.thePlayer.lastTickPosX + (this.mc.thePlayer.posX - this.mc.thePlayer.lastTickPosX) * event.getPartialTicks() + Math.sin(d) * this.rangeValue.getValue().doubleValue() - ((IRenderManager)this.mc.getRenderManager()).getRenderPosX();
                final double y2 = this.mc.thePlayer.lastTickPosY + (this.mc.thePlayer.posY - this.mc.thePlayer.lastTickPosY) * event.getPartialTicks() - ((IRenderManager)this.mc.getRenderManager()).getRenderPosY();
                final double z2 = this.mc.thePlayer.lastTickPosZ + (this.mc.thePlayer.posZ - this.mc.thePlayer.lastTickPosZ) * event.getPartialTicks() + Math.cos(d) * this.rangeValue.getValue().doubleValue() - ((IRenderManager)this.mc.getRenderManager()).getRenderPosZ();
                GL11.glVertex3d(x2, y2, z2);
            }
            GL11.glEnd();
            RenderUtils.post3D();
        }
    }
    
    public static EntityLivingBase getTarget() {
        return KillAura.curTarget;
    }
    
    public static EntityLivingBase getBlockTarget() {
        return KillAura.blockTarget;
    }
    
    public static double getRandomDoubleInRange(final double minDouble, final double maxDouble) {
        return (minDouble >= maxDouble) ? minDouble : (new Random().nextDouble() * (maxDouble - minDouble) + minDouble);
    }
    
    public float getIYaw() {
        return this.iyaw;
    }
    
    public float getIPitch() {
        return this.ipitch;
    }
    
    static {
        playerValue = new Option<Boolean>("Player", "Player", true);
        animalValue = new Option<Boolean>("Animal", "Animal", false);
        monsterValue = new Option<Boolean>("Monster", "Monster", false);
        neutralValue = new Option<Boolean>("Neutral", "Neutral", false);
        attackValue = new Option<Boolean>("Post", "Post", false);
        preferValue = new Option<Boolean>("Prefer", "Prefer", false);
        deathValue = new Option<Boolean>("Death", "Death", true);
        multi = new Option<Boolean>("Multi", "Multi", false);
        invisibleValue = new Option<Boolean>("Invisible", "invisible", false);
        throughWallValue = new Option<Boolean>("ThroughWall", "ThroughWall", false);
        RotValue = new Option<Boolean>("Rotations", "Rotations", true);
        preventFlagValue = new Option<Boolean>("Flag", "Flag", true);
        autoBlockValue = new Option<Boolean>("AutoBlock", "AutoBlock", false);
    }
    
    enum priority
    {
        Range, 
        Fov, 
        Angle, 
        Health, 
        Armor, 
        Slowly;
    }
    
    enum AuraMode
    {
        Switch, 
        Single;
    }
    
    enum ESPMode
    {
        None, 
        Novoline, 
        Vape;
    }
    
    enum HitLocation
    {
        AUTO(0.0), 
        HEAD(1.0), 
        CHEST(1.5), 
        FEET(3.5);
        
        private double offset;
        
        private HitLocation(final double offset) {
            this.offset = offset;
        }
        
        public double getOffset() {
            return this.offset;
        }
    }
}
