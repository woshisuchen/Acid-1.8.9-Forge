package acid.utils.render;

import acid.utils.Helper;
import net.minecraft.util.Vec3;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.AxisAlignedBB;
import java.nio.IntBuffer;
import java.nio.FloatBuffer;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.opengl.GL11;
import org.lwjgl.BufferUtils;

public class RenderingUtil
{
    public static double[] convertTo2D(final double x, final double y, final double z) {
        final FloatBuffer screenCoords = BufferUtils.createFloatBuffer(3);
        final IntBuffer viewport = BufferUtils.createIntBuffer(16);
        final FloatBuffer modelView = BufferUtils.createFloatBuffer(16);
        final FloatBuffer projection = BufferUtils.createFloatBuffer(16);
        GL11.glGetFloat(2982, modelView);
        GL11.glGetFloat(2983, projection);
        GL11.glGetInteger(2978, viewport);
        final boolean result = GLU.gluProject((float)x, (float)y, (float)z, modelView, projection, viewport, screenCoords);
        double[] array2;
        if (result) {
            final double[] array = array2 = new double[3];
            array[0] = screenCoords.get(0);
            array[1] = Display.getHeight() - screenCoords.get(1);
            array[2] = screenCoords.get(2);
        }
        else {
            array2 = null;
        }
        return array2;
    }
    
    public static double[] boundingBox(final double x, final double y, final double z, final AxisAlignedBB boundingBox) {
        final double[] pos1 = convertTo2D(boundingBox.minX - x, boundingBox.minY - y, boundingBox.minZ - z);
        final double[] pos2 = convertTo2D(boundingBox.maxX - x, boundingBox.minY - y, boundingBox.minZ - z);
        final double[] pos3 = convertTo2D(boundingBox.maxX - x, boundingBox.minY - y, boundingBox.maxZ - z);
        final double[] pos4 = convertTo2D(boundingBox.minX - x, boundingBox.minY - y, boundingBox.maxZ - z);
        final double[] pos5 = convertTo2D(boundingBox.minX - x, boundingBox.maxY - y, boundingBox.minZ - z);
        final double[] pos6 = convertTo2D(boundingBox.maxX - x, boundingBox.maxY - y, boundingBox.minZ - z);
        final double[] pos7 = convertTo2D(boundingBox.maxX - x, boundingBox.maxY - y, boundingBox.maxZ - z);
        final double[] pos8 = convertTo2D(boundingBox.minX - x, boundingBox.maxY - y, boundingBox.maxZ - z);
        final boolean shouldRender = pos1[2] > 0.0 && pos1[2] <= 1.0 && pos2[2] > 0.0 && pos2[2] <= 1.0 && pos3[2] > 0.0 && pos3[2] <= 1.0 && pos4[2] > 0.0 && pos4[2] <= 1.0 && pos5[2] > 0.0 && pos5[2] <= 1.0 && pos6[2] > 0.0 && pos6[2] <= 1.0 && pos7[2] > 0.0 && pos7[2] <= 1.0 && pos8[2] > 0.0 && pos8[2] <= 1.0;
        if (shouldRender) {
            return new double[] { -1337.0, -1337.0, -1337.0, -1337.0, -1337.0, -1337.0, -1337.0, -1337.0, -1337.0, -1337.0, -1337.0, -1337.0, -1337.0, -1337.0, -1337.0, -1337.0, -1337.0, -1337.0, -1337.0, -1337.0 };
        }
        double startX = pos1[0];
        double startY = pos1[1];
        double endX = pos8[0];
        double endY = pos8[1];
        final double[] xValues = { pos1[0], pos2[0], pos3[0], pos4[0], pos5[0], pos6[0], pos7[0], pos8[0] };
        final double[] yValues = { pos1[1], pos2[1], pos3[1], pos4[1], pos5[1], pos6[1], pos7[1], pos8[1] };
        double[] var26 = xValues;
        for (int var27 = xValues.length, var28 = 0; var28 < var27; ++var28) {
            final Double bdubs = var26[var28];
            if (bdubs < startX) {
                startX = bdubs;
            }
        }
        var26 = xValues;
        for (int var27 = xValues.length, var28 = 0; var28 < var27; ++var28) {
            final Double bdubs = var26[var28];
            if (bdubs > endX) {
                endX = bdubs;
            }
        }
        var26 = yValues;
        for (int var27 = yValues.length, var28 = 0; var28 < var27; ++var28) {
            final Double bdubs = var26[var28];
            if (bdubs < startY) {
                startY = bdubs;
            }
        }
        var26 = yValues;
        for (int var27 = yValues.length, var28 = 0; var28 < var27; ++var28) {
            final Double bdubs = var26[var28];
            if (bdubs > endY) {
                endY = bdubs;
            }
        }
        return new double[] { pos1[0], pos1[1], pos2[0], pos2[1], pos3[0], pos3[1], pos4[0], pos4[1], pos5[0], pos5[1], pos6[0], pos6[1], pos7[0], pos7[1], pos8[0], pos8[1], startX, startY, endX, endY };
    }
    
    public static void setupRender(final boolean start) {
        if (start) {
            GlStateManager.enableBlend();
            GL11.glEnable(2848);
            GlStateManager.disableDepth();
            GlStateManager.disableTexture2D();
            GlStateManager.blendFunc(770, 771);
            GL11.glHint(3154, 4354);
        }
        else {
            GlStateManager.disableBlend();
            GlStateManager.enableTexture2D();
            GL11.glDisable(2848);
            GlStateManager.enableDepth();
        }
        GlStateManager.depthMask(!start);
    }
    
    public static void drawFancy(final double d, final double e, final double f2, final double f3, final int nameColor) {
        final float alpha = (nameColor >> 24 & 0xFF) / 255.0f;
        final float red = (nameColor >> 16 & 0xFF) / 255.0f;
        final float green = (nameColor >> 8 & 0xFF) / 255.0f;
        final float blue = (nameColor & 0xFF) / 255.0f;
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL11.glPushMatrix();
        GL11.glEnable(2848);
        GL11.glEnable(2881);
        GL11.glEnable(2832);
        GL11.glEnable(3042);
        GL11.glColor4f(red, green, blue, alpha);
        GL11.glBegin(7);
        GL11.glVertex2d(f2 + 1.300000011920929, e);
        GL11.glVertex2d(d + 1.0, e);
        GL11.glVertex2d(d - 1.300000011920929, f3);
        GL11.glVertex2d(f2 - 1.0, f3);
        GL11.glEnd();
        GL11.glDisable(2848);
        GL11.glDisable(2881);
        GL11.glDisable(2832);
        GL11.glDisable(3042);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GL11.glPopMatrix();
    }
    
    public static void drawGradient(final double x, final double y, final double x2, final double y2, final int col1, final int col2) {
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
        GL11.glVertex2d(x2, y);
        GL11.glVertex2d(x, y);
        GL11.glColor4f(f6, f7, f8, f5);
        GL11.glVertex2d(x, y2);
        GL11.glVertex2d(x2, y2);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glShadeModel(7424);
        GL11.glColor4d(1.0, 1.0, 1.0, 1.0);
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
        GL11.glDisable(2848);
        GL11.glShadeModel(7424);
    }
    
    public static void drawColorPicker(final double left, final double top, final double right, final double bottom, final int col2) {
        final float f4 = (col2 >> 24 & 0xFF) / 255.0f;
        final float f5 = (col2 >> 16 & 0xFF) / 255.0f;
        final float f6 = (col2 >> 8 & 0xFF) / 255.0f;
        final float f7 = (col2 & 0xFF) / 255.0f;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glShadeModel(7425);
        GL11.glPushMatrix();
        GL11.glBegin(7);
        GL11.glColor4f(f5, f6, f7, f4);
        GL11.glVertex2d(left, top);
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
        GL11.glVertex2d(left, bottom);
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
        GL11.glVertex2d(right, bottom);
        GL11.glColor4f(f5, f6, f7, f4);
        GL11.glVertex2d(right, top);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glShadeModel(7424);
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
        final WorldRenderer worldRenderer = Tessellator.getInstance().getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(var7, var8, var9, var6);
        Tessellator.getInstance().draw();
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
    
    public static void drawLines(final AxisAlignedBB boundingBox) {
        GL11.glPushMatrix();
        GL11.glBegin(2);
        GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
        GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
        GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
        GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
        GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
        GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
        GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
        GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
        GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
        GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
        GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
        GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
        GL11.glEnd();
        GL11.glPopMatrix();
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
        if (!GL11.glIsEnabled(2896)) {
            GL11.glEnable(2896);
        }
    }
    
    public static void glColor(final float alpha, final int redRGB, final int greenRGB, final int blueRGB) {
        final float red = 0.003921569f * redRGB;
        final float green = 0.003921569f * greenRGB;
        final float blue = 0.003921569f * blueRGB;
        GL11.glColor4f(red, green, blue, alpha);
    }
    
    public static void drawRect(final float x, final float y, final float x1, final float y1) {
        GL11.glBegin(7);
        GL11.glVertex2f(x, y1);
        GL11.glVertex2f(x1, y1);
        GL11.glVertex2f(x1, y);
        GL11.glVertex2f(x, y);
        GL11.glEnd();
    }
    
    public static void glColor(final int hex) {
        final float alpha = (hex >> 24 & 0xFF) / 255.0f;
        final float red = (hex >> 16 & 0xFF) / 255.0f;
        final float green = (hex >> 8 & 0xFF) / 255.0f;
        final float blue = (hex & 0xFF) / 255.0f;
        GL11.glColor4f(red, green, blue, alpha);
    }
    
    public static void drawRect(final float x, final float y, final float x1, final float y1, final int color) {
        enableGL2D();
        glColor(color);
        drawRect(x, y, x1, y1);
        disableGL2D();
    }
    
    public static void drawRoundedRect(final float x, final float y, final float x1, final float y1, final int borderC, final int insideC) {
        drawRect(x + 0.5f, y, x1 - 0.5f, y + 0.5f, insideC);
        drawRect(x + 0.5f, y1 - 0.5f, x1 - 0.5f, y1, insideC);
        drawRect(x, y + 0.5f, x1, y1 - 0.5f, insideC);
    }
    
    public static void drawHLine(float x, float y, final float x1, final int y1) {
        if (y < x) {
            final float var5 = x;
            x = y;
            y = var5;
        }
        drawRect(x, x1, y + 1.0f, x1 + 1.0f, y1);
    }
    
    public static void drawVLine(final float x, final float y, final float x1, final int y1) {
        final float var11 = (y1 >> 24 & 0xFF) / 255.0f;
        final float var12 = (y1 >> 16 & 0xFF) / 255.0f;
        final float var13 = (y1 >> 8 & 0xFF) / 255.0f;
        final float var14 = (y1 & 0xFF) / 255.0f;
        pre3D();
        final Tessellator tes = Tessellator.getInstance();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        RenderHelper.disableStandardItemLighting();
        GL11.glBegin(6);
        GL11.glVertex2f(x, y);
        GL11.glVertex2f(x1, y);
        GL11.glVertex2f(x + (x1 - x) / 2.0f, y + 3.0f);
        GL11.glEnd();
        tes.draw();
        RenderHelper.enableStandardItemLighting();
        post3D();
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
    
    public static void drawCircle(float cx, float cy, float r, final int num_segments, final int c) {
        GL11.glPushMatrix();
        cx *= 2.0f;
        cy *= 2.0f;
        final float f = (c >> 24 & 0xFF) / 255.0f;
        final float f2 = (c >> 16 & 0xFF) / 255.0f;
        final float f3 = (c >> 8 & 0xFF) / 255.0f;
        final float f4 = (c & 0xFF) / 255.0f;
        final float theta = (float)(6.2831852 / num_segments);
        final float p = (float)Math.cos(theta);
        final float s = (float)Math.sin(theta);
        float x;
        r = (x = r * 2.0f);
        float y = 0.0f;
        enableGL2D();
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        GL11.glColor4f(f2, f3, f4, f);
        GL11.glBegin(2);
        for (int ii = 0; ii < num_segments; ++ii) {
            GL11.glVertex2f(x + cx, y + cy);
            final float t = x;
            x = p * x - s * y;
            y = s * t + p * y;
        }
        GL11.glEnd();
        GL11.glScalef(2.0f, 2.0f, 2.0f);
        disableGL2D();
        GL11.glPopMatrix();
    }
    
    public static void drawBorderedCircle(final int circleX, final int circleY, final double radius, final double width, final int borderColor, final int innerColor) {
        enableGL2D();
        drawCircle(circleX, circleY, (float)(radius - 0.5 + width), 72, borderColor);
        drawFullCircle(circleX, circleY, radius, innerColor);
        disableGL2D();
    }
    
    public static void drawCircleNew(final float x, final float y, final float radius, final int numberOfSides) {
        final float z = 0.0f;
        final int numberOfVertices = numberOfSides + 2;
        final float doublePi = 6.2831855f;
    }
    
    public static void drawFullCircle(int cx, int cy, double r, final int c) {
        r *= 2.0;
        cx *= 2;
        cy *= 2;
        final float f = (c >> 24 & 0xFF) / 255.0f;
        final float f2 = (c >> 16 & 0xFF) / 255.0f;
        final float f3 = (c >> 8 & 0xFF) / 255.0f;
        final float f4 = (c & 0xFF) / 255.0f;
        enableGL2D();
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        GL11.glColor4f(f2, f3, f4, f);
        GL11.glBegin(6);
        for (int i = 0; i <= 2160; ++i) {
            final double x = Math.sin(i * 3.141592653589793 / 360.0) * r;
            final double y = Math.cos(i * 3.141592653589793 / 360.0) * r;
            GL11.glVertex2d(cx + x, cy + y);
        }
        GL11.glEnd();
        GL11.glScalef(2.0f, 2.0f, 2.0f);
        disableGL2D();
    }
    
    public static Vec3 interpolateRender(final EntityPlayer player) {
        final float part = Helper.getTimer().renderPartialTicks;
        final double interpX = player.lastTickPosX + (player.posX - player.lastTickPosX) * part;
        final double interpY = player.lastTickPosY + (player.posY - player.lastTickPosY) * part;
        final double interpZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * part;
        return new Vec3(interpX, interpY, interpZ);
    }
    
    public static double interpolate(final double old, final double now, final float partialTicks) {
        return old + (now - old) * partialTicks;
    }
    
    public static float interpolate(final float old, final float now, final float partialTicks) {
        return old + (now - old) * partialTicks;
    }
}
