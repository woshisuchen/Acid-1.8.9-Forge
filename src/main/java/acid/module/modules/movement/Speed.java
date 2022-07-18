package acid.module.modules.movement;

import acid.api.events.world.EventMove;
import acid.api.EventHandler;
import acid.management.ModuleManager;
import acid.module.modules.render.ClickGui;
import acid.Client;
import acid.utils.PlayerUtils;
import acid.api.events.world.EventPreUpdate;
import acid.utils.Helper;
import acid.api.value.Value;
import java.awt.Color;
import acid.module.ModuleType;
import acid.utils.timer.TimerUtil;
import acid.api.value.Option;
import acid.api.value.Numbers;
import acid.api.value.Mode;
import acid.module.Module;

public class Speed extends Module
{
    private Mode<Enum> mode;
    public static final Numbers<Number> timerspeed;
    public static final Numbers<Number> speed;
    public static Option<Boolean> strafe;
    public static Option<Boolean> jump;
    private TimerUtil timer;
    
    public Speed() {
        super("Speed", new String[] { "zoom" }, ModuleType.Movement);
        this.mode = new Mode<Enum>("Mode", "mode", SpeedMode.values(), SpeedMode.Hypixel);
        this.timer = new TimerUtil();
        this.setColor(new Color(99, 248, 91).getRGB());
        this.addValues(this.mode, Speed.strafe, Speed.jump, Speed.timerspeed, Speed.speed);
    }
    
    @Override
    public void onDisable() {
        Helper.getTimer().timerSpeed = 1.0f;
    }
    
    @EventHandler
    private void onMove2(final EventPreUpdate e) {
        if (Speed.jump.getValue() && PlayerUtils.MovementInput() && this.mc.thePlayer.onGround) {
            this.mc.thePlayer.jump();
        }
        Client.getModuleManager();
        if (!ModuleManager.getModuleByClass(ClickGui.class).isEnabled()) {
            if (Speed.strafe.getValue() && this.mc.thePlayer.moveStrafing == 0.0f) {
                Helper.getTimer().timerSpeed = 1.0f;
                return;
            }
            Helper.getTimer().timerSpeed = Speed.timerspeed.getValue().floatValue();
        }
        else {
            Helper.getTimer().timerSpeed = 1.0f;
        }
    }
    
    @EventHandler
    private void onMove(final EventMove e) {
        this.setSuffix("Legit");
        if (this.mode.getValue() == SpeedMode.Hypixel && PlayerUtils.MovementInput() && this.mc.thePlayer.onGround) {
            e.setX(e.getX() + e.getX() * Speed.speed.getValue().doubleValue());
            e.setZ(e.getZ() + e.getZ() * Speed.speed.getValue().doubleValue());
        }
    }
    
    static {
        timerspeed = new Numbers<Number>("set Timer", 1.05, 1.0, 1.07, 0.01);
        speed = new Numbers<Number>("set Speed", 0.05, 0.0, 0.2, 0.01);
        Speed.strafe = new Option<Boolean>("Strafe Only", Boolean.valueOf(false));
        Speed.jump = new Option<Boolean>("auto Jump", Boolean.valueOf(false));
    }
    
    enum SpeedMode
    {
        Hypixel;
    }
}
