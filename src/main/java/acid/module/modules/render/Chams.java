package acid.module.modules.render;

import acid.api.events.rendering.EventPostRenderPlayer;
import acid.api.EventHandler;
import org.lwjgl.opengl.GL11;
import acid.api.events.rendering.EventPreRenderPlayer;
import java.awt.Color;
import acid.api.value.Value;
import acid.module.ModuleType;
import acid.api.value.Mode;
import acid.module.Module;

public class Chams extends Module
{
    public Mode<Enum> mode;
    
    public Chams() {
        super("Chams", new String[] { "seethru", "cham" }, ModuleType.Render);
        this.mode = new Mode<Enum>("Mode", "mode", ChamsMode.values(), ChamsMode.Textured);
        this.addValues(this.mode);
        this.setColor(new Color(159, 190, 192).getRGB());
    }
    
    @EventHandler
    private void preRenderPlayer(final EventPreRenderPlayer e) {
        GL11.glEnable(32823);
        GL11.glPolygonOffset(1.0f, -1100000.0f);
    }
    
    @EventHandler
    private void postRenderPlayer(final EventPostRenderPlayer e) {
        GL11.glDisable(32823);
        GL11.glPolygonOffset(1.0f, 1100000.0f);
    }
    
    public enum ChamsMode
    {
        Textured, 
        Normal;
    }
}
