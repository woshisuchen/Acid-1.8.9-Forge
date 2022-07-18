package buzz.gaoyusense.injection.mixins;

import org.spongepowered.asm.mixin.Overwrite;
import net.minecraft.scoreboard.Team;
import net.minecraft.scoreboard.ScorePlayerTeam;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.client.network.NetworkPlayerInfo;
import com.google.common.collect.Ordering;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import org.spongepowered.asm.mixin.Mixin;
import buzz.gaoyusense.injection.interfaces.IGuiPlayerTabOverlay;

@Mixin({ GuiPlayerTabOverlay.class })
public class MixinGuiPlayerTabOverlay implements IGuiPlayerTabOverlay
{
    @Shadow
    @Final
    private static Ordering<NetworkPlayerInfo> field_175252_a;
    
    @Override
    public Ordering<NetworkPlayerInfo> getField() {
        return MixinGuiPlayerTabOverlay.field_175252_a;
    }
    
    @Overwrite
    public String getPlayerName(final NetworkPlayerInfo networkPlayerInfoIn) {
        final String prefix = "";
        String result = "";
        if (networkPlayerInfoIn.getDisplayName() != null) {
            result = prefix + networkPlayerInfoIn.getDisplayName().getFormattedText();
        }
        else {
            result = prefix + ScorePlayerTeam.formatPlayerName((Team)networkPlayerInfoIn.getPlayerTeam(), networkPlayerInfoIn.getGameProfile().getName());
        }
        return result;
    }
}
