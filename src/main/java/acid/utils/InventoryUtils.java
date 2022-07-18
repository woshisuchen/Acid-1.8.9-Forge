package acid.utils;

import net.minecraft.item.ItemSword;
import java.util.ArrayList;
import java.util.Iterator;
import com.google.common.collect.Multimap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.util.DamageSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.Minecraft;

public class InventoryUtils
{
    public static Minecraft mc;
    
    public void dropSlot(final int slot) {
        final int windowId = new GuiInventory((EntityPlayer)InventoryUtils.mc.thePlayer).inventorySlots.windowId;
        InventoryUtils.mc.playerController.windowClick(windowId, slot, 1, 4, (EntityPlayer)InventoryUtils.mc.thePlayer);
    }
    
    public static void updateInventory() {
        for (int index = 0; index < 44; ++index) {
            try {
                final int offset = (index < 9) ? 36 : 0;
                Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue((Packet)new C10PacketCreativeInventoryAction(index + offset, Minecraft.getMinecraft().thePlayer.inventory.mainInventory[index]));
            }
            catch (Exception ex) {}
        }
    }
    
    public static ItemStack getStackInSlot(final int slot) {
        return InventoryUtils.mc.thePlayer.inventory.getStackInSlot(slot);
    }
    
    public static boolean isBestArmorOfTypeInInv(final ItemStack is) {
        try {
            if (is == null) {
                return false;
            }
            if (is.getItem() == null) {
                return false;
            }
            if (is.getItem() != null && !(is.getItem() instanceof ItemArmor)) {
                return false;
            }
            final ItemArmor ia = (ItemArmor)is.getItem();
            final int prot = getArmorProt(is);
            for (int i = 0; i < 4; ++i) {
                final ItemStack stack = InventoryUtils.mc.thePlayer.inventory.armorInventory[i];
                if (stack != null) {
                    final ItemArmor otherArmor = (ItemArmor)stack.getItem();
                    final int otherProt;
                    if (otherArmor.armorType == ia.armorType && (otherProt = getArmorProt(stack)) >= prot) {
                        return false;
                    }
                }
            }
            for (int i = 0; i < InventoryUtils.mc.thePlayer.inventory.getSizeInventory() - 4; ++i) {
                final ItemStack stack = InventoryUtils.mc.thePlayer.inventory.getStackInSlot(i);
                if (stack != null && stack.getItem() instanceof ItemArmor) {
                    final ItemArmor otherArmor = (ItemArmor)stack.getItem();
                    final int otherProt;
                    if (otherArmor.armorType == ia.armorType && otherArmor != ia && (otherProt = getArmorProt(stack)) >= prot) {
                        return false;
                    }
                }
            }
        }
        catch (Exception ex) {}
        return true;
    }
    
    public static boolean hotbarHas(final Item item) {
        for (int index = 0; index <= 36; ++index) {
            final ItemStack stack = Minecraft.getMinecraft().thePlayer.inventory.getStackInSlot(index);
            if (stack != null && stack.getItem() == item) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean hotbarHas(final Item item, final int slotID) {
        for (int index = 0; index <= 36; ++index) {
            final ItemStack stack = Minecraft.getMinecraft().thePlayer.inventory.getStackInSlot(index);
            if (stack != null && stack.getItem() == item && getSlotID(stack.getItem()) == slotID) {
                return true;
            }
        }
        return false;
    }
    
    public static int getSlotID(final Item item) {
        for (int index = 0; index <= 36; ++index) {
            final ItemStack stack = Minecraft.getMinecraft().thePlayer.inventory.getStackInSlot(index);
            if (stack != null && stack.getItem() == item) {
                return index;
            }
        }
        return -1;
    }
    
    public static ItemStack getItemBySlotID(final int slotID) {
        for (int index = 0; index <= 36; ++index) {
            final ItemStack stack = Minecraft.getMinecraft().thePlayer.inventory.getStackInSlot(index);
            if (stack != null && getSlotID(stack.getItem()) == slotID) {
                return stack;
            }
        }
        return null;
    }
    
    public static int getArmorProt(final ItemStack i) {
        int armorprot = -1;
        if (i != null && i.getItem() != null && i.getItem() instanceof ItemArmor) {
            armorprot = ((ItemArmor)i.getItem()).getArmorMaterial().getDamageReductionAmount(getItemType(i)) + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[] { i }, DamageSource.generic);
        }
        return armorprot;
    }
    
    public static int getBestSwordSlotID(final ItemStack item, final double damage) {
        for (int index = 0; index <= 36; ++index) {
            final ItemStack stack = Minecraft.getMinecraft().thePlayer.inventory.getStackInSlot(index);
            if (stack != null && stack == item && getSwordDamage(stack) == getSwordDamage(item)) {
                return index;
            }
        }
        return -1;
    }
    
    private static double getSwordDamage(final ItemStack itemStack) {
        double damage = 0.0;
        final Optional attributeModifier = itemStack.getAttributeModifiers().values().stream().findFirst();
        if (attributeModifier.isPresent()) {
            damage = ((AttributeModifier) attributeModifier.get()).getAmount();
        }
        return damage += EnchantmentHelper.getModifierForCreature(itemStack, EnumCreatureAttribute.UNDEFINED);
    }
    
    public boolean isBestChest(final int slot) {
        if (getStackInSlot(slot) != null && getStackInSlot(slot).getItem() != null && getStackInSlot(slot).getItem() instanceof ItemArmor) {
            final int slotProtection = ((ItemArmor)InventoryUtils.mc.thePlayer.inventory.getStackInSlot(slot).getItem()).getArmorMaterial().getDamageReductionAmount(getItemType(InventoryUtils.mc.thePlayer.inventory.getStackInSlot(slot))) + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[] { InventoryUtils.mc.thePlayer.inventory.getStackInSlot(slot) }, DamageSource.generic);
            if (InventoryUtils.mc.thePlayer.inventory.armorInventory[2] != null) {
                final ItemArmor ia = (ItemArmor)InventoryUtils.mc.thePlayer.inventory.armorInventory[2].getItem();
                final ItemStack is = InventoryUtils.mc.thePlayer.inventory.armorInventory[2];
                final ItemArmor ia2 = (ItemArmor)getStackInSlot(slot).getItem();
                final int otherProtection = ((ItemArmor)is.getItem()).getArmorMaterial().getDamageReductionAmount(getItemType(is)) + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[] { is }, DamageSource.generic);
                if (otherProtection > slotProtection || otherProtection == slotProtection) {
                    return false;
                }
            }
            for (int i = 0; i < InventoryUtils.mc.thePlayer.inventory.getSizeInventory(); ++i) {
                if (getStackInSlot(i) != null && InventoryUtils.mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemArmor) {
                    final int otherProtection2 = ((ItemArmor)InventoryUtils.mc.thePlayer.inventory.getStackInSlot(i).getItem()).getArmorMaterial().getDamageReductionAmount(getItemType(InventoryUtils.mc.thePlayer.inventory.getStackInSlot(i))) + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[] { InventoryUtils.mc.thePlayer.inventory.getStackInSlot(i) }, DamageSource.generic);
                    final ItemArmor ia2 = (ItemArmor)getStackInSlot(slot).getItem();
                    final ItemArmor ia3 = (ItemArmor)getStackInSlot(i).getItem();
                    if (ia2.armorType == 1 && ia3.armorType == 1 && otherProtection2 > slotProtection) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    public boolean isBestHelmet(final int slot) {
        if (getStackInSlot(slot) != null && getStackInSlot(slot).getItem() != null && getStackInSlot(slot).getItem() instanceof ItemArmor) {
            final int slotProtection = ((ItemArmor)InventoryUtils.mc.thePlayer.inventory.getStackInSlot(slot).getItem()).getArmorMaterial().getDamageReductionAmount(getItemType(InventoryUtils.mc.thePlayer.inventory.getStackInSlot(slot))) + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[] { InventoryUtils.mc.thePlayer.inventory.getStackInSlot(slot) }, DamageSource.generic);
            if (InventoryUtils.mc.thePlayer.inventory.armorInventory[3] != null) {
                final ItemArmor ia = (ItemArmor)InventoryUtils.mc.thePlayer.inventory.armorInventory[3].getItem();
                final ItemStack is = InventoryUtils.mc.thePlayer.inventory.armorInventory[3];
                final ItemArmor ia2 = (ItemArmor)getStackInSlot(slot).getItem();
                final int otherProtection = ((ItemArmor)is.getItem()).getArmorMaterial().getDamageReductionAmount(getItemType(is)) + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[] { is }, DamageSource.generic);
                if (otherProtection > slotProtection || otherProtection == slotProtection) {
                    return false;
                }
            }
            for (int i = 0; i < InventoryUtils.mc.thePlayer.inventory.getSizeInventory(); ++i) {
                if (getStackInSlot(i) != null && InventoryUtils.mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemArmor) {
                    final int otherProtection2 = ((ItemArmor)InventoryUtils.mc.thePlayer.inventory.getStackInSlot(i).getItem()).getArmorMaterial().getDamageReductionAmount(getItemType(InventoryUtils.mc.thePlayer.inventory.getStackInSlot(i))) + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[] { InventoryUtils.mc.thePlayer.inventory.getStackInSlot(i) }, DamageSource.generic);
                    final ItemArmor ia2 = (ItemArmor)getStackInSlot(slot).getItem();
                    final ItemArmor ia3 = (ItemArmor)getStackInSlot(i).getItem();
                    if (ia2.armorType == 0 && ia3.armorType == 0 && otherProtection2 > slotProtection) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    public boolean isBestLeggings(final int slot) {
        if (getStackInSlot(slot) != null && getStackInSlot(slot).getItem() != null && getStackInSlot(slot).getItem() instanceof ItemArmor) {
            final int slotProtection = ((ItemArmor)InventoryUtils.mc.thePlayer.inventory.getStackInSlot(slot).getItem()).getArmorMaterial().getDamageReductionAmount(getItemType(InventoryUtils.mc.thePlayer.inventory.getStackInSlot(slot))) + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[] { InventoryUtils.mc.thePlayer.inventory.getStackInSlot(slot) }, DamageSource.generic);
            if (InventoryUtils.mc.thePlayer.inventory.armorInventory[1] != null) {
                final ItemArmor ia = (ItemArmor)InventoryUtils.mc.thePlayer.inventory.armorInventory[1].getItem();
                final ItemStack is = InventoryUtils.mc.thePlayer.inventory.armorInventory[1];
                final ItemArmor ia2 = (ItemArmor)getStackInSlot(slot).getItem();
                final int otherProtection = ((ItemArmor)is.getItem()).getArmorMaterial().getDamageReductionAmount(getItemType(is)) + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[] { is }, DamageSource.generic);
                if (otherProtection > slotProtection || otherProtection == slotProtection) {
                    return false;
                }
            }
            for (int i = 0; i < InventoryUtils.mc.thePlayer.inventory.getSizeInventory(); ++i) {
                if (getStackInSlot(i) != null && InventoryUtils.mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemArmor) {
                    final int otherProtection2 = ((ItemArmor)InventoryUtils.mc.thePlayer.inventory.getStackInSlot(i).getItem()).getArmorMaterial().getDamageReductionAmount(getItemType(InventoryUtils.mc.thePlayer.inventory.getStackInSlot(i))) + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[] { InventoryUtils.mc.thePlayer.inventory.getStackInSlot(i) }, DamageSource.generic);
                    final ItemArmor ia2 = (ItemArmor)getStackInSlot(slot).getItem();
                    final ItemArmor ia3 = (ItemArmor)getStackInSlot(i).getItem();
                    if (ia2.armorType == 2 && ia3.armorType == 2 && otherProtection2 > slotProtection) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    public boolean isBestBoots(final int slot) {
        if (getStackInSlot(slot) != null && getStackInSlot(slot).getItem() != null && getStackInSlot(slot).getItem() instanceof ItemArmor) {
            final int slotProtection = ((ItemArmor)InventoryUtils.mc.thePlayer.inventory.getStackInSlot(slot).getItem()).getArmorMaterial().getDamageReductionAmount(getItemType(InventoryUtils.mc.thePlayer.inventory.getStackInSlot(slot))) + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[] { InventoryUtils.mc.thePlayer.inventory.getStackInSlot(slot) }, DamageSource.generic);
            if (InventoryUtils.mc.thePlayer.inventory.armorInventory[0] != null) {
                final ItemArmor ia = (ItemArmor)InventoryUtils.mc.thePlayer.inventory.armorInventory[0].getItem();
                final ItemStack is = InventoryUtils.mc.thePlayer.inventory.armorInventory[0];
                final ItemArmor ia2 = (ItemArmor)getStackInSlot(slot).getItem();
                final int otherProtection = ((ItemArmor)is.getItem()).getArmorMaterial().getDamageReductionAmount(getItemType(is)) + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[] { is }, DamageSource.generic);
                if (otherProtection > slotProtection || otherProtection == slotProtection) {
                    return false;
                }
            }
            for (int i = 0; i < InventoryUtils.mc.thePlayer.inventory.getSizeInventory(); ++i) {
                if (getStackInSlot(i) != null && InventoryUtils.mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemArmor) {
                    final int otherProtection2 = ((ItemArmor)InventoryUtils.mc.thePlayer.inventory.getStackInSlot(i).getItem()).getArmorMaterial().getDamageReductionAmount(getItemType(InventoryUtils.mc.thePlayer.inventory.getStackInSlot(i))) + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[] { InventoryUtils.mc.thePlayer.inventory.getStackInSlot(i) }, DamageSource.generic);
                    final ItemArmor ia2 = (ItemArmor)getStackInSlot(slot).getItem();
                    final ItemArmor ia3 = (ItemArmor)getStackInSlot(i).getItem();
                    if (ia2.armorType == 3 && ia3.armorType == 3 && otherProtection2 > slotProtection) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    public boolean isBestSword(final int slotIn) {
        return this.getBestWeapon() == slotIn;
    }
    
    public static int getItemType(final ItemStack itemStack) {
        if (itemStack.getItem() instanceof ItemArmor) {
            final ItemArmor armor = (ItemArmor)itemStack.getItem();
            return armor.armorType;
        }
        return -1;
    }
    
    public static float getItemDamage(final ItemStack itemStack) {
        final Multimap multimap = itemStack.getAttributeModifiers();
        final Iterator iterator;
        if (!multimap.isEmpty() && (iterator = multimap.entries().iterator()).hasNext()) {
            final Map.Entry entry = (Entry) iterator.next();
            final AttributeModifier attributeModifier = (AttributeModifier) entry.getValue();
            final double damage = (attributeModifier.getOperation() != 1 && attributeModifier.getOperation() != 2) ? attributeModifier.getAmount() : (attributeModifier.getAmount() * 100.0);
            return (attributeModifier.getAmount() > 1.0) ? (1.0f + (float)damage) : 1.0f;
        }
        return 1.0f;
    }
    
    public boolean hasItemMoreTimes(final int slotIn) {
        final boolean has = false;
        final ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
        stacks.clear();
        for (int i = 0; i < InventoryUtils.mc.thePlayer.inventory.getSizeInventory(); ++i) {
            if (!stacks.contains(getStackInSlot(i))) {
                stacks.add(getStackInSlot(i));
            }
            else if (getStackInSlot(i) == getStackInSlot(slotIn)) {
                return true;
            }
        }
        return false;
    }
    
    public int getBestWeaponInHotbar() {
        final int originalSlot = InventoryUtils.mc.thePlayer.inventory.currentItem;
        int weaponSlot = -1;
        float weaponDamage = 1.0f;
        for (int slot = 0; slot < 9; slot = (byte)(slot + 1)) {
            final ItemStack itemStack = InventoryUtils.mc.thePlayer.inventory.getStackInSlot(slot);
            if (itemStack != null) {
                float damage = getItemDamage(itemStack);
                if ((damage += EnchantmentHelper.getModifierForCreature(itemStack, EnumCreatureAttribute.UNDEFINED)) > weaponDamage) {
                    weaponDamage = damage;
                    weaponSlot = slot;
                }
            }
        }
        if (weaponSlot != -1) {
            return weaponSlot;
        }
        return originalSlot;
    }
    
    public int getBestWeapon() {
        final int originalSlot = InventoryUtils.mc.thePlayer.inventory.currentItem;
        int weaponSlot = -1;
        float weaponDamage = 1.0f;
        for (int slot = 0; slot < InventoryUtils.mc.thePlayer.inventory.getSizeInventory(); slot = (byte)(slot + 1)) {
            final ItemStack itemStack;
            if (getStackInSlot(slot) != null && (itemStack = getStackInSlot(slot)) != null && itemStack.getItem() != null && itemStack.getItem() instanceof ItemSword) {
                float damage = getItemDamage(itemStack);
                if ((damage += EnchantmentHelper.getModifierForCreature(itemStack, EnumCreatureAttribute.UNDEFINED)) > weaponDamage) {
                    weaponDamage = damage;
                    weaponSlot = slot;
                }
            }
        }
        if (weaponSlot != -1) {
            return weaponSlot;
        }
        return originalSlot;
    }
    
    public int getArmorProt(final int i) {
        int armorprot = -1;
        if (getStackInSlot(i) != null && getStackInSlot(i).getItem() != null && getStackInSlot(i).getItem() instanceof ItemArmor) {
            armorprot = ((ItemArmor)InventoryUtils.mc.thePlayer.inventory.getStackInSlot(i).getItem()).getArmorMaterial().getDamageReductionAmount(getItemType(InventoryUtils.mc.thePlayer.inventory.getStackInSlot(i))) + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[] { InventoryUtils.mc.thePlayer.inventory.getStackInSlot(i) }, DamageSource.generic);
        }
        return armorprot;
    }
    
    public static int getFirstItem(final Item i1) {
        for (int j = 0; j < InventoryUtils.mc.thePlayer.inventory.getSizeInventory(); ++j) {
            if (getStackInSlot(j) != null && getStackInSlot(j).getItem() != null && getStackInSlot(j).getItem() == i1) {
                return j;
            }
        }
        return -1;
    }
    
    public static boolean isBestSword(final ItemStack itemSword, final int slot) {
        if (itemSword != null && itemSword.getItem() instanceof ItemSword) {
            for (int i = 0; i < InventoryUtils.mc.thePlayer.inventory.getSizeInventory(); ++i) {
                final ItemStack iStack = InventoryUtils.mc.thePlayer.inventory.getStackInSlot(i);
                if (iStack != null && iStack.getItem() instanceof ItemSword && getItemDamage(iStack) >= getItemDamage(itemSword) && slot != i) {
                    return false;
                }
            }
        }
        return true;
    }
    
    static {
        InventoryUtils.mc = Minecraft.getMinecraft();
    }
}
