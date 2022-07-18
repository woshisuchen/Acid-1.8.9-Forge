package acid.command.commands;

import java.util.Iterator;
import acid.utils.Helper;
import net.minecraft.util.EnumChatFormatting;
import acid.module.Module;
import acid.management.ModuleManager;
import acid.Client;
import acid.command.Command;

public class Cheats extends Command
{
    public Cheats() {
        super("Cheats", new String[] { "mods" }, "", "sketit");
    }
    
    @Override
    public String execute(final String[] args) {
        if (args.length == 0) {
            final Client instance = Client.instance;
            Client.getModuleManager();
            final StringBuilder list = new StringBuilder(String.valueOf(ModuleManager.getModules().size()) + " Cheats - ");
            final Client instance2 = Client.instance;
            Client.getModuleManager();
            for (final Module cheat : ModuleManager.getModules()) {
                list.append(cheat.isEnabled() ? EnumChatFormatting.GREEN : EnumChatFormatting.RED).append(cheat.getName()).append(", ");
            }
            Helper.sendMessage("> " + list.toString().substring(0, list.toString().length() - 2));
        }
        else {
            Helper.sendMessage("> Correct usage .cheats");
        }
        return null;
    }
}
