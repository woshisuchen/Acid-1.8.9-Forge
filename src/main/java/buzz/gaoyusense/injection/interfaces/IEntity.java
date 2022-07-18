package buzz.gaoyusense.injection.interfaces;

import net.minecraft.util.Vec3;
import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;

public interface IEntity
{
    int getNextStepDistance();
    
    void setNextStepDistance(final int p0);
    
    int getFire();
    
    void setFire(final int p0);
    
    AxisAlignedBB getBoundingBox();
    
    boolean isOverOfMaterial(final Material p0);
    
    Vec3 getVectorForRotation(final float p0, final float p1);
}
