package buzz.gaoyusense.injection.mixins;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import acid.api.EventBus;
import acid.api.events.misc.EventChat;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.util.IChatComponent;
import net.minecraft.client.gui.GuiNewChat;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ GuiNewChat.class })
public class MixinGuiNewChat
{
    @Inject(method = "printChatMessageWithOptionalDeletion", at = { @At("RETURN") }, cancellable = true)
    public void printChatMessageWithOptionalDeletion(final IChatComponent chatComponent, final int chatLineId, final CallbackInfo ci) {
        final EventChat ec = new EventChat(chatComponent.getUnformattedText());
        EventBus.getInstance().call(ec);
    }
}
