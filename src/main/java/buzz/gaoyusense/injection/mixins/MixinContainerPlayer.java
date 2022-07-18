package buzz.gaoyusense.injection.mixins;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import acid.api.EventBus;
import acid.api.events.misc.EventInventory;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerPlayer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ ContainerPlayer.class })
public class MixinContainerPlayer
{
    @Inject(method = "onContainerClosed", at = { @At("HEAD") }, cancellable = true)
    public void onContainerClosed(final EntityPlayer playerIn, final CallbackInfo ci) {
        final EventInventory event = new EventInventory(playerIn);
        EventBus.getInstance().call(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}
