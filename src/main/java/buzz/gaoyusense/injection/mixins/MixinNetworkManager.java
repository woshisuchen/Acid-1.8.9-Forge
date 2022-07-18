package buzz.gaoyusense.injection.mixins;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import acid.api.events.world.EventPacketSend;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import acid.api.EventBus;
import acid.api.events.world.EventPacketReceive;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.network.Packet;
import io.netty.channel.ChannelHandlerContext;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.spongepowered.asm.mixin.Final;
import java.util.Queue;
import org.spongepowered.asm.mixin.Shadow;
import io.netty.channel.Channel;
import net.minecraft.network.NetworkManager;
import org.spongepowered.asm.mixin.Mixin;
import buzz.gaoyusense.injection.interfaces.INetworkManager;

@Mixin({ NetworkManager.class })
public abstract class MixinNetworkManager implements INetworkManager
{
    @Shadow
    private Channel field_150746_k;
    @Final
    @Shadow
    private Queue field_150745_j;
    private final ReentrantReadWriteLock readWriteLock;
    
    public MixinNetworkManager() {
        this.readWriteLock = new ReentrantReadWriteLock();
    }
    
    @Inject(method = "channelRead0", at = { @At("HEAD") }, cancellable = true)
    private void read(final ChannelHandlerContext context, final Packet<?> packet, final CallbackInfo callback) {
        final EventPacketReceive event = new EventPacketReceive(packet);
        EventBus.getInstance().call(event);
        if (event.isCancelled()) {
            callback.cancel();
        }
    }
    
    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = { @At("HEAD") }, cancellable = true)
    private void send(final Packet<?> packet, final CallbackInfo callback) {
        final EventPacketSend event = new EventPacketSend(packet);
        EventBus.getInstance().call(event);
        if (event.isCancelled()) {
            callback.cancel();
        }
    }
    
    @Shadow
    public abstract boolean func_150724_d();
    
    @Override
    public void sendPacketNoEvent(final Packet packet) {
        if (this.field_150746_k != null && this.field_150746_k.isOpen()) {
            this.func_150733_h();
            this.func_150732_b(packet, null);
        }
        else {
            this.field_150745_j.add(new InboundHandlerTuplePacketListener(packet, (GenericFutureListener<? extends Future<? super Void>>[])null));
        }
    }
    
    @Override
    public void sendPacketSilent(final Packet packetIn) {
        if (this.func_150724_d()) {
            this.func_150733_h();
            this.func_150732_b(packetIn, null);
        }
        else {
            this.readWriteLock.writeLock().lock();
            try {
                this.field_150745_j.add(new InboundHandlerTuplePacketListener(packetIn, (GenericFutureListener<? extends Future<? super Void>>[])null));
            }
            finally {
                this.readWriteLock.writeLock().unlock();
            }
        }
    }
    
    @Shadow
    protected abstract void func_150732_b(final Packet p0, final GenericFutureListener[] p1);
    
    @Shadow
    protected abstract void func_150733_h();
}
