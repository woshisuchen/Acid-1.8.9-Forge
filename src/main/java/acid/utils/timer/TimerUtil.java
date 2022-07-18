package acid.utils.timer;

import net.minecraft.util.MathHelper;

public class TimerUtil
{
    private long lastMS;
    
    private long getCurrentMS() {
        return System.nanoTime() / 1000000L;
    }
    
    public boolean hasReached(final double milliseconds) {
        return this.getCurrentMS() - this.lastMS >= milliseconds;
    }
    
    public void reset() {
        this.lastMS = this.getCurrentMS();
    }
    
    public boolean delay(final float milliSec) {
        return this.getTime() - this.lastMS >= milliSec;
    }
    
    public boolean delay(final double var1) {
        return MathHelper.clamp_float((float)(this.getCurrentMS() - this.lastMS), 0.0f, (float)var1) >= var1;
    }
    
    public long getTime() {
        return System.nanoTime() / 1000000L;
    }
    
    public final boolean hasPassed(final long milliseconds) {
        return this.getCurrentMS() - this.lastMS > milliseconds;
    }
}
