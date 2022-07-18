package acid.utils.mixin;

import buzz.gaoyusense.injection.interfaces.IMixinMinecraft;
import net.minecraft.client.Minecraft;

public interface MinecraftToMixin
{
    public static final Minecraft mc = Minecraft.getMinecraft();
    public static final IMixinMinecraft mixMC = (IMixinMinecraft)MinecraftToMixin.mc;
}
