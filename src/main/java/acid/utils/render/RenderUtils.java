package acid.utils.render;

import buzz.gaoyusense.injection.interfaces.IRenderManager;
import net.minecraft.util.BlockPos;
import org.lwjgl.util.glu.Cylinder;
import net.minecraft.item.ItemStack;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.gui.Gui;
import java.awt.Color;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.Minecraft;

public final class RenderUtils
{
    private static final Minecraft mc;
    private static final Frustum frustrum;
    public static boolean SetCustomYaw;
    public static float CustomYaw;
    public static boolean SetCustomPitch;
    public static float CustomPitch;
    
    public static double interpolate(final double current, final double old, final double scale) {
        return old + (current - old) * scale;
    }
    
    public static boolean isInViewFrustrum(final Entity entity) {
        return isInViewFrustrum(entity.getEntityBoundingBox()) || entity.ignoreFrustumCheck;
    }
    
    private static boolean isInViewFrustrum(final AxisAlignedBB bb) {
        final Entity current = RenderUtils.mc.getRenderViewEntity();
        RenderUtils.frustrum.setPosition(current.posX, current.posY, current.posZ);
        return RenderUtils.frustrum.isBoundingBoxInFrustum(bb);
    }
    
    public static void drawImg(final ResourceLocation loc, final double posX, final double posY, final double width, final double height) {
        RenderUtils.mc.getTextureManager().bindTexture(loc);
        final float f = 1.0f / (float)width;
        final float f2 = 1.0f / (float)height;
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(posX, posY + height, 0.0).tex((double)(0.0f * f), (double)((0.0f + (float)height) * f2)).endVertex();
        worldrenderer.pos(posX + width, posY + height, 0.0).tex((double)((0.0f + (float)width) * f), (double)((0.0f + (float)height) * f2)).endVertex();
        worldrenderer.pos(posX + width, posY, 0.0).tex((double)((0.0f + (float)width) * f), (double)(0.0f * f2)).endVertex();
        worldrenderer.pos(posX, posY, 0.0).tex((double)(0.0f * f), (double)(0.0f * f2)).endVertex();
        tessellator.draw();
    }
    
    public static void drawGradientRect(final double left, final double top, final double right, final double bottom, final int startColor, final int endColor) {
        final float f = (startColor >> 24 & 0xFF) / 255.0f;
        final float f2 = (startColor >> 16 & 0xFF) / 255.0f;
        final float f3 = (startColor >> 8 & 0xFF) / 255.0f;
        final float f4 = (startColor & 0xFF) / 255.0f;
        final float f5 = (endColor >> 24 & 0xFF) / 255.0f;
        final float f6 = (endColor >> 16 & 0xFF) / 255.0f;
        final float f7 = (endColor >> 8 & 0xFF) / 255.0f;
        final float f8 = (endColor & 0xFF) / 255.0f;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(right, top, 0.0).color(f2, f3, f4, f).endVertex();
        worldrenderer.pos(left, top, 0.0).color(f2, f3, f4, f).endVertex();
        worldrenderer.pos(left, bottom, 0.0).color(f6, f7, f8, f5).endVertex();
        worldrenderer.pos(right, bottom, 0.0).color(f6, f7, f8, f5).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }
    
    public static void prepareScissorBox(final float x, final float y, final float x2, final float y2) {
        final ScaledResolution scale = new ScaledResolution(RenderUtils.mc);
        final int factor = scale.getScaleFactor();
        GL11.glScissor((int)(x * factor), (int)((scale.getScaledHeight() - y2) * factor), (int)((x2 - x) * factor), (int)((y2 - y) * factor));
    }
    
    public static void drawBorderedRect2(final float x, final float y, final float x2, final float y2, final float l1, final int col1, final int col2) {
        drawRect(x, y, x2, y2, col2);
        final float f = (col1 >> 24 & 0xFF) / 255.0f;
        final float f2 = (col1 >> 16 & 0xFF) / 255.0f;
        final float f3 = (col1 >> 8 & 0xFF) / 255.0f;
        final float f4 = (col1 & 0xFF) / 255.0f;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glPushMatrix();
        GL11.glColor4f(f2, f3, f4, f);
        GL11.glLineWidth(l1);
        GL11.glBegin(1);
        GL11.glVertex2d((double)x, (double)y);
        GL11.glVertex2d((double)x, (double)y2);
        GL11.glVertex2d((double)x2, (double)y2);
        GL11.glVertex2d((double)x2, (double)y);
        GL11.glVertex2d((double)x, (double)y);
        GL11.glVertex2d((double)x2, (double)y);
        GL11.glVertex2d((double)x, (double)y2);
        GL11.glVertex2d((double)x2, (double)y2);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
    }
    
    public static void startSmooth() {
        GL11.glEnable(2848);
        GL11.glEnable(2881);
        GL11.glEnable(2832);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
        GL11.glHint(3153, 4354);
    }
    
    public static void endSmooth() {
        GL11.glDisable(2848);
        GL11.glDisable(2881);
        GL11.glEnable(2832);
    }
    
    public static void drawMenuBackground(final ScaledResolution sr) {
        drawRect(0.0, 0.0, sr.getScaledWidth(), sr.getScaledHeight(), new Color(34, 34, 34).getRGB());
        drawImg(new ResourceLocation("client/images/background.png"), 0.0, 0.0, sr.getScaledWidth(), sr.getScaledHeight());
    }
    
    public static void drawBorderedRect(final double left, final double top, final double right, final double bottom, final double borderWidth, final int insideColor, final int borderColor, final boolean borderIncludedInBounds) {
        drawRect(left - (borderIncludedInBounds ? 0.0 : borderWidth), top - (borderIncludedInBounds ? 0.0 : borderWidth), right + (borderIncludedInBounds ? 0.0 : borderWidth), bottom + (borderIncludedInBounds ? 0.0 : borderWidth), borderColor);
        drawRect(left + (borderIncludedInBounds ? borderWidth : 0.0), top + (borderIncludedInBounds ? borderWidth : 0.0), right - (borderIncludedInBounds ? borderWidth : 0.0), bottom - (borderIncludedInBounds ? borderWidth : 0.0), insideColor);
    }
    
    public static void drawOutline(final double x, final double y, final double width, final double height, final double lineWidth, final int color) {
        drawRect(x, y, x + width, y + lineWidth, color);
        drawRect(x, y, x + lineWidth, y + height, color);
        drawRect(x, y + height - lineWidth, x + width, y + height, color);
        drawRect(x + width - lineWidth, y, x + width, y + height, color);
    }
    
    public static void drawGradientSideways(final double left, final double top, final double right, final double bottom, final int col1, final int col2) {
        final float f = (col1 >> 24 & 0xFF) / 255.0f;
        final float f2 = (col1 >> 16 & 0xFF) / 255.0f;
        final float f3 = (col1 >> 8 & 0xFF) / 255.0f;
        final float f4 = (col1 & 0xFF) / 255.0f;
        final float f5 = (col2 >> 24 & 0xFF) / 255.0f;
        final float f6 = (col2 >> 16 & 0xFF) / 255.0f;
        final float f7 = (col2 >> 8 & 0xFF) / 255.0f;
        final float f8 = (col2 & 0xFF) / 255.0f;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glShadeModel(7425);
        GL11.glPushMatrix();
        GL11.glBegin(7);
        GL11.glColor4f(f2, f3, f4, f);
        GL11.glVertex2d(left, top);
        GL11.glVertex2d(left, bottom);
        GL11.glColor4f(f6, f7, f8, f5);
        GL11.glVertex2d(right, bottom);
        GL11.glVertex2d(right, top);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
    }
    
    public static void drawRect(double left, double top, double right, double bottom, final int color) {
        if (left < right) {
            final double i = left;
            left = right;
            right = i;
        }
        if (top < bottom) {
            final double j = top;
            top = bottom;
            bottom = j;
        }
        final float f3 = (color >> 24 & 0xFF) / 255.0f;
        final float f4 = (color >> 16 & 0xFF) / 255.0f;
        final float f5 = (color >> 8 & 0xFF) / 255.0f;
        final float f6 = (color & 0xFF) / 255.0f;
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(f4, f5, f6, f3);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(left, bottom, 0.0).endVertex();
        worldrenderer.pos(right, bottom, 0.0).endVertex();
        worldrenderer.pos(right, top, 0.0).endVertex();
        worldrenderer.pos(left, top, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
    
    public static void drawRect(double left, double top, double right, double bottom, final float opacity) {
        if (left < right) {
            final double i = left;
            left = right;
            right = i;
        }
        if (top < bottom) {
            final double j = top;
            top = bottom;
            bottom = j;
        }
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(0.1f, 0.1f, 0.1f, opacity);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(left, bottom, 0.0).endVertex();
        worldrenderer.pos(right, bottom, 0.0).endVertex();
        worldrenderer.pos(right, top, 0.0).endVertex();
        worldrenderer.pos(left, top, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
    
    public static void drawBoundingBox(final AxisAlignedBB aa2) {
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa2.minX, aa2.minY, aa2.minZ).endVertex();
        worldRenderer.pos(aa2.minX, aa2.maxY, aa2.minZ).endVertex();
        worldRenderer.pos(aa2.maxX, aa2.minY, aa2.minZ).endVertex();
        worldRenderer.pos(aa2.maxX, aa2.maxY, aa2.minZ).endVertex();
        worldRenderer.pos(aa2.maxX, aa2.minY, aa2.maxZ).endVertex();
        worldRenderer.pos(aa2.maxX, aa2.maxY, aa2.maxZ).endVertex();
        worldRenderer.pos(aa2.minX, aa2.minY, aa2.maxZ).endVertex();
        worldRenderer.pos(aa2.minX, aa2.maxY, aa2.maxZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa2.maxX, aa2.maxY, aa2.minZ).endVertex();
        worldRenderer.pos(aa2.maxX, aa2.minY, aa2.minZ).endVertex();
        worldRenderer.pos(aa2.minX, aa2.maxY, aa2.minZ).endVertex();
        worldRenderer.pos(aa2.minX, aa2.minY, aa2.minZ).endVertex();
        worldRenderer.pos(aa2.minX, aa2.maxY, aa2.maxZ).endVertex();
        worldRenderer.pos(aa2.minX, aa2.minY, aa2.maxZ).endVertex();
        worldRenderer.pos(aa2.maxX, aa2.maxY, aa2.maxZ).endVertex();
        worldRenderer.pos(aa2.maxX, aa2.minY, aa2.maxZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa2.minX, aa2.maxY, aa2.minZ).endVertex();
        worldRenderer.pos(aa2.maxX, aa2.maxY, aa2.minZ).endVertex();
        worldRenderer.pos(aa2.maxX, aa2.maxY, aa2.maxZ).endVertex();
        worldRenderer.pos(aa2.minX, aa2.maxY, aa2.maxZ).endVertex();
        worldRenderer.pos(aa2.minX, aa2.maxY, aa2.minZ).endVertex();
        worldRenderer.pos(aa2.minX, aa2.maxY, aa2.maxZ).endVertex();
        worldRenderer.pos(aa2.maxX, aa2.maxY, aa2.maxZ).endVertex();
        worldRenderer.pos(aa2.maxX, aa2.maxY, aa2.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa2.minX, aa2.minY, aa2.minZ).endVertex();
        worldRenderer.pos(aa2.maxX, aa2.minY, aa2.minZ).endVertex();
        worldRenderer.pos(aa2.maxX, aa2.minY, aa2.maxZ).endVertex();
        worldRenderer.pos(aa2.minX, aa2.minY, aa2.maxZ).endVertex();
        worldRenderer.pos(aa2.minX, aa2.minY, aa2.minZ).endVertex();
        worldRenderer.pos(aa2.minX, aa2.minY, aa2.maxZ).endVertex();
        worldRenderer.pos(aa2.maxX, aa2.minY, aa2.maxZ).endVertex();
        worldRenderer.pos(aa2.maxX, aa2.minY, aa2.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa2.minX, aa2.minY, aa2.minZ).endVertex();
        worldRenderer.pos(aa2.minX, aa2.maxY, aa2.minZ).endVertex();
        worldRenderer.pos(aa2.minX, aa2.minY, aa2.maxZ).endVertex();
        worldRenderer.pos(aa2.minX, aa2.maxY, aa2.maxZ).endVertex();
        worldRenderer.pos(aa2.maxX, aa2.minY, aa2.maxZ).endVertex();
        worldRenderer.pos(aa2.maxX, aa2.maxY, aa2.maxZ).endVertex();
        worldRenderer.pos(aa2.maxX, aa2.minY, aa2.minZ).endVertex();
        worldRenderer.pos(aa2.maxX, aa2.maxY, aa2.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa2.minX, aa2.maxY, aa2.maxZ).endVertex();
        worldRenderer.pos(aa2.minX, aa2.minY, aa2.maxZ).endVertex();
        worldRenderer.pos(aa2.minX, aa2.maxY, aa2.minZ).endVertex();
        worldRenderer.pos(aa2.minX, aa2.minY, aa2.minZ).endVertex();
        worldRenderer.pos(aa2.maxX, aa2.maxY, aa2.minZ).endVertex();
        worldRenderer.pos(aa2.maxX, aa2.minY, aa2.minZ).endVertex();
        worldRenderer.pos(aa2.maxX, aa2.maxY, aa2.maxZ).endVertex();
        worldRenderer.pos(aa2.maxX, aa2.minY, aa2.maxZ).endVertex();
        tessellator.draw();
    }
    
    public static void drawEntityESP(final double x, final double y, final double z, final double width, final double height, final float red, final float green, final float blue, final float alpha, final float lineRed, final float lineGreen, final float lineBlue, final float lineAlpha, final float lineWdith) {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glColor4f(red, green, blue, alpha);
        drawBoundingBox(new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width));
        GL11.glLineWidth(lineWdith);
        GL11.glColor4f(lineRed, lineGreen, lineBlue, lineAlpha);
        drawOutlinedBoundingBox(new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width));
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }
    
    public static void drawOutlinedBoundingBox(final AxisAlignedBB aa2) {
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(3, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa2.minX, aa2.minY, aa2.minZ).endVertex();
        worldRenderer.pos(aa2.maxX, aa2.minY, aa2.minZ).endVertex();
        worldRenderer.pos(aa2.maxX, aa2.minY, aa2.maxZ).endVertex();
        worldRenderer.pos(aa2.minX, aa2.minY, aa2.maxZ).endVertex();
        worldRenderer.pos(aa2.minX, aa2.minY, aa2.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(3, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa2.minX, aa2.maxY, aa2.minZ).endVertex();
        worldRenderer.pos(aa2.maxX, aa2.maxY, aa2.minZ).endVertex();
        worldRenderer.pos(aa2.maxX, aa2.maxY, aa2.maxZ).endVertex();
        worldRenderer.pos(aa2.minX, aa2.maxY, aa2.maxZ).endVertex();
        worldRenderer.pos(aa2.minX, aa2.maxY, aa2.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(1, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa2.minX, aa2.minY, aa2.minZ).endVertex();
        worldRenderer.pos(aa2.minX, aa2.maxY, aa2.minZ).endVertex();
        worldRenderer.pos(aa2.maxX, aa2.minY, aa2.minZ).endVertex();
        worldRenderer.pos(aa2.maxX, aa2.maxY, aa2.minZ).endVertex();
        worldRenderer.pos(aa2.maxX, aa2.minY, aa2.maxZ).endVertex();
        worldRenderer.pos(aa2.maxX, aa2.maxY, aa2.maxZ).endVertex();
        worldRenderer.pos(aa2.minX, aa2.minY, aa2.maxZ).endVertex();
        worldRenderer.pos(aa2.minX, aa2.maxY, aa2.maxZ).endVertex();
        tessellator.draw();
    }
    
    public static void drawblock(final double a2, final double a22, final double a3, final int a4, final int a5, final float a6) {
        final float a23 = (a4 >> 24 & 0xFF) / 255.0f;
        final float a24 = (a4 >> 16 & 0xFF) / 255.0f;
        final float a25 = (a4 >> 8 & 0xFF) / 255.0f;
        final float a26 = (a4 & 0xFF) / 255.0f;
        final float a27 = (a5 >> 24 & 0xFF) / 255.0f;
        final float a28 = (a5 >> 16 & 0xFF) / 255.0f;
        final float a29 = (a5 >> 8 & 0xFF) / 255.0f;
        final float a30 = (a5 & 0xFF) / 255.0f;
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glColor4f(a24, a25, a26, a23);
        drawOutlinedBoundingBox(new AxisAlignedBB(a2, a22, a3, a2 + 1.0, a22 + 1.0, a3 + 1.0));
        GL11.glLineWidth(a6);
        GL11.glColor4f(a28, a29, a30, a27);
        drawOutlinedBoundingBox(new AxisAlignedBB(a2, a22, a3, a2 + 1.0, a22 + 1.0, a3 + 1.0));
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }
    
    public static void rectangle(double left, double top, double right, double bottom, final int color) {
        if (left < right) {
            final double var5 = left;
            left = right;
            right = var5;
        }
        if (top < bottom) {
            final double var5 = top;
            top = bottom;
            bottom = var5;
        }
        final float var6 = (color >> 24 & 0xFF) / 255.0f;
        final float var7 = (color >> 16 & 0xFF) / 255.0f;
        final float var8 = (color >> 8 & 0xFF) / 255.0f;
        final float var9 = (color & 0xFF) / 255.0f;
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(var7, var8, var9, var6);
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(left, bottom, 0.0).endVertex();
        worldRenderer.pos(right, bottom, 0.0).endVertex();
        worldRenderer.pos(right, top, 0.0).endVertex();
        worldRenderer.pos(left, top, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    public static void rectangleBordered(final double x, final double y, final double x1, final double y1, final double width, final int internalColor, final int borderColor) {
        rectangle(x + width, y + width, x1 - width, y1 - width, internalColor);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        rectangle(x + width, y, x1 - width, y + width, borderColor);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        rectangle(x, y, x + width, y1, borderColor);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        rectangle(x1 - width, y, x1, y1, borderColor);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        rectangle(x + width, y1 - width, x1 - width, y1, borderColor);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    public static void color(final int color) {
        final float f2 = (color >> 24 & 0xFF) / 255.0f;
        final float f3 = (color >> 16 & 0xFF) / 255.0f;
        final float f4 = (color >> 8 & 0xFF) / 255.0f;
        final float f5 = (color & 0xFF) / 255.0f;
        GL11.glColor4f(f3, f4, f5, f2);
    }
    
    public static void arcEllipse(final float x, final float y, float start, float end, final float w, final float h, final int color) {
        GlStateManager.color(0.0f, 0.0f, 0.0f);
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.0f);
        float temp = 0.0f;
        if (start > end) {
            temp = end;
            end = start;
            start = temp;
        }
        final float var11 = (color >> 24 & 0xFF) / 255.0f;
        final float var12 = (color >> 16 & 0xFF) / 255.0f;
        final float var13 = (color >> 8 & 0xFF) / 255.0f;
        final float var14 = (color & 0xFF) / 255.0f;
        final Tessellator var15 = Tessellator.getInstance();
        final WorldRenderer var16 = var15.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(var12, var13, var14, var11);
        if (var11 > 0.5f) {
            GL11.glEnable(2848);
            GL11.glLineWidth(2.0f);
            GL11.glBegin(3);
            for (float i = end; i >= start; i -= 4.0f) {
                final float ldx = (float)Math.cos(i * 3.141592653589793 / 180.0) * w * 1.001f;
                final float ldy = (float)Math.sin(i * 3.141592653589793 / 180.0) * h * 1.001f;
                GL11.glVertex2f(x + ldx, y + ldy);
            }
            GL11.glEnd();
            GL11.glDisable(2848);
        }
        GL11.glBegin(6);
        for (float i = end; i >= start; i -= 4.0f) {
            final float ldx = (float)Math.cos(i * 3.141592653589793 / 180.0) * w;
            final float ldy = (float)Math.sin(i * 3.141592653589793 / 180.0) * h;
            GL11.glVertex2f(x + ldx, y + ldy);
        }
        GL11.glEnd();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
    
    public static void arc(final float x, final float y, final float start, final float end, final float radius, final int color) {
        arcEllipse(x, y, start, end, radius, radius, color);
    }
    
    public static void circle(final float x, final float y, final float radius, final int fill) {
        arc(x, y, 0.0f, 360.0f, radius, fill);
    }
    
    public static void drawRoundedRect(float x, float y, float x2, float y2, final float round, final int color) {
        x += (float)(round / 2.0f + 0.5);
        y += (float)(round / 2.0f + 0.5);
        x2 -= (float)(round / 2.0f + 0.5);
        y2 -= (float)(round / 2.0f + 0.5);
        Gui.drawRect((int)x, (int)y, (int)x2, (int)y2, color);
        circle(x2 - round / 2.0f, y + round / 2.0f, round, color);
        circle(x + round / 2.0f, y2 - round / 2.0f, round, color);
        circle(x + round / 2.0f, y + round / 2.0f, round, color);
        circle(x2 - round / 2.0f, y2 - round / 2.0f, round, color);
        Gui.drawRect((int)(x - round / 2.0f - 0.5f), (int)(y + round / 2.0f), (int)x2, (int)(y2 - round / 2.0f), color);
        Gui.drawRect((int)x, (int)(y + round / 2.0f), (int)(x2 + round / 2.0f + 0.5f), (int)(y2 - round / 2.0f), color);
        Gui.drawRect((int)(x + round / 2.0f), (int)(y - round / 2.0f - 0.5f), (int)(x2 - round / 2.0f), (int)(y2 - round / 2.0f), color);
        Gui.drawRect((int)(x + round / 2.0f), (int)y, (int)(x2 - round / 2.0f), (int)(y2 + round / 2.0f + 0.5f), color);
    }
    
    public static void drawImage(final ResourceLocation image, final int x, final int y, final int width, final int height, final Color color) {
        final ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDepthMask(false);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(color.getRed() / 255.0f, color.getBlue() / 255.0f, color.getRed() / 255.0f, 1.0f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(image);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, width, height, (float)width, (float)height);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
    }
    
    public static void setCustomYaw(final float customYaw) {
        RenderUtils.CustomYaw = customYaw;
        RenderUtils.SetCustomYaw = true;
        RenderUtils.mc.thePlayer.rotationYawHead = customYaw;
    }
    
    public static void resetPlayerYaw() {
        RenderUtils.SetCustomYaw = false;
    }
    
    public static float getCustomYaw() {
        return RenderUtils.CustomYaw;
    }
    
    public static void setCustomPitch(final float customPitch) {
        RenderUtils.CustomPitch = customPitch;
        RenderUtils.SetCustomPitch = true;
    }
    
    public static void resetPlayerPitch() {
        RenderUtils.SetCustomPitch = false;
    }
    
    public static float getCustomPitch() {
        return RenderUtils.CustomPitch;
    }
    
    public static void glColor(final Color color) {
        final float red = color.getRed() / 255.0f;
        final float green = color.getGreen() / 255.0f;
        final float blue = color.getBlue() / 255.0f;
        final float alpha = color.getAlpha() / 255.0f;
        GlStateManager.color(red, green, blue, alpha);
    }
    
    public static void pre() {
        GL11.glDisable(2929);
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
    }
    
    public static void post() {
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glColor3d(1.0, 1.0, 1.0);
    }
    
    public static void drawEntityOnScreen(final int posX, final int posY, final int scale, final EntityLivingBase ent) {
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
        GlStateManager.rotate(0.0f, 1.0f, 0.0f, 0.0f);
        ent.renderYawOffset = 10 * ent.ticksExisted % 360;
        ent.rotationYaw = 10 * ent.ticksExisted % 360;
        ent.rotationPitch = 0.0f;
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
    
    public static void renderItemStack(final ItemStack stack, final int x, final int y) {
        GlStateManager.pushMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.clear(256);
        RenderHelper.enableStandardItemLighting();
        Minecraft.getMinecraft().getRenderItem().zLevel = -150.0f;
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
        Minecraft.getMinecraft().getRenderItem().renderItemOverlays(Minecraft.getMinecraft().fontRendererObj, stack, x, y);
        Minecraft.getMinecraft().getRenderItem().zLevel = 0.0f;
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableCull();
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
    }
    
    public static int getColorFromPercentage(final float current, final float max) {
        final float percentage = current / max / 3.0f;
        return Color.HSBtoRGB(percentage, 1.0f, 1.0f);
    }
    
    public static void drawRectSized(final float x, final float y, final float width, final float height, final int color) {
        drawRect(x, y, x + width, y + height, color);
    }
    
    public static double lerp(final double v0, final double v1, final double t) {
        return (1.0 - t) * v0 + t * v1;
    }
    
    public static double interpolate(final double old, final double now, final float partialTicks) {
        return old + (now - old) * partialTicks;
    }
    
    public static float interpolate(final float old, final float now, final float partialTicks) {
        return old + (now - old) * partialTicks;
    }
    
    public static void enableGL2D() {
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glDepthMask(true);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
    }
    
    public static void disableGL2D() {
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glHint(3155, 4352);
    }
    
    public static void glColor(final int hex) {
        final float alpha = (hex >> 24 & 0xFF) / 255.0f;
        final float red = (hex >> 16 & 0xFF) / 255.0f;
        final float green = (hex >> 8 & 0xFF) / 255.0f;
        final float blue = (hex & 0xFF) / 255.0f;
        GL11.glColor4f(red, green, blue, alpha);
    }
    
    public static void drawBorderedRectReliant(final float x, final float y, final float x1, final float y1, final float lineWidth, final int inside, final int border) {
        enableGL2D();
        drawRect(x, y, x1, y1, inside);
        glColor(border);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glLineWidth(lineWidth);
        GL11.glBegin(3);
        GL11.glVertex2f(x, y);
        GL11.glVertex2f(x, y1);
        GL11.glVertex2f(x1, y1);
        GL11.glVertex2f(x1, y);
        GL11.glVertex2f(x, y);
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        disableGL2D();
    }
    
    public static void disableSmoothLine() {
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDisable(3042);
        GL11.glEnable(3008);
        GL11.glDepthMask(true);
        GL11.glCullFace(1029);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glHint(3155, 4352);
    }
    
    public static void enableSmoothLine(final float width) {
        GL11.glDisable(3008);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glEnable(2884);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
        GL11.glLineWidth(width);
    }
    
    public static void TScylinder1(final Entity player, final double x, final double y, final double z, final double range, final int s, final int color) {
        final Cylinder c = new Cylinder();
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        glColor(0);
        enableSmoothLine(3.5f);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
        glColor(color);
        GL11.glBegin(2);
        GL11.glEnd();
        c.draw((float)(range + 0.25), (float)(range + 0.25), 0.0f, s, 0);
        c.setDrawStyle(100011);
        disableSmoothLine();
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GlStateManager.translate(x, y, z);
        enableSmoothLine(3.5f);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
        glColor(color);
        GL11.glBegin(2);
        GL11.glEnd();
        c.draw((float)(range + 0.25), (float)(range + 0.25), 0.0f, s, 0);
        disableSmoothLine();
        GlStateManager.resetColor();
        GL11.glPopMatrix();
    }
    
    public static void TScylinder2(final Entity player, final double x, final double y, final double z, final double range, final int s, final int color) {
        final Cylinder c = new Cylinder();
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        glColor(0);
        enableSmoothLine(3.5f);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
        glColor(color);
        GL11.glBegin(3);
        GL11.glEnd();
        c.draw((float)(range + 0.25), (float)(range + 0.25), 0.0f, s, 0);
        c.setDrawStyle(100011);
        disableSmoothLine();
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GlStateManager.translate(x, y, z);
        enableSmoothLine(3.5f);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
        glColor(color);
        GL11.glBegin(3);
        GL11.glEnd();
        c.draw((float)(range + 0.25), (float)(range + 0.25), 0.0f, s, 0);
        disableSmoothLine();
        GlStateManager.resetColor();
        GL11.glPopMatrix();
    }
    
    public static void drawSelectionBoundingBox(final AxisAlignedBB boundingBox) {
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(3, DefaultVertexFormats.POSITION);
        worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
        worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();
        worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
        worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
        worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(3, DefaultVertexFormats.POSITION);
        worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
        worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
        worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(1, DefaultVertexFormats.POSITION);
        worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
        worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
        worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();
        worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
        worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
        worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
        worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        tessellator.draw();
    }
    
    public static void drawImage(final ResourceLocation image, final int x, final int y, final int width, final int height) {
        final ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDepthMask(false);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(image);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, width, height, (float)width, (float)height);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
    }
    
    public static int fadeTo(final int startColor, final int endColor, final float progress) {
        final float invert = 1.0f - progress;
        final int r = (int)((startColor >> 16 & 0xFF) * invert + (endColor >> 16 & 0xFF) * progress);
        final int g = (int)((startColor >> 8 & 0xFF) * invert + (endColor >> 8 & 0xFF) * progress);
        final int b = (int)((startColor & 0xFF) * invert + (endColor & 0xFF) * progress);
        final int a = (int)((startColor >> 24 & 0xFF) * invert + (endColor >> 24 & 0xFF) * progress);
        return (a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF);
    }
    
    public static void enableBlending() {
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
    }
    
    public static void color1(final int color) {
        GL11.glColor4ub((byte)(color >> 16 & 0xFF), (byte)(color >> 8 & 0xFF), (byte)(color & 0xFF), (byte)(color >> 24 & 0xFF));
    }
    
    public static void glColor2(final Color color) {
        final float red = color.getRed() / 255.0f;
        final float green = color.getGreen() / 255.0f;
        final float blue = color.getBlue() / 255.0f;
        final float alpha = color.getAlpha() / 255.0f;
        GlStateManager.color(red, green, blue, alpha);
    }
    
    public static void drawAxisAlignedBB(final AxisAlignedBB axisAlignedBB, final Color color) {
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3042);
        GL11.glLineWidth(2.0f);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        glColor2(color);
        drawFilledBox(axisAlignedBB);
        GlStateManager.resetColor();
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
    }
    
    public static void drawFilledBox(final AxisAlignedBB axisAlignedBB) {
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        tessellator.draw();
    }
    
    public static void pre3D() {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glShadeModel(7425);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glDisable(2929);
        GL11.glDisable(2896);
        GL11.glDepthMask(false);
        GL11.glHint(3154, 4354);
    }
    
    public static void post3D() {
        GL11.glDepthMask(true);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        if (!GL11.glIsEnabled(2896)) {}
    }
    
    public static void drawLine(final BlockPos var0, final int var1) {
        final double var2 = var0.getX() - ((IRenderManager)RenderUtils.mc.getRenderManager()).getRenderPosX() + 0.5;
        final double var3 = var0.getY() - ((IRenderManager)RenderUtils.mc.getRenderManager()).getRenderPosY() + 0.5;
        final double var4 = var0.getZ() - ((IRenderManager)RenderUtils.mc.getRenderManager()).getRenderPosZ() + 0.5;
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glEnable(2848);
        GL11.glDisable(2929);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glLineWidth(1.0f);
        float var5 = (float)(RenderUtils.mc.thePlayer.posX - var0.getX());
        var5 = (float)(RenderUtils.mc.thePlayer.posY - var0.getY());
        final float var6 = (var1 >> 16 & 0xFF) / 255.0f;
        final float var7 = (var1 >> 8 & 0xFF) / 255.0f;
        final float var8 = (var1 & 0xFF) / 255.0f;
        final float var9 = (var1 >> 24 & 0xFF) / 255.0f;
        GL11.glColor4f(var6, var7, var8, var9);
        GL11.glLoadIdentity();
        final boolean var10 = RenderUtils.mc.gameSettings.viewBobbing;
        RenderUtils.mc.gameSettings.viewBobbing = false;
        GL11.glBegin(3);
        GL11.glVertex3d(0.0, (double)RenderUtils.mc.thePlayer.getEyeHeight(), 0.0);
        GL11.glVertex3d(var2, var3, var4);
        GL11.glVertex3d(var2, var3, var4);
        GL11.glEnd();
        RenderUtils.mc.gameSettings.viewBobbing = var10;
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }
    
    public static void drawSolidBlockESP(final BlockPos var0, final int var1) {
        final double var2 = var0.getX() - ((IRenderManager)RenderUtils.mc.getRenderManager()).getRenderPosX();
        final double var3 = var0.getY() - ((IRenderManager)RenderUtils.mc.getRenderManager()).getRenderPosY();
        final double var4 = var0.getZ() - ((IRenderManager)RenderUtils.mc.getRenderManager()).getRenderPosZ();
        final double var5 = RenderUtils.mc.theWorld.getBlockState(var0).getBlock().getBlockBoundsMaxY() - RenderUtils.mc.theWorld.getBlockState(var0).getBlock().getBlockBoundsMinY();
        final float var6 = (var1 >> 16 & 0xFF) / 255.0f;
        final float var7 = (var1 >> 8 & 0xFF) / 255.0f;
        final float var8 = (var1 & 0xFF) / 255.0f;
        final float var9 = (var1 >> 24 & 0xFF) / 255.0f;
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glLineWidth(1.0f);
        GL11.glColor4f(var6, var7, var8, var9);
        drawOutlinedBoundingBox(new AxisAlignedBB(var2, var3, var4, var2 + 1.0, var3 + var5, var4 + 1.0));
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glDisable(2848);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
        GlStateManager.disableBlend();
        GL11.glPopMatrix();
    }
    
    static {
        mc = Minecraft.getMinecraft();
        frustrum = new Frustum();
        RenderUtils.SetCustomYaw = false;
        RenderUtils.CustomYaw = 0.0f;
        RenderUtils.SetCustomPitch = false;
        RenderUtils.CustomPitch = 0.0f;
    }
}
