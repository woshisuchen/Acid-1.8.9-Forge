package acid.module.modules.player;

import net.minecraft.client.Minecraft;
import acid.management.ModuleManager;
import acid.Client;
import net.minecraft.entity.Entity;
import acid.module.ModuleType;
import acid.module.Module;

public class Teams extends Module
{
    public Teams() {
        super("Teams", new String[0], ModuleType.Player);
    }
    
    public static boolean isOnSameTeam(final Entity entity) {
        final Client instance = Client.instance;
        Client.getModuleManager();
        if (!ModuleManager.getModuleByClass(Teams.class).isEnabled()) {
            return false;
        }
        if (Minecraft.getMinecraft().thePlayer.getDisplayName().getUnformattedText().startsWith("¡ì")) {
            if (Minecraft.getMinecraft().thePlayer.getDisplayName().getUnformattedText().length() <= 2 || entity.getDisplayName().getUnformattedText().length() <= 2) {
                return false;
            }
            if (Minecraft.getMinecraft().thePlayer.getDisplayName().getUnformattedText().substring(0, 2).equals(entity.getDisplayName().getUnformattedText().substring(0, 2))) {
                return true;
            }
        }
        return false;
    }
}
