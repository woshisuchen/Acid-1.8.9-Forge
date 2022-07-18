package acid.module.modules.movement;

import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import acid.api.events.world.EventPacketSend;
import acid.utils.MovementUtils;
import acid.api.events.world.EventMove;
import acid.api.EventHandler;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.gui.GuiChat;
import acid.api.events.world.EventPreUpdate;
import acid.api.value.Value;
import acid.module.ModuleType;
import acid.api.value.Option;
import acid.module.Module;

public class InvMove extends Module
{
    boolean nomove;
    public static Option<Boolean> Cancel;
    
    public InvMove() {
        super("InvMove", new String[0], ModuleType.Movement);
        this.addValues(InvMove.Cancel);
    }
    
    @EventHandler
    public void onUpdate(final EventPreUpdate event) {
        if (this.mc.currentScreen != null && !(this.mc.currentScreen instanceof GuiChat)) {
            final KeyBinding[] key = { this.mc.gameSettings.keyBindForward, this.mc.gameSettings.keyBindBack, this.mc.gameSettings.keyBindLeft, this.mc.gameSettings.keyBindRight, this.mc.gameSettings.keyBindSprint, this.mc.gameSettings.keyBindJump };
            KeyBinding[] array;
            for (int length = (array = key).length, i = 0; i < length; ++i) {
                final KeyBinding b = array[i];
                KeyBinding.setKeyBindState(b.getKeyCode(), Keyboard.isKeyDown(b.getKeyCode()));
            }
        }
    }
    
    @EventHandler
    public void move(final EventMove e) {
        if (InvMove.Cancel.getValue() && this.nomove) {
            MovementUtils.setSpeed(e, 0.0);
        }
    }
    
    @EventHandler
    public void onmove(final EventPacketSend ep) {
        if (InvMove.Cancel.getValue()) {
            if (this.mc.currentScreen instanceof GuiInventory || this.mc.currentScreen instanceof GuiChat || this.mc.currentScreen instanceof GuiChest) {
                if (EventPacketSend.getPacket() instanceof C0EPacketClickWindow) {
                    this.nomove = true;
                }
            }
            else {
                this.nomove = false;
            }
        }
    }
    
    static {
        InvMove.Cancel = new Option<Boolean>("Cancel", "Cancel", Boolean.TRUE);
    }
}
