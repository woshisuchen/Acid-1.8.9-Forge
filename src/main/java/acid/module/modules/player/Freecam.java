package acid.module.modules.player;

import net.minecraft.network.Packet;
import acid.utils.MovementUtils;
import net.minecraft.util.IChatComponent;
import net.minecraft.event.HoverEvent;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ChatComponentText;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.util.AxisAlignedBB;
import acid.api.events.misc.EventCollideWithBlock;
import net.minecraft.network.play.client.C03PacketPlayer;
import acid.api.events.world.EventPacketReceive;
import acid.api.EventHandler;
import net.minecraft.entity.player.PlayerCapabilities;
import acid.api.events.world.EventPreUpdate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import java.awt.Color;
import acid.module.ModuleType;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import acid.module.Module;

public class Freecam extends Module
{
    private EntityOtherPlayerMP copy;
    private double x;
    private double y;
    private double z;
    
    public Freecam() {
        super("Freecam", new String[] { "Freecam" }, ModuleType.World);
        this.setColor(new Color(221, 214, 51).getRGB());
    }
    
    @Override
    public void onEnable() {
        (this.copy = new EntityOtherPlayerMP((World)this.mc.theWorld, this.mc.thePlayer.getGameProfile())).clonePlayer((EntityPlayer)this.mc.thePlayer, true);
        this.copy.setLocationAndAngles(this.mc.thePlayer.posX, this.mc.thePlayer.posY, this.mc.thePlayer.posZ, this.mc.thePlayer.rotationYaw, this.mc.thePlayer.rotationPitch);
        this.copy.rotationYawHead = this.mc.thePlayer.rotationYawHead;
        this.copy.setEntityId(-1337);
        this.copy.setSneaking(this.mc.thePlayer.isSneaking());
        this.mc.theWorld.addEntityToWorld(this.copy.getEntityId(), (Entity)this.copy);
        this.x = this.mc.thePlayer.posX;
        this.y = this.mc.thePlayer.posY;
        this.z = this.mc.thePlayer.posZ;
    }
    
    @EventHandler
    private void onPreMotion(final EventPreUpdate e) {
        final PlayerCapabilities paky = new PlayerCapabilities();
        paky.isFlying = true;
        this.mc.thePlayer.noClip = true;
        this.mc.thePlayer.capabilities.setFlySpeed(0.1f);
        e.setCancelled(true);
    }
    
    @EventHandler
    private void onPacketSend(final EventPacketReceive e) {
        if (EventPacketReceive.getPacket() instanceof C03PacketPlayer) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    private void onBB(final EventCollideWithBlock e) {
        e.setBoundingBox(null);
    }
    
    @Override
    public void onDisable() {
        final int x = (int)this.mc.thePlayer.posX;
        final int y = (int)this.mc.thePlayer.posY;
        final int z = (int)this.mc.thePlayer.posZ;
        final IChatComponent ChatComponent = (IChatComponent)new ChatComponentText(String.format("%s%s", ChatFormatting.GREEN + "Acid" + " > " + ChatFormatting.GRAY, this.mc.thePlayer.getName() + ChatFormatting.GOLD + " [" + x + "," + y + "," + z + "]"));
        ChatComponent.appendSibling(new ChatComponentText(EnumChatFormatting.RED + " [点我TP到位置]").setChatStyle(new ChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, new StringBuilder().insert(0, ".tp ").append(x + " " + y + " " + z).toString())).setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (IChatComponent)new ChatComponentText("点击TP到玩家：" + this.mc.thePlayer.getName())))));
        this.mc.thePlayer.addChatMessage(ChatComponent);
        MovementUtils.setMotion(0.0);
        this.mc.thePlayer.setLocationAndAngles(this.copy.posX, this.copy.posY, this.copy.posZ, this.copy.rotationYaw, this.copy.rotationPitch);
        this.mc.thePlayer.rotationYawHead = this.copy.rotationYawHead;
        this.mc.theWorld.removeEntityFromWorld(this.copy.getEntityId());
        this.mc.thePlayer.setSneaking(this.copy.isSneaking());
        this.copy = null;
        this.mc.renderGlobal.loadRenderers();
        this.mc.thePlayer.setPosition(this.x, this.y, this.z);
        this.mc.getNetHandler().addToSendQueue((Packet)new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 0.01, this.mc.thePlayer.posZ, this.mc.thePlayer.onGround));
        final PlayerCapabilities paky = new PlayerCapabilities();
        paky.isFlying = false;
        this.mc.thePlayer.noClip = false;
        this.mc.theWorld.removeEntityFromWorld(-1);
    }
}
