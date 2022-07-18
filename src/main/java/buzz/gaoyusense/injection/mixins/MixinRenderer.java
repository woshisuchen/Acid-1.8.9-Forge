package buzz.gaoyusense.injection.mixins;

import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.client.renderer.entity.Render;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.entity.Entity;

@Mixin({ Render.class })
abstract class MixinRenderer<T extends Entity>
{
    @Shadow
    protected abstract boolean bindEntityTexture(final T p0);
}
