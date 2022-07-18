package acid.module.modules.render;

import com.google.common.collect.Lists;
import acid.utils.render.RenderUtils;
import acid.utils.render.ColorUtils;
import net.minecraft.block.Block;
import java.util.Iterator;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import acid.api.events.rendering.EventRender3D;
import acid.api.EventHandler;
import acid.api.events.world.EventTick;
import java.awt.Color;
import acid.api.value.Value;
import acid.module.ModuleType;
import acid.utils.timer.TimerUtil;
import acid.api.value.Option;
import acid.api.value.Numbers;
import java.util.HashSet;
import java.util.List;
import acid.module.Module;

public class Xray extends Module
{
    public static int alpha;
    public static boolean isEnabled;
    public static List blockIdList;
    private static final HashSet blockPosList;
    public static Numbers<Number> opacity;
    public static Numbers<Number> distance;
    public static Numbers<Number> delay;
    public static Option<Boolean> ESP;
    public static Option<Boolean> UHC;
    public static Option<Boolean> CAVE;
    public static Option<Boolean> update;
    public static Option<Boolean> Tracers;
    public static Option<Boolean> CoalOre;
    public static Option<Boolean> RedStoneOre;
    public static Option<Boolean> IronOre;
    public static Option<Boolean> GoldOre;
    public static Option<Boolean> DiamondOre;
    public static Option<Boolean> EmeraldOre;
    public static Option<Boolean> LapisOre;
    private TimerUtil timer;
    private int opacity2;
    
    public Xray() {
        super("Xray", new String[] { "xrai", "oreesp" }, ModuleType.Render);
        this.timer = new TimerUtil();
        this.opacity2 = 160;
        this.addValues(Xray.opacity, Xray.distance, Xray.delay, Xray.CAVE, Xray.ESP, Xray.UHC, Xray.update, Xray.Tracers, Xray.DiamondOre, Xray.GoldOre, Xray.IronOre, Xray.CoalOre, Xray.EmeraldOre, Xray.LapisOre, Xray.RedStoneOre);
        this.setColor(Color.GREEN.getRGB());
        Xray.update.getClass();
    }
    
    public int getOpacity() {
        return this.opacity2;
    }
    
    public HashSet<Integer> getBlocks() {
        return (HashSet<Integer>)Xray.blockPosList;
    }
    
    public static boolean containsID(final int id) {
        return Xray.blockPosList.contains(id);
    }
    
    private void onToggle(final boolean var1) {
        Xray.blockPosList.clear();
        this.mc.renderGlobal.loadRenderers();
        Xray.isEnabled = var1;
    }
    
    @Override
    public void onEnable() {
        this.opacity2 = Xray.opacity.getValue().intValue();
        this.onToggle(true);
    }
    
    @Override
    public void onDisable() {
        this.onToggle(false);
        this.timer.reset();
    }
    
    @EventHandler
    public void update(final EventTick var1) {
        if (Xray.alpha != Xray.opacity.getValue().intValue()) {
            this.mc.renderGlobal.loadRenderers();
            Xray.alpha = Xray.opacity.getValue().intValue();
        }
        if (Xray.update.getValue() && this.timer.delay(1000.0 * Xray.delay.getValue().intValue())) {
            this.mc.renderGlobal.loadRenderers();
            this.timer.reset();
        }
    }
    
    @EventHandler
    public void onRender3D(final EventRender3D var1) {
        if (Xray.ESP.getValue()) {
            final Iterator var2 = Xray.blockPosList.iterator();
            if (var2.hasNext()) {
                final BlockPos var3 = (BlockPos) var2.next();
                if (this.mc.thePlayer.getDistance((double)var3.getX(), 0.0, (double)var3.getZ()) <= Xray.distance.getValue().intValue()) {
                    final Block var4 = this.mc.theWorld.getBlockState(var3).getBlock();
                    if (var4 == Blocks.diamond_ore && Xray.DiamondOre.getValue()) {
                        this.render3D(var3, 0, 255, 255);
                    }
                    if (var4 == Blocks.iron_ore && Xray.IronOre.getValue()) {
                        this.render3D(var3, 225, 225, 225);
                    }
                    if (var4 == Blocks.lapis_ore && Xray.LapisOre.getValue()) {
                        this.render3D(var3, 0, 0, 255);
                    }
                    if (var4 == Blocks.redstone_ore && Xray.RedStoneOre.getValue()) {
                        this.render3D(var3, 255, 0, 0);
                    }
                    if (var4 == Blocks.coal_ore && Xray.CoalOre.getValue()) {
                        this.render3D(var3, 0, 30, 30);
                    }
                    if (var4 == Blocks.emerald_ore && Xray.EmeraldOre.getValue()) {
                        this.render3D(var3, 0, 255, 0);
                    }
                    if (var4 == Blocks.gold_ore && Xray.GoldOre.getValue()) {
                        this.render3D(var3, 255, 255, 0);
                    }
                }
            }
        }
    }
    
    private void render3D(final BlockPos var1, final int var2, final int var3, final int var4) {
        if (Xray.ESP.getValue()) {
            RenderUtils.drawSolidBlockESP(var1, ColorUtils.getColor(var2, var3, var4));
        }
        if (Xray.Tracers.getValue()) {
            RenderUtils.drawLine(var1, ColorUtils.getColor(var2, var3, var4));
        }
    }
    
    public static boolean showESP() {
        return Xray.ESP.getValue();
    }
    
    public static int getDistance() {
        return (int) Xray.distance.getValue();
    }
    
    static {
        Xray.blockIdList = Lists.newArrayList((Object[])new Integer[] { 10, 11, 8, 9, 14, 15, 16, 21, 41, 42, 46, 48, 52, 56, 57, 61, 62, 73, 74, 84, 89, 103, 116, 117, 118, 120, 129, 133, 137, 145, 152, 153, 154 });
        blockPosList = new HashSet();
        Xray.opacity = new Numbers<Number>("OPACITY", "OPACITY", 160.0, 0.0, 255.0, 5.0);
        Xray.distance = new Numbers<Number>("XRAY_DISTANCE", "XRAY_DISTANCE", 42.0, 12.0, 64.0, 4.0);
        Xray.delay = new Numbers<Number>("XR_DELAY", "XR_DELAY", 10.0, 1.0, 30.0, 0.5);
        Xray.ESP = new Option<Boolean>("ESP", "ESP", true);
        Xray.UHC = new Option<Boolean>("UHC", "UHC", true);
        Xray.CAVE = new Option<Boolean>("CAVE", "CAVE", true);
        Xray.update = new Option<Boolean>("UPDATE", "UPDATE", true);
        Xray.Tracers = new Option<Boolean>("Tracers", "Tracers", false);
        Xray.CoalOre = new Option<Boolean>("Coal Ore", "Coal Ore", true);
        Xray.RedStoneOre = new Option<Boolean>("RedStone Ore", "RedStone Ore", true);
        Xray.IronOre = new Option<Boolean>("Iron Ore", "Iron Ore", true);
        Xray.GoldOre = new Option<Boolean>("Gold Ore", "Gold Ore", true);
        Xray.DiamondOre = new Option<Boolean>("Diamond Ore", "Diamond Ore", true);
        Xray.EmeraldOre = new Option<Boolean>("Emerald Ore", "Emerald Ore", true);
        Xray.LapisOre = new Option<Boolean>("Lapis Ore", "Lapis Ore", true);
    }
}
