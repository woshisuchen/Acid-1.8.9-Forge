package acid.module.modules.player;

import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemTool;
import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import acid.api.EventHandler;
import java.util.Objects;
import acid.api.events.world.EventPostUpdate;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.Entity;
import acid.module.ModuleType;
import acid.module.Module;

public class AutoTool extends Module
{
    public AutoTool() {
        super("Auto Tool", new String[] { "AutoTool" }, ModuleType.Player);
        super.setRemoved(true);
        this.setCustomName("Auto Tool");
    }
    
    public Entity getItems(final double range) {
        Entity tempEntity = null;
        double dist = range;
        for (final Object i : this.mc.theWorld.loadedEntityList) {
            final Entity entity = (Entity)i;
            if (this.mc.thePlayer.onGround && this.mc.thePlayer.isCollidedVertically && entity instanceof EntityItem) {
                final double curDist = this.mc.thePlayer.getDistanceToEntity(entity);
                if (curDist > dist) {
                    continue;
                }
                dist = curDist;
                tempEntity = entity;
            }
        }
        return tempEntity;
    }
    
    @EventHandler
    public void onClickBlock(final EventPostUpdate e) {
        final boolean checks = !this.mc.thePlayer.isEating();
        if (checks && this.mc.playerController.getIsHittingBlock() && !Objects.isNull(this.mc.objectMouseOver.getBlockPos())) {
            this.bestTool(this.mc.objectMouseOver.getBlockPos().getX(), this.mc.objectMouseOver.getBlockPos().getY(), this.mc.objectMouseOver.getBlockPos().getZ());
        }
    }
    
    public void bestSword(final Entity targetEntity) {
        int bestSlot = 0;
        float f = -1.0f;
        for (int i1 = 36; i1 < 45; ++i1) {
            if (this.mc.thePlayer.inventoryContainer.inventorySlots.toArray()[i1] != null && targetEntity != null) {
                final ItemStack curSlot = this.mc.thePlayer.inventoryContainer.getSlot(i1).getStack();
                if (curSlot != null && curSlot.getItem() instanceof ItemSword) {
                    final ItemSword sword = (ItemSword)curSlot.getItem();
                    if (sword.getDamageVsEntity() > f) {
                        bestSlot = i1 - 36;
                        f = sword.getDamageVsEntity();
                    }
                }
            }
        }
        if (f > -1.0f) {
            this.mc.thePlayer.inventory.currentItem = bestSlot;
            this.mc.playerController.updateController();
        }
    }
    
    public void bestTool(final int x, final int y, final int z) {
        final int blockId = Block.getIdFromBlock(this.mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock());
        int bestSlot = 0;
        float f = -1.0f;
        for (int i1 = 36; i1 < 45; ++i1) {
            try {
                final ItemStack curSlot = this.mc.thePlayer.inventoryContainer.getSlot(i1).getStack();
                if ((curSlot.getItem() instanceof ItemTool || curSlot.getItem() instanceof ItemSword || curSlot.getItem() instanceof ItemShears) && curSlot.getStrVsBlock(Block.getBlockById(blockId)) > f) {
                    bestSlot = i1 - 36;
                    f = curSlot.getStrVsBlock(Block.getBlockById(blockId));
                }
            }
            catch (Exception ex) {}
        }
        if (f != -1.0f) {
            this.mc.thePlayer.inventory.currentItem = bestSlot;
            this.mc.playerController.updateController();
        }
    }
}
