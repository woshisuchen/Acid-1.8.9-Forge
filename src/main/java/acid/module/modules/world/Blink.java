package acid.module.modules.world;

import java.util.Iterator;
import acid.api.EventHandler;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import acid.api.events.world.EventPacketSend;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import com.mojang.authlib.GameProfile;
import java.util.UUID;
import java.awt.Color;
import java.util.ArrayList;
import acid.module.ModuleType;
import net.minecraft.network.Packet;
import java.util.List;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import acid.module.Module;

public class Blink extends Module
{
    private EntityOtherPlayerMP blinkEntity;
    private List<Packet> packetList;
    
    //acid 大神赢了利息 哈哈哈
    public Blink() {
        super("Blink", new String[] { "blonk" }, ModuleType.Player);
        this.packetList = new ArrayList<Packet>();
    }
    
    @Override
    public void onEnable() {
        this.setColor(new Color(200, 100, 200).getRGB());
        if (this.mc.thePlayer == null) {
            return;
        }
        this.blinkEntity = new EntityOtherPlayerMP((World)this.mc.theWorld, new GameProfile(new UUID(69L, 96L), "Blink"));
        this.blinkEntity.inventory = this.mc.thePlayer.inventory;
        this.blinkEntity.inventoryContainer = this.mc.thePlayer.inventoryContainer;
        this.blinkEntity.setPositionAndRotation(this.mc.thePlayer.posX, this.mc.thePlayer.posY, this.mc.thePlayer.posZ, this.mc.thePlayer.rotationYaw, this.mc.thePlayer.rotationPitch);
        this.blinkEntity.rotationYawHead = this.mc.thePlayer.rotationYawHead;
        this.mc.theWorld.addEntityToWorld(this.blinkEntity.getEntityId(), (Entity)this.blinkEntity);
    }
    
    @EventHandler
    private void onPacketSend(final EventPacketSend event) {
        if (EventPacketSend.getPacket() instanceof C0BPacketEntityAction || EventPacketSend.getPacket() instanceof C03PacketPlayer || EventPacketSend.getPacket() instanceof C02PacketUseEntity || EventPacketSend.getPacket() instanceof C0APacketAnimation || EventPacketSend.getPacket() instanceof C08PacketPlayerBlockPlacement) {
            this.packetList.add(EventPacketSend.getPacket());
            event.setCancelled(true);
        }
    }
    
    @Override
    public void onDisable() {
        for (final Packet packet : this.packetList) {
            this.mc.getNetHandler().addToSendQueue(packet);
        }
        this.packetList.clear();
        this.mc.theWorld.removeEntityFromWorld(this.blinkEntity.getEntityId());
    }
}
