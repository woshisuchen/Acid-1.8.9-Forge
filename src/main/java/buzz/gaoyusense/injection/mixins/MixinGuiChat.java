package buzz.gaoyusense.injection.mixins;

import org.spongepowered.asm.mixin.Overwrite;
import java.io.IOException;
import net.minecraft.util.IChatComponent;
import org.lwjgl.input.Mouse;
import acid.module.modules.render.HUD;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.GuiChat;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;

@SideOnly(Side.CLIENT)
@Mixin({ GuiChat.class })
public abstract class MixinGuiChat extends GuiScreen
{
    @Shadow
    protected GuiTextField inputField;
    private int x;
    private int y;
    private int dragX;
    private int dragY;
    private boolean dragging;
    
    @Inject(method = "initGui", at = { @At("RETURN") })
    private void init(final CallbackInfo callbackInfo) {
        this.dragging = false;
    }
    
    public void mouseReleased(final int mouseX, final int mouseY, final int state) {
        if (HUD.isHover(mouseX, mouseY) && state == 0) {
            this.dragX = mouseX - HUD.getHudX();
            this.dragY = mouseY - HUD.getHudY();
            this.dragging = false;
        }
    }
    
    @Overwrite
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        if (HUD.isHover(mouseX, mouseY) && mouseButton == 0) {
            this.dragX = mouseX - HUD.getHudX();
            this.dragY = mouseY - HUD.getHudY();
            this.dragging = true;
            return;
        }
        if (mouseButton == 0) {
            final IChatComponent ichatcomponent = this.mc.ingameGUI.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY());
            if (this.handleComponentClick(ichatcomponent)) {
                return;
            }
        }
        this.inputField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
    
    @Inject(method = "drawScreen", at = { @At("HEAD") })
    public void mouse(final int mouseX, final int mouseY, final float partialTicks, final CallbackInfo info) {
        if (HUD.isHover(mouseX, mouseY)) {
            if (!Mouse.isButtonDown(0) && this.dragging) {
                this.dragging = false;
            }
            if (this.dragging) {
                HUD.setHudX(mouseX - this.dragX);
                HUD.setHudY(mouseY - this.dragY);
            }
        }
    }
}
