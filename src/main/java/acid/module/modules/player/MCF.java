package acid.module.modules.player;

import acid.api.EventHandler;
import acid.management.FriendManager;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Mouse;
import acid.api.events.world.EventPreUpdate;
import java.awt.Color;
import acid.module.ModuleType;
import acid.module.Module;

public class MCF extends Module
{
    private boolean down;
    
    public MCF() {
        super("MCF", new String[] { "middleclickfriends", "middleclick" }, ModuleType.Player);
        this.setColor(new Color(241, 175, 67).getRGB());
    }
    
    @EventHandler
    private void onClick(final EventPreUpdate e) {
        if (Mouse.isButtonDown(2) && !this.down) {
            if (this.mc.objectMouseOver.entityHit != null) {
                final EntityPlayer player = (EntityPlayer)this.mc.objectMouseOver.entityHit;
                final String playername = player.getName();
                if (!FriendManager.isFriend(playername)) {
                    this.mc.thePlayer.sendChatMessage(".f add " + playername);
                }
                else {
                    this.mc.thePlayer.sendChatMessage(".f del " + playername);
                }
            }
            this.down = true;
        }
        if (!Mouse.isButtonDown(2)) {
            this.down = false;
        }
    }
}
