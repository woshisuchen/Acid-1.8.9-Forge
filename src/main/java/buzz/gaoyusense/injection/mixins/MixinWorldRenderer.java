package buzz.gaoyusense.injection.mixins;

import org.spongepowered.asm.mixin.Overwrite;
import acid.utils.render.ColorUtils;
import acid.management.ModuleManager;
import acid.module.modules.render.Xray;
import acid.Client;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.client.renderer.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ WorldRenderer.class })
public abstract class MixinWorldRenderer
{
    @Shadow
    private boolean noColor;
    @Shadow
    private IntBuffer rawIntBuffer;
    @Shadow
    private int vertexFormatIndex;
    int iR;
    int iG;
    int iB;
    
    @Shadow
    public abstract int getColorIndex(final int p0);
    
    @Overwrite
    public void putColorMultiplier(final float red, final float green, final float blue, final int p_178978_4_) {
        final int i = this.getColorIndex(p_178978_4_);
        int j = -1;
        if (!this.noColor) {
            j = this.rawIntBuffer.get(i);
            if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
                this.iR = (int)((j & 0xFF) * red);
                this.iG = (int)((j >> 8 & 0xFF) * green);
                this.iB = (int)((j >> 16 & 0xFF) * blue);
                j &= 0xFF000000;
                j = (j | this.iB << 16 | this.iG << 8 | this.iR);
            }
            else {
                this.iR = (int)((j >> 24 & 0xFF) * red);
                this.iG = (int)((j >> 16 & 0xFF) * green);
                this.iB = (int)((j >> 8 & 0xFF) * blue);
                j &= 0xFF;
                j = (j | this.iR << 24 | this.iG << 16 | this.iB << 8);
            }
        }
        Client.getModuleManager();
        final Xray x = (Xray)ModuleManager.getModuleByClass(Xray.class);
        Client.getModuleManager();
        if (ModuleManager.getModuleByClass(Xray.class).isEnabled()) {
            j = ColorUtils.getColor(this.iR, this.iG, this.iB, x.getOpacity());
        }
        this.rawIntBuffer.put(i, j);
    }
}
