package acid.module.modules.world;

import acid.api.EventHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import io.netty.buffer.Unpooled;
import java.io.ByteArrayOutputStream;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import acid.api.events.world.EventPacketSend;
import acid.module.ModuleType;
import acid.module.Module;

public class LunarSpoof extends Module
{
    public LunarSpoof() {
        super("LunarSpoof", new String[] { "LunarSpoof", "LunarSpoof" }, ModuleType.World);
    }
    
    @EventHandler
    public void Send(final EventPacketSend e) {
        if (EventPacketSend.getPacket() instanceof C17PacketCustomPayload) {
            final C17PacketCustomPayload pay = (C17PacketCustomPayload)EventPacketSend.getPacket();
            if (pay.getChannelName().equalsIgnoreCase("MC|Brand")) {
                final ByteArrayOutputStream b = new ByteArrayOutputStream();
                final ByteBuf message = Unpooled.buffer();
                message.writeBytes("Lunar-Client".getBytes());
                this.mc.thePlayer.sendQueue.addToSendQueue((Packet)new C17PacketCustomPayload("Registered", new PacketBuffer(message)));
            }
        }
    }
}
