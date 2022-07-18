package buzz.gaoyusense.injection.mixins;

import org.spongepowered.asm.mixin.Overwrite;
import acid.module.Module;
import acid.management.ModuleManager;
import acid.module.modules.combat.Reach;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import acid.api.events.world.EventAttack;
import acid.api.EventBus;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.world.WorldSettings;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import org.spongepowered.asm.mixin.Mixin;
import buzz.gaoyusense.injection.interfaces.IPlayerControllerMP;

@Mixin({ PlayerControllerMP.class })
public class MixinPlayerControllerMP implements IPlayerControllerMP
{
    @Shadow
    private WorldSettings.GameType currentGameType;
    @Shadow
    private float curBlockDamageMP;
    @Shadow
    private int blockHitDelay;
    
    @Inject(method = "attackEntity", at = { @At("HEAD") })
    public void attack(final EntityPlayer playerIn, final Entity targetEntity, final CallbackInfo info) {
        EventBus.getInstance().call(new EventAttack(targetEntity, true));
    }
    
    @Inject(method = "attackEntity", at = { @At("RETURN") })
    public void attack2(final EntityPlayer playerIn, final Entity targetEntity, final CallbackInfo info) {
        EventBus.getInstance().call(new EventAttack(targetEntity, false));
    }
    
    @Overwrite
    public float getBlockReachDistance() {
        if (ModuleManager.getModuleByClass(Reach.class).isEnabled()) {
            return (float)Reach.getReach() + 1.5f;
        }
        return this.currentGameType.isCreative() ? 5.0f : 4.5f;
    }
    
    @Override
    public float getCurBlockDamageMP() {
        return this.curBlockDamageMP;
    }
    
    @Override
    public int getBlockDELAY() {
        return this.blockHitDelay;
    }
    
    @Override
    public void setCurBlockDamageMP(final float f) {
        this.curBlockDamageMP = f;
    }
    
    @Override
    public void setBlockHitDelay(final int i) {
        this.blockHitDelay = i;
    }
}
