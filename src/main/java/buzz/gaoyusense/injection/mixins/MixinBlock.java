package buzz.gaoyusense.injection.mixins;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import acid.management.ModuleManager;
import acid.module.modules.render.Xray;
import acid.Client;
import net.minecraft.util.EnumWorldBlockLayer;
import org.spongepowered.asm.mixin.Overwrite;
import acid.api.EventBus;
import acid.api.events.misc.EventCollideWithBlock;
import net.minecraft.entity.Entity;
import java.util.List;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.client.Minecraft;
import net.minecraft.block.material.Material;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import buzz.gaoyusense.injection.interfaces.IBlock;
import net.minecraft.block.Block;

@Mixin({ Block.class })
public abstract class MixinBlock extends Block implements IBlock
{
    @Shadow
    @Final
    protected BlockState blockState;
    @Shadow
    protected double minX;
    @Shadow
    protected double minY;
    @Shadow
    protected double minZ;
    @Shadow
    protected double maxX;
    @Shadow
    protected double maxY;
    @Shadow
    protected double maxZ;
    @Final
    @Shadow
    protected Material blockMaterial;
    Minecraft mc;
    int blockID;
    private Block BLOCK;
    
    public MixinBlock(final Material materialIn) {
        super(materialIn);
        this.mc = Minecraft.getMinecraft();
        this.blockID = 0;
    }
    
    @Shadow
    public abstract boolean isFullCube();
    
    @Shadow
    public abstract boolean isBlockNormalCube();
    
    @Overwrite
    public void addCollisionBoxesToList(final World worldIn, final BlockPos pos, final IBlockState state, final AxisAlignedBB mask, final List<AxisAlignedBB> list, final Entity collidingEntity) {
        AxisAlignedBB axisalignedbb = this.getCollisionBoundingBox(worldIn, pos, state);
        final EventCollideWithBlock blockBBEvent = new EventCollideWithBlock(this.blockState.getBlock(), pos, axisalignedbb);
        EventBus.getInstance().call(blockBBEvent);
        axisalignedbb = blockBBEvent.getBoundingBox();
        if (axisalignedbb != null && mask.intersectsWith(axisalignedbb)) {
            list.add(axisalignedbb);
        }
    }
    
    @Overwrite
    public EnumWorldBlockLayer getBlockLayer() {
        Client.getModuleManager();
        final Xray x = (Xray)ModuleManager.getModuleByClass(Xray.class);
        if (x.isEnabled()) {
            return Xray.containsID(getIdFromBlock((Block)this)) ? EnumWorldBlockLayer.SOLID : EnumWorldBlockLayer.TRANSLUCENT;
        }
        return EnumWorldBlockLayer.SOLID;
    }
    
    @Inject(method = "getAmbientOcclusionLightValue", at = { @At("HEAD") }, cancellable = true)
    private void getAmbientOcclusionLightValue(final CallbackInfoReturnable<Float> floatCallbackInfoReturnable) {
        Client.getModuleManager();
        if (ModuleManager.getModuleByClass(Xray.class).isEnabled()) {
            floatCallbackInfoReturnable.setReturnValue(1.0f);
        }
    }
    
    @Overwrite
    public AxisAlignedBB getCollisionBoundingBox(final World worldIn, final BlockPos pos, final IBlockState state) {
        return new AxisAlignedBB(pos.getX() + this.minX, pos.getY() + this.minY, pos.getZ() + this.minZ, pos.getX() + this.maxX, pos.getY() + this.maxY, pos.getZ() + this.maxZ);
    }
}
