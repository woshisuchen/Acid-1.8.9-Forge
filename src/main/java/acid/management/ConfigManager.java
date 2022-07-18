package acid.management;

import java.util.List;
import acid.api.value.Mode;
import acid.api.value.Numbers;
import acid.api.value.Option;
import acid.module.modules.render.ClickGui;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Iterator;
import org.lwjgl.input.Keyboard;
import acid.api.value.Value;
import acid.module.Module;
import java.io.File;
import acid.utils.Helper;

public class ConfigManager
{
    public static void SaveConfig(final String dirs) {
        Helper.sendMessage("尝试保存配置:" + dirs);
        final File dir = new File(FileManager.dir, dirs);
        if (!dir.exists()) {
            dir.mkdir();
        }
        String values = "";
        for (final Module m : ModuleManager.getModules()) {
            for (final Value v : m.getValues()) {
                values = String.valueOf(values) + String.format("%s:%s:%s%s", m.getName(), v.getName(), v.getValue(), System.lineSeparator());
            }
        }
        FileManager.save(dir, "Values.txt", values, false);
        String content = "";
        for (final Module i : ModuleManager.getModules()) {
            content += String.format("%s:%s%s", i.getName(), Keyboard.getKeyName(i.getKey()), System.lineSeparator());
        }
        FileManager.save(dir, "Binds.txt", content, false);
        String enabled = "";
        for (final Module mod : ModuleManager.getModules()) {
            if (mod.isEnabled()) {
                enabled += String.format("%s%s", mod.getName(), System.lineSeparator());
            }
        }
        FileManager.save(dir, "Enabled.txt", enabled, false);
        String Hiddens = "";
        for (final Module j : ModuleManager.getModules()) {
            if (j.wasRemoved()) {
                Hiddens = String.valueOf(Hiddens) + j.getName() + System.lineSeparator();
            }
        }
        FileManager.save("Hidden.txt", Hiddens, false);
        new File(dir + "/CustomName.txt").delete();
        String name = "";
        for (final Module k : ModuleManager.getModules()) {
            if (k.getCustomName() != null) {
                name = String.valueOf(name) + String.format("%s:%s%s", k.getName(), k.getCustomName(), System.lineSeparator());
            }
        }
        FileManager.save("CustomName.txt", name, false);
        final String ConfigInfo = String.format("%s:%s%s", dirs, "SB", System.lineSeparator());
        FileManager.save(dir, "Config.lynxsenseConfigInfo", ConfigInfo, false);
        Helper.sendMessage("保存配置:" + dirs + " 完成");
    }
    
    public static void LoadConfig(final String dirs) {
        final File dir = new File(FileManager.dir, dirs);
        if (!dir.exists()) {
            Helper.sendMessage("您加载的配置不存在!");
            return;
        }
        try {
            final File info = new File(dir, "Config.lynxsenseConfigInfo");
            final BufferedReader bufferedReader = new BufferedReader(new FileReader(info));
            final String s = bufferedReader.readLine();
            final String[] s2 = s.split(":");
            Helper.sendMessage("配置:" + s2[0] + " 作者:" + s2[1]);
        }
        catch (Exception ex) {}
        final List<String> binds = FileManager.read(dir, "Binds.txt");
        for (final String v : binds) {
            final String name = v.split(":")[0];
            final String bind = v.split(":")[1];
            final Module m = ModuleManager.getModuleByName(name);
            if (m == null) {
                continue;
            }
            m.setKey(Keyboard.getKeyIndex(bind.toUpperCase()));
        }
        if (ModuleManager.getModuleByClass(ClickGui.class).getKey() == 0) {
            ModuleManager.getModuleByClass(ClickGui.class).setKey(Keyboard.getKeyIndex("RSHIFT"));
        }
        final List<String> enabled = FileManager.read(dir, "Enabled.txt");
        for (final Module i : ModuleManager.modules) {
            if (i.isEnabled()) {
                i.setEnabled(false);
            }
        }
        for (final String v2 : enabled) {
            final Module j = ModuleManager.getModuleByName(v2);
            if (j == null) {
                continue;
            }
            if (j.isEnabled()) {
                continue;
            }
            j.setEnabled(true);
        }
        final List<String> names = FileManager.read(dir, "CustomName.txt");
        for (final String v3 : names) {
            final String name2 = v3.split(":")[0];
            final String cusname = v3.split(":")[1];
            final Module k = ModuleManager.getModuleByName(name2);
            if (k == null) {
                continue;
            }
            k.setCustomName(cusname);
        }
        final List<String> names2 = FileManager.read(dir, "Hidden.txt");
        for (final String v4 : names2) {
            final Module l = ModuleManager.getModuleByName(v4);
            if (l == null) {
                continue;
            }
            l.setRemoved(true);
        }
        final List<String> vals = FileManager.read(dir, "Values.txt");
        for (final String v5 : vals) {
            final String name3 = v5.split(":")[0];
            final String values = v5.split(":")[1];
            final Module k2 = ModuleManager.getModuleByName(name3);
            if (k2 == null) {
                continue;
            }
            for (final Value value : k2.getValues()) {
                if (!value.getName().equalsIgnoreCase(values)) {
                    continue;
                }
                if (value instanceof Option) {
                    value.setValue(Boolean.parseBoolean(v5.split(":")[2]));
                }
                else if (value instanceof Numbers) {
                    value.setValue(Double.parseDouble(v5.split(":")[2]));
                }
                else {
                    ((Mode)value).setMode(v5.split(":")[2]);
                }
            }
        }
    }
    
    public static void RemoveConfig(final String dirs) {
        final File dir = new File(FileManager.dir, dirs);
        if (!dir.exists()) {
            Helper.sendMessage("您尝试删除的配置不存在!");
        }
        else {
            deleteFile(dir);
            Helper.sendMessage("配置" + dirs + "已删除!");
        }
    }
    
    public static void deleteFile(final File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            }
            else {
                final File[] listFiles3;
                final File[] array;
                final File[] listFiles2 = array = (listFiles3 = file.listFiles());
                for (final File file2 : array) {
                    deleteFile(file2);
                }
            }
            file.delete();
        }
        else {
            System.err.println("路径不存在?");
        }
    }
}
