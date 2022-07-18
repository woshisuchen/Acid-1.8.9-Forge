package acid.module.modules.player;

import acid.api.value.Value;
import java.awt.Color;
import java.util.Arrays;
import net.minecraft.init.Blocks;
import net.minecraft.block.Block;
import acid.module.ModuleType;
import java.util.List;
import acid.api.value.Numbers;
import acid.module.Module;

public class FastPlace extends Module
{
    public final Numbers<Double> delay;
    public final List blackList;
    
    public FastPlace() {
        super("FastPlace", new String[] { "fPlace", "fc", "fp" }, ModuleType.Player);
        this.delay = new Numbers<Double>("Delay", 1.0, 0.0, 4.0, 1.0);
        this.blackList = Arrays.asList(Blocks.air, Blocks.water, Blocks.torch, Blocks.redstone_torch, Blocks.ladder, Blocks.flowing_water, Blocks.lava, Blocks.flowing_lava, Blocks.enchanting_table, Blocks.carpet, Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.chest, Blocks.torch, Blocks.anvil, Blocks.web, Blocks.redstone_torch, Blocks.brewing_stand, Blocks.waterlily, Blocks.farmland, Blocks.sand, Blocks.beacon, Blocks.double_plant, Blocks.noteblock, Blocks.hopper, Blocks.dispenser, Blocks.dropper);
        this.setColor(new Color(226, 197, 78).getRGB());
        this.addValues(this.delay);
    }
}
