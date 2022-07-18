package acid.command;

import acid.utils.Helper;

public abstract class Command
{
    private String name;
    private String[] alias;
    private String syntax;
    private String help;
    
    public Command(final String name, final String[] alias, final String syntax, final String help) {
        this.name = name.toLowerCase();
        this.syntax = syntax.toLowerCase();
        this.help = help;
        this.alias = alias;
    }
    
    public abstract String execute(final String[] p0);
    
    public String getName() {
        return this.name;
    }
    
    public String[] getAlias() {
        return this.alias;
    }
    
    public String getSyntax() {
        return this.syntax;
    }
    
    public String getHelp() {
        return this.help;
    }
    
    public void syntaxError(final String msg) {
        Helper.sendMessage(String.format("§7Invalid command usage", msg));
    }
    
    public void syntaxError(final byte errorType) {
        switch (errorType) {
            case 0: {
                this.syntaxError("bad argument");
                break;
            }
            case 1: {
                this.syntaxError("argument gay");
                break;
            }
        }
    }
}
