package acid.module.modules.render;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.Tessellator;
import acid.api.EventHandler;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.entity.AbstractClientPlayer;
import org.lwjgl.opengl.GL11;
import acid.ui.font.FontLoaders;
import acid.utils.render.RenderUtils;
import acid.utils.render.RenderUtil;
import java.awt.Color;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.gui.GuiChat;
import acid.module.modules.combat.KillAura;
import acid.management.ModuleManager;
import acid.Client;
import acid.api.events.rendering.EventRender2D;
import acid.api.value.Value;
import java.util.HashMap;
import acid.module.ModuleType;
import acid.api.value.Numbers;
import net.minecraft.entity.EntityLivingBase;
import java.util.Map;
import acid.api.value.Mode;
import acid.utils.timer.TimerUtil;
import acid.module.Module;

public final class TargetHUD extends Module
{
    private final TimerUtil animationStopwatch;
    private double healthBarWidth;
    private float lastHealth;
    public final Mode<Rape> mode;
    private final Map<EntityLivingBase, Double> entityDamageMap;
    private final Map<EntityLivingBase, Integer> entityArmorCache;
    private int animWidth;
    double r2;
    public static Numbers<Double> xn;
    public static Numbers<Double> yn;
    
    public TargetHUD() {
        super("TargetHUD", new String[] { "targetInformationHud" }, ModuleType.Render);
        this.animationStopwatch = new TimerUtil();
        this.lastHealth = 0.0f;
        this.mode = new Mode<Rape>("TargetHud Mode", Rape.values(), Rape.Moon);
        this.entityDamageMap = new HashMap<EntityLivingBase, Double>();
        this.entityArmorCache = new HashMap<EntityLivingBase, Integer>();
        this.addValues(this.mode, TargetHUD.xn, TargetHUD.yn);
    }
    
    @EventHandler
    public void onRender2D(final EventRender2D event) {
        this.setSuffix(this.mode.getValue().name());
        final FontRenderer fr = this.mc.fontRendererObj;
        final Client instance = Client.instance;
        Client.getModuleManager();
        final HUD hud = (HUD)ModuleManager.getModuleByClass(HUD.class);
        final Client instance2 = Client.instance;
        Client.getModuleManager();
        final KillAura killAura = (KillAura)ModuleManager.getModuleByClass(KillAura.class);
        final boolean guichat = this.mc.currentScreen instanceof GuiChat;
        final ScaledResolution scaledResolution = event.getSR();
        final int var141 = scaledResolution.getScaledWidth();
        final int var142 = scaledResolution.getScaledHeight();
        final float n = 2.0f;
        final int x = (int)(Object)TargetHUD.xn.getValue();
        final int y = (int)(Object)TargetHUD.yn.getValue();
        final EntityLivingBase target = (EntityLivingBase)(guichat ? this.mc.thePlayer : KillAura.curTarget);
        if (target != null) {
            final float health1 = target.getHealth();
            final float maxHealth1 = target.getMaxHealth();
            float healthPercentage11 = health1 / maxHealth1;
            switch (this.mode.getValue()) {
                case Moon: {
                    if (target instanceof EntityPlayer) {
                        GlStateManager.pushMatrix();
                        GlStateManager.translate((double)x, (double)y, 0.0);
                        final int n2 = 200;
                        final int n3 = 160;
                        RenderUtil.drawRoundedRect2(n2 - 72.0f, n3 + 35.0f, n2 - 30.0f + 78.0f, n3 + 75.0f, 6.0, new Color(20, 35, 37, 200).getRGB());
                        final float health2 = target.getHealth();
                        healthPercentage11 = health2 / target.getMaxHealth();
                        float scaledWidth = 0.0f;
                        if (healthPercentage11 != this.lastHealth) {
                            final float scaledHeight = healthPercentage11 - this.lastHealth;
                            scaledWidth = this.lastHealth;
                            this.lastHealth += scaledHeight / 20.0f;
                        }
                        Color healthcolor = Color.WHITE;
                        if (healthPercentage11 * 100.0f > 75.0f) {
                            healthcolor = Color.GREEN;
                        }
                        else if (healthPercentage11 * 100.0f > 50.0f && healthPercentage11 * 100.0f < 75.0f) {
                            healthcolor = Color.YELLOW;
                        }
                        else if (healthPercentage11 * 100.0f < 50.0f && healthPercentage11 * 100.0f > 25.0f) {
                            healthcolor = Color.ORANGE;
                        }
                        else if (healthPercentage11 * 100.0f < 25.0f) {
                            healthcolor = Color.RED;
                        }
                        RenderUtils.drawRect(n2 - 62.0f, n3 + 62.0f, n2 - 62.0f + 108.0f * scaledWidth, n3 + 63.6f, healthcolor.getRGB());
                        final float armorValue = target.getTotalArmorValue();
                        final double armorWidth = armorValue / 20.0;
                        FontLoaders.logo10.drawStringWithShadow("r", n2 - 69.0f, n3 + 70.0f, new Color(87, 160, 250).getRGB());
                        FontLoaders.logo10.drawStringWithShadow("s", n2 - 69.0f, n3 + 63.0f, new Color(255, 85, 85).getRGB());
                        RenderUtils.drawRect(n2 - 62.0f, n3 + 69.0f, n2 - 62.0f + 108.0 * armorWidth, n3 + 70.6f, new Color(80, 120, 255).getRGB());
                        FontLoaders.kiona17.drawStringWithShadow(((EntityPlayer)target).getGameProfile().getName(), n2 - 47, n3 + 40, -1);
                        FontLoaders.kiona14.drawString("Blocking: " + ((EntityPlayer)target).isBlocking(), n2 - 47, n3 + 51, -1);
                        final float hurtPercent = target.hurtTime;
                        float scale;
                        if (hurtPercent == 0.0f) {
                            scale = 1.0f;
                        }
                        else if (hurtPercent < 0.5f) {
                            scale = 1.0f - 0.2f * hurtPercent * 1.0f;
                        }
                        else {
                            scale = 0.8f + 0.2f * (hurtPercent - 0.5f) * 0.1f;
                        }
                        final int size = 22;
                        GL11.glPushMatrix();
                        GL11.glTranslatef((float)(n2 - 70), (float)(n3 + 37), 0.0f);
                        GL11.glScalef(scale, scale, scale);
                        GL11.glTranslatef(size * 0.5f * (1.0f - scale) / scale, size * 0.5f * (1.0f - scale) / scale, 0.0f);
                        GL11.glColor4f(1.0f, 1.0f - hurtPercent, 1.0f - hurtPercent, 1.0f);
                        this.quickDrawHead(((AbstractClientPlayer)target).getLocationSkin(), 0, 0, size, size);
                        GL11.glPopMatrix();
                        GL11.glPopMatrix();
                        break;
                    }
                    break;
                }
            }
        }
    }
    
    public static void drawScaledCustomSizeModalRect(final int x, final int y, final float u, final float v, final int uWidth, final int vHeight, final int width, final int height, final float tileWidth, final float tileHeight) {
        final float f = 1.0f / tileWidth;
        final float f2 = 1.0f / tileHeight;
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos((double)x, (double)(y + height), 0.0).tex((double)(u * f), (double)((v + vHeight) * f2)).endVertex();
        worldrenderer.pos((double)(x + width), (double)(y + height), 0.0).tex((double)((u + uWidth) * f), (double)((v + vHeight) * f2)).endVertex();
        worldrenderer.pos((double)(x + width), (double)y, 0.0).tex((double)((u + uWidth) * f), (double)(v * f2)).endVertex();
        worldrenderer.pos((double)x, (double)y, 0.0).tex((double)(u * f), (double)(v * f2)).endVertex();
        tessellator.draw();
    }
    
    public void quickDrawHead(final ResourceLocation skin, final int x, final int y, final int width, final int height) {
        this.mc.getTextureManager().bindTexture(skin);
        drawScaledCustomSizeModalRect(x, y, 8.0f, 8.0f, 8, 8, width, height, 64.0f, 64.0f);
        drawScaledCustomSizeModalRect(x, y, 40.0f, 8.0f, 8, 8, width, height, 64.0f, 64.0f);
    }
    
    public void drawFace(final double x, final double y, final float u, final float v, final int uWidth, final int vHeight, final int width, final int height, final float tileWidth, final float tileHeight, final AbstractClientPlayer target) {
        try {
            final ResourceLocation skin = target.getLocationSkin();
            Minecraft.getMinecraft().getTextureManager().bindTexture(skin);
            GL11.glEnable(3042);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            Gui.drawScaledCustomSizeModalRect((int)x, (int)y, u, v, uWidth, vHeight, width, height, tileWidth, tileHeight);
            GL11.glDisable(3042);
        }
        catch (Exception var15) {
            var15.printStackTrace();
        }
    }
    
    public static void drawEntityOnScreen(final int posX, final int posY, final int scale, final float mouseX, final float mouseY, final EntityLivingBase ent) {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)posX, (float)posY, 50.0f);
        GlStateManager.scale((float)(-scale), (float)scale, (float)scale);
        GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f);
        final float f = ent.renderYawOffset;
        final float f2 = ent.rotationYaw;
        final float f3 = ent.rotationPitch;
        final float f4 = ent.prevRotationYawHead;
        final float f5 = ent.rotationYawHead;
        GlStateManager.rotate(135.0f, 0.0f, 1.0f, 0.0f);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-(float)Math.atan(mouseY / 40.0f) * 20.0f, 1.0f, 0.0f, 0.0f);
        ent.renderYawOffset = (float)Math.atan(mouseX / 40.0f) * 20.0f;
        ent.rotationYaw = (float)Math.atan(mouseX / 40.0f) * 40.0f;
        ent.rotationPitch = -(float)Math.atan(mouseY / 40.0f) * 20.0f;
        ent.rotationYawHead = ent.rotationYaw;
        ent.prevRotationYawHead = ent.rotationYaw;
        GlStateManager.translate(0.0f, 0.0f, 0.0f);
        final RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setPlayerViewY(180.0f);
        rendermanager.setRenderShadow(false);
        rendermanager.renderEntityWithPosYaw((Entity)ent, 0.0, 0.0, 0.0, 0.0f, 1.0f);
        rendermanager.setRenderShadow(true);
        ent.renderYawOffset = f;
        ent.rotationYaw = f2;
        ent.rotationPitch = f3;
        ent.prevRotationYawHead = f4;
        ent.rotationYawHead = f5;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
    }
    
    @Override
    public void onDisable() {
        super.onDisable();
    }
    
    private static int alphaToInt(final float alpha, final int offset) {
        return Math.min(255, (int)Math.ceil(alpha * 255.0f) + offset);
    }
    
    static {
        TargetHUD.xn = new Numbers<Double>("X", "X", 20.0, -1000.0, 1920.0, 10.0);
        TargetHUD.yn = new Numbers<Double>("Y", "Y", 10.0, -1000.0, 1080.0, 10.0);
    }
    
    public enum Rape
    {
        Moon;
    }
}
