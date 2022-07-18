package acid.command.commands;

import acid.utils.Helper;
import acid.management.ConfigManager;
import acid.command.Command;

public class ConfigManagerCommand extends Command
{
    public ConfigManagerCommand() {
        super("ConfigManager", new String[] { "cm" }, "", "Load or Save Local Config");
    }
    
    @Override
    public String execute(final String[] args) {
        if (args.length == 2 && args[0].equalsIgnoreCase("save")) {
            ConfigManager.SaveConfig(args[1]);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("load")) {
            ConfigManager.LoadConfig(args[1]);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
            ConfigManager.RemoveConfig(args[1]);
        }
        if (args.length != 2) {
            Helper.sendMessageWithoutPrefix("§7§m§l==================================");
            Helper.sendMessageWithoutPrefix("§b§lStylle ConfigManager");
            Helper.sendMessageWithoutPrefix("§b.cm save <Configuration name> :§7 Save Config");
            Helper.sendMessageWithoutPrefix("§b.cm load <Configuration name> :§7 Load Config");
            Helper.sendMessageWithoutPrefix("§b.cm remove <Configuration name> :§7 Remove Config");
            Helper.sendMessageWithoutPrefix("§7§m§l==================================");
        }
        return null;
    }
}
