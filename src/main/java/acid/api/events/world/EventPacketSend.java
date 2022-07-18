package acid.api.events.world;

import net.minecraft.network.Packet;
import acid.api.Event;

public class EventPacketSend extends Event
{
    public static Packet packet;
    private boolean sendPacketInEvent;
    
    public EventPacketSend(final Packet packet) {
        EventPacketSend.packet = packet;
        this.sendPacketInEvent = false;
    }
    
    public static Packet getPacket() {
        return EventPacketSend.packet;
    }
    
    public void setPacket(final Packet packet) {
        EventPacketSend.packet = packet;
    }
    
    public void sendPacketInEvent() {
        this.sendPacketInEvent = true;
    }
    
    public boolean isSendPacketInEvent() {
        return this.sendPacketInEvent;
    }
}
