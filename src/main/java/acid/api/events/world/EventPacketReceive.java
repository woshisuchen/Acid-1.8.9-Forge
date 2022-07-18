package acid.api.events.world;

import net.minecraft.network.Packet;
import acid.api.Event;

public class EventPacketReceive extends Event
{
    public static Packet packet;
    
    public EventPacketReceive(final Packet packet) {
        EventPacketReceive.packet = packet;
    }
    
    public static Packet getPacket() {
        return EventPacketReceive.packet;
    }
    
    public void setPacket(final Packet packet) {
        EventPacketReceive.packet = packet;
    }
}
