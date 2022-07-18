package buzz.gaoyusense.injection.mixins;

import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import buzz.gaoyusense.injection.interfaces.IEntityPlayer;

@Mixin({ EntityPlayer.class })
public class MixinEntityPlayer implements IEntityPlayer
{
    @Shadow
    public int field_71072_f;
    @Shadow
    protected float field_71102_ce;
    
    @Override
    public void setSpeedInAir(final float i) {
        this.field_71102_ce = i;
    }
    
    @Override
    public void setItemInUseCount(final int i) {
        this.field_71072_f = i;
    }
}
