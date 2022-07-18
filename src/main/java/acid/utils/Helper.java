package acid.utils;

import buzz.gaoyusense.injection.interfaces.IMixinMinecraft;
import net.minecraft.util.Timer;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import acid.Client;
import net.minecraft.client.Minecraft;

public class Helper
{
    public static Minecraft mc;
    public static boolean canSendMotionPacket;
    
    public static void sendMessageOLD(final String msg) {
        final Object[] arrobject = new Object[2];
        Client.instance.getClass();
        arrobject[0] = EnumChatFormatting.BLUE + "ETB" + EnumChatFormatting.GRAY + ": ";
        arrobject[1] = msg;
        Helper.mc.thePlayer.addChatMessage((IChatComponent)new ChatComponentText(String.format("%s%s", arrobject)));
    }
    
    public static void sendMessage(final String message) {
        new ChatUtils.ChatMessageBuilder(true, true).appendText(message).setColor(EnumChatFormatting.GRAY).build().displayClientSided();
    }
    
    public static void sendMessageWithoutPrefix(final String message) {
        new ChatUtils.ChatMessageBuilder(false, true).appendText(message).setColor(EnumChatFormatting.GRAY).build().displayClientSided();
    }
    
    public static boolean onServer(final String server) {
        return !Helper.mc.isSingleplayer() && Helper.mc.getCurrentServerData().serverIP.toLowerCase().contains(server);
    }
    
    public static Timer getTimer() {
        return ((IMixinMinecraft)Minecraft.getMinecraft()).getTimer();
    }
    
    static {
        Helper.mc = Minecraft.getMinecraft();
        Helper.canSendMotionPacket = true;
    }
}
