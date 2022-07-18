package buzz.gaoyusense.injection.interfaces;

import net.minecraft.client.network.NetworkPlayerInfo;
import com.google.common.collect.Ordering;

public interface IGuiPlayerTabOverlay
{
    Ordering<NetworkPlayerInfo> getField();
}
