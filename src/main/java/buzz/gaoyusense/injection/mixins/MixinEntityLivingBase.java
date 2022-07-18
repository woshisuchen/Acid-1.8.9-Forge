package buzz.gaoyusense.injection.mixins;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import acid.module.Module;
import acid.management.ModuleManager;
import acid.module.modules.player.NoJumpDelay;
import acid.Client;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.Overwrite;
import net.minecraft.util.MathHelper;
import acid.api.EventBus;
import acid.api.events.misc.EventJump;
import acid.module.modules.movement.Sprint;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.Potion;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import buzz.gaoyusense.injection.interfaces.IEntityLivingBase;
import net.minecraft.entity.Entity;

@Mixin({ EntityLivingBase.class })
public abstract class MixinEntityLivingBase extends Entity implements IEntityLivingBase
{
    @Shadow
    private int jumpTicks;
    
    public MixinEntityLivingBase() {
        super((World)null);
    }
    
    @Shadow
    protected abstract int getArmSwingAnimationEnd();
    
    @Shadow
    protected abstract float getJumpUpwardsMotion();
    
    @Shadow
    public abstract PotionEffect getActivePotionEffect(final Potion p0);
    
    @Shadow
    public abstract boolean isPotionActive(final Potion p0);
    
    public int runGetArmSwingAnimationEnd() {
        return this.getArmSwingAnimationEnd();
    }
    
    public int getJumpTicks() {
        return this.jumpTicks;
    }
    
    @Overwrite
    protected void jump() {
        final double ymot = (!Sprint.ww.getValue() && this.isPotionActive(Potion.jump)) ? (this.getJumpUpwardsMotion() + (this.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1f) : ((double)this.getJumpUpwardsMotion());
        final EventJump ej = new EventJump(ymot, true);
        EventBus.getInstance().register(ej);
        if (ej.isCancelled()) {
            return;
        }
        this.motionY = ej.getMotionY();
        if (!Sprint.ww2.getValue() && this.isPotionActive(Potion.jump)) {
            this.motionY += (this.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1f;
        }
        if (this.isSprinting()) {
            final float f = this.rotationYaw * 0.017453292f;
            this.motionX -= MathHelper.sin(f) * 0.2f;
            this.motionZ += MathHelper.cos(f) * 0.2f;
        }
        this.isAirBorne = true;
    }
    
    @Inject(method = "onLivingUpdate", at = { @At("HEAD") })
    private void headLiving(final CallbackInfo callbackInfo) {
        Client.getModuleManager();
        if (ModuleManager.getModuleByClass(NoJumpDelay.class).isEnabled()) {
            this.jumpTicks = 0;
        }
    }
    
    public void setJumpTicks(final int a) {
        this.jumpTicks = a;
    }
}
