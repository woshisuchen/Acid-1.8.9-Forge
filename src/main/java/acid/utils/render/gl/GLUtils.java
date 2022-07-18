package acid.utils.render.gl;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.glu.GLU;
import acid.utils.math.Vec3f;
import net.minecraft.client.renderer.GlStateManager;
import java.nio.IntBuffer;
import java.nio.FloatBuffer;

public final class GLUtils
{
    public static final FloatBuffer MODELVIEW;
    public static final FloatBuffer PROJECTION;
    public static final IntBuffer VIEWPORT;
    public static final FloatBuffer TO_SCREEN_BUFFER;
    public static final FloatBuffer TO_WORLD_BUFFER;
    
    public static void init() {
    }
    
    public static float[] getColor(final int hex) {
        return new float[] { (hex >> 16 & 0xFF) / 255.0f, (hex >> 8 & 0xFF) / 255.0f, (hex & 0xFF) / 255.0f, (hex >> 24 & 0xFF) / 255.0f };
    }
    
    public static void glColor(final int hex) {
        final float[] color = getColor(hex);
        GlStateManager.color(color[0], color[1], color[2], color[3]);
    }
    
    public static void rotateX(final float angle, final double x, final double y, final double z) {
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(angle, 1.0f, 0.0f, 0.0f);
        GlStateManager.translate(-x, -y, -z);
    }
    
    public static void rotateY(final float angle, final double x, final double y, final double z) {
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(angle, 0.0f, 1.0f, 0.0f);
        GlStateManager.translate(-x, -y, -z);
    }
    
    public static void rotateZ(final float angle, final double x, final double y, final double z) {
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(angle, 0.0f, 0.0f, 1.0f);
        GlStateManager.translate(-x, -y, -z);
    }
    
    public static Vec3f toScreen(final Vec3f pos) {
        return toScreen(pos.getX(), pos.getY(), pos.getZ());
    }
    
    public static Vec3f toScreen(final double x, final double y, final double z) {
        final boolean result = GLU.gluProject((float)x, (float)y, (float)z, GLUtils.MODELVIEW, GLUtils.PROJECTION, GLUtils.VIEWPORT, (FloatBuffer)GLUtils.TO_SCREEN_BUFFER.clear());
        if (result) {
            return new Vec3f(GLUtils.TO_SCREEN_BUFFER.get(0), Display.getHeight() - GLUtils.TO_SCREEN_BUFFER.get(1), GLUtils.TO_SCREEN_BUFFER.get(2));
        }
        return null;
    }
    
    public static Vec3f toWorld(final Vec3f pos) {
        return toWorld(pos.getX(), pos.getY(), pos.getZ());
    }
    
    public static Vec3f toWorld(final double x, final double y, final double z) {
        final boolean result = GLU.gluUnProject((float)x, (float)y, (float)z, GLUtils.MODELVIEW, GLUtils.PROJECTION, GLUtils.VIEWPORT, (FloatBuffer)GLUtils.TO_WORLD_BUFFER.clear());
        if (result) {
            return new Vec3f(GLUtils.TO_WORLD_BUFFER.get(0), GLUtils.TO_WORLD_BUFFER.get(1), GLUtils.TO_WORLD_BUFFER.get(2));
        }
        return null;
    }
    
    public static FloatBuffer getModelview() {
        return GLUtils.MODELVIEW;
    }
    
    public static FloatBuffer getProjection() {
        return GLUtils.PROJECTION;
    }
    
    public static IntBuffer getViewport() {
        return GLUtils.VIEWPORT;
    }
    
    static {
        MODELVIEW = BufferUtils.createFloatBuffer(16);
        PROJECTION = BufferUtils.createFloatBuffer(16);
        VIEWPORT = BufferUtils.createIntBuffer(16);
        TO_SCREEN_BUFFER = BufferUtils.createFloatBuffer(3);
        TO_WORLD_BUFFER = BufferUtils.createFloatBuffer(3);
    }
}
