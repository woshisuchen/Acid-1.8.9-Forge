package acid.management;

import acid.command.Command;
import acid.management.FileManager;
import acid.management.FriendManager;
import acid.utils.Helper;
import net.minecraft.util.EnumChatFormatting;

class FriendManager$1
extends Command {
    private final FriendManager fm;
    final FriendManager this$0;

    FriendManager$1(FriendManager var1, String $anonymous0, String[] $anonymous1, String $anonymous2, String $anonymous3) {
        super($anonymous0, $anonymous1, $anonymous2, $anonymous3);
        this.this$0 = var1;
        this.fm = var1;
    }

    public String execute(String[] args) {
        if (args.length >= 3) {
            if (args[0].equalsIgnoreCase("add")) {
                String friends = "";
                friends = friends + String.format((String)"%s:%s%s", (Object[])new Object[]{args[1], args[2], System.lineSeparator()});
                FriendManager.access$0().put(args[1], args[2]);
                Helper.sendMessage((String)("> " + String.format((String)"%s has been added as %s", (Object[])new Object[]{args[1], args[2]})));
                FileManager.save((String)"Friends.txt", (String)friends, (boolean)true);
            } else if (args[0].equalsIgnoreCase("del")) {
                FriendManager.access$0().remove((Object)args[1]);
                Helper.sendMessage((String)("> " + String.format((String)"%s has been removed from your friends list", (Object[])new Object[]{args[1]})));
            } else if (args[0].equalsIgnoreCase("list")) {
                if (FriendManager.access$0().size() > 0) {
                    int var5 = 1;
                    for (String fr : FriendManager.access$0().values()) {
                        Helper.sendMessage((String)("> " + String.format((String)"%s. %s", (Object[])new Object[]{var5, fr})));
                        ++var5;
                    }
                } else {
                    Helper.sendMessage((String)"> get some friends fag lmao");
                }
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add")) {
                String friends = "";
                friends = friends + String.format((String)"%s%s", (Object[])new Object[]{args[1], System.lineSeparator()});
                FriendManager.access$0().put(args[1], args[1]);
                Helper.sendMessage((String)("> " + String.format((String)"%s has been added as %s", (Object[])new Object[]{args[1], args[1]})));
                FileManager.save((String)"Friends.txt", (String)friends, (boolean)true);
            } else if (args[0].equalsIgnoreCase("del")) {
                FriendManager.access$0().remove((Object)args[1]);
                Helper.sendMessage((String)("> " + String.format((String)"%s has been removed from your friends list", (Object[])new Object[]{args[1]})));
            } else if (args[0].equalsIgnoreCase("list")) {
                if (FriendManager.access$0().size() > 0) {
                    int var5 = 1;
                    for (String fr : FriendManager.access$0().values()) {
                        Helper.sendMessage((String)("> " + String.format((String)"%s. %s", (Object[])new Object[]{var5, fr})));
                        ++var5;
                    }
                } else {
                    Helper.sendMessage((String)"> you dont have any you lonely fuck");
                }
            }
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                if (FriendManager.access$0().size() > 0) {
                    int var5 = 1;
                    for (String fr : FriendManager.access$0().values()) {
                        Helper.sendMessage((String)String.format((String)"%s. %s", (Object[])new Object[]{var5, fr}));
                        ++var5;
                    }
                } else {
                    Helper.sendMessage((String)"you dont have any you lonely fuck");
                }
            } else if (!args[0].equalsIgnoreCase("add") && !args[0].equalsIgnoreCase("del")) {
                Helper.sendMessage((String)("> Correct usage: " + EnumChatFormatting.GRAY + "Valid .f add/del <player>"));
            } else {
                Helper.sendMessage((String)("> " + EnumChatFormatting.GRAY + "Please enter a players name"));
            }
        } else if (args.length == 0) {
            Helper.sendMessage((String)("> Correct usage: " + EnumChatFormatting.GRAY + "Valid .f add/del <player>"));
        }
        return null;
    }
}
