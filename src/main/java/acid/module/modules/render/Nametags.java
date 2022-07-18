package acid.module.modules.render;

import java.util.HashMap;
import acid.utils.render.RenderingUtil;
import acid.utils.Helper;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.Minecraft;
import java.text.NumberFormat;
import java.util.Iterator;
import java.text.SimpleDateFormat;
import org.apache.commons.lang3.StringUtils;
import net.minecraft.potion.PotionEffect;
import org.lwjgl.opengl.GL11;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemArmor;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import java.awt.Color;
import acid.utils.RenderUtil;
import acid.utils.render.Colors;
import acid.management.ModuleManager;
import acid.Client;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import java.util.Comparator;
import acid.management.FriendManager;
import java.util.Collection;
import java.util.ArrayList;
import net.minecraft.client.gui.ScaledResolution;
import acid.api.events.rendering.EventRender2D;
import acid.api.EventHandler;
import acid.api.events.rendering.EventRender3D;
import acid.api.value.Value;
import acid.module.ModuleType;
import acid.api.value.Option;
import net.minecraft.entity.player.EntityPlayer;
import java.util.Map;
import acid.api.value.Numbers;
import acid.module.Module;

public class Nametags extends Module
{
    private Numbers<Double> scale;
    public static Map<EntityPlayer, double[]> entityPositions;
    public static final Option<Boolean> ARMOR;
    public static final Option<Boolean> HEALTH;
    public static final Option<Boolean> INVISIBLES;
    public static final Option<Boolean> OPACITY;
    public static final Option<Boolean> distance;
    
    public Nametags() {
        super("NameTags", new String[] { "tags" }, ModuleType.Render);
        this.scale = new Numbers<Double>("Scale", "scale", 0.5, 0.0, 5.0, 0.05);
        this.addValues(this.scale, Nametags.ARMOR, Nametags.HEALTH, Nametags.INVISIBLES, Nametags.OPACITY, Nametags.distance);
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
    private void onRender3DEvent(final EventRender3D event) {
        try {
            this.updatePositions();
        }
        catch (Exception ex) {}
    }
    
    @EventHandler
    private void onRender3DEvent2(final EventRender2D er) {
        final ScaledResolution scaledRes = new ScaledResolution(this.mc);
        final ArrayList<EntityPlayer> entList = new ArrayList<EntityPlayer>(Nametags.entityPositions.keySet());
        entList.sort(Comparator.comparing(o -> TargetESP.isPriority(o) || FriendManager.isFriend(o.getName())));
        for (final EntityPlayer ent : entList) {
            int backgroundColor = 0;
            if (ent != this.mc.thePlayer) {
                if (!Nametags.INVISIBLES.getValue() && ent.isInvisible()) {
                    continue;
                }
                final int dist = (int)this.mc.thePlayer.getDistanceToEntity((Entity)ent);
                if (!(ent instanceof EntityPlayer)) {
                    continue;
                }
                GlStateManager.pushMatrix();
                String str = ent.getDisplayName().getFormattedText();
                str = str.replace(ent.getDisplayName().getFormattedText(), FriendManager.isFriend(ent.getName()) ? ("§b" + FriendManager.getAlias(ent.getName())) : ("§f" + ent.getDisplayName().getFormattedText()));
                final double[] renderPositions = Nametags.entityPositions.get(ent);
                if (renderPositions[3] < 0.0 || renderPositions[3] >= 1.0) {
                    GlStateManager.popMatrix();
                }
                else {
                    GlStateManager.translate(renderPositions[0] / scaledRes.getScaleFactor(), renderPositions[1] / scaledRes.getScaleFactor(), 0.0);
                    this.scale();
                    final String xin = "§c❤";
                    final String healthInfo = "[" + (int)ent.getHealth() + xin + "§a]";
                    GlStateManager.translate(0.0, -2.5, 0.0);
                    final int mouseY = er.getSR().getScaledHeight() / 2;
                    final int mouseX = er.getSR().getScaledWidth() / 2;
                    final double translateX = renderPositions[0] / scaledRes.getScaleFactor();
                    final double translateY = renderPositions[1] / scaledRes.getScaleFactor();
                    Client.getModuleManager();
                    final boolean isPriority = (ModuleManager.getModuleByClass(TargetESP.class).isEnabled() && TargetESP.isPriority(ent)) || FriendManager.isFriend(ent.getName());
                    float percentage = (float)(1.0 - ((float)Math.abs(mouseX - translateX) + Math.abs(mouseY - translateY)) / (mouseX + mouseY) * 3.0);
                    if (percentage < 0.2) {
                        percentage = 0.2f;
                    }
                    if (!Nametags.OPACITY.getValue() || isPriority) {
                        percentage = 1.0f;
                    }
                    final int n = FriendManager.isFriend(ent.getName()) ? Colors.getColor(0, 0, 0, 0) : (backgroundColor = (isPriority ? Colors.getColor(55, 35, 0) : Colors.getColor(0, (int)(120.0f * percentage))));
                    final int borderColor = FriendManager.isFriend(ent.getName()) ? Colors.getColor(100, 100, 125, 0) : (isPriority ? Colors.getColor(255, 0, 0) : backgroundColor);
                    final float strWidth = this.mc.fontRendererObj.getStringWidth(str);
                    final float strWidth2 = this.mc.fontRendererObj.getStringWidth(healthInfo) - 1;
                    RenderUtil.rectangleBordered(-strWidth / 2.0f - 2.0f, -13.0, strWidth / 2.0f + 2.0f, 0.0, 0.5, backgroundColor, borderColor);
                    final int x3 = (int)(renderPositions[0] + -strWidth / 2.0f - 3.0) / 2 - 26;
                    final int x4 = (int)(renderPositions[0] + strWidth / 2.0f + 3.0) / 2 + 20;
                    final int y1 = (int)(renderPositions[1] - 30.0) / 2;
                    final int y2 = (int)(renderPositions[1] + 11.0) / 2;
                    this.mc.fontRendererObj.drawStringWithShadow(str, -strWidth / 2.0f, -10.5f, Colors.getColor(255, (int)(200.0f * percentage)));
                    final boolean healthOption = !Nametags.HEALTH.getValue();
                    final boolean armor = !Nametags.ARMOR.getValue();
                    final boolean bl;
                    final boolean hovered = bl = (x3 < mouseX && mouseX < x4 && y1 < mouseY && mouseY < y2);
                    if (!healthOption || hovered) {
                        final float health = ent.getHealth();
                        final float[] fractions = { 0.0f, 0.5f, 1.0f };
                        final Color[] colors = { Color.RED, Color.YELLOW, Color.GREEN };
                        final float progress = health / ent.getMaxHealth();
                        final Color customColor = (health >= 0.0f) ? blendColors(fractions, colors, progress).brighter() : Color.RED;
                        try {
                            RenderUtil.rectangleBordered(strWidth / 2.0f + 2.0f, -13.0, strWidth / 2.0f + 5.5f + strWidth2, 0.0, 0.5, backgroundColor, borderColor);
                            this.mc.fontRendererObj.drawStringWithShadow(healthInfo, strWidth / 2.0f + 1.5f, -10.5f, Colors.getColor(customColor.getRed(), customColor.getGreen(), customColor.getBlue(), (int)(200.0f * percentage)));
                        }
                        catch (Exception ex) {}
                    }
                    if (Nametags.distance.getValue()) {
                        final String distanceStr = Nametags.distance.getValue() ? ("§c[§c" + dist + "m§c] ") : "";
                        final float witdh = this.mc.fontRendererObj.getStringWidth(distanceStr);
                        RenderUtil.rectangleBordered(-strWidth / 2.0f - witdh - 3.0, -13.0, -strWidth / 2.0f - 2.0f, 0.0, 0.5, backgroundColor, borderColor);
                        this.mc.fontRendererObj.drawStringWithShadow(distanceStr, -strWidth / 2.0f - witdh + 0.3f, -10.5f, Colors.getColor(255, (int)(150.0f * percentage)));
                    }
                    if (hovered || !armor) {
                        final ArrayList<ItemStack> itemsToRender = new ArrayList<ItemStack>();
                        for (int i = 0; i < 5; ++i) {
                            final ItemStack stack = ent.getEquipmentInSlot(i);
                            if (stack != null) {
                                itemsToRender.add(stack);
                            }
                        }
                        int x5 = (int)(-(itemsToRender.size() * 8.5));
                        for (final ItemStack stack2 : itemsToRender) {
                            RenderHelper.enableGUIStandardItemLighting();
                            this.mc.getRenderItem().renderItemIntoGUI(stack2, x5, -31);
                            this.mc.getRenderItem().renderItemOverlays(this.mc.fontRendererObj, stack2, x5, -29);
                            x5 += 3;
                            RenderHelper.disableStandardItemLighting();
                            if (stack2 == null) {
                                continue;
                            }
                            int y3 = 21;
                            final int sLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack2);
                            final int fLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack2);
                            final int kLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.knockback.effectId, stack2);
                            if (sLevel > 0) {
                                drawEnchantTag("Sh" + this.getColor(sLevel) + sLevel, x5, y3);
                                y3 -= 9;
                            }
                            if (fLevel > 0) {
                                drawEnchantTag("Fir" + this.getColor(fLevel) + fLevel, x5, y3);
                                y3 -= 9;
                            }
                            if (kLevel > 0) {
                                drawEnchantTag("Kb" + this.getColor(kLevel) + kLevel, x5, y3);
                            }
                            else if (stack2.getItem() instanceof ItemArmor) {
                                final int pLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack2);
                                final int tLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack2);
                                final int uLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack2);
                                if (pLevel > 0) {
                                    drawEnchantTag("P" + this.getColor(pLevel) + pLevel, x5, y3);
                                    y3 -= 9;
                                }
                                if (tLevel > 0) {
                                    drawEnchantTag("Th" + this.getColor(tLevel) + tLevel, x5, y3);
                                    y3 -= 9;
                                }
                                if (uLevel > 0) {
                                    drawEnchantTag("Unb" + this.getColor(uLevel) + uLevel, x5, y3);
                                }
                            }
                            else if (stack2.getItem() instanceof ItemBow) {
                                final int powLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack2);
                                final int punLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack2);
                                final int fireLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack2);
                                if (powLevel > 0) {
                                    drawEnchantTag("Pow" + this.getColor(powLevel) + powLevel, x5, y3);
                                    y3 -= 9;
                                }
                                if (punLevel > 0) {
                                    drawEnchantTag("Pun" + this.getColor(punLevel) + punLevel, x5, y3);
                                    y3 -= 9;
                                }
                                if (fireLevel > 0) {
                                    drawEnchantTag("Fir" + this.getColor(fireLevel) + fireLevel, x5, y3);
                                }
                            }
                            else if (stack2.getRarity() == EnumRarity.EPIC) {
                                drawEnchantTag("§6§lGod", x5, y3);
                            }
                            final int var7 = (int)Math.round(255.0 - stack2.getItemDamage() * 255.0 / stack2.getMaxDamage());
                            final int var8 = 255 - var7 << 16 | var7 << 8;
                            final Color customColor2 = new Color(var8).brighter();
                            final int x6 = (int)(x5 * 1.75);
                            if (stack2.getMaxDamage() - stack2.getItemDamage() > 0) {
                                GlStateManager.pushMatrix();
                                GlStateManager.disableDepth();
                                GL11.glScalef(0.57f, 0.57f, 0.57f);
                                this.mc.fontRendererObj.drawStringWithShadow("" + (stack2.getMaxDamage() - stack2.getItemDamage()), (float)x6, -54.0f, customColor2.getRGB());
                                GlStateManager.enableDepth();
                                GlStateManager.popMatrix();
                            }
                            y3 = -53;
                            for (final Object o2 : ent.getActivePotionEffects()) {
                                final PotionEffect pot = (PotionEffect)o2;
                                final String potName = StringUtils.capitalize(pot.getEffectName().substring(pot.getEffectName().lastIndexOf(".") + 1));
                                final int XD = pot.getDuration() / 20;
                                final SimpleDateFormat df = new SimpleDateFormat("m:ss");
                                final String time = df.format(XD * 1000);
                                this.mc.fontRendererObj.drawStringWithShadow((XD > 0) ? (potName + " " + time) : "", -30.0f, (float)y3, -1);
                                y3 -= 8;
                            }
                            x5 += 12;
                        }
                    }
                    GlStateManager.popMatrix();
                }
            }
        }
        Nametags.entityPositions.clear();
    }
    
    public static Color blendColors(final float[] fractions, final Color[] colors, final float progress) {
        final Color color = null;
        if (fractions == null) {
            throw new IllegalArgumentException("Fractions can't be null");
        }
        if (colors == null) {
            throw new IllegalArgumentException("Colours can't be null");
        }
        if (fractions.length != colors.length) {
            throw new IllegalArgumentException("Fractions and colours must have equal number of elements");
        }
        final int[] indicies = getFractionIndicies(fractions, progress);
        final float[] range = { fractions[indicies[0]], fractions[indicies[1]] };
        final Color[] colorRange = { colors[indicies[0]], colors[indicies[1]] };
        final float max = range[1] - range[0];
        final float value = progress - range[0];
        final float weight = value / max;
        return blend(colorRange[0], colorRange[1], 1.0f - weight);
    }
    
    public static int[] getFractionIndicies(final float[] fractions, final float progress) {
        final int[] range = new int[2];
        int startPoint;
        for (startPoint = 0; startPoint < fractions.length && fractions[startPoint] <= progress; ++startPoint) {}
        if (startPoint >= fractions.length) {
            startPoint = fractions.length - 1;
        }
        range[0] = startPoint - 1;
        range[1] = startPoint;
        return range;
    }
    
    public static Color blend(final Color color1, final Color color2, final double ratio) {
        final float r = (float)ratio;
        final float ir = 1.0f - r;
        final float[] rgb1 = new float[3];
        final float[] rgb2 = new float[3];
        color1.getColorComponents(rgb1);
        color2.getColorComponents(rgb2);
        float red = rgb1[0] * r + rgb2[0] * ir;
        float green = rgb1[1] * r + rgb2[1] * ir;
        float blue = rgb1[2] * r + rgb2[2] * ir;
        if (red < 0.0f) {
            red = 0.0f;
        }
        else if (red > 255.0f) {
            red = 255.0f;
        }
        if (green < 0.0f) {
            green = 0.0f;
        }
        else if (green > 255.0f) {
            green = 255.0f;
        }
        if (blue < 0.0f) {
            blue = 0.0f;
        }
        else if (blue > 255.0f) {
            blue = 255.0f;
        }
        Color color3 = null;
        try {
            color3 = new Color(red, green, blue);
        }
        catch (IllegalArgumentException exp) {
            final NumberFormat nf = NumberFormat.getNumberInstance();
            System.out.println(nf.format(red) + "; " + nf.format(green) + "; " + nf.format(blue));
            exp.printStackTrace();
        }
        return color3;
    }
    
    private String getColor(final int level) {
        switch (level) {
            case 1: {
                return "§f";
            }
            case 2: {
                return "§a";
            }
            case 3: {
                return "§3";
            }
            case 4: {
                return "§4";
            }
            case 5: {
                return "§6";
            }
            default: {
                return "§f";
            }
        }
    }
    
    private static void drawEnchantTag(final String text, int x, int y) {
        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
        x *= (int)1.75;
        GL11.glScalef(0.57f, 0.57f, 0.57f);
        final FontRenderer fontRendererObj = Minecraft.getMinecraft().fontRendererObj;
        final float n = x;
        final int n2 = -30;
        y -= 4;
        fontRendererObj.drawStringWithShadow(text, n, (float)(n2 - y), Colors.getColor(255));
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
    }
    
    public void scale() {
        float scale = (float)(Object)this.scale.getValue();
        GlStateManager.scale(scale *= (float)((this.mc.currentScreen == null && this.mc.gameSettings.smoothCamera) ? 2.0 : 1.0), scale, scale);
    }
    
    private void updatePositions() {
        Nametags.entityPositions.clear();
        final float pTicks = Helper.getTimer().renderPartialTicks;
        for (final Object entity : this.mc.theWorld.getLoadedEntityList()) {
            final EntityPlayer ent;
            if (entity instanceof EntityPlayer && (ent = (EntityPlayer)entity) != this.mc.thePlayer) {
                if (!Nametags.INVISIBLES.getValue() && ent.isInvisible()) {
                    continue;
                }
                final double x = ent.lastTickPosX + (ent.posX - ent.lastTickPosX) * pTicks - this.mc.getRenderManager().viewerPosX;
                double y = ent.lastTickPosY + (ent.posY - ent.lastTickPosY) * pTicks - this.mc.getRenderManager().viewerPosY;
                final double z = ent.lastTickPosZ + (ent.posZ - ent.lastTickPosZ) * pTicks - this.mc.getRenderManager().viewerPosZ;
                final double[] convertedPoints = RenderingUtil.convertTo2D(x, y += ent.height + 0.3, z);
                if (convertedPoints[2] < 0.0) {
                    continue;
                }
                if (convertedPoints[2] >= 1.0) {
                    continue;
                }
                Nametags.entityPositions.put(ent, new double[] { convertedPoints[0], convertedPoints[1], convertedPoints[1], convertedPoints[2] });
            }
        }
    }
    
    static {
        Nametags.entityPositions = new HashMap<EntityPlayer, double[]>();
        ARMOR = new Option<Boolean>("ARMOR", Boolean.valueOf(false));
        HEALTH = new Option<Boolean>("HEALTH", Boolean.valueOf(false));
        INVISIBLES = new Option<Boolean>("INVISIBLES", Boolean.valueOf(false));
        OPACITY = new Option<Boolean>("OPACITY", Boolean.valueOf(false));
        distance = new Option<Boolean>("distance", Boolean.valueOf(false));
    }
}
