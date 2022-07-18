package buzz.gaoyusense.injection;

import java.util.Map;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.launch.MixinBootstrap;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

public class MixinLoader implements IFMLLoadingPlugin
{
    public MixinLoader() {
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.enjoytheban.json");
        MixinEnvironment.getDefaultEnvironment().setSide(MixinEnvironment.Side.CLIENT);
    }
    
    public String[] getASMTransformerClass() {
        return new String[0];
    }
    
    public String getModContainerClass() {
        return null;
    }
    
    public String getSetupClass() {
        return null;
    }
    
    public void injectData(final Map<String, Object> data) {
    }
    
    public String getAccessTransformerClass() {
        return null;
    }
}
