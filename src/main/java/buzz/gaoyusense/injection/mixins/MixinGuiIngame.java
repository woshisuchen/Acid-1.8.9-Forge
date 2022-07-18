package buzz.gaoyusense.injection.mixins;

import net.minecraft.client.renderer.GlStateManager;
import acid.api.events.rendering.EventRender2D;
import acid.api.events.rendering.EventRenderGui;
import net.minecraft.client.gui.ScaledResolution;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.Overwrite;
import acid.api.events.misc.EventTitle;
import acid.api.EventBus;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.gui.Gui;

@SideOnly(Side.CLIENT)
@Mixin({ GuiIngame.class })
public class MixinGuiIngame extends Gui
{
    public Minecraft mc;
    @Shadow
    public String displayedTitle;
    @Shadow
    public String displayedSubTitle;
    @Shadow
    public int titlesTimer;
    @Shadow
    public int titleFadeIn;
    @Shadow
    public int titleDisplayTime;
    @Shadow
    public int titleFadeOut;
    
    public MixinGuiIngame() {
        this.displayedTitle = "";
        this.displayedSubTitle = "";
    }
    
    @Inject(method = "showCrosshair", at = { @At("HEAD") }, cancellable = true)
    private void injectCrosshair(CallbackInfoReturnable<Boolean> cir) {
        if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 1 || Minecraft.getMinecraft().gameSettings.thirdPersonView == 2) {
            cir.setReturnValue(false);
        }
    }
//  傻逼方法还重写呢 这个event无调用 如果需要自行添加
    @Overwrite
    public void displayTitle(String title, String subTitle, int timeFadeIn, int displayTime, int timeFadeOut)
    {
        if (title == null && subTitle == null && timeFadeIn < 0 && displayTime < 0 && timeFadeOut < 0)
        {
            this.displayedTitle = "";
            this.displayedSubTitle = "";
            this.titlesTimer = 0;
        }
        else if (title != null)
        {
        	EventBus.getInstance().call(new EventTitle(title));
            this.displayedTitle = title;
            this.titlesTimer = this.titleFadeIn + this.titleDisplayTime + this.titleFadeOut;
        }
        else if (subTitle != null)
        {
            this.displayedSubTitle = subTitle;
        }
        else
        {
            if (timeFadeIn >= 0)
            {
                this.titleFadeIn = timeFadeIn;
            }

            if (displayTime >= 0)
            {
                this.titleDisplayTime = displayTime;
            }

            if (timeFadeOut >= 0)
            {
                this.titleFadeOut = timeFadeOut;
            }

            if (this.titlesTimer > 0)
            {
                this.titlesTimer = this.titleFadeIn + this.titleDisplayTime + this.titleFadeOut;
            }
        }
    }
    
    @Inject(method = "renderGameOverlay", at = { @At("RETURN") }, cancellable = true)
    private void renderGameOverlay(float partialTicks, CallbackInfo ci) {
        ScaledResolution scaledresolution = new ScaledResolution(this.mc);
        EventBus.getInstance().call(new EventRenderGui(scaledresolution));
        EventBus.getInstance().call(new EventRender2D(scaledresolution, partialTicks));
        GlStateManager.color(1.0f, 1.0f, 1.0f);
    }
    
    @Inject(method = "renderTooltip", at = { @At("HEAD") }, cancellable = true)
    private void renderTooltip(ScaledResolution sr, float partialTicks, CallbackInfo ci) {
        EventBus.getInstance().call(new EventRender2D(sr, partialTicks));
        GlStateManager.color(1.0f, 1.0f, 1.0f);
    }
}
