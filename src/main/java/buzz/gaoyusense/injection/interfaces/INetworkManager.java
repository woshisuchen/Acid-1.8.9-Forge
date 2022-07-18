package buzz.gaoyusense.injection.interfaces;

import java.net.InetAddress;
import net.minecraft.network.Packet;

public interface INetworkManager
{
    void sendPacketNoEvent(final Packet p0);
    
    void sendPacketSilent(final Packet p0);
    
    void createNetworkManagerAndConnect(final InetAddress p0, final int p1, final boolean p2);
}
