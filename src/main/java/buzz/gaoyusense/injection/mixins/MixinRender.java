package buzz.gaoyusense.injection.mixins;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.Render;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ Render.class })
public abstract class MixinRender
{
    @Shadow
    protected RenderManager renderManager;
    
    @Shadow
    public <T extends Entity> void doRender(final T entity, final double x, final double y, final double z, final float entityYaw, final float partialTicks) {
    }
    
    @Shadow
    protected abstract <T extends Entity> boolean bindEntityTexture(final T p0);
}
