package acid.module.modules.world;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemArmor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C16PacketClientStatus;
import acid.api.EventHandler;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiInventory;
import acid.api.events.world.EventTick;
import acid.api.value.Value;
import acid.module.ModuleType;
import acid.api.value.Option;
import acid.api.value.Numbers;
import acid.utils.timer.TimeHelper;
import acid.module.Module;

public class AutoArmor extends Module
{
    private TimeHelper timer;
    public final Numbers<Number> delay;
    public final Option<Boolean> inventoryOnly;
    
    public AutoArmor() {
        super("AutoArmor", new String[] { "AutoArmor" }, ModuleType.Player);
        this.timer = new TimeHelper();
        this.delay = new Numbers<Number>("Delay", 1.0, 0.0, 5.0, 1.0);
        this.inventoryOnly = new Option<Boolean>("Inventory Only", Boolean.valueOf(true));
        this.addValues(this.inventoryOnly, this.delay);
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
    }
    
    @Override
    public void onDisable() {
        super.onDisable();
    }
    
    @EventHandler
    public void onMotionUpdate(final EventTick event) {
        final long delay2 = this.delay.getValue().intValue() * 100L;
        if (this.inventoryOnly.getValue() && !(this.mc.currentScreen instanceof GuiInventory)) {
            return;
        }
        if ((this.mc.currentScreen == null || this.mc.currentScreen instanceof GuiInventory || this.mc.currentScreen instanceof GuiChat) && this.timer.check(delay2)) {
            this.getBestArmor();
            this.timer.reset();
        }
    }
    
    public void getBestArmor() {
        for (int type = 1; type < 5; ++type) {
            if (this.mc.thePlayer.inventoryContainer.getSlot(4 + type).getHasStack()) {
                final ItemStack is = this.mc.thePlayer.inventoryContainer.getSlot(4 + type).getStack();
                if (isBestArmor(is, type)) {
                    continue;
                }
                final C16PacketClientStatus p = new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT);
                this.mc.thePlayer.sendQueue.addToSendQueue((Packet)p);
                this.drop(4 + type);
            }
            for (int i = 9; i < 45; ++i) {
                if (this.mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                    final ItemStack is2 = this.mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                    if (isBestArmor(is2, type) && getProtection(is2) > 0.0f) {
                        this.shiftClick(i);
                        this.timer.reset();
                        if (this.delay.getValue().longValue() > 0L) {
                            return;
                        }
                    }
                }
            }
        }
    }
    
    public static boolean isBestArmor(final ItemStack stack, final int type) {
        final float prot = getProtection(stack);
        String strType = "";
        if (type == 1) {
            strType = "helmet";
        }
        else if (type == 2) {
            strType = "chestplate";
        }
        else if (type == 3) {
            strType = "leggings";
        }
        else if (type == 4) {
            strType = "boots";
        }
        if (!stack.getUnlocalizedName().contains(strType)) {
            return false;
        }
        for (int i = 5; i < 45; ++i) {
            if (Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack is = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getStack();
                if (getProtection(is) > prot && is.getUnlocalizedName().contains(strType)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public void shiftClick(final int slot) {
        this.mc.playerController.windowClick(this.mc.thePlayer.inventoryContainer.windowId, slot, 0, 1, (EntityPlayer)this.mc.thePlayer);
    }
    
    public void drop(final int slot) {
        this.mc.playerController.windowClick(this.mc.thePlayer.inventoryContainer.windowId, slot, 1, 4, (EntityPlayer)this.mc.thePlayer);
    }
    
    public static float getProtection(final ItemStack stack) {
        float prot = 0.0f;
        if (stack.getItem() instanceof ItemArmor) {
            final ItemArmor armor = (ItemArmor)stack.getItem();
            prot += (float)(armor.damageReduceAmount + (100 - armor.damageReduceAmount) * EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack) * 0.0075);
            prot += (float)(EnchantmentHelper.getEnchantmentLevel(Enchantment.blastProtection.effectId, stack) / 100.0);
            prot += (float)(EnchantmentHelper.getEnchantmentLevel(Enchantment.fireProtection.effectId, stack) / 100.0);
            prot += (float)(EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack) / 100.0);
            prot += (float)(EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack) / 50.0);
            prot += (float)(EnchantmentHelper.getEnchantmentLevel(Enchantment.baneOfArthropods.effectId, stack) / 100.0);
        }
        return prot;
    }
}
