package acid.utils.math;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.util.BlockPos;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import java.util.Random;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import acid.utils.Helper;

public class RotationUtil
{
    public static float pitch() {
        return Helper.mc.thePlayer.rotationPitch;
    }
    
    public static void pitch(final float pitch) {
        Helper.mc.thePlayer.rotationPitch = pitch;
    }
    
    public static float yaw() {
        return Helper.mc.thePlayer.rotationYaw;
    }
    
    public static void yaw(final float yaw) {
        Helper.mc.thePlayer.rotationYaw = yaw;
    }
    
    public static float[] faceTarget(final Entity target, final float p_706252, final float p_706253, final boolean miss) {
        final double var4 = target.posX - Helper.mc.thePlayer.posX;
        final double var5 = target.posZ - Helper.mc.thePlayer.posZ;
        double var7;
        if (target instanceof EntityLivingBase) {
            final EntityLivingBase var6 = (EntityLivingBase)target;
            var7 = var6.posY + var6.getEyeHeight() - (Helper.mc.thePlayer.posY + Helper.mc.thePlayer.getEyeHeight());
        }
        else {
            var7 = (target.getEntityBoundingBox().minY + target.getEntityBoundingBox().maxY) / 2.0 - (Helper.mc.thePlayer.posY + Helper.mc.thePlayer.getEyeHeight());
        }
        final Random rnd = new Random();
        final double var8 = MathHelper.sqrt_double(var4 * var4 + var5 * var5);
        final float var9 = (float)(Math.atan2(var5, var4) * 180.0 / 3.141592653589793) - 90.0f;
        final float var10 = (float)(-Math.atan2(var7 - ((target instanceof EntityPlayer) ? 0.25 : 0.0), var8) * 180.0 / 3.141592653589793);
        final float pitch = changeRotation(Helper.mc.thePlayer.rotationPitch, var10, p_706253);
        final float yaw = changeRotation(Helper.mc.thePlayer.rotationYaw, var9, p_706252);
        return new float[] { yaw, pitch };
    }
    
    public static float changeRotation(final float p_706631, final float p_706632, final float p_706633) {
        float var4 = MathHelper.wrapAngleTo180_float(p_706632 - p_706631);
        if (var4 > p_706633) {
            var4 = p_706633;
        }
        if (var4 < -p_706633) {
            var4 = -p_706633;
        }
        return p_706631 + var4;
    }
    
    public static float[] getRotationToEntity(final Entity entity) {
        final double pX = Minecraft.getMinecraft().thePlayer.posX;
        final double pY = Minecraft.getMinecraft().thePlayer.posY + Minecraft.getMinecraft().thePlayer.getEyeHeight();
        final double pZ = Minecraft.getMinecraft().thePlayer.posZ;
        final double eX = entity.posX;
        final double eY = entity.posY + entity.getEyeHeight();
        final double eZ = entity.posZ;
        final double dX = pX - eX;
        final double dY = pY - eY;
        final double dZ = pZ - eZ;
        final double dH = Math.sqrt(Math.pow(dX, 2.0) + Math.pow(dZ, 2.0));
        float yaw = 0.0f;
        float pitch = 0.0f;
        yaw = (float)(Math.toDegrees(Math.atan2(dZ, dX)) + 90.0);
        pitch = (float)Math.toDegrees(Math.atan2(dH, dY));
        return new float[] { yaw, 90.0f - pitch };
    }
    
    public static float[] getRotations(final Entity entity) {
        if (entity == null) {
            return null;
        }
        final double diffX = entity.posX - Helper.mc.thePlayer.posX;
        final double diffZ = entity.posZ - Helper.mc.thePlayer.posZ;
        double diffY;
        if (entity instanceof EntityLivingBase) {
            final EntityLivingBase elb = (EntityLivingBase)entity;
            diffY = elb.posY + (elb.getEyeHeight() - 0.4) - (Helper.mc.thePlayer.posY + Helper.mc.thePlayer.getEyeHeight());
        }
        else {
            diffY = (entity.getCollisionBoundingBox().minY + entity.getCollisionBoundingBox().maxY) / 2.0 - (Helper.mc.thePlayer.posY + Helper.mc.thePlayer.getEyeHeight());
        }
        final double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
        final float yaw = (float)(Math.atan2(diffZ, diffX) * 180.0 / 3.141592653589793) - 90.0f;
        final float pitch = (float)(-Math.atan2(diffY, dist) * 180.0 / 3.141592653589793);
        return new float[] { yaw, pitch };
    }
    
    public static float[] grabBlockRotations(final BlockPos pos) {
        return getVecRotation(Helper.mc.thePlayer.getPositionVector().addVector(0.0, (double)Helper.mc.thePlayer.getEyeHeight(), 0.0), new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
    }
    
    public static float[] getVecRotation(final Vec3 position) {
        return getVecRotation(Helper.mc.thePlayer.getPositionVector().addVector(0.0, (double)Helper.mc.thePlayer.getEyeHeight(), 0.0), position);
    }
    
    public static Vec3 flat(final Vec3 v) {
        return new Vec3(v.xCoord, 0.0, v.zCoord);
    }
    
    public static float[] getVecRotation(final Vec3 origin, final Vec3 position) {
        final Vec3 difference = position.subtract(origin);
        final double distance = flat(difference).lengthVector();
        final float yaw = (float)Math.toDegrees(Math.atan2(difference.zCoord, difference.xCoord)) - 90.0f;
        final float pitch = (float)(-Math.toDegrees(Math.atan2(difference.yCoord, distance)));
        return new float[] { yaw, pitch };
    }
    
    public static int wrapAngleToDirection(final float yaw, final int zones) {
        int angle = (int)(yaw + 360 / (2 * zones) + 0.5) % 360;
        if (angle < 0) {
            angle += 360;
        }
        return angle / (360 / zones);
    }
    
    public static boolean canEntityBeSeen(final Entity e) {
        final Vec3 vec1 = new Vec3(Helper.mc.thePlayer.posX, Helper.mc.thePlayer.posY + Helper.mc.thePlayer.getEyeHeight(), Helper.mc.thePlayer.posZ);
        final AxisAlignedBB box = e.getEntityBoundingBox();
        Vec3 vec2 = new Vec3(e.posX, e.posY + e.getEyeHeight() / 1.32f, e.posZ);
        final double minx = e.posX - 0.25;
        final double maxx = e.posX + 0.25;
        final double miny = e.posY;
        final double maxy = e.posY + Math.abs(e.posY - box.maxY);
        final double minz = e.posZ - 0.25;
        final double maxz = e.posZ + 0.25;
        boolean see = Helper.mc.theWorld.rayTraceBlocks(vec1, vec2) == null;
        if (see) {
            return true;
        }
        vec2 = new Vec3(maxx, miny, minz);
        see = (Helper.mc.theWorld.rayTraceBlocks(vec1, vec2) == null);
        if (see) {
            return true;
        }
        vec2 = new Vec3(minx, miny, minz);
        see = (Helper.mc.theWorld.rayTraceBlocks(vec1, vec2) == null);
        if (see) {
            return true;
        }
        vec2 = new Vec3(minx, miny, maxz);
        see = (Helper.mc.theWorld.rayTraceBlocks(vec1, vec2) == null);
        if (see) {
            return true;
        }
        vec2 = new Vec3(maxx, miny, maxz);
        see = (Helper.mc.theWorld.rayTraceBlocks(vec1, vec2) == null);
        if (see) {
            return true;
        }
        vec2 = new Vec3(maxx, maxy, minz);
        see = (Helper.mc.theWorld.rayTraceBlocks(vec1, vec2) == null);
        if (see) {
            return true;
        }
        vec2 = new Vec3(minx, maxy, minz);
        see = (Helper.mc.theWorld.rayTraceBlocks(vec1, vec2) == null);
        if (see) {
            return true;
        }
        vec2 = new Vec3(minx, maxy, maxz - 0.1);
        see = (Helper.mc.theWorld.rayTraceBlocks(vec1, vec2) == null);
        if (see) {
            return true;
        }
        vec2 = new Vec3(maxx, maxy, maxz);
        see = (Helper.mc.theWorld.rayTraceBlocks(vec1, vec2) == null);
        return see;
    }
    
    public static float getYawDifference(final float currentYaw, final double targetX, final double targetY, final double targetZ) {
        final double deltaX = targetX - Helper.mc.thePlayer.posX;
        final double deltaY = targetY - Helper.mc.thePlayer.posY;
        final double deltaZ = targetZ - Helper.mc.thePlayer.posZ;
        double yawToEntity = 0.0;
        final double degrees = Math.toDegrees(Math.atan(deltaZ / deltaX));
        if (deltaZ < 0.0 && deltaX < 0.0) {
            if (deltaX != 0.0) {
                yawToEntity = 90.0 + degrees;
            }
        }
        else if (deltaZ < 0.0 && deltaX > 0.0) {
            if (deltaX != 0.0) {
                yawToEntity = -90.0 + degrees;
            }
        }
        else if (deltaZ != 0.0) {
            yawToEntity = Math.toDegrees(-Math.atan(deltaX / deltaZ));
        }
        return MathHelper.wrapAngleTo180_float(-(currentYaw - (float)yawToEntity));
    }
    
    public static float[] getRotationBlock(final BlockPos pos) {
        return getRotationsByVec(Helper.mc.thePlayer.getPositionVector().addVector(0.0, (double)Helper.mc.thePlayer.getEyeHeight(), 0.0), new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
    }
    
    private static float[] getRotationsByVec(final Vec3 origin, final Vec3 position) {
        final Vec3 difference = position.subtract(origin);
        final double distance = flat(difference).lengthVector();
        final float yaw = (float)Math.toDegrees(Math.atan2(difference.zCoord, difference.xCoord)) - 90.0f;
        final float pitch = (float)(-Math.toDegrees(Math.atan2(difference.yCoord, distance)));
        final float llllllllllllIlIIlIIlIlIlIIIlIIIl = Helper.mc.thePlayer.renderYawOffset + MathHelper.wrapAngleTo180_float(yaw - Helper.mc.thePlayer.renderYawOffset);
        return new float[] { yaw, MathHelper.clamp_float(pitch, -90.0f, 90.0f), llllllllllllIlIIlIIlIlIlIIIlIIIl };
    }
    
    public static float getDistanceBetweenAngles(final float angle1, final float angle2) {
        float angle3 = Math.abs(angle1 - angle2) % 360.0f;
        if (angle3 > 180.0f) {
            angle3 = 360.0f - angle3;
        }
        return angle3;
    }
    
    public static float[] getAngles(final EntityLivingBase entity) {
        if (entity == null) {
            return null;
        }
        final EntityPlayerSP player = Helper.mc.thePlayer;
        final double diffX = entity.posX - player.posX;
        final double diffY = entity.posY + entity.getEyeHeight() * 0.9 - (player.posY + player.getEyeHeight());
        final double diffZ = entity.posZ - player.posZ;
        final double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
        final float yaw = (float)(Math.atan2(diffZ, diffX) * 180.0 / 3.141592653589793) - 90.0f;
        final float pitch = (float)(-(Math.atan2(diffY, dist) * 180.0 / 3.141592653589793));
        return new float[] { player.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - player.rotationYaw), player.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - player.rotationPitch) };
    }
    
    public static float Random(final EntityLivingBase ent) {
        final int[] stage = { 2, 4, 6, 8, 10 };
        float Random = 0.0f;
        final boolean yes = false;
        for (int tick = 0; tick < 10; ++tick) {
            if (ent.hurtTime <= stage[0]) {
                tick = stage[0];
            }
            if (ent.hurtTime <= stage[1]) {
                tick = stage[1];
            }
            if (ent.hurtTime <= stage[2]) {
                tick = stage[2];
            }
            if (ent.hurtTime <= stage[3]) {
                tick = stage[3];
            }
            if (ent.hurtTime <= stage[4]) {
                tick = stage[4];
            }
            if (tick <= 2) {
                Random = (float)(Math.random() * -0.05000000074505806);
            }
            if (tick <= 4) {
                Random = 0.0f;
            }
            if (tick <= 6) {
                Random = 0.0f;
            }
            if (tick <= 8) {
                Random = 0.0f;
            }
            if (tick <= 10) {
                Random = (float)(Math.random() * 0.05000000074505806);
            }
        }
        if (Random > 1.0f) {
            Random = 0.0f;
        }
        return Random;
    }
    
    public static float[] getRotations1(final EntityLivingBase ent) {
        final double x = ent.posX;
        final double z = ent.posZ;
        final double y = ent.posY + ent.getEyeHeight() / 2.0f;
        final Vec3 enemyCoords = new Vec3(ent.posX, y, ent.posZ);
        final Vec3 myCoords = new Vec3(Helper.mc.thePlayer.posX, Helper.mc.thePlayer.posY + 1.35 + Random(ent) * 10.0f, Helper.mc.thePlayer.posZ);
        return getVecRotation(myCoords, enemyCoords);
    }
}
