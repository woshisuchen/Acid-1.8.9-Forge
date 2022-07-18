package acid.management;

import acid.api.EventHandler;
import acid.utils.Helper;
import java.util.Arrays;
import acid.api.events.misc.EventChat;
import java.util.Optional;
import acid.api.EventBus;
import acid.command.commands.ConfigManagerCommand;
import acid.command.commands.setName;
import acid.command.commands.Hidden;
import acid.command.commands.Cheats;
import acid.command.commands.VClip;
import acid.command.commands.Bind;
import acid.command.commands.Toggle;
import acid.command.commands.Help;
import java.util.ArrayList;
import acid.command.Command;
import java.util.List;

public class CommandManager implements Manager
{
    private List<Command> commands;
    
    @Override
    public void init() {
        (this.commands = new ArrayList<Command>()).add(new Command("test", new String[] { "test" }, "", "testing") {
            @Override
            public String execute(final String[] args) {
                for (Command command : CommandManager.this.commands) {}
                return null;
            }
        });
        this.commands.add(new Help());
        this.commands.add(new Toggle());
        this.commands.add(new Bind());
        this.commands.add(new VClip());
        this.commands.add(new Cheats());
        this.commands.add(new Hidden());
        this.commands.add(new setName());
        this.commands.add(new ConfigManagerCommand());
        EventBus.getInstance().register(this);
    }
    
    public List<Command> getCommands() {
        return this.commands;
    }
    
    public Optional<Command> getCommandByName(String name) {
        return this.commands.stream().filter(c2 -> {
            boolean isAlias = false;
            for (String str : c2.getAlias()) {
                if (!str.equalsIgnoreCase(name)) continue;
                isAlias = true;
                break;
            }
            return c2.getName().equalsIgnoreCase(name) || isAlias;
        }).findFirst();
    }

    public void add(Command command) {
        this.commands.add(command);
    }
    
    @EventHandler
    private void onChat(final EventChat e) {
        if (e.getMessage().length() > 1 && e.getMessage().startsWith(".")) {
            e.setCancelled(true);
            final String[] args = e.getMessage().trim().substring(1).split(" ");
            final Optional<Command> possibleCmd = this.getCommandByName(args[0]);
            if (possibleCmd.isPresent()) {
                final String result = possibleCmd.get().execute(Arrays.copyOfRange(args, 1, args.length));
                if (result != null && !result.isEmpty()) {
                    Helper.sendMessage(result);
                }
            }
            else {
                Helper.sendMessage(String.format("Command not found Try '%shelp'", "."));
            }
        }
    }
}
