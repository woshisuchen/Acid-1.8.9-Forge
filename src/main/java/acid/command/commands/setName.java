package acid.command.commands;

import java.util.ArrayList;
import acid.module.Module;
import net.minecraft.util.EnumChatFormatting;
import acid.Client;
import acid.utils.Helper;
import java.util.List;
import acid.command.Command;

public class setName extends Command
{
    public static List<String> list;
    
    public setName() {
        super("setName", new String[] { "sv", "setcustomname" }, "", "set custom name for a module.");
    }
    
    @Override
    public String execute(final String[] args) {
        if (args.length == 0) {
            Helper.sendMessage("Correct usage .sv <module> [name]");
            return null;
        }
        boolean found = false;
        final Client instance = Client.instance;
        final Module m = Client.getModuleManager().getAlias(args[0]);
        if (m != null) {
            found = true;
            if (args.length >= 2) {
                final StringBuilder string = new StringBuilder();
                for (int i = 1; i < args.length; ++i) {
                    String tempString = args[i];
                    tempString = tempString.replace('&', '§');
                    string.append(tempString).append(" ");
                }
                m.setCustomName(string.toString().trim());
                Helper.sendMessage(EnumChatFormatting.BLUE + m.getName() + EnumChatFormatting.GRAY + " was" + EnumChatFormatting.GREEN + " set" + EnumChatFormatting.GRAY + " to " + EnumChatFormatting.YELLOW + string.toString().trim());
            }
            else {
                m.setCustomName(null);
                Helper.sendMessage(EnumChatFormatting.BLUE + m.getName() + EnumChatFormatting.GRAY + " was" + EnumChatFormatting.RED + " reset");
            }
        }
        if (!found) {
            Helper.sendMessage("Module name " + EnumChatFormatting.RED + args[0] + EnumChatFormatting.GRAY + " is invalid");
        }
        return null;
    }
    
    static {
        setName.list = new ArrayList<String>();
    }
}
