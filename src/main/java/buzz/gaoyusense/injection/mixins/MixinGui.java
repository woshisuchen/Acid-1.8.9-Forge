package buzz.gaoyusense.injection.mixins;

import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ Gui.class })
public abstract class MixinGui
{
    @Shadow
    protected float field_73735_i;
}
