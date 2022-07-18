package buzz.gaoyusense.injection.mixins;

import acid.module.modules.render.NoFov;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraft.world.World;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraft.client.renderer.GlStateManager;
import acid.module.Module;
import acid.module.modules.render.ViewClip;
import org.spongepowered.asm.mixin.Overwrite;
import net.minecraft.util.AxisAlignedBB;
import java.util.List;
import net.minecraft.util.Vec3;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import com.google.common.base.Predicates;
import net.minecraft.util.EntitySelectors;
import acid.module.modules.combat.Reach;
import acid.management.ModuleManager;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import acid.api.EventBus;
import acid.api.events.rendering.EventRender3D;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.entity.Entity;
import net.minecraft.client.Minecraft;
import java.nio.FloatBuffer;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import buzz.gaoyusense.injection.interfaces.IEntityRenderer;

@Mixin({ EntityRenderer.class })
public abstract class MixinEntityRenderer implements IEntityRenderer
{
    @Shadow
    public static int shaderCount;
    @Shadow
    private static Logger logger;
    private final float thirdPersonDistance = 4.0f;
    private final float thirdPersonDistanceTemp = 4.0f;
    @Shadow
    private final FloatBuffer fogColorBuffer;
    private boolean cloudFog;
    @Shadow
    private Minecraft mc;
    @Shadow
    private Entity pointedEntity;
    @Shadow
    private int shaderIndex;
    @Shadow
    private boolean useShader;
    @Shadow
    private ShaderGroup theShaderGroup;
    @Shadow
    private IResourceManager resourceManager;
    @Shadow
    private float fogColorRed;
    @Shadow
    private float fogColorGreen;
    @Shadow
    private float fogColorBlue;
    @Shadow
    private float farPlaneDistance;
    @Shadow
    private float fovModifierHandPrev;
    @Shadow
    private float bossColorModifier;
    @Shadow
    private float fovModifierHand;
    @Shadow
    private float bossColorModifierPrev;
    
    public MixinEntityRenderer() {
        this.fogColorBuffer = GLAllocation.createDirectFloatBuffer(16);
    }
    
    @Shadow
    protected abstract void setupCameraTransform(final float p0, final int p1);
    
    @Override
    public void runSetupCameraTransform(final float partialTicks, final int pass) {
        this.setupCameraTransform(partialTicks, pass);
    }
    
    @Override
    public void loadShader2(final ResourceLocation resourceLocationIn) {
        if (OpenGlHelper.isFramebufferEnabled()) {
            try {
                (this.theShaderGroup = new ShaderGroup(this.mc.getTextureManager(), this.resourceManager, this.mc.getFramebuffer(), resourceLocationIn)).createBindFramebuffers(this.mc.displayWidth, this.mc.displayHeight);
                this.useShader = true;
            }
            catch (IOException ioexception) {
                MixinEntityRenderer.logger.warn("Failed to load shader: " + resourceLocationIn, (Throwable)ioexception);
                this.shaderIndex = MixinEntityRenderer.shaderCount;
                this.useShader = false;
            }
            catch (JsonSyntaxException jsonsyntaxexception) {
                MixinEntityRenderer.logger.warn("Failed to load shader: " + resourceLocationIn, (Throwable)jsonsyntaxexception);
                this.shaderIndex = MixinEntityRenderer.shaderCount;
                this.useShader = false;
            }
        }
    }
    
    @Inject(method = "renderWorldPass", at = { @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/EntityRenderer;renderHand:Z", shift = At.Shift.BEFORE) })
    private void renderWorldPass(final int pass, final float partialTicks, final long finishTimeNano, final CallbackInfo callbackInfo) {
        final EventRender3D eventRender = new EventRender3D(partialTicks);
        EventBus.getInstance().call(eventRender);
    }
    
    @Overwrite
    public void getMouseOver(final float p_getMouseOver_1_) {
        final Entity entity = this.mc.getRenderViewEntity();
        if (entity != null && this.mc.theWorld != null) {
            this.mc.mcProfiler.startSection("pick");
            this.mc.pointedEntity = null;
            double d0 = ModuleManager.getModuleByName("Reach").isEnabled() ? Reach.getReach() : this.mc.playerController.getBlockReachDistance();
            this.mc.objectMouseOver = entity.rayTrace(ModuleManager.getModuleByName("Reach").isEnabled() ? Reach.getReach() : d0, p_getMouseOver_1_);
            double d2 = d0;
            final Vec3 vec3 = entity.getPositionEyes(p_getMouseOver_1_);
            boolean flag = false;
            if (this.mc.playerController.extendedReach()) {
                d0 = 6.0;
                d2 = 6.0;
            }
            else if (d0 > 3.0) {
                flag = true;
            }
            if (this.mc.objectMouseOver != null) {
                d2 = this.mc.objectMouseOver.hitVec.distanceTo(vec3);
            }
            if (ModuleManager.getModuleByName("Reach").isEnabled()) {
                d2 = Reach.getReach();
                final MovingObjectPosition vec4 = entity.rayTrace(d2, p_getMouseOver_1_);
                if (vec4 != null) {
                    d2 = vec4.hitVec.distanceTo(vec3);
                }
            }
            final Vec3 var24 = entity.getLook(p_getMouseOver_1_);
            final Vec3 vec5 = vec3.addVector(var24.xCoord * d0, var24.yCoord * d0, var24.zCoord * d0);
            this.pointedEntity = null;
            Vec3 vec6 = null;
            final float f = 1.0f;
            final List list = this.mc.theWorld.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().addCoord(var24.xCoord * d0, var24.yCoord * d0, var24.zCoord * d0).expand((double)f, (double)f, (double)f), Predicates.and(EntitySelectors.NOT_SPECTATING, p_apply_1_ -> p_apply_1_.canBeCollidedWith()));
            double d3 = d2;
            for (int j = 0; j < list.size(); ++j) {
                final Entity entity2 = (Entity) list.get(j);
                final float f2 = entity2.getCollisionBorderSize();
                final AxisAlignedBB axisalignedbb = entity2.getEntityBoundingBox().expand((double)f2, (double)f2, (double)f2);
                final MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec5);
                if (axisalignedbb.isVecInside(vec3)) {
                    if (d3 >= 0.0) {
                        this.pointedEntity = entity2;
                        vec6 = ((movingobjectposition == null) ? vec3 : movingobjectposition.hitVec);
                        d3 = 0.0;
                    }
                }
                else if (movingobjectposition != null) {
                    final double d4 = vec3.distanceTo(movingobjectposition.hitVec);
                    if (d4 < d3 || d3 == 0.0) {
                        if (entity2 == entity.ridingEntity) {
                            if (d3 == 0.0) {
                                this.pointedEntity = entity2;
                                vec6 = movingobjectposition.hitVec;
                            }
                        }
                        else {
                            this.pointedEntity = entity2;
                            vec6 = movingobjectposition.hitVec;
                            d3 = d4;
                        }
                    }
                }
            }
            if (this.pointedEntity != null && flag && vec3.distanceTo(vec6) > (ModuleManager.getModuleByName("Reach").isEnabled() ? Reach.getReach() : 3.0)) {
                this.pointedEntity = null;
                this.mc.objectMouseOver = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, vec6, (EnumFacing)null, new BlockPos(vec6));
            }
            if (this.pointedEntity != null && (d3 < d2 || this.mc.objectMouseOver == null)) {
                this.mc.objectMouseOver = new MovingObjectPosition(this.pointedEntity, vec6);
                if (this.pointedEntity instanceof EntityLivingBase || this.pointedEntity instanceof EntityItemFrame) {
                    this.mc.pointedEntity = this.pointedEntity;
                }
            }
            this.mc.mcProfiler.endSection();
        }
    }
    
    @Inject(method = "hurtCameraEffect", at = { @At("HEAD") }, cancellable = true)
    private void injectHurtCameraEffect(final CallbackInfo callbackInfo) {
        if (ModuleManager.getModuleByName("NoHurtCam").isEnabled()) {
            callbackInfo.cancel();
        }
    }
    
    @Inject(method = "orientCamera", at = { @At(value = "INVOKE", target = "Lnet/minecraft/util/Vec3;distanceTo(Lnet/minecraft/util/Vec3;)D") }, cancellable = true)
    private void cameraClip(final float partialTicks, final CallbackInfo callbackInfo) {
        if (ModuleManager.getModuleByClass(ViewClip.class).isEnabled()) {
            callbackInfo.cancel();
            final Entity entity = this.mc.getRenderViewEntity();
            float f = entity.getEyeHeight();
            if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isPlayerSleeping()) {
                ++f;
                GlStateManager.translate(0.0f, 0.3f, 0.0f);
                if (!this.mc.gameSettings.debugCamEnable) {
                    final BlockPos blockpos = new BlockPos(entity);
                    final IBlockState iblockstate = this.mc.theWorld.getBlockState(blockpos);
                    ForgeHooksClient.orientBedCamera((IBlockAccess)this.mc.theWorld, blockpos, iblockstate, entity);
                    GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks + 180.0f, 0.0f, -1.0f, 0.0f);
                    GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, -1.0f, 0.0f, 0.0f);
                }
            }
            else if (this.mc.gameSettings.thirdPersonView > 0) {
                this.getClass();
                final float n = 4.0f;
                this.getClass();
                final float n2 = 4.0f;
                this.getClass();
                final double d3 = n + (n2 - 4.0f) * partialTicks;
                if (this.mc.gameSettings.debugCamEnable) {
                    GlStateManager.translate(0.0f, 0.0f, (float)(Object)(-ViewClip.N.getValue()));
                }
                else {
                    final float f2 = entity.rotationYaw;
                    float f3 = entity.rotationPitch;
                    if (this.mc.gameSettings.thirdPersonView == 2) {
                        f3 += 180.0f;
                    }
                    if (this.mc.gameSettings.thirdPersonView == 2) {
                        GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0.0f);
                    }
                    GlStateManager.rotate(entity.rotationPitch - f3, 1.0f, 0.0f, 0.0f);
                    GlStateManager.rotate(entity.rotationYaw - f2, 0.0f, 1.0f, 0.0f);
                    GlStateManager.translate(0.0f, 0.0f, (float)(Object)(-ViewClip.N.getValue()));
                    GlStateManager.rotate(f2 - entity.rotationYaw, 0.0f, 1.0f, 0.0f);
                    GlStateManager.rotate(f3 - entity.rotationPitch, 1.0f, 0.0f, 0.0f);
                }
            }
            else {
                GlStateManager.translate(0.0f, 0.0f, -0.1f);
            }
            if (!this.mc.gameSettings.debugCamEnable) {
                float yaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks + 180.0f;
                final float pitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
                final float roll = 0.0f;
                if (entity instanceof EntityAnimal) {
                    final EntityAnimal entityanimal = (EntityAnimal)entity;
                    yaw = entityanimal.prevRotationYawHead + (entityanimal.rotationYawHead - entityanimal.prevRotationYawHead) * partialTicks + 180.0f;
                }
                final Block block = ActiveRenderInfo.getBlockAtEntityViewpoint((World)this.mc.theWorld, entity, partialTicks);
                final EntityViewRenderEvent.CameraSetup event = new EntityViewRenderEvent.CameraSetup((EntityRenderer)((Object)this), entity, block, (double)partialTicks, yaw, pitch, roll);
                MinecraftForge.EVENT_BUS.post((Event)event);
                GlStateManager.rotate(event.roll, 0.0f, 0.0f, 1.0f);
                GlStateManager.rotate(event.pitch, 1.0f, 0.0f, 0.0f);
                GlStateManager.rotate(event.yaw, 0.0f, 1.0f, 0.0f);
            }
            GlStateManager.translate(0.0f, -f, 0.0f);
            final double d4 = entity.prevPosX + (entity.posX - entity.prevPosX) * partialTicks;
            final double d5 = entity.prevPosY + (entity.posY - entity.prevPosY) * partialTicks + f;
            final double d6 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * partialTicks;
            this.cloudFog = this.mc.renderGlobal.hasCloudFog(d4, d5, d6, partialTicks);
        }
    }
    
    @Overwrite
    private void updateFovModifierHand() {
        float f = 1.0f;
        if (this.mc.getRenderViewEntity() instanceof AbstractClientPlayer) {
            final AbstractClientPlayer abstractclientplayer = (AbstractClientPlayer)this.mc.getRenderViewEntity();
            f = abstractclientplayer.getFovModifier();
        }
        this.fovModifierHandPrev = this.fovModifierHand;
        this.fovModifierHand += (f - this.fovModifierHand) * 0.5f;
        if (this.fovModifierHand > 1.5f) {
            this.fovModifierHand = 1.5f;
        }
        if (this.fovModifierHand < 0.1f) {
            this.fovModifierHand = 0.1f;
        }
        if (ModuleManager.getModuleByName("NoFov").isEnabled()) {
            this.fovModifierHand = (float)(Object)NoFov.fovspoof.getValue();
        }
    }
}
