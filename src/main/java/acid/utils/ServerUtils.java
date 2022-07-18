package acid.utils;

import java.util.HashMap;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiMultiplayer;
import acid.ui.MainMenu;
import net.minecraft.client.multiplayer.ServerData;
import java.util.Map;

public final class ServerUtils
{
    private static final Map<String, Long> serverIpPingCache;
    private static final String HYPIXEL = "hypixel.net";
    public static ServerData serverData;
    
    public static void update(final String ip, final long ping) {
        ServerUtils.serverIpPingCache.put(ip, ping);
    }
    
    public static long getPingToServer(final String ip) {
        return ServerUtils.serverIpPingCache.get(ip);
    }
    
    public static boolean isOnServer(final String ip) {
        return !Helper.mc.isSingleplayer() && getCurrentServerIP().endsWith(ip);
    }
    
    public static String getCurrentServerIP() {
        return Helper.mc.isSingleplayer() ? "Singleplayer" : Helper.mc.getCurrentServerData().serverIP;
    }
    
    public static boolean isOnHypixel() {
        return isOnServer("hypixel.net");
    }
    
    public static long getPingToCurrentServer() {
        return Helper.mc.isSingleplayer() ? 0L : getPingToServer(getCurrentServerIP());
    }
    
    public static void connectToLastServer() {
        if (ServerUtils.serverData == null) {
            return;
        }
        Helper.mc.displayGuiScreen((GuiScreen)new GuiConnecting((GuiScreen)new GuiMultiplayer((GuiScreen)new MainMenu()), Helper.mc, ServerUtils.serverData));
    }
    
    static {
        serverIpPingCache = new HashMap<String, Long>();
    }
}
