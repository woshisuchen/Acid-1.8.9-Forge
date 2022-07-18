package acid.module.modules.render;

import net.minecraft.client.gui.GuiScreen;
import acid.ui.astolfo.ClickUi;
import acid.module.ModuleType;

import org.lwjgl.input.Keyboard;

import acid.api.value.Mode;
import acid.module.Module;

public class ClickGui extends Module
{
    private Mode mode;
    
    public ClickGui() {
        super("ClickGui", new String[] { "clickui" }, ModuleType.Render);
        this.mode = new Mode("Mode", RenderMode.values(), RenderMode.Novoline);
        super.setRemoved(true);
        this.addValues(this.mode);
        this.setKey(Keyboard.KEY_RSHIFT);
    }
    
    @Override
    public void onEnable() {
    	
//        if (this.mode.getValue() == RenderMode.astolfo) {
            this.mc.displayGuiScreen((GuiScreen)new ClickUi());
//            this.setEnabled(false);
//        }
        this.setEnabled(false);
    }
    
    enum RenderMode
    {
        Novoline, 
        astolfo;
    }
}
