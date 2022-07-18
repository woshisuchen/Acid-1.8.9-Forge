package buzz.gaoyusense.injection.interfaces;

import net.minecraft.util.Timer;
import net.minecraft.util.Session;

public interface IMixinMinecraft
{
    Session getSession();
    
    Timer getTimer();
    
    void setSession(final Session p0);
    
    void setClickCounter(final int p0);
    
    void runCrinkMouse();
}
