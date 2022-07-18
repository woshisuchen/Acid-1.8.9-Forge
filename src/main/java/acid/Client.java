package acid;

import java.util.Iterator;
import acid.api.value.Value;
import acid.module.Module;
import acid.management.FileManager;
import net.minecraft.util.ResourceLocation;
import acid.management.FriendManager;
import acid.ui.login.AltManager;
import acid.management.CommandManager;
import acid.management.ModuleManager;

public class Client
{
    public static final String name = "Acid";
    public final double version = 0.6;
    public static boolean publicMode;
    public static Client instance;
    private static ModuleManager modulemanager;
    private CommandManager commandmanager;
    private AltManager altmanager;
    private FriendManager friendmanager;
    public static ResourceLocation CLIENT_CAPE;
    
    public void initiate() {
        (this.commandmanager = new CommandManager()).init();
        (this.friendmanager = new FriendManager()).init();
        (Client.modulemanager = new ModuleManager()).init();
        this.altmanager = new AltManager();
        AltManager.init();
        AltManager.setupAlts();
        FileManager.init();
    }
    
    public static ModuleManager getModuleManager() {
        return Client.modulemanager;
    }
    
    public CommandManager getCommandManager() {
        return this.commandmanager;
    }
    
    public AltManager getAltManager() {
        return this.altmanager;
    }
    
    public void shutDown() {
        String values = "";
        final Client instance = Client.instance;
        getModuleManager();
        for (final Module m : ModuleManager.getModules()) {
            for (final Value v : m.getValues()) {
                values = String.valueOf(values) + String.format("%s:%s:%s%s", m.getName(), v.getName(), v.getValue(), System.lineSeparator());
            }
        }
        FileManager.save("Values.txt", values, false);
        String enabled = "";
        final Client instance2 = Client.instance;
        getModuleManager();
        for (final Module i : ModuleManager.getModules()) {
            if (!i.isEnabled()) {
                continue;
            }
            enabled = String.valueOf(enabled) + String.format("%s%s", i.getName(), System.lineSeparator());
        }
        FileManager.save("Enabled.txt", enabled, false);
        String Hiddens = "";
        for (final Module j : ModuleManager.getModules()) {
            if (j.wasRemoved()) {
                Hiddens = String.valueOf(Hiddens) + j.getName() + System.lineSeparator();
            }
        }
        FileManager.save("Hidden.txt", Hiddens, false);
        String name = "";
        for (final Module k : ModuleManager.getModules()) {
            if (k.getCustomName() != null) {
                name = String.valueOf(name) + String.format("%s:%s%s", k.getName(), k.getCustomName(), System.lineSeparator());
            }
        }
        FileManager.save("CustomName.txt", name, false);
    }
    
    public static String getModuleName(final Module mod) {
        final String ModuleName = mod.getName();
        final String CustomName = mod.getCustomName();
        if (CustomName != null) {
            return CustomName;
        }
        return ModuleName;
    }
    
    static {
        Client.publicMode = false;
        Client.instance = new Client();
        Client.CLIENT_CAPE = new ResourceLocation("ETB/cape.png");
    }
}
