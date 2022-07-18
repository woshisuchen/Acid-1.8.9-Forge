package buzz.gaoyusense.injection.mixins;

import org.spongepowered.asm.mixin.Overwrite;
import acid.module.modules.render.Xray;
import net.minecraft.client.renderer.chunk.SetVisibility;
import net.minecraft.util.EnumFacing;
import java.util.Set;
import net.minecraft.util.BlockPos;
import java.util.BitSet;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Final;
import net.minecraft.client.renderer.chunk.VisGraph;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ VisGraph.class })
public abstract class MixinVisGraph
{
    @Final
    @Shadow
    private static int[] field_178613_e;
    @Final
    @Shadow
    private BitSet field_178612_d;
    @Shadow
    private int field_178611_f;
    
    private static int getIndex(final BlockPos pos) {
        return getIndex(pos.getX() & 0xF, pos.getY() & 0xF, pos.getZ() & 0xF);
    }
    
    private static int getIndex(final int x, final int y, final int z) {
        return x << 0 | y << 8 | z << 4;
    }
    
    @Shadow
    public abstract Set<EnumFacing> func_178604_a(final int p0);
    
    @Overwrite
    public SetVisibility computeVisibility() {
        final SetVisibility var1 = new SetVisibility();
        if (Xray.isEnabled) {
            var1.setAllVisible(true);
            return var1;
        }
        if (4096 - this.field_178611_f < 256) {
            var1.setAllVisible(true);
        }
        else if (this.field_178611_f == 0) {
            var1.setAllVisible(false);
        }
        else {
            for (final int var2 : MixinVisGraph.field_178613_e) {
                if (!this.field_178612_d.get(var2)) {
                    var1.setManyVisible((Set)this.func_178604_a(var2));
                }
            }
        }
        return var1;
    }
    
    @Overwrite
    public void func_178606_a(final BlockPos var1) {
        if (!Xray.isEnabled) {
            this.field_178612_d.set(getIndex(var1), true);
            --this.field_178611_f;
        }
    }
}
