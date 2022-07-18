package acid.command.commands;

import java.util.ArrayList;
import acid.module.Module;
import net.minecraft.util.EnumChatFormatting;
import acid.Client;
import acid.utils.Helper;
import java.util.List;
import acid.command.Command;

public class Hidden extends Command
{
    public static List<String> list;
    
    public Hidden() {
        super("hidden", new String[] { "h", "hide" }, "", "Hide a module.");
    }
    
    @Override
    public String execute(final String[] args) {
        if (args.length == 0) {
            Helper.sendMessage("Correct usage .h <module>");
            return null;
        }
        for (final String s : args) {
            boolean found = false;
            final Module m = Client.getModuleManager().getAlias(s);
            if (m != null) {
                found = true;
                if (!m.wasRemoved()) {
                    m.setRemoved(true);
                    Helper.sendMessage(m.getName() + EnumChatFormatting.GRAY + " was" + EnumChatFormatting.RED + " hidden");
                }
                else {
                    m.setRemoved(false);
                    Helper.sendMessage(m.getName() + EnumChatFormatting.GRAY + " was" + EnumChatFormatting.GREEN + " shown");
                }
            }
            if (!found) {
                Helper.sendMessage("Module name " + EnumChatFormatting.RED + s + EnumChatFormatting.GRAY + " is invalid");
            }
        }
        return null;
    }
    
    static {
        Hidden.list = new ArrayList<String>();
    }
}
