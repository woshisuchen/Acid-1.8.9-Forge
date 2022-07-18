package buzz.gaoyusense.injection.mixins;

import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.client.renderer.entity.RenderManager;
import org.spongepowered.asm.mixin.Mixin;
import buzz.gaoyusense.injection.interfaces.IRenderManager;

@Mixin({ RenderManager.class })
public class MixinRenderManager implements IRenderManager
{
    @Shadow
    private double field_78725_b;
    @Shadow
    private double field_78726_c;
    @Shadow
    private double field_78723_d;
    
    @Override
    public double getRenderPosX() {
        return this.field_78725_b;
    }
    
    @Override
    public double getRenderPosY() {
        return this.field_78726_c;
    }
    
    @Override
    public double getRenderPosZ() {
        return this.field_78723_d;
    }
}
