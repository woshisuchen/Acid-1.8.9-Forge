package acid.utils;

import java.util.List;
import java.util.Comparator;
import java.util.Arrays;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.util.MathHelper;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.block.BlockAir;
import acid.api.events.world.EventPreUpdate;
import acid.api.events.world.EventMove;
import net.minecraft.client.Minecraft;

public class MovementUtils
{
    private static final Minecraft mc;
    
    public static void setSpeed(final EventMove moveEvent, final double moveSpeed, final float pseudoYaw, final double pseudoStrafe, final double pseudoForward) {
        double forward = pseudoForward;
        double strafe = pseudoStrafe;
        float yaw = pseudoYaw;
        if (forward == 0.0 && strafe == 0.0) {
            moveEvent.setZ(0.0);
            moveEvent.setX(0.0);
        }
        else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += ((forward > 0.0) ? -45 : 45);
                }
                else if (strafe < 0.0) {
                    yaw += ((forward > 0.0) ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                }
                else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            final double cos = Math.cos(Math.toRadians(yaw + 90.0f));
            final double sin = Math.sin(Math.toRadians(yaw + 90.0f));
            moveEvent.setX(forward * moveSpeed * cos + strafe * moveSpeed * sin);
            moveEvent.setZ(forward * moveSpeed * sin - strafe * moveSpeed * cos);
        }
    }
    
    public static void setMotion(final EventMove moveEvent, final double moveSpeed, final double pseudoForward, final double pseudoStrafe) {
        double forward = pseudoForward;
        double strafe = MovementUtils.mc.thePlayer.movementInput.moveStrafe;
        float yaw = MovementUtils.mc.thePlayer.rotationYaw;
        if (forward == 0.0 && strafe == 0.0) {
            moveEvent.setZ(0.0);
            moveEvent.setX(0.0);
        }
        else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += ((forward > 0.0) ? -45 : 45);
                }
                else if (strafe < 0.0) {
                    yaw += ((forward > 0.0) ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                }
                else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            final double cos = Math.cos(Math.toRadians(yaw + 90.0f));
            final double sin = Math.sin(Math.toRadians(yaw + 90.0f));
            moveEvent.setX((forward * moveSpeed * cos + strafe * moveSpeed * sin) * pseudoStrafe);
            moveEvent.setZ((forward * moveSpeed * sin - strafe * moveSpeed * cos) * pseudoStrafe);
        }
    }
    
    public static void setCockSpeed(final EventPreUpdate moveEvent, final double moveSpeed, final float pseudoYaw, final double pseudoStrafe, final double pseudoForward) {
        double forward = pseudoForward;
        double strafe = pseudoStrafe;
        float yaw = pseudoYaw;
        if (forward == 0.0 && strafe == 0.0) {
            moveEvent.setZ(0.0);
            moveEvent.setX(0.0);
        }
        else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += ((forward > 0.0) ? -45 : 45);
                }
                else if (strafe < 0.0) {
                    yaw += ((forward > 0.0) ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                }
                else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            final double cos = Math.cos(Math.toRadians(yaw + 90.0f));
            final double sin = Math.sin(Math.toRadians(yaw + 90.0f));
            moveEvent.setX(forward * moveSpeed * cos + strafe * moveSpeed * sin);
            moveEvent.setZ(forward * moveSpeed * sin - strafe * moveSpeed * cos);
        }
    }
    
    public static boolean isBlockAbovePlayer() {
        return !(MovementUtils.mc.theWorld.getBlockState(new BlockPos(MovementUtils.mc.thePlayer.posX, MovementUtils.mc.thePlayer.getEntityBoundingBox().maxY + 0.41999998688697815, MovementUtils.mc.thePlayer.posZ)).getBlock() instanceof BlockAir);
    }
    
    public static void setPos(final double x, final double y, final double z) {
        MovementUtils.mc.thePlayer.setPosition(MovementUtils.mc.thePlayer.posX + x, MovementUtils.mc.thePlayer.posY + y, MovementUtils.mc.thePlayer.posZ + z);
    }
    
    public static double getBaseMoveSpeed3(final double speed) {
        double baseSpeed = speed;
        if (MovementUtils.mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            baseSpeed *= 1.0 + 0.2 * (MovementUtils.mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
        }
        return baseSpeed;
    }
    
    public static double getJumpBoostModifier(double baseJumpHeight) {
        if (MovementUtils.mc.thePlayer.isPotionActive(Potion.jump)) {
            final int amplifier = MovementUtils.mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier();
            baseJumpHeight += (amplifier + 1) * 0.1f;
        }
        return baseJumpHeight;
    }
    
    public static boolean isOnGround() {
        return MovementUtils.mc.thePlayer.onGround && MovementUtils.mc.thePlayer.isCollidedVertically;
    }
    
    public static boolean isMoving() {
        return MovementUtils.mc.thePlayer != null && (MovementUtils.mc.thePlayer.movementInput.moveForward != 0.0f || MovementUtils.mc.thePlayer.movementInput.moveStrafe != 0.0f);
    }
    
    public static boolean isOnGround(final double height) {
        return !MovementUtils.mc.theWorld.getCollidingBoundingBoxes((Entity)MovementUtils.mc.thePlayer, MovementUtils.mc.thePlayer.getEntityBoundingBox().offset(0.0, -height, 0.0)).isEmpty();
    }
    
    public static int getSpeedEffect() {
        if (MovementUtils.mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            return MovementUtils.mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1;
        }
        return 0;
    }
    
    public static void setSpeed(final EventMove moveEvent, final double moveSpeed, final double v1) {
        double forward = MovementUtils.mc.thePlayer.movementInput.moveForward;
        double strafe = MovementUtils.mc.thePlayer.movementInput.moveStrafe;
        float yaw = MovementUtils.mc.thePlayer.rotationYaw;
        if (forward == 0.0 && strafe == 0.0) {
            moveEvent.setX(0.0);
            moveEvent.setZ(0.0);
        }
        else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += ((forward > 0.0) ? -45 : 45);
                }
                else if (strafe < 0.0) {
                    yaw += ((forward > 0.0) ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                }
                else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            final double v2 = Math.sin(Math.toRadians(yaw));
            final double v3 = Math.cos(Math.toRadians(yaw));
            moveEvent.setX((forward * moveSpeed * -v2 + strafe * moveSpeed * v3) * v1);
            moveEvent.setZ((forward * moveSpeed * v3 - strafe * moveSpeed * -v2) * v1);
        }
    }
    
    public static void setSpeed(final double moveSpeed, final double v1) {
        double forward = MovementUtils.mc.thePlayer.movementInput.moveForward;
        double strafe = MovementUtils.mc.thePlayer.movementInput.moveStrafe;
        float yaw = MovementUtils.mc.thePlayer.rotationYaw;
        if (forward == 0.0 && strafe == 0.0) {
            MovementUtils.mc.thePlayer.motionX = 0.0;
            MovementUtils.mc.thePlayer.motionZ = 0.0;
        }
        else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += ((forward > 0.0) ? -45 : 45);
                }
                else if (strafe < 0.0) {
                    yaw += ((forward > 0.0) ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                }
                else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            MovementUtils.mc.thePlayer.motionX = (forward * moveSpeed * Math.cos(Math.toRadians(yaw + 88.0)) + strafe * moveSpeed * Math.sin(Math.toRadians((yaw += (float)W(-2.5, 2.5)) + 87.9000015258789))) * v1;
            MovementUtils.mc.thePlayer.motionZ = (forward * moveSpeed * Math.sin(Math.toRadians(yaw + 88.0)) - strafe * moveSpeed * Math.cos(Math.toRadians(yaw + 87.9000015258789))) * v1;
        }
    }
    
    public static double W(final double a, final double a2) {
        final Random a3 = new Random();
        double a4 = a3.nextDouble() * (a2 - a);
        if (a4 > a2) {
            a4 = a2;
        }
        final double a5;
        if ((a5 = a4 + a) <= a2) {
            return a5;
        }
        return a2;
    }
    
    public static void setMotion(final double speed) {
        double forward = MovementUtils.mc.thePlayer.movementInput.moveForward;
        double strafe = MovementUtils.mc.thePlayer.movementInput.moveStrafe;
        double yaw = MovementUtils.mc.thePlayer.rotationYaw;
        if (forward == 0.0 && strafe == 0.0) {
            MovementUtils.mc.thePlayer.motionX = 0.0;
            MovementUtils.mc.thePlayer.motionZ = 0.0;
        }
        else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += ((forward > 0.0) ? -35 : 35);
                }
                else if (strafe < 0.0) {
                    yaw += ((forward > 0.0) ? 35 : -35);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                }
                else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            final double v9 = Math.sin(Math.toRadians(yaw));
            final double v10 = Math.cos(Math.toRadians(yaw));
            MovementUtils.mc.thePlayer.motionX = forward * speed * -v9 + strafe * speed * v10;
            MovementUtils.mc.thePlayer.motionZ = forward * speed * v10 - strafe * speed * -v9;
        }
    }
    
    public static float getSpeed() {
        return (float)Math.sqrt(MovementUtils.mc.thePlayer.motionX * MovementUtils.mc.thePlayer.motionX + MovementUtils.mc.thePlayer.motionZ * MovementUtils.mc.thePlayer.motionZ);
    }
    
    public static double getJumpBoostModifier(double baseJumpHeight, final boolean potionJumpHeight) {
        if (MovementUtils.mc.thePlayer.isPotionActive(Potion.jump) && potionJumpHeight) {
            final int amplifier = MovementUtils.mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier();
            baseJumpHeight += (amplifier + 1) * 0.1f;
        }
        return baseJumpHeight;
    }
    
    public static boolean isInLiquid() {
        if (MovementUtils.mc.thePlayer.isInWater()) {
            return true;
        }
        boolean inLiquid = false;
        final int y = (int)MovementUtils.mc.thePlayer.getEntityBoundingBox().minY;
        for (int x = MathHelper.floor_double(MovementUtils.mc.thePlayer.getEntityBoundingBox().minX); x < MathHelper.floor_double(MovementUtils.mc.thePlayer.getEntityBoundingBox().maxX) + 1; ++x) {
            for (int z = MathHelper.floor_double(MovementUtils.mc.thePlayer.getEntityBoundingBox().minZ); z < MathHelper.floor_double(MovementUtils.mc.thePlayer.getEntityBoundingBox().maxZ) + 1; ++z) {
                final Block block = MovementUtils.mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
                if (block != null && block.getMaterial() != Material.air) {
                    if (!(block instanceof BlockLiquid)) {
                        return false;
                    }
                    inLiquid = true;
                }
            }
        }
        return inLiquid;
    }
    
    public static double getJumpHeight(final double baseJumpHeight) {
        if (isInLiquid()) {
            return 0.13499999955296516;
        }
        if (MovementUtils.mc.thePlayer.isPotionActive(Potion.jump)) {
            return baseJumpHeight + (MovementUtils.mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1.0f) * 0.1f;
        }
        return baseJumpHeight;
    }
    
    public static double getJumpHeight() {
        return getJumpHeight(0.41999998688697815);
    }
    
    public static float getSensitivityMultiplier() {
        final float f = Minecraft.getMinecraft().gameSettings.mouseSensitivity * 0.6f + 0.2f;
        return f * f * f * 8.0f * 0.15f;
    }
    
    public static void bypassOffSet(final EventPreUpdate event) {
        if (isMoving()) {
            final List<Double> BypassOffset = Arrays.asList(0.125, 0.25, 0.375, 0.625, 0.75, 0.015625, 0.5, 0.0625, 0.875, 0.1875);
            final double d3 = event.getY() % 1.0;
            BypassOffset.sort(Comparator.comparingDouble(PreY -> Math.abs(PreY - d3)));
            double acc = event.getY() - d3 + BypassOffset.get(0);
            if (Math.abs(BypassOffset.get(0) - d3) < 0.005) {
                EventPreUpdate.setY(acc);
                EventPreUpdate.setOnground(true);
            }
            else {
                final List<Double> BypassOffset2 = Arrays.asList(0.715, 0.945, 0.09, 0.155, 0.14, 0.045, 0.63, 0.31);
                final double d3_ = event.getY() % 1.0;
                BypassOffset2.sort(Comparator.comparingDouble(PreY -> Math.abs(PreY - d3_)));
                acc = event.getY() - d3_ + BypassOffset2.get(0);
                if (Math.abs(BypassOffset2.get(0) - d3_) < 0.005) {
                    EventPreUpdate.setY(acc);
                }
            }
        }
    }
    
    public static void setSpeed(final EventMove moveEvent, final double moveSpeed) {
        setSpeed(moveEvent, moveSpeed, MovementUtils.mc.thePlayer.rotationYaw, MovementUtils.mc.thePlayer.movementInput.moveStrafe, MovementUtils.mc.thePlayer.movementInput.moveForward);
    }
    
    static {
        mc = Minecraft.getMinecraft();
    }
}
