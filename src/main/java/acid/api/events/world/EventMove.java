package acid.api.events.world;

import net.minecraft.potion.Potion;
import buzz.gaoyusense.injection.interfaces.IEntityPlayerSP;
import net.minecraft.client.Minecraft;
import acid.api.Event;

public class EventMove extends Event
{
    public double x;
    public double y;
    public double z;
    private boolean onGround;
    
    public EventMove(final double a, final double b, final double c) {
        this.x = a;
        this.y = b;
        this.z = c;
    }
    
    public double getX() {
        return this.x;
    }
    
    public void setX(final double x) {
        this.x = x;
    }
    
    public double getY() {
        return this.y;
    }
    
    public void setY(final double y) {
        this.y = y;
    }
    
    public double getZ() {
        return this.z;
    }
    
    public void setZ(final double z) {
        this.z = z;
    }
    
    public boolean isOnGround() {
        return this.onGround;
    }
    
    public void setOnGround(final boolean onGround) {
        this.onGround = onGround;
    }
    
    public void setSpeed(final double speed) {
        float yaw = Minecraft.getMinecraft().thePlayer.rotationYaw;
        double forward = Minecraft.getMinecraft().thePlayer.movementInput.moveForward;
        double strafe = Minecraft.getMinecraft().thePlayer.movementInput.moveStrafe;
        if (forward == 0.0 && strafe == 0.0) {
            this.setX(0.0);
            this.setZ(0.0);
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
                else {
                    forward = -1.0;
                }
            }
            final double cos = Math.cos(Math.toRadians(yaw + 90.0f));
            final double sin = Math.sin(Math.toRadians(yaw + 90.0f));
            this.setX(forward * speed * cos + strafe * speed * sin);
            this.setZ(forward * speed * sin - strafe * speed * cos);
        }
    }
    
    public void setSpeed(final double speed, float yaw, double strafe, double forward) {
        if (forward == 0.0 && strafe == 0.0) {
            this.setX(0.0);
            this.setZ(0.0);
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
                else {
                    forward = -1.0;
                }
            }
            final double cos = Math.cos(Math.toRadians(yaw + 90.0f));
            final double sin = Math.sin(Math.toRadians(yaw + 90.0f));
            this.setX(forward * speed * cos + strafe * speed * sin);
            this.setZ(forward * speed * sin - strafe * speed * cos);
        }
    }
    
    public void setMovementSpeed(final double movementSpeed) {
        this.setX(-(Math.sin(((IEntityPlayerSP)Minecraft.getMinecraft().thePlayer).getDirection()) * Math.max(movementSpeed, this.getMovementSpeed())));
        this.setZ(Math.cos(((IEntityPlayerSP)Minecraft.getMinecraft().thePlayer).getDirection()) * Math.max(movementSpeed, this.getMovementSpeed()));
    }
    
    public double getMovementSpeed() {
        double baseSpeed = 0.2873;
        if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.moveSpeed)) {
            final int amplifier = Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
        }
        return baseSpeed;
    }
    
    public double getMotionY(double mY) {
        if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.jump)) {
            mY += (Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1;
        }
        return mY;
    }
    
    public double getLegitMotion() {
        return 0.41999998688697815;
    }
}
