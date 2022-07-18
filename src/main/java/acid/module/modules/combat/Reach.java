package acid.module.modules.combat;

import acid.management.ModuleManager;
import acid.api.value.Value;
import java.awt.Color;
import acid.module.ModuleType;
import java.util.Random;
import acid.api.value.Option;
import acid.api.value.Numbers;
import acid.utils.timer.TimeHelper;
import acid.module.Module;

public class Reach extends Module
{
    public long lastAttack;
    public TimeHelper timer;
    public static Numbers<Double> reach;
    public Option<Boolean> combo;
    public Random rand;
    public double currentRange;
    
    public Reach() {
        super("Reach", new String[] { "Reach" }, ModuleType.Combat);
        this.lastAttack = 0L;
        this.timer = new TimeHelper();
        this.combo = new Option<Boolean>("Combo", Boolean.valueOf(false));
        this.rand = new Random();
        this.currentRange = 3.0;
        this.setColor(new Color(191, 191, 191).getRGB());
        this.addValues(Reach.reach, this.combo);
    }
    
    public static double getReach() {
        return ModuleManager.getModuleByName("Reach").isEnabled() ? ((double)(float)(Object)Reach.reach.getValue()) : 3.0;
    }
    
    static {
        Reach.reach = new Numbers<Double>("Reach", 3.0, 3.0, 6.0, 0.1);
    }
}
