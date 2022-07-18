package acid.command.commands;

import net.minecraft.util.EnumChatFormatting;
import acid.utils.math.MathUtil;
import acid.utils.Helper;
import acid.utils.timer.TimerUtil;
import acid.command.Command;

public class VClip extends Command
{
    private TimerUtil timer;
    
    public VClip() {
        super("Vc", new String[] { "Vclip", "clip", "verticalclip", "clip" }, "", "Teleport down a specific ammount");
        this.timer = new TimerUtil();
    }
    
    @Override
    public String execute(final String[] args) {
        if (!Helper.onServer("enjoytheban")) {
            if (args.length > 0) {
                if (MathUtil.parsable(args[0], (byte)4)) {
                    final float distance = Float.parseFloat(args[0]);
                    Helper.mc.thePlayer.setPosition(Helper.mc.thePlayer.posX, Helper.mc.thePlayer.posY + distance, Helper.mc.thePlayer.posZ);
                    Helper.sendMessage("> Vclipped " + distance + " blocks");
                }
                else {
                    this.syntaxError(EnumChatFormatting.GRAY + args[0] + " is not a valid number");
                }
            }
            else {
                this.syntaxError(EnumChatFormatting.GRAY + "Valid .vclip <number>");
            }
        }
        else {
            Helper.sendMessage("> You cannot use vclip on the ETB Server.");
        }
        return null;
    }
}
