package acid.module.modules.render;

import acid.api.value.Value;
import acid.module.ModuleType;
import acid.api.value.Mode;
import acid.api.value.Option;
import acid.api.value.Numbers;
import acid.module.Module;

public class Animation extends Module
{
    public int multiplier;
    public final Numbers<Double> blockHValue;
    public final Numbers<Double> blockVValue;
    public final Numbers<Double> blockSValue;
    public final Numbers<Double> armHValue;
    public final Numbers<Double> armVValue;
    public final Numbers<Double> armSValue;
    public final Numbers<Double> swingSlowValue;
    private Numbers<Double> crack;
    public final Option<Boolean> forceHeightValue;
    public static Option<Boolean> Itemblock;
    public final Mode<Enum> modeValue;
    
    public Animation() {
        super("Camera", new String[] { "Camera" }, ModuleType.Render);
        this.multiplier = 50;
        this.blockHValue = new Numbers<Double>("Block H", "Block H", 0.0, -1.5, 1.5, 0.05);
        this.blockVValue = new Numbers<Double>("Block V", "Block V", 0.0, -1.5, 1.5, 0.05);
        this.blockSValue = new Numbers<Double>("Block S", "Block S", 0.0, -1.5, 1.5, 0.05);
        this.armHValue = new Numbers<Double>("Arm H", "Arm H", 0.0, -3.0, 3.0, 0.05);
        this.armVValue = new Numbers<Double>("Arm V", "Arm V", 0.0, -3.0, 3.0, 0.05);
        this.armSValue = new Numbers<Double>("Arm S", "Arm S", 0.0, -3.0, 3.0, 0.05);
        this.swingSlowValue = new Numbers<Double>("Swing", "Novoline moments", 0.0, -3.0, 15.0, 1.0);
        this.crack = new Numbers<Double>("CrackSize", "CrackSize", 1.0, 0.0, 10.0, 1.0);
        this.forceHeightValue = new Option<Boolean>("Force Height", "Just force", false);
        this.modeValue = new Mode<Enum>("Mode", "Block mode", modeEnums.values(), modeEnums.None);
        this.addValues(this.modeValue, this.forceHeightValue, Animation.Itemblock, this.blockHValue, this.blockVValue, this.blockSValue, this.armHValue, this.armVValue, this.armSValue, this.swingSlowValue, this.crack);
    }
    
    static {
        Animation.Itemblock = new Option<Boolean>("Itemblock", Boolean.valueOf(false));
    }
    
    public enum modeEnums
    {
        None, 
        Old, 
        Push, 
        Slide, 
        Jello, 
        Rainy, 
        Swang, 
        Swong, 
        Swong2, 
        Dislike, 
        Leaked;
    }
}
