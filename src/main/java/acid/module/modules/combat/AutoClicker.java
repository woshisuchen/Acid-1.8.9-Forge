package acid.module.modules.combat;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import acid.api.events.world.EventClickMouse;
import buzz.gaoyusense.injection.interfaces.IEntityLivingBase;
import acid.api.EventHandler;
import buzz.gaoyusense.injection.interfaces.IMixinMinecraft;
import java.util.concurrent.ThreadLocalRandom;
import buzz.gaoyusense.injection.interfaces.IKeyBinding;
import acid.api.events.world.EventUpdate;
import acid.api.value.Value;
import acid.module.ModuleType;
import acid.api.value.Option;
import acid.api.value.Numbers;
import java.util.Random;
import acid.utils.timer.TimeHelper;
import acid.module.Module;

public class AutoClicker extends Module
{
    private final TimeHelper left;
    private final TimeHelper right;
    Random random;
    public static boolean isClicking;
    public boolean isDone;
    public int timer;
    public Numbers<Double> maxCps;
    public Numbers<Double> minCps;
    public Option<Boolean> blockHit;
    public Option<Boolean> jitter;
    
    public AutoClicker() {
        super("AutoClicker", new String[] { "AutoClicker", "AutoClicker" }, ModuleType.Combat);
        this.left = new TimeHelper();
        this.right = new TimeHelper();
        this.random = new Random();
        this.isDone = true;
        this.maxCps = new Numbers<Double>("MaxCPS", 12.0, 1.0, 20.0, 1.0);
        this.minCps = new Numbers<Double>("MinCPS", 8.0, 1.0, 20.0, 1.0);
        this.blockHit = new Option<Boolean>("BlockHit", Boolean.valueOf(false));
        this.jitter = new Option<Boolean>("Jitter", Boolean.valueOf(false));
        this.addValues(this.maxCps, this.minCps, this.blockHit, this.jitter);
    }
    
    @Override
    public void onEnable() {
        this.isDone = true;
        this.timer = 0;
        super.onEnable();
    }
    
    @Override
    public void onDisable() {
        this.isDone = true;
        super.onDisable();
    }
    
    private long getDelay() {
        return (long)((int)(Object)this.maxCps.getValue() + this.random.nextDouble() * ((int)(Object)this.minCps.getValue() - (int)(Object)this.maxCps.getValue()));
    }
    
    @EventHandler
    public void onUpdate(final EventUpdate event) {
        if (this.mc.thePlayer != null) {
            AutoClicker.isClicking = false;
            if ((int)(Object)this.minCps.getValue() > (int)(Object)this.maxCps.getValue()) {
                this.minCps.setValue((double)this.maxCps.getValue());
            }
            if (((IKeyBinding)this.mc.gameSettings.keyBindAttack).getPress() && this.mc.thePlayer.isUsingItem()) {
                this.swingItemNoPacket();
            }
            if (((IKeyBinding)this.mc.gameSettings.keyBindAttack).getPress() && !this.mc.thePlayer.isUsingItem() && this.left.isDelayComplete(Double.valueOf(1000.0 / ThreadLocalRandom.current().nextLong((long)(Object)this.minCps.getValue(), (long)(Object)this.maxCps.getValue() + 1L)))) {
                if (this.jitter.getValue()) {
                    this.jitter(this.random);
                }
                ((IMixinMinecraft)this.mc).setClickCounter(0);
                ((IMixinMinecraft)this.mc).runCrinkMouse();
                AutoClicker.isClicking = true;
                this.left.reset();
            }
        }
        if (!this.isDone) {
            switch (this.timer) {
                case 0: {
                    ((IKeyBinding)this.mc.gameSettings.keyBindUseItem).setPress(false);
                    break;
                }
                case 1:
                case 2: {
                    ((IKeyBinding)this.mc.gameSettings.keyBindUseItem).setPress(true);
                    break;
                }
                case 3: {
                    ((IKeyBinding)this.mc.gameSettings.keyBindUseItem).setPress(false);
                    this.isDone = true;
                    this.timer = -1;
                    break;
                }
            }
            ++this.timer;
        }
    }
    
    public void swingItemNoPacket() {
        if (!this.mc.thePlayer.isSwingInProgress || this.mc.thePlayer.swingProgressInt >= ((IEntityLivingBase)this.mc.thePlayer).runGetArmSwingAnimationEnd() / 2 || this.mc.thePlayer.swingProgressInt < 0) {
            this.mc.thePlayer.swingProgressInt = -1;
            this.mc.thePlayer.isSwingInProgress = true;
        }
    }
    
    @EventHandler
    public void onCrink(final EventClickMouse event) {
        final ItemStack stack = this.mc.thePlayer.getCurrentEquippedItem();
        if (stack != null && ((IKeyBinding)this.mc.gameSettings.keyBindRight).getPress() && this.blockHit.getValue() && stack.getItem() instanceof ItemSword && !this.mc.thePlayer.isUsingItem()) {
            if (!this.isDone || this.timer > 0) {
                return;
            }
            this.isDone = false;
        }
    }
    
    public void jitter(final Random rand) {
        if (rand.nextBoolean()) {
            if (rand.nextBoolean()) {
                final EntityPlayerSP thePlayer = this.mc.thePlayer;
                thePlayer.rotationPitch -= (float)(rand.nextFloat() * 0.6);
            }
            else {
                final EntityPlayerSP thePlayer2 = this.mc.thePlayer;
                thePlayer2.rotationPitch += (float)(rand.nextFloat() * 0.6);
            }
        }
        else if (rand.nextBoolean()) {
            final EntityPlayerSP thePlayer3 = this.mc.thePlayer;
            thePlayer3.rotationYaw -= (float)(rand.nextFloat() * 0.6);
        }
        else {
            final EntityPlayerSP thePlayer4 = this.mc.thePlayer;
            thePlayer4.rotationYaw += (float)(rand.nextFloat() * 0.6);
        }
    }
    
    static {
        AutoClicker.isClicking = false;
    }
}
