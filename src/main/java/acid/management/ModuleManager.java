package acid.management;

import acid.api.value.Mode;
import acid.api.value.Numbers;
import acid.api.value.Option;
import acid.api.value.Value;
import org.lwjgl.input.Keyboard;
import acid.api.events.rendering.EventRender2D;
import net.minecraft.client.renderer.GlStateManager;
import acid.utils.render.gl.GLUtils;
import java.nio.FloatBuffer;
import acid.api.events.rendering.EventRender3D;
import acid.api.EventHandler;
import acid.api.events.misc.EventKey;
import java.util.ArrayList;
import acid.module.ModuleType;
import acid.api.EventBus;
import acid.module.modules.player.Teams;
import acid.module.modules.movement.InvMove;
import acid.module.modules.movement.Step;
import acid.module.modules.render.NoHurtCam;
import acid.module.modules.render.Xray;
import acid.module.modules.world.Blink;
import acid.module.modules.render.Chams;
import acid.module.modules.render.ViewClip;
import acid.module.modules.movement.Scaffold;
import acid.module.modules.world.AutoArmor;
import acid.module.modules.render.FullBright;
import acid.module.modules.combat.Velocity;
import acid.module.modules.combat.Reach;
import acid.module.modules.render.Nametags;
import acid.module.modules.player.MCF;
import acid.module.modules.render.NoFov;
import acid.module.modules.player.Freecam;
import acid.module.modules.combat.AntiBot;
import acid.module.modules.player.NoJumpDelay;
import acid.module.modules.movement.NoSlow;
import acid.module.modules.player.SpeedMine;
import acid.module.modules.combat.AutoClicker;
import acid.module.modules.player.UhcHelper;
import acid.module.modules.player.FastPlace;
import acid.module.modules.player.NoFall;
import acid.module.modules.render.TargetESP;
import acid.module.modules.render.TargetHUD;
import acid.module.modules.movement.Speed;
import acid.module.modules.combat.Criticals;
import acid.module.modules.combat.Hitbox;
import acid.module.modules.world.LunarSpoof;
import acid.module.modules.render.ClickGui;
import acid.module.modules.combat.AntiKB;
import acid.module.modules.world.AutoRejoin;
import acid.module.modules.player.AutoTool;
import acid.module.modules.combat.KeepSprint;
import acid.module.modules.render.Animation;
import acid.module.modules.combat.KillAura;
import acid.module.modules.movement.Sprint;
import acid.module.modules.render.HUD;
import acid.module.Module;
import java.util.List;

public class ModuleManager implements Manager
{
    public static List<Module> modules;
    private boolean enabledNeededMod;
    public boolean nicetry;
    
    public ModuleManager() {
        this.enabledNeededMod = true;
        this.nicetry = true;
    }
    
    @Override
    public void init() {
        ModuleManager.modules.add(new HUD());
        ModuleManager.modules.add(new Sprint());
        ModuleManager.modules.add(new KillAura());
        ModuleManager.modules.add(new Animation());
        ModuleManager.modules.add(new KeepSprint());
        ModuleManager.modules.add(new AutoTool());
        ModuleManager.modules.add(new AutoRejoin());
        ModuleManager.modules.add(new AntiKB());
        ModuleManager.modules.add(new ClickGui());
        ModuleManager.modules.add(new LunarSpoof());
        ModuleManager.modules.add(new Hitbox());
        ModuleManager.modules.add(new Criticals());
        ModuleManager.modules.add(new Speed());
        ModuleManager.modules.add(new TargetHUD());
        ModuleManager.modules.add(new TargetESP());
        ModuleManager.modules.add(new NoFall());
        ModuleManager.modules.add(new FastPlace());
        ModuleManager.modules.add(new UhcHelper());
        ModuleManager.modules.add(new AutoClicker());
        ModuleManager.modules.add(new SpeedMine());
        ModuleManager.modules.add(new NoSlow());
        ModuleManager.modules.add(new NoJumpDelay());
        ModuleManager.modules.add(new AntiBot());
        ModuleManager.modules.add(new Freecam());
        ModuleManager.modules.add(new NoFov());
        ModuleManager.modules.add(new MCF());
        ModuleManager.modules.add(new Nametags());
        ModuleManager.modules.add(new Reach());
        ModuleManager.modules.add(new Velocity());
        ModuleManager.modules.add(new FullBright());
        ModuleManager.modules.add(new AutoArmor());
        ModuleManager.modules.add(new Scaffold());
        ModuleManager.modules.add(new ViewClip());
        ModuleManager.modules.add(new Chams());
        ModuleManager.modules.add(new Blink());
        ModuleManager.modules.add(new Xray());
        ModuleManager.modules.add(new NoHurtCam());
        ModuleManager.modules.add(new Step());
        ModuleManager.modules.add(new InvMove());
        ModuleManager.modules.add(new Teams());
        this.readSettings();
        for (final Module m : ModuleManager.modules) {
            m.makeCommand();
        }
        EventBus.getInstance().register(this);
    }
    
    public static List<Module> getModules() {
        return ModuleManager.modules;
    }
    
    public static Module getModuleByClass(final Class<? extends Module> cls) {
        for (final Module m : ModuleManager.modules) {
            if (m.getClass() != cls) {
                continue;
            }
            return m;
        }
        return null;
    }
    
    public static Module getModuleByName(final String name) {
        for (final Module m : ModuleManager.modules) {
            if (!m.getName().equalsIgnoreCase(name)) {
                continue;
            }
            return m;
        }
        return null;
    }
    
    public Module getAlias(final String name) {
        for (final Module f : ModuleManager.modules) {
            if (f.getName().equalsIgnoreCase(name)) {
                return f;
            }
            for (final String s : f.getAlias()) {
                if (s.equalsIgnoreCase(name)) {
                    return f;
                }
            }
        }
        return null;
    }
    
    public List<Module> getModulesInType(final ModuleType t) {
        final ArrayList<Module> output = new ArrayList<Module>();
        for (final Module m : ModuleManager.modules) {
            if (m.getType() != t) {
                continue;
            }
            output.add(m);
        }
        return output;
    }
    
    @EventHandler
    private void onKeyPress(final EventKey e) {
        for (final Module m : ModuleManager.modules) {
            if (m.getKey() != e.getKey()) {
                continue;
            }
            m.setEnabled(!m.isEnabled());
        }
    }
    
    @EventHandler
    private void onGLHack(final EventRender3D e) {
        GlStateManager.getFloat(2982, (FloatBuffer)GLUtils.MODELVIEW.clear());
        GlStateManager.getFloat(2983, (FloatBuffer)GLUtils.PROJECTION.clear());
    }
    
    @EventHandler
    private void on2DRender(final EventRender2D e) {
        if (this.enabledNeededMod) {
            this.enabledNeededMod = false;
            for (final Module m : ModuleManager.modules) {
                if (!m.enabledOnStartup) {
                    continue;
                }
                m.setEnabled(true);
            }
        }
    }
    
    private void readSettings() {
        final List<String> binds = FileManager.read("Binds.txt");
        for (final String v : binds) {
            final String name = v.split(":")[0];
            final String bind = v.split(":")[1];
            final Module m = getModuleByName(name);
            if (m == null) {
                continue;
            }
            m.setKey(Keyboard.getKeyIndex(bind.toUpperCase()));
        }
        final List<String> enabled = FileManager.read("Enabled.txt");
        for (final String v2 : enabled) {
            final Module i = getModuleByName(v2);
            if (i == null) {
                continue;
            }
            i.enabledOnStartup = true;
        }
        final List<String> vals = FileManager.read("Values.txt");
        for (final String v3 : vals) {
            final String name2 = v3.split(":")[0];
            final String values = v3.split(":")[1];
            final Module j = getModuleByName(name2);
            if (j == null) {
                continue;
            }
            for (final Value value : j.getValues()) {
                if (!value.getName().equalsIgnoreCase(values)) {
                    continue;
                }
                if (value instanceof Option) {
                    value.setValue(Boolean.parseBoolean(v3.split(":")[2]));
                }
                else if (value instanceof Numbers) {
                    value.setValue(Double.parseDouble(v3.split(":")[2]));
                }
                else {
                    ((Mode)value).setMode(v3.split(":")[2]);
                }
            }
        }
        final List<String> names = FileManager.read("CustomName.txt");
        for (final String v4 : names) {
            final String name3 = v4.split(":")[0];
            final String cusname = v4.split(":")[1];
            final Module k = getModuleByName(name3);
            if (k == null) {
                continue;
            }
            k.setCustomName(cusname);
        }
        final List<String> names2 = FileManager.read("Hidden.txt");
        for (final String v5 : names2) {
            final Module j = getModuleByName(v5);
            if (j == null) {
                continue;
            }
            j.setRemoved(true);
        }
    }
    
    static {
        ModuleManager.modules = new ArrayList<Module>();
    }
}
