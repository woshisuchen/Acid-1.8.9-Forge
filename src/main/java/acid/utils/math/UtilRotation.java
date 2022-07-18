package acid.utils.math;

import java.util.Random;
import net.minecraft.util.MathHelper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.Entity;
import net.minecraft.client.Minecraft;

public class UtilRotation
{
    private static Minecraft mc;
    
    public static float[] getAngles(final Entity entity) {
        final double x = entity.posX - UtilRotation.mc.thePlayer.posX;
        final double z = entity.posZ - UtilRotation.mc.thePlayer.posZ;
        final double y = (entity instanceof EntityEnderman) ? (entity.posY - UtilRotation.mc.thePlayer.posY) : (entity.posY + (entity.getEyeHeight() - 1.9) - UtilRotation.mc.thePlayer.posY + (UtilRotation.mc.thePlayer.getEyeHeight() - 1.9));
        final double helper = MathHelper.sqrt_double(x * x + z * z);
        float newYaw = (float)Math.toDegrees(-Math.atan(x / z));
        final float newPitch = (float)(-Math.toDegrees(Math.atan(y / helper)));
        if (z < 0.0 && x < 0.0) {
            newYaw = (float)(90.0 + Math.toDegrees(Math.atan(z / x)));
        }
        else if (z < 0.0 && x > 0.0) {
            newYaw = (float)(-90.0 + Math.toDegrees(Math.atan(z / x)));
        }
        return new float[] { newPitch, newYaw };
    }
    
    public static double getDistanceBetweenAngles(final float angle1, final float angle2) {
        float distance = Math.abs(angle1 - angle2) % 360.0f;
        if (distance > 180.0f) {
            distance = 360.0f - distance;
        }
        return distance;
    }
    
    public static void jitterEffect(final Random rand) {
        if (rand.nextBoolean()) {
            UtilRotation.mc.thePlayer.rotationPitch = (rand.nextBoolean() ? ((float)(UtilRotation.mc.thePlayer.rotationPitch - rand.nextFloat() * 0.8)) : ((float)(UtilRotation.mc.thePlayer.rotationPitch + rand.nextFloat() * 0.8)));
        }
        else {
            UtilRotation.mc.thePlayer.rotationYaw = (rand.nextBoolean() ? ((float)(UtilRotation.mc.thePlayer.rotationYaw - rand.nextFloat() * 0.8)) : ((float)(UtilRotation.mc.thePlayer.rotationYaw + rand.nextFloat() * 0.8)));
        }
    }
    
    static {
        UtilRotation.mc = Minecraft.getMinecraft();
    }
}
