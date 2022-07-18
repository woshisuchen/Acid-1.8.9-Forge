package buzz.gaoyusense.injection.mixins;

import org.spongepowered.asm.mixin.Overwrite;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;
import acid.module.modules.combat.KillAura;
import org.lwjgl.input.Keyboard;
import acid.utils.Helper;
import acid.utils.PlayerUtils;
import acid.module.modules.movement.Scaffold;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.MathHelper;
import net.minecraft.client.renderer.GlStateManager;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import acid.module.Module;
import acid.management.ModuleManager;
import acid.module.modules.render.Nametags;
import acid.Client;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.Final;
import org.apache.logging.log4j.Logger;
import net.minecraft.client.model.ModelBase;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import buzz.gaoyusense.injection.interfaces.IRendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;

@Mixin({ RendererLivingEntity.class })
public abstract class MixinRendererLivingEntity<T extends EntityLivingBase> extends MixinRender implements IRendererLivingEntity
{
    @Shadow
    protected boolean renderOutlines;
    @Shadow
    protected ModelBase mainModel;
    @Shadow
    @Final
    private static Logger logger;
    
    public MixinRendererLivingEntity() {
        this.renderOutlines = false;
    }
    
    @Inject(method = "canRenderName", at = { @At("HEAD") }, cancellable = true)
    private <T extends EntityLivingBase> void canRenderName(final T entity, final CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        Client.getModuleManager();
        if (ModuleManager.getModuleByClass(Nametags.class).isEnabled()) {
            callbackInfoReturnable.setReturnValue(false);
        }
    }
    
    @Shadow
    protected abstract float interpolateRotation(final float p0, final float p1, final float p2);
    
    @Shadow
    protected abstract float getSwingProgress(final T p0, final float p1);
    
    @Shadow
    protected abstract void renderLivingAt(final T p0, final double p1, final double p2, final double p3);
    
    @Shadow
    protected abstract void rotateCorpse(final T p0, final float p1, final float p2, final float p3);
    
    @Shadow
    protected abstract float handleRotationFloat(final T p0, final float p1);
    
    @Shadow
    protected abstract void preRenderCallback(final T p0, final float p1);
    
    @Shadow
    protected abstract boolean setScoreTeamColor(final EntityLivingBase p0);
    
    @Shadow
    protected abstract void unsetScoreTeamColor();
    
    @Shadow
    protected abstract void renderModel(final T p0, final float p1, final float p2, final float p3, final float p4, final float p5, final float p6);
    
    @Shadow
    protected abstract void renderLayers(final T p0, final float p1, final float p2, final float p3, final float p4, final float p5, final float p6, final float p7);
    
    @Shadow
    protected abstract boolean setDoRenderBrightness(final T p0, final float p1);
    
    @Shadow
    protected abstract void unsetBrightness();
    
    @Override
    public void doRenderModel(final Object entitylivingbaseIn, final float a, final float b, final float c, final float d, final float e, final float scaleFactor) {
        this.renderModel((T)entitylivingbaseIn, a, b, c, d, e, scaleFactor);
    }
    
    @Override
    public void doRenderLayers(final Object entitylivingbaseIn, final float a, final float b, final float partialTicks, final float d, final float e, final float f, final float g) {
        this.renderLayers((T)entitylivingbaseIn, a, b, partialTicks, d, e, f, g);
    }
    
    @Overwrite
    public void doRender(final T entity, final double x, final double y, final double z, final float entityYaw, final float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        this.mainModel.swingProgress = this.getSwingProgress(entity, partialTicks);
        this.mainModel.isRiding = entity.isRiding();
        this.mainModel.isChild = entity.isChild();
        try {
            float f = this.interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks);
            final float f2 = this.interpolateRotation(entity.prevRotationYawHead, entity.rotationYawHead, partialTicks);
            float f3 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
            float f4 = f2 - f;
            if (this.mainModel.isRiding && entity.ridingEntity instanceof EntityLivingBase) {
                final EntityLivingBase entitylivingbase = (EntityLivingBase)entity.ridingEntity;
                f = this.interpolateRotation(entitylivingbase.prevRenderYawOffset, entitylivingbase.renderYawOffset, partialTicks);
                f4 = f2 - f;
                float f5 = MathHelper.wrapAngleTo180_float(f4);
                if (f5 < -85.0f) {
                    f5 = -85.0f;
                }
                if (f5 >= 85.0f) {
                    f5 = 85.0f;
                }
                f = f2 - f5;
                if (f5 * f5 > 2500.0f) {
                    f += f5 * 0.2f;
                }
            }
            if (entity instanceof EntityPlayerSP) {
                Client.getModuleManager();
                if (ModuleManager.getModuleByClass(Scaffold.class).isEnabled()) {
                    Client.getModuleManager();
                    final Scaffold scaffold = (Scaffold)ModuleManager.getModuleByClass(Scaffold.class);
                    if (PlayerUtils.isMoving() || Keyboard.isKeyDown(Helper.mc.gameSettings.keyBindJump.getKeyCode())) {
                        f = this.interpolateRotation(scaffold.getYaw(), scaffold.getYaw(), partialTicks);
                        final float renderYaw = this.interpolateRotation(scaffold.getYaw(), scaffold.getYaw(), partialTicks) - f;
                        final float renderPitch = this.interpolateRotation(scaffold.getPitch(), scaffold.getPitch(), partialTicks);
                        f4 = renderYaw;
                        f3 = renderPitch;
                    }
                }
                else {
                    Client.getModuleManager();
                    if (ModuleManager.getModuleByClass(KillAura.class).isEnabled()) {
                        Client.getModuleManager();
                        final KillAura killAura = (KillAura)ModuleManager.getModuleByClass(KillAura.class);
                        if (KillAura.curTarget != null) {
                            f = this.interpolateRotation(killAura.getIYaw(), killAura.getIYaw(), partialTicks);
                            final float renderYaw = this.interpolateRotation(killAura.getPrevIYaw(), killAura.getIYaw(), partialTicks) - f;
                            final float renderPitch = this.interpolateRotation(killAura.getPrevIPitch(), killAura.getIPitch(), partialTicks);
                            f4 = renderYaw;
                            f3 = renderPitch;
                        }
                    }
                }
            }
            this.renderLivingAt(entity, x, y, z);
            final float f6 = this.handleRotationFloat(entity, partialTicks);
            this.rotateCorpse(entity, f6, f, partialTicks);
            GlStateManager.enableRescaleNormal();
            GlStateManager.scale(-1.0f, -1.0f, 1.0f);
            this.preRenderCallback(entity, partialTicks);
            GlStateManager.translate(0.0f, -1.5078125f, 0.0f);
            float f7 = entity.prevLimbSwingAmount + (entity.limbSwingAmount - entity.prevLimbSwingAmount) * partialTicks;
            float f8 = entity.limbSwing - entity.limbSwingAmount * (1.0f - partialTicks);
            if (entity.isChild()) {
                f8 *= 3.0f;
            }
            if (f7 > 1.0f) {
                f7 = 1.0f;
            }
            GlStateManager.enableAlpha();
            this.mainModel.setLivingAnimations((EntityLivingBase)entity, f8, f7, partialTicks);
            this.mainModel.setRotationAngles(f8, f7, f6, f4, f3, 0.0625f, (Entity)entity);
            if (this.renderOutlines) {
                final boolean flag1 = this.setScoreTeamColor(entity);
                this.renderModel(entity, f8, f7, f6, f4, f3, 0.0625f);
                if (flag1) {
                    this.unsetScoreTeamColor();
                }
            }
            else {
                final boolean flag2 = this.setDoRenderBrightness(entity, partialTicks);
                this.renderModel(entity, f8, f7, f6, f4, f3, 0.0625f);
                if (flag2) {
                    this.unsetBrightness();
                }
                GlStateManager.depthMask(true);
                if (!(entity instanceof EntityPlayer) || !((EntityPlayer)entity).isSpectator()) {
                    this.renderLayers(entity, f8, f7, partialTicks, f6, f4, f3, 0.0625f);
                }
            }
            GlStateManager.disableRescaleNormal();
        }
        catch (Exception exception) {
            MixinRendererLivingEntity.logger.error("Couldn't render entity", (Throwable)exception);
        }
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.enableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
        if (!this.renderOutlines) {
            super.doRender(entity, x, y, z, entityYaw, partialTicks);
        }
    }
}
