package buzz.gaoyusense.injection.mixins;

import net.minecraft.item.EnumAction;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Items;
import acid.module.Module;
import acid.management.ModuleManager;
import acid.Client;
import org.spongepowered.asm.mixin.Overwrite;
import net.minecraft.util.MathHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.entity.AbstractClientPlayer;
import acid.module.modules.render.Animation;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ ItemRenderer.class })
public abstract class MixinItemRenderer
{
    @Shadow
    private Minecraft mc;
    @Shadow
    private float equippedProgress;
    @Shadow
    private float prevEquippedProgress;
    @Shadow
    private ItemStack itemToRender;
    @Shadow
    private RenderManager renderManager;
    private Animation animations;
    
    @Shadow
    protected abstract void rotateArroundXAndY(final float p0, final float p1);
    
    @Shadow
    protected abstract void setLightMapFromPlayer(final AbstractClientPlayer p0);
    
    @Shadow
    protected abstract void rotateWithPlayerRotations(final EntityPlayerSP p0, final float p1);
    
    @Shadow
    protected abstract void renderItemMap(final AbstractClientPlayer p0, final float p1, final float p2, final float p3);
    
    @Shadow
    protected abstract void performDrinking(final AbstractClientPlayer p0, final float p1);
    
    @Shadow
    protected abstract void doBlockTransformations();
    
    @Shadow
    protected abstract void doBowTransformations(final float p0, final AbstractClientPlayer p1);
    
    @Shadow
    protected abstract void doItemUsedTransformations(final float p0);
    
    @Shadow
    public abstract void renderItem(final EntityLivingBase p0, final ItemStack p1, final ItemCameraTransforms.TransformType p2);
    
    @Shadow
    protected abstract void renderPlayerArm(final AbstractClientPlayer p0, final float p1, final float p2);
    
    @Overwrite
    private void transformFirstPersonItem(final float equipProgress, final float swingProgress) {
        this.doItemRenderGLTranslate();
        GlStateManager.translate(0.0f, equipProgress * -0.6f, 0.0f);
        GlStateManager.rotate(45.0f, 0.0f, 1.0f, 0.0f);
        final float f = MathHelper.sin(swingProgress * swingProgress * 3.1415927f);
        final float f2 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * 3.1415927f);
        GlStateManager.rotate(f * -20.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(f2 * -20.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotate(f2 * -80.0f, 1.0f, 0.0f, 0.0f);
        this.doItemRenderGLScale();
    }
    
    private void doItemRenderGLTranslate() {
        GlStateManager.translate(0.56f, -0.52f, -0.71999997f);
    }
    
    private void doItemRenderGLScale() {
        GlStateManager.scale(0.4f, 0.4f, 0.4f);
    }
    
    @Overwrite
    public void renderItemInFirstPerson(final float partialTicks) {
        float f = 1.0f - (this.prevEquippedProgress + (this.equippedProgress - this.prevEquippedProgress) * partialTicks);
        final AbstractClientPlayer abstractclientplayer = (AbstractClientPlayer)this.mc.thePlayer;
        final float f2 = abstractclientplayer.getSwingProgress(partialTicks);
        final float f3 = abstractclientplayer.prevRotationPitch + (abstractclientplayer.rotationPitch - abstractclientplayer.prevRotationPitch) * partialTicks;
        final float f4 = abstractclientplayer.prevRotationYaw + (abstractclientplayer.rotationYaw - abstractclientplayer.prevRotationYaw) * partialTicks;
        final float var2 = 1.0f - (this.prevEquippedProgress + (this.equippedProgress - this.prevEquippedProgress) * partialTicks);
        final EntityPlayerSP var3 = this.mc.thePlayer;
        final float var4 = var3.getSwingProgress(partialTicks);
        this.rotateArroundXAndY(f3, f4);
        this.setLightMapFromPlayer(abstractclientplayer);
        this.rotateWithPlayerRotations((EntityPlayerSP)abstractclientplayer, partialTicks);
        GlStateManager.enableRescaleNormal();
        GlStateManager.pushMatrix();
        if (this.itemToRender != null) {
            final Client instance = Client.instance;
            Client.getModuleManager();
            final Animation m = (Animation)ModuleManager.getModuleByClass(Animation.class);
            if (this.itemToRender.getItem() == Items.filled_map) {
                this.renderItemMap(abstractclientplayer, f3, f, f2);
            }
            else if (abstractclientplayer.getItemInUseCount() > 0) {
                final EnumAction enumaction = this.itemToRender.getItemUseAction();
                switch (enumaction) {
                    case NONE: {
                        this.transformFirstPersonItem(f, 0.0f);
                        break;
                    }
                    case EAT:
                    case DRINK: {
                        this.performDrinking(abstractclientplayer, partialTicks);
                        this.transformFirstPersonItem(f, m.isEnabled() ? f2 : 0.0f);
                        break;
                    }
                    case BLOCK: {
                        if (m.isEnabled()) {
                            GlStateManager.translate((double)m.blockHValue.getValue(), (double)m.blockVValue.getValue(), (double)m.blockSValue.getValue());
                            if (m.forceHeightValue.getValue()) {
                                f = 0.0f;
                            }
                            switch ((Animation.modeEnums)m.modeValue.getValue()) {
                                case None: {
                                    this.transformFirstPersonItem(f, 0.0f);
                                    this.doBlockTransformations();
                                    break;
                                }
                                case Old: {
                                    this.transformFirstPersonItem(f, f2);
                                    this.doBlockTransformations();
                                    GlStateManager.rotate(0.0f, 90.0f, -35.0f, 1.0f);
                                    GlStateManager.translate(-0.35f, 0.125f, 0.0f);
                                    GlStateManager.scale(0.75f, 0.75f, 0.75f);
                                    break;
                                }
                                case Push: {
                                    this.transformFirstPersonItem(f, 0.0f);
                                    this.doBlockTransformations();
                                    GlStateManager.rotate(-MathHelper.sin(MathHelper.sqrt_float(f2) * 3.1415927f) * 35.0f, -8.0f, -0.0f, 9.0f);
                                    GlStateManager.rotate(-MathHelper.sin(MathHelper.sqrt_float(f2) * 3.1415927f) * 10.0f, 1.0f, -0.4f, -0.5f);
                                    break;
                                }
                                case Slide: {
                                    this.transformFirstPersonItem(f, 0.0f);
                                    this.doBlockTransformations();
                                    GlStateManager.rotate(-MathHelper.sin(MathHelper.sqrt_float(f2) * 3.1415927f) * 40.0f, 1.0E-7f, 0.0f, 1.0E-7f);
                                    break;
                                }
                                case Jello: {
                                    this.transformFirstPersonItem(f, 0.0f);
                                    this.doBlockTransformations();
                                    GlStateManager.rotate(this.mc.thePlayer.isSwingInProgress ? (-Math.min(400L, ((System.currentTimeMillis() % 400L > 200L) ? Math.abs(Math.abs(System.currentTimeMillis()) % 400L - 400L) : (System.currentTimeMillis() % 400L)) * 2L) / 6.0f) : 1.0f, 1.0f, -1.0f, 1.0f);
                                    break;
                                }
                                case Rainy: {
                                    this.transformFirstPersonItem(f, 0.0f);
                                    this.doBlockTransformations();
                                    GlStateManager.rotate(-MathHelper.sin(MathHelper.sqrt_float(f2) * 3.1415927f) * 20.0f, -1.0f, 0.0f, 9.0f);
                                    GlStateManager.rotate(-MathHelper.sin(MathHelper.sqrt_float(f2) * 3.1415927f) * 10.0f, -1.0f, 0.0f, 9.0f);
                                    break;
                                }
                                case Swang: {
                                    this.transformFirstPersonItem(f, f2);
                                    GlStateManager.rotate(MathHelper.sin(MathHelper.sqrt_float(f2) * 3.1415927f) * 30.0f / 2.0f, -MathHelper.sin(MathHelper.sqrt_float(f2) * 3.1415927f), -0.0f, 9.0f);
                                    GlStateManager.rotate(MathHelper.sin(MathHelper.sqrt_float(f2) * 3.1415927f) * 40.0f, 1.0f, -MathHelper.sin(MathHelper.sqrt_float(f2) * 3.1415927f) / 2.0f, -0.0f);
                                    this.doBlockTransformations();
                                    break;
                                }
                                case Swong: {
                                    this.transformFirstPersonItem(f, 0.0f);
                                    GlStateManager.rotate(-MathHelper.sin(MathHelper.sqrt_float(f2) * 3.1415927f) * 20.0f, MathHelper.sin(MathHelper.sqrt_float(f2) * 3.1415927f) / 2.0f, -0.0f, 9.0f);
                                    GlStateManager.rotate(-MathHelper.sin(MathHelper.sqrt_float(f2) * 3.1415927f) * 30.0f, 1.0f, MathHelper.sin(MathHelper.sqrt_float(f2) * 3.1415927f) / 2.0f, 0.0f);
                                    this.doBlockTransformations();
                                    break;
                                }
                                case Swong2: {
                                    this.transformFirstPersonItem(f, 0.0f);
                                    GlStateManager.rotate(-MathHelper.sin(MathHelper.sqrt_float(f2) * 3.1415927f) * 40.0f, 0.0f, 0.0f, 9.0f);
                                    GlStateManager.rotate(-MathHelper.sin(MathHelper.sqrt_float(f2) * 3.1415927f) * 50.0f, 1.0f, MathHelper.sin(MathHelper.sqrt_float(f2) * 3.1415927f) / 2.0f, 0.0f);
                                    this.doBlockTransformations();
                                    break;
                                }
                                case Dislike: {
                                    this.transformFirstPersonItem(f, 0.0f);
                                    this.doBlockTransformations();
                                    GlStateManager.rotate(-MathHelper.sin(MathHelper.sqrt_float(f2) * 3.1415927f) * 70.0f / 2.0f, -8.0f, -0.0f, 9.0f);
                                    GlStateManager.rotate(-MathHelper.sin(MathHelper.sqrt_float(f2) * 3.1415927f) * 70.0f, 1.5f, -0.4f, -0.0f);
                                    break;
                                }
                                case Leaked: {
                                    final float scale = MathHelper.sin(MathHelper.sqrt_float(f2) * 3.1415927f) / 8.0f;
                                    this.transformFirstPersonItem(f, 0.0f);
                                    this.doBlockTransformations();
                                    GlStateManager.scale(0.8 + scale, 0.8 + scale, 0.8 + scale);
                                    GlStateManager.rotate(-MathHelper.sin((float)(MathHelper.sqrt_float(f2) * 3.141592653589793)) * 20.0f, 0.0f, 1.2f, -0.8f);
                                    GlStateManager.rotate(-MathHelper.sin((float)(MathHelper.sqrt_float(f2) * 3.141592653589793)) * 30.0f, 1.0f, 0.0f, 0.0f);
                                    break;
                                }
                            }
                            break;
                        }
                        this.transformFirstPersonItem(f, 0.0f);
                        this.doBlockTransformations();
                        break;
                    }
                    case BOW: {
                        this.transformFirstPersonItem(f, m.isEnabled() ? f2 : 0.0f);
                        this.doBowTransformations(partialTicks, abstractclientplayer);
                        break;
                    }
                }
            }
            else {
                if (m.isEnabled()) {
                    GlStateManager.translate((double)m.armHValue.getValue(), (double)m.armVValue.getValue(), (double)m.armSValue.getValue());
                }
                this.doItemUsedTransformations(f2);
                this.transformFirstPersonItem(f, f2);
            }
            this.renderItem((EntityLivingBase)abstractclientplayer, this.itemToRender, ItemCameraTransforms.TransformType.FIRST_PERSON);
        }
        else if (!abstractclientplayer.isInvisible()) {
            this.renderPlayerArm(abstractclientplayer, f, f2);
        }
        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
    }
}
