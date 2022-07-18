package acid.api.events.misc;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.block.Block;
import acid.api.Event;

public class EventCollideWithBlock extends Event
{
    private Block block;
    private BlockPos blockPos;
    public AxisAlignedBB boundingBox;
    
    public EventCollideWithBlock(final Block block, final BlockPos pos, final AxisAlignedBB boundingBox) {
        this.block = block;
        this.blockPos = pos;
        this.boundingBox = boundingBox;
    }
    
    public Block getBlock() {
        return this.block;
    }
    
    public BlockPos getPos() {
        return this.blockPos;
    }
    
    public AxisAlignedBB getBoundingBox() {
        return this.boundingBox;
    }
    
    public void setBlock(final Block block) {
        this.block = block;
    }
    
    public void setBlockPos(final BlockPos blockPos) {
        this.blockPos = blockPos;
    }
    
    public void setBoundingBox(final AxisAlignedBB boundingBox) {
        this.boundingBox = boundingBox;
    }
}
