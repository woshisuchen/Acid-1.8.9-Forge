package buzz.gaoyusense.injection.interfaces;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

public interface IBlock
{
    boolean shouldSideBeRendered_(final IBlockAccess p0, final BlockPos p1, final EnumFacing p2);
}
