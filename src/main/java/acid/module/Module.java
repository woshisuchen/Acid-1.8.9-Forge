package acid.module;

import acid.command.Command;
import acid.api.value.Mode;
import java.util.Iterator;
import acid.management.FileManager;
import org.lwjgl.input.Keyboard;
import acid.management.ModuleManager;
import acid.Client;
import acid.api.EventBus;
import net.minecraft.util.EnumChatFormatting;
import acid.module.modules.render.HUD;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.client.Minecraft;
import acid.api.value.Value;
import java.util.List;

public class Module
{
    public String name;
    private String suffix;
    private int color;
    private String[] alias;
    private boolean enabled;
    public boolean enabledOnStartup;
    private int key;
    public List<Value> values;
    public ModuleType type;
    private boolean removed;
    public Minecraft mc;
    public static Random random;
    private String cusname;
    
    public Module(final String name, final String[] alias, final ModuleType type) {
        this.enabledOnStartup = false;
        this.mc = Minecraft.getMinecraft();
        this.name = name;
        this.alias = alias;
        this.type = type;
        this.suffix = "";
        this.key = 0;
        this.removed = false;
        this.enabled = false;
        this.cusname = null;
        this.values = new ArrayList<Value>();
    }
    
    public String getName() {
        if (HUD.wasLower.getValue()) {
            return this.name.toLowerCase();
        }
        return this.name;
    }
    
    public String[] getAlias() {
        return this.alias;
    }
    
    public ModuleType getType() {
        return this.type;
    }
    
    public boolean isEnabled() {
        return this.enabled;
    }
    
    public boolean wasRemoved() {
        return this.removed;
    }
    
    public void setRemoved(final boolean removed) {
        this.removed = removed;
    }
    
    public String getSuffix() {
        return this.suffix;
    }
    
    public void setSuffix(final Object obj) {
        final String suffix = obj.toString();
        if (suffix.isEmpty()) {
            this.suffix = suffix;
        }
        else {
            this.suffix = (suffix.isEmpty() ? suffix : String.format("%s", EnumChatFormatting.GRAY + suffix));
        }
    }
    
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
        if (enabled) {
            this.onEnable();
            EventBus.getInstance().register(this);
        }
        else {
            EventBus.getInstance().unregister(this);
            this.onDisable();
        }
    }
    
    public void setColor(final int color) {
        this.color = color;
    }
    
    public int getColor() {
        return this.color;
    }
    
    protected void addValues(final Value... values) {
        for (final Value value : values) {
            this.values.add(value);
        }
    }
    
    public List<Value> getValues() {
        return this.values;
    }
    
    public int getKey() {
        return this.key;
    }
    
    public void setKey(final int key) {
        this.key = key;
        String content = "";
        final Client instance = Client.instance;
        Client.getModuleManager();
        for (final Module m : ModuleManager.getModules()) {
            content += String.format("%s:%s%s", m.getName(), Keyboard.getKeyName(m.getKey()), System.lineSeparator());
        }
        FileManager.save("Binds.txt", content, false);
    }
    
    public void onEnable() {
    }
    
    public void onDisable() {
    }
    
    public void makeCommand() {
        if (this.values.size() > 0) {
            String options = "";
            String other = "";
            for (final Value v : this.values) {
                if (!(v instanceof Mode)) {
                    if (options.isEmpty()) {
                        options = String.valueOf(options) + v.getName();
                    }
                    else {
                        options = String.valueOf(options) + String.format(", %s", v.getName());
                    }
                }
            }
            for (final Value v : this.values) {
                if (v instanceof Mode) {
                    final Mode mode = (Mode)v;
                    Enum[] modes;
                    for (int length = (modes = mode.getModes()).length, i = 0; i < length; ++i) {
                        final Enum e = modes[i];
                        if (other.isEmpty()) {
                            other = String.valueOf(other) + e.name().toLowerCase();
                        }
                        else {
                            other = String.valueOf(other) + String.format(", %s", e.name().toLowerCase());
                        }
                    }
                }
            }
            //这里懒得转一个class 有点脑子自己转一下 new 直接写 很简单
            Client.instance.getCommandManager().add(new Module$1(this, this.name, this.alias, String.format("%s%s", options.isEmpty() ? "" : String.format("%s,", options), other.isEmpty() ? "" : String.format("%s", other)), "Setup this module"));
        }
    }
    
    public String getCustomName() {
        return this.cusname;
    }
    
    public void setCustomName(final String name) {
        this.cusname = name;
    }
    
    static {
        Module.random = new Random();
    }
}
