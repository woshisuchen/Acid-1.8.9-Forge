package buzz.gaoyusense.injection.mixins;

import org.spongepowered.asm.mixin.Overwrite;
import acid.module.modules.render.Xray;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockGrass;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.block.Block;

@Mixin({ BlockGrass.class })
public abstract class MixinBlockGrass extends Block
{
    public MixinBlockGrass(final Material materialIn) {
        super(materialIn);
    }
    
    @Overwrite
    public EnumWorldBlockLayer getBlockLayer() {
        return Xray.isEnabled ? super.getBlockLayer() : EnumWorldBlockLayer.CUTOUT_MIPPED;
    }
}
