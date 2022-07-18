package acid.command.commands;

import acid.module.Module;
import net.minecraft.util.EnumChatFormatting;
import acid.Client;
import acid.utils.Helper;
import acid.command.Command;

public class Toggle extends Command
{
    public Toggle() {
        super("t", new String[] { "toggle", "togl", "turnon", "enable" }, "", "Toggles a specified Module");
    }
    
    @Override
    public String execute(final String[] args) {
        String modName = "";
        if (args.length > 1) {
            modName = args[1];
        }
        else if (args.length < 1) {
            Helper.sendMessageWithoutPrefix("\247bCorrect usage:\2477 .t <module>");
        }
        boolean found = false;
        final Client instance = Client.instance;
        final Module m = Client.getModuleManager().getAlias(args[0]);
        if (m != null) {
            if (!m.isEnabled()) {
                m.setEnabled(true);
            }
            else {
                m.setEnabled(false);
            }
            found = true;
            if (m.isEnabled()) {
                Helper.sendMessage("> " + m.getName() + EnumChatFormatting.GRAY + " was" + EnumChatFormatting.GREEN + " enabled");
            }
            else {
                Helper.sendMessage("> " + m.getName() + EnumChatFormatting.GRAY + " was" + EnumChatFormatting.RED + " disabled");
            }
        }
        if (!found) {
            Helper.sendMessage("> Module name " + EnumChatFormatting.RED + args[0] + EnumChatFormatting.GRAY + " is invalid");
        }
        return null;
    }
}
