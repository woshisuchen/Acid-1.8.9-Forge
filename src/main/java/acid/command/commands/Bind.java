package acid.command.commands;

import acid.module.Module;
import acid.utils.Helper;
import org.lwjgl.input.Keyboard;
import acid.Client;
import acid.command.Command;

public class Bind extends Command
{
    public Bind() {
        super("Bind", new String[] { "b" }, "", "sketit");
    }
    
    @Override
    public String execute(final String[] args) {
        if (args.length >= 2) {
            final Client instance = Client.instance;
            final Module m = Client.getModuleManager().getAlias(args[0]);
            if (m != null) {
                final int k = Keyboard.getKeyIndex(args[1].toUpperCase());
                m.setKey(k);
                final Object[] arrobject = { m.getName(), (k == 0) ? "none" : args[1].toUpperCase() };
                Helper.sendMessage(String.format("> Bound %s to %s", arrobject));
            }
            else {
                Helper.sendMessage("> Invalid module name, double check spelling.");
            }
        }
        else {
            Helper.sendMessageWithoutPrefix("\247bCorrect usage:\2477 .bind <module> <key>");
        }
        return null;
    }
}
