package acid.utils.timer;

public class TimeHelper
{
    public long lastMs;
    
    public TimeHelper() {
        this.lastMs = 0L;
    }
    
    public boolean isDelayComplete(final long delay) {
        return System.currentTimeMillis() - this.lastMs > delay;
    }
    
    public long getCurrentMS() {
        return System.nanoTime() / 1000000L;
    }
    
    public void reset() {
        this.lastMs = System.currentTimeMillis();
    }
    
    public long getLastMs() {
        return this.lastMs;
    }
    
    public void setLastMs(final int i) {
        this.lastMs = System.currentTimeMillis() + i;
    }
    
    public boolean hasReached(final long milliseconds) {
        return this.getCurrentMS() - this.lastMs >= milliseconds;
    }
    
    public boolean isDelayComplete(final float delay) {
        return System.currentTimeMillis() - this.lastMs > delay;
    }
    
    public boolean isDelayComplete(final Double delay) {
        return System.currentTimeMillis() - this.lastMs > delay;
    }
    
    public boolean reach(final long milliseconds) {
        return System.currentTimeMillis() - this.lastMs >= milliseconds;
    }
    
    public long getTime() {
        return System.nanoTime() / 1000000L;
    }
    
    public boolean check(final float milliseconds) {
        return this.getTime() >= milliseconds;
    }
    
    public boolean reach(final double milliseconds) {
        return System.currentTimeMillis() - this.lastMs >= milliseconds;
    }
    
    public long elapsed() {
        return System.currentTimeMillis() - this.lastMs;
    }
}
