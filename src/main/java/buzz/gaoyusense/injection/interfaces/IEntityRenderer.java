package buzz.gaoyusense.injection.interfaces;

import net.minecraft.util.ResourceLocation;

public interface IEntityRenderer
{
    void runSetupCameraTransform(final float p0, final int p1);
    
    void loadShader2(final ResourceLocation p0);
}
