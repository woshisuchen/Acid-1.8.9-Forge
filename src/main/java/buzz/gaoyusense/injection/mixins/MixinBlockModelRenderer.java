package buzz.gaoyusense.injection.mixins;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import acid.module.Module;
import acid.management.ModuleManager;
import acid.module.modules.render.Xray;
import acid.Client;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.BlockPos;
import net.minecraft.block.Block;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.world.IBlockAccess;
import net.minecraft.client.renderer.BlockModelRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ BlockModelRenderer.class })
public abstract class MixinBlockModelRenderer
{
    @Inject(method = "renderModelAmbientOcclusion", at = { @At("HEAD") }, cancellable = true)
    private void renderModelAmbientOcclusion(final IBlockAccess blockAccessIn, final IBakedModel modelIn, final Block blockIn, final BlockPos blockPosIn, final WorldRenderer worldRendererIn, final boolean checkSide, final CallbackInfoReturnable<Boolean> booleanCallbackInfoReturnable) {
        Client.getModuleManager();
        final Xray xray = (Xray)ModuleManager.getModuleByClass(Xray.class);
        if (xray.isEnabled() && !xray.getBlocks().contains(blockIn)) {
            booleanCallbackInfoReturnable.setReturnValue(false);
        }
    }
    
    @Inject(method = "renderModelStandard", at = { @At("HEAD") }, cancellable = true)
    private void renderModelStandard(final IBlockAccess blockAccessIn, final IBakedModel modelIn, final Block blockIn, final BlockPos blockPosIn, final WorldRenderer worldRendererIn, final boolean checkSides, final CallbackInfoReturnable<Boolean> booleanCallbackInfoReturnable) {
        Client.getModuleManager();
        final Xray xray = (Xray)ModuleManager.getModuleByClass(Xray.class);
        if (xray.isEnabled() && !xray.getBlocks().contains(blockIn)) {
            booleanCallbackInfoReturnable.setReturnValue(false);
        }
    }
}
