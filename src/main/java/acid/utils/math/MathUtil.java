package acid.utils.math;

import net.minecraft.entity.Entity;
import net.minecraft.potion.Potion;
import acid.utils.Helper;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.util.MathHelper;
import java.util.Random;

public class MathUtil
{
    public static Random random;
    
    public static double toDecimalLength(final double in, final int places) {
        return Double.parseDouble(String.format("%." + places + "f", in));
    }
    
    public static double round(final double in, int places) {
        places = (int)MathHelper.clamp_double((double)places, 0.0, 2.147483647E9);
        return Double.parseDouble(String.format("%." + places + "f", in));
    }
    
    public static boolean parsable(final String s, final byte type) {
        try {
            switch (type) {
                case 0: {
                    Short.parseShort(s);
                    break;
                }
                case 1: {
                    Byte.parseByte(s);
                    break;
                }
                case 2: {
                    Integer.parseInt(s);
                    break;
                }
                case 3: {
                    Float.parseFloat(s);
                    break;
                }
                case 4: {
                    Double.parseDouble(s);
                    break;
                }
                case 5: {
                    Long.parseLong(s);
                    break;
                }
            }
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public static double square(final double in) {
        return in * in;
    }
    
    public static double randomDouble(final double min, final double max) {
        return ThreadLocalRandom.current().nextDouble(min, max);
    }
    
    public static double getBaseMovementSpeed() {
        double baseSpeed = 0.2873;
        if (Helper.mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            final int amplifier = Helper.mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
        }
        return baseSpeed;
    }
    
    public static double getHighestOffset(final double max) {
        for (double i = 0.0; i < max; i += 0.01) {
            final int[] arrn2;
            final int[] arrn = arrn2 = new int[] { -2, -1, 0, 1, 2 };
            for (int n = arrn.length, n2 = 0; n2 < n; ++n2) {
                final int offset = arrn2[n2];
                if (Helper.mc.theWorld.getCollidingBoundingBoxes((Entity)Helper.mc.thePlayer, Helper.mc.thePlayer.getEntityBoundingBox().offset(Helper.mc.thePlayer.motionX * offset, i, Helper.mc.thePlayer.motionZ * offset)).size() > 0) {
                    return i - 0.01;
                }
            }
        }
        return max;
    }
    
    public static boolean inRange(final double x, final double y, final double maxX, final double maxY, final double minX, final double minY) {
        return x > minX && x < maxX && y > minY && y < maxY;
    }
    
    static {
        MathUtil.random = new Random();
    }
    
    public static class NumberType
    {
        public static final byte SHORT = 0;
        public static final byte BYTE = 1;
        public static final byte INT = 2;
        public static final byte FLOAT = 3;
        public static final byte DOUBLE = 4;
        public static final byte LONG = 5;
        
        public static byte getByType(final Class cls) {
            if (cls == Short.class) {
                return 0;
            }
            if (cls == Byte.class) {
                return 1;
            }
            if (cls == Integer.class) {
                return 2;
            }
            if (cls == Float.class) {
                return 3;
            }
            if (cls == Double.class) {
                return 4;
            }
            if (cls == Long.class) {
                return 5;
            }
            return -1;
        }
    }
}
