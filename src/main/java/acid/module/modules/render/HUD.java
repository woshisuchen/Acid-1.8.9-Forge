package acid.module.modules.render;

import acid.utils.math.MathUtil;
import acid.utils.Helper;
import acid.api.EventHandler;
import net.minecraft.client.gui.FontRenderer;
import java.util.Iterator;
import acid.ui.font.CFontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.MathHelper;
import net.minecraft.util.EnumChatFormatting;
import acid.utils.render.RenderUtil;
import acid.management.ModuleManager;
import acid.Client;
import java.util.ArrayList;
import acid.ui.font.FontLoaders;
import acid.api.events.rendering.EventRender2D;
import acid.api.value.Value;
import java.awt.Color;
import acid.module.ModuleType;
import acid.api.value.Option;
import acid.module.Module;

public class HUD extends Module
{
    private Option<Boolean> info;
    private Option<Boolean> rainbow;
    private Option<Boolean> customlogo;
    private Option<Boolean> customfont;
    public static Option<Boolean> wasLower;
    public static boolean shouldMove;
    public static boolean useFont;
    private String[] directions;
    private static int hudX;
    private static int hudY;
    
    public HUD() {
        super("HUD", new String[] { "gui" }, ModuleType.Render);
        this.info = new Option<Boolean>("Information", "information", true);
        this.rainbow = new Option<Boolean>("Rainbow", "rainbow", false);
        this.customlogo = new Option<Boolean>("Logo", "logo", false);
        this.customfont = new Option<Boolean>("Font", "font", false);
        this.directions = new String[] { "S", "SW", "W", "NW", "N", "NE", "E", "SE" };
        this.setColor(new Color(HUD.random.nextInt(255), HUD.random.nextInt(255), HUD.random.nextInt(255)).getRGB());
        this.setEnabled(true);
        this.setRemoved(true);
        this.addValues(this.info, this.rainbow, this.customlogo, this.customfont, HUD.wasLower);
    }
    
    @EventHandler
    private void renderHud(EventRender2D event) {
        CFontRenderer font = FontLoaders.kiona18;
        if (((Boolean)this.customfont.getValue()).booleanValue()) {
            useFont = true;
        } else if (!((Boolean)this.customfont.getValue()).booleanValue()) {
            useFont = false;
        }
        if (!this.mc.gameSettings.showDebugInfo) {
            int n;
            int staticColor;
            ArrayList<Module> sorted = new ArrayList();
            Client.instance.getModuleManager();
            int yTotal = 0;
            for (int i = 0; i < sorted.size(); ++i) {
                yTotal += font.getHeight() + 5;
            }
            for (Module m : ModuleManager.getModules()) {
                if (!m.isEnabled() || m.wasRemoved()) continue;
                sorted.add(m);
            }
            if (useFont) {
                sorted.sort((o1, o2) -> font.getStringWidth(o2.getSuffix().isEmpty() ? Client.getModuleName((Module)o2) : String.format((String)"%s %s", (Object[])new Object[]{Client.getModuleName((Module)o2), o2.getSuffix()})) - font.getStringWidth(o1.getSuffix().isEmpty() ? Client.getModuleName((Module)o1) : String.format((String)"%s %s", (Object[])new Object[]{Client.getModuleName((Module)o1), o1.getSuffix()})));
            } else {
                sorted.sort((o1, o2) -> this.mc.fontRendererObj.getStringWidth(o2.getSuffix().isEmpty() ? Client.getModuleName((Module)o2) : String.format((String)"%s %s", (Object[])new Object[]{Client.getModuleName((Module)o2), o2.getSuffix()})) - this.mc.fontRendererObj.getStringWidth(o1.getSuffix().isEmpty() ? Client.getModuleName((Module)o1) : String.format((String)"%s %s", (Object[])new Object[]{Client.getModuleName((Module)o1), o1.getSuffix()})));
            }
            int x2 = hudX;
            int y = hudY;
            int rainbowTick = 0;
            if (useFont) {
                for (Module m : sorted) {
                    staticColor = HUD.astolfoColors(sorted.indexOf((Object)m) * 6, yTotal);
                    String name = m.getSuffix().isEmpty() ? Client.getModuleName((Module)m) : String.format((String)"%s %s", (Object[])new Object[]{Client.getModuleName((Module)m), m.getSuffix()});
                    float x = hudX < -481 ? (float)(x2 - 60 + RenderUtil.width()) : (float)(x2 + RenderUtil.width() - font.getStringWidth(name));
                    Color rainbow = new Color(Color.HSBtoRGB((float)((float)((double)this.mc.thePlayer.ticksExisted / 150.0 + Math.sin((double)((double)rainbowTick / 50.0 * 1.6))) % 1.0f), (float)0.5f, (float)1.0f));
                    font.drawStringWithShadow(name, (double)(x - 3.0f), (double)(y + 1), (Boolean)this.rainbow.getValue() != false ? rainbow.getRGB() : staticColor);
                    if (++rainbowTick > 50) {
                        rainbowTick = 0;
                    }
                    y += 9;
                }
            } else {
                for (Module m : sorted) {
                    staticColor = HUD.astolfoColors(sorted.indexOf((Object)m) * 6, yTotal);
                    String name = m.getSuffix().isEmpty() ? Client.getModuleName((Module)m) : String.format((String)"%s %s", (Object[])new Object[]{Client.getModuleName((Module)m), m.getSuffix()});
                    float x = hudX < -481 ? (float)(x2 - 60 + RenderUtil.width()) : (float)(x2 + RenderUtil.width() - this.mc.fontRendererObj.getStringWidth(name));
                    Color rainbow = new Color(Color.HSBtoRGB((float)((float)((double)this.mc.thePlayer.ticksExisted / 150.0 + Math.sin((double)((double)rainbowTick / 50.0 * 1.6))) % 1.0f), (float)0.5f, (float)1.0f));
                    this.mc.fontRendererObj.drawStringWithShadow(name, x - 2.0f, (float)y, (Boolean)this.rainbow.getValue() != false ? rainbow.getRGB() : staticColor);
                    if (++rainbowTick > 50) {
                        rainbowTick = 0;
                    }
                    y += 9;
                }
            }
            String text = EnumChatFormatting.GRAY + "X" + EnumChatFormatting.WHITE + ": " + MathHelper.floor_double((double)this.mc.thePlayer.posX) + " " + EnumChatFormatting.GRAY + "Y" + EnumChatFormatting.WHITE + ": " + MathHelper.floor_double((double)this.mc.thePlayer.posY) + " " + EnumChatFormatting.GRAY + "Z" + EnumChatFormatting.WHITE + ": " + MathHelper.floor_double((double)this.mc.thePlayer.posZ);
            if (useFont) {
                int ychat = this.mc.ingameGUI.getChatGUI().getChatOpen() ? 24 : 10;
                n = ychat;
                if (((Boolean)this.info.getValue()).booleanValue()) {
                    font.drawStringWithShadow(text, 4.0, (double)(new ScaledResolution(this.mc).getScaledHeight() - ychat), new Color(11, 12, 17).getRGB());
                }
            } else {
                int ychat = this.mc.ingameGUI.getChatGUI().getChatOpen() ? 25 : 10;
                n = ychat;
                if (((Boolean)this.info.getValue()).booleanValue()) {
                    this.mc.fontRendererObj.drawStringWithShadow(text, 4.0f, (float)(new ScaledResolution(this.mc).getScaledHeight() - ychat), new Color(11, 12, 17).getRGB());
                }
            }
        }
    }
    public static int getHudX() {
        return HUD.hudX;
    }
    
    public static int getHudY() {
        return HUD.hudY;
    }
    
    public static void setHudX(final int hudX1) {
        HUD.hudX = hudX1;
    }
    
    public static void setHudY(final int hudY1) {
        HUD.hudY = hudY1;
    }
    
    public static boolean isHover(final int mouseX, final int mouseY) {
        final ScaledResolution res = new ScaledResolution(Helper.mc);
        if (MathUtil.inRange(mouseX, mouseY, getHudX() + res.getScaledWidth() + 3, getHudY() + res.getScaledHeight() - 238, getHudX() + res.getScaledWidth() - 70, getHudY() - 15)) {
            final Client instance = Client.instance;
            Client.getModuleManager();
            if (ModuleManager.getModuleByClass(HUD.class).isEnabled()) {
                return true;
            }
        }
        return false;
    }
    
    public static int astolfoColors(final int yOffset, final int yTotal) {
        float speed;
        float hue;
        for (speed = 2800.0f, hue = System.currentTimeMillis() % (int)speed + (yTotal - yOffset) * 8; hue > speed; hue -= speed) {}
        hue /= speed;
        if (hue > 0.5) {
            hue = 0.5f - (hue - 0.5f);
        }
        hue += 0.85;
        return Color.HSBtoRGB(hue, 0.85f, 1.0f);
    }
    
    static {
        HUD.wasLower = new Option<Boolean>("Lower", "Lower", true);
        HUD.hudX = 0;
        HUD.hudY = 2;
    }
}
