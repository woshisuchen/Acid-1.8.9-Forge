package buzz.gaoyusense.injection.mixins;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.Packet;

class InboundHandlerTuplePacketListener
{
    private final Packet packet;
    private final GenericFutureListener<? extends Future<? super Void>>[] futureListeners;
    
    public InboundHandlerTuplePacketListener(final Packet inPacket, final GenericFutureListener<? extends Future<? super Void>>... inFutureListeners) {
        this.packet = inPacket;
        this.futureListeners = inFutureListeners;
    }
}
