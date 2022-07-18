package acid.management;

import java.util.List;
import acid.command.Command;
import acid.Client;

import java.util.HashMap;

public class FriendManager implements Manager
{
    private static HashMap friends;
    
    public void init() {
        FriendManager.friends = new HashMap<String,String>();
        final List<String> frriends = FileManager.read("Friends.txt");
        
        for (final String v : frriends) {
            if (v.contains(":")) {
                final String name = v.split(":")[0];
                final String alias = v.split(":")[1];
                FriendManager.friends.put(name, alias);
            }
            else {
                FriendManager.friends.put(v, v);
            }
        }
        
        Client.instance.getCommandManager().add((Command)new FriendManager$1(this, "f", new String[] { "friend", "fren", "fr" }, "add/del/list name alias", "Manage client friends"));
    }
    
    public static boolean isFriend(final String name) {
        return FriendManager.friends.containsKey(name);
    }
    
    public static String getAlias(final Object friends2) {
        return (String) FriendManager.friends.get(friends2);
    }
    
    public static HashMap getFriends() {
        return FriendManager.friends;
    }
    
    static HashMap<String,String> access$0() {
        return FriendManager.friends;
    }
}
