package acid.utils;

import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.block.material.Material;
import buzz.gaoyusense.injection.interfaces.IKeyBinding;
import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockAir;
import net.minecraft.util.BlockPos;
import net.minecraft.item.ItemSword;
import net.minecraft.entity.Entity;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import net.minecraft.client.Minecraft;

public class PlayerUtils
{
    private static final Minecraft mc;
    
    public static boolean isMoving() {
        return !Helper.mc.thePlayer.isCollidedHorizontally && !Helper.mc.thePlayer.isSneaking() && (Helper.mc.thePlayer.movementInput.moveForward != 0.0f || Helper.mc.thePlayer.movementInput.moveStrafe != 0.0f);
    }
    
    public static float getDirection() {
        float yaw = Helper.mc.thePlayer.rotationYaw;
        final float forward = Helper.mc.thePlayer.moveForward;
        final float strafe = Helper.mc.thePlayer.moveStrafing;
        yaw += ((forward < 0.0f) ? 180 : 0);
        if (strafe < 0.0f) {
            yaw += ((forward < 0.0f) ? -45.0f : ((forward == 0.0f) ? 90.0f : 45.0f));
        }
        if (strafe > 0.0f) {
            yaw -= ((forward < 0.0f) ? -45.0f : ((forward == 0.0f) ? 90.0f : 45.0f));
        }
        return yaw * 0.017453292f;
    }
    
    public static void setSpeed(final double speed) {
        Helper.mc.thePlayer.motionX = -MathHelper.sin(getDirection()) * speed;
        Helper.mc.thePlayer.motionZ = MathHelper.cos(getDirection()) * speed;
    }
    
    public static float getSpeed() {
        final float vel = (float)Math.sqrt(Helper.mc.thePlayer.motionX * Helper.mc.thePlayer.motionX + Helper.mc.thePlayer.motionZ * Helper.mc.thePlayer.motionZ);
        return vel;
    }
    
    public static boolean isMoving2() {
        return Helper.mc.thePlayer.moveForward != 0.0f || Helper.mc.thePlayer.moveStrafing != 0.0f;
    }
    
    public static int getJumpEffect() {
        if (Helper.mc.thePlayer.isPotionActive(Potion.jump)) {
            return Helper.mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1;
        }
        return 0;
    }
    
    public static boolean isOnGround(final double height) {
        return !Helper.mc.theWorld.getCollidingBoundingBoxes((Entity)Helper.mc.thePlayer, Helper.mc.thePlayer.getEntityBoundingBox().offset(0.0, -height, 0.0)).isEmpty();
    }
    
    public static boolean isHoldingSword() {
        return Helper.mc.thePlayer.getCurrentEquippedItem() != null && Helper.mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword;
    }
    
    public static boolean isInLiquid() {
        boolean inLiquid = false;
        final AxisAlignedBB playerBB = PlayerUtils.mc.thePlayer.getEntityBoundingBox();
        final int y = (int)playerBB.minY;
        for (int x = MathHelper.floor_double(playerBB.minX); x < MathHelper.floor_double(playerBB.maxX) + 1; ++x) {
            for (int z = MathHelper.floor_double(playerBB.minZ); z < MathHelper.floor_double(playerBB.maxZ) + 1; ++z) {
                final Block block = PlayerUtils.mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
                if (block != null) {
                    if (!(block instanceof BlockAir)) {
                        if (!(block instanceof BlockLiquid)) {
                            return false;
                        }
                        inLiquid = true;
                    }
                }
            }
        }
        return inLiquid;
    }
    
    public static boolean MovementInput() {
        return ((IKeyBinding)PlayerUtils.mc.gameSettings.keyBindForward).getPress() || ((IKeyBinding)PlayerUtils.mc.gameSettings.keyBindLeft).getPress() || ((IKeyBinding)PlayerUtils.mc.gameSettings.keyBindRight).getPress() || ((IKeyBinding)PlayerUtils.mc.gameSettings.keyBindBack).getPress();
    }
    
    public static boolean isInWater() {
        return PlayerUtils.mc.theWorld.getBlockState(new BlockPos(PlayerUtils.mc.thePlayer.posX, PlayerUtils.mc.thePlayer.posY, PlayerUtils.mc.thePlayer.posZ)).getBlock().getMaterial() == Material.water;
    }
    
    public static void tellPlayer(final String string) {
        if (string != null && PlayerUtils.mc.thePlayer != null) {
            PlayerUtils.mc.thePlayer.addChatMessage((IChatComponent)new ChatComponentText("¡ìc[DEBUG] ¡ìr " + string));
        }
    }
    
    static {
        mc = Minecraft.getMinecraft();
    }
}
