package buzz.gaoyusense.injection.mixins;

import acid.ui.MainMenu;
import net.minecraft.client.gui.GuiMainMenu;
import acid.api.events.world.EventTick;
import acid.api.events.world.EventClickMouse;
import net.minecraft.item.ItemBlock;
import acid.management.ModuleManager;
import acid.module.modules.player.FastPlace;
import acid.api.events.misc.EventKey;
import acid.api.EventBus;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.lwjgl.opengl.Display;
import acid.Client;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mutable;
import net.minecraft.util.Session;
import net.minecraft.util.Timer;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import buzz.gaoyusense.injection.interfaces.IMixinMinecraft;

@SideOnly(Side.CLIENT)
@Mixin({ Minecraft.class })
public abstract class MixinMinecraft implements IMixinMinecraft
{
    @Shadow
    public GuiScreen currentScreen;
    @Shadow
    private Timer timer;
    @Shadow
    public int rightClickDelayTimer;
    @Shadow
    @Mutable
    @Final
    private Session session;
    @Shadow
    private int leftClickCounter;
    
    @Shadow
    protected abstract void clickMouse();
    
    @Override
    public void runCrinkMouse() {
        this.clickMouse();
    }
    
    @Override
    public void setClickCounter(final int a) {
        this.leftClickCounter = a;
    }
    
    @Inject(method = "startGame", at = { @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;ingameGUI:Lnet/minecraft/client/gui/GuiIngame;", shift = At.Shift.AFTER) })
    private void startGame(final CallbackInfo ci) {
        final StringBuilder append = new StringBuilder().append("acid ");
        Client.instance.getClass();
        Display.setTitle(append.append(0.6).toString());
        Client.instance.initiate();
    }
    
    @Inject(method = "runTick", at = { @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;dispatchKeypresses()V", shift = At.Shift.AFTER) })
    private void onKey(final CallbackInfo ci) {
        if (Keyboard.getEventKeyState() && this.currentScreen == null) {
            EventBus.getInstance().call(new EventKey((Keyboard.getEventKey() == 0) ? (Keyboard.getEventCharacter() + 'Ā') : Keyboard.getEventKey()));
        }
    }
    
    @Inject(method = "rightClickMouse", at = { @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;rightClickDelayTimer:I", shift = At.Shift.AFTER) })
    private void rightClickMouse(final CallbackInfo callbackInfo) {
        Client.getModuleManager();
        final FastPlace fastPlace = (FastPlace)ModuleManager.getModuleByClass(FastPlace.class);
        if (fastPlace.isEnabled() && Minecraft.getMinecraft().thePlayer.getHeldItem() != null && Minecraft.getMinecraft().thePlayer.getHeldItem().getItem() instanceof ItemBlock && !fastPlace.blackList.contains(((ItemBlock)Minecraft.getMinecraft().thePlayer.getHeldItem().getItem()).getBlock())) {
            this.rightClickDelayTimer = (int)(Object)fastPlace.delay.getValue();
        }
    }
    
    @Inject(method = "clickMouse", at = { @At("HEAD") })
    private void clickMouse(final CallbackInfo ci) {
        EventBus.getInstance().call(new EventClickMouse());
    }
    
    @Inject(method = "runTick", at = { @At("RETURN") })
    private void runTick(final CallbackInfo ci) {
        EventBus.getInstance().call(new EventTick());
        if (this.currentScreen instanceof GuiMainMenu) {
            Minecraft.getMinecraft().displayGuiScreen((GuiScreen)new MainMenu());
        }
    }
    
    @Inject(method = "shutdown", at = { @At("HEAD") })
    private void onShutdown(final CallbackInfo ci) {
        Client.instance.shutDown();
    }
    
    @Override
    public Timer getTimer() {
        return this.timer;
    }
    
    @Override
    public Session getSession() {
        return this.session;
    }
    
    @Override
    public void setSession(final Session session) {
        this.session = session;
    }
}
