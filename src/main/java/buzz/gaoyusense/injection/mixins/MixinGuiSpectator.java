package buzz.gaoyusense.injection.mixins;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import acid.api.events.rendering.EventRender2D;
import acid.api.EventBus;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.GuiSpectator;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ GuiSpectator.class })
public class MixinGuiSpectator
{
    @Inject(method = "renderTooltip", at = { @At("RETURN") })
    private void renderTooltipPost(final ScaledResolution p_175264_1_, final float p_175264_2_, final CallbackInfo callbackInfo) {
        EventBus.getInstance().call(new EventRender2D(p_175264_1_, p_175264_2_));
    }
}
