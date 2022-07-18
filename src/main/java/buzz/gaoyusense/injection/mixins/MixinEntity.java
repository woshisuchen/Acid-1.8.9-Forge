package buzz.gaoyusense.injection.mixins;

import org.spongepowered.asm.mixin.Overwrite;
import acid.module.modules.combat.KillAura;
import acid.module.Module;
import acid.management.ModuleManager;
import acid.module.modules.combat.Hitbox;
import acid.Client;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.util.AxisAlignedBB;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import buzz.gaoyusense.injection.interfaces.IEntity;

@Mixin({ Entity.class })
public abstract class MixinEntity implements IEntity
{
    @Shadow
    public double posX;
    @Shadow
    public double posY;
    @Shadow
    public double posZ;
    @Shadow
    public float rotationYaw;
    @Shadow
    public float rotationPitch;
    @Shadow
    public boolean onGround;
    @Shadow
    private int nextStepDistance;
    @Shadow
    private int fire;
    @Shadow
    private AxisAlignedBB boundingBox;
    @Shadow
    public World worldObj;
    
    @Override
    public int getNextStepDistance() {
        return this.nextStepDistance;
    }
    
    @Shadow
    public abstract Vec3 getVectorForRotation(final float p0, final float p1);
    
    @Override
    public void setNextStepDistance(final int distance) {
        this.nextStepDistance = distance;
    }
    
    @Override
    public int getFire() {
        return this.fire;
    }
    
    @Override
    public void setFire(final int i) {
        this.fire = i;
    }
    
    @Override
    public AxisAlignedBB getBoundingBox() {
        return this.boundingBox;
    }
    
    @Overwrite
    public float getCollisionBorderSize() {
        Client.getModuleManager();
        final Hitbox hitBox = (Hitbox)ModuleManager.getModuleByClass(Hitbox.class);
        Client.getModuleManager();
        final KillAura aura = (KillAura)ModuleManager.getModuleByClass(KillAura.class);
        if (hitBox.isEnabled() && aura.hitbox((Entity)((Object)this))) {
            return 0.1f + Hitbox.getSize();
        }
        return 0.1f;
    }
}
