package acid.module.modules.movement;

import acid.utils.PlayerUtils;
import acid.api.events.world.EventPreUpdate;
import acid.api.events.misc.EventChat;
import acid.api.EventHandler;
import acid.api.events.world.EventTick;
import acid.api.value.Value;
import java.awt.Color;
import acid.module.ModuleType;
import acid.api.value.Option;
import acid.module.Module;

public class Sprint extends Module
{
    public static boolean isSprinting;
    private Option<Boolean> Re;
    private Option<Boolean> omni;
    public static Option<Boolean> ww;
    public static Option<Boolean> ww2;
    Option bback;
    
    public Sprint() {
        super("Sprint", new String[] { "run" }, ModuleType.Movement);
        this.Re = new Option<Boolean>("AutoRespawn", "AutoRespawn", true);
        this.omni = new Option<Boolean>("Omni-Directional", "omni", true);
        this.bback = new Option("AutoBack", true);
        this.setColor(new Color(158, 205, 125).getRGB());
        this.addValues(this.omni, this.Re, this.bback, Sprint.ww, Sprint.ww2);
    }
    
    @Override
    public void onDisable() {
        Sprint.isSprinting = false;
        this.mc.thePlayer.setSprinting(false);
        super.onDisable();
    }
    
    @EventHandler
    public void tick(final EventTick e) {
        if (this.Re.getValue() && this.mc.thePlayer.isDead) {
            this.mc.thePlayer.respawnPlayer();
        }
    }
    
    @EventHandler
    private void onChat(final EventChat e) {
        if ((boolean)this.bback.getValue()) {
            if (e.getMessage().contains("Flying or related.")) {
                if (this.mc.thePlayer == null) {
                    return;
                }
                this.mc.thePlayer.sendChatMessage("/lobby");
                new Thread(() -> {
                    try {
                        Thread.sleep(200L);
                    }
                    catch (InterruptedException e2) {
                        e2.printStackTrace();
                    }
                    this.mc.thePlayer.sendChatMessage("/back");
                    return;
                }).start();
            }
            if (e.getMessage().contains("You were spawned in Limbo.")) {
                if (this.mc.thePlayer == null) {
                    return;
                }
                this.mc.thePlayer.sendChatMessage("/lobby");
                new Thread(() -> {
                    try {
                        Thread.sleep(200L);
                    }
                    catch (InterruptedException e3) {
                        e3.printStackTrace();
                    }
                    this.mc.thePlayer.sendChatMessage("/back");
                }).start();
            }
        }
    }
    
    @EventHandler
    private void onUpdate(final EventPreUpdate event) {
        final boolean canSprint = this.mc.thePlayer.getFoodStats().getFoodLevel() > 6.0f || this.mc.thePlayer.capabilities.allowFlying;
        if (PlayerUtils.MovementInput() && canSprint) {
            Sprint.isSprinting = true;
            this.mc.thePlayer.setSprinting(true);
        }
        else {
            Sprint.isSprinting = false;
        }
    }
    
    static {
        Sprint.ww = new Option<Boolean>("wwJump", "wwJump", true);
        Sprint.ww2 = new Option<Boolean>("wwJump2", "wwJump2", true);
    }
}
