package buzz.gaoyusense.injection.mixins;

import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.client.settings.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import buzz.gaoyusense.injection.interfaces.IKeyBinding;

@Mixin({ KeyBinding.class })
public class MixinKeyBinding implements IKeyBinding
{
    @Shadow
    private boolean field_74513_e;
    
    @Override
    public boolean getPress() {
        return this.field_74513_e;
    }
    
    @Override
    public void setPress(final Boolean b) {
        this.field_74513_e = b;
    }
}
