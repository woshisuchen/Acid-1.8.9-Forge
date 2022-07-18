package acid.ui.login;

import acid.management.FileManager;
import acid.Client;
import acid.utils.mixin.MinecraftToMixin;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.Agent;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.net.Proxy;
import net.minecraft.util.Session;
import net.minecraft.client.Minecraft;

public class AltLoginThread extends Thread
{
    private final Minecraft mc;
    private final String password;
    private String status;
    private final String username;
    
    public AltLoginThread(final String username, final String password) {
        super("Alt Login Thread");
        this.mc = Minecraft.getMinecraft();
        this.username = username;
        this.password = password;
        this.status = "¡ìeWaiting...";
    }
    
    private final Session createSession(final String username, final String password) {
        final YggdrasilAuthenticationService service = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
        final YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication)service.createUserAuthentication(Agent.MINECRAFT);
        auth.setUsername(username);
        auth.setPassword(password);
        try {
            auth.logIn();
            return new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(), auth.getAuthenticatedToken(), "mojang");
        }
        catch (AuthenticationException authenticationException) {
            return null;
        }
    }
    
    public String getStatus() {
        return this.status;
    }
    
    @Override
    public void run() {
        if (this.password.equals("")) {
            MinecraftToMixin.mixMC.setSession(new Session(this.username, "", "", "mojang"));
            this.status = "¡ìaLogged in. (" + this.username + " - offline name)";
            return;
        }
        this.status = "¡ìeLogging in...";
        final Session auth = this.createSession(this.username, this.password);
        if (auth == null) {
            this.status = "¡ìcLogin failed!";
        }
        else {
            Client.instance.getAltManager().setLastAlt(new Alt(this.username, this.password));
            FileManager.saveLastAlt();
            this.status = "¡ìaLogged in. (" + auth.getUsername() + ")";
            MinecraftToMixin.mixMC.setSession(auth);
        }
    }
    
    public void setStatus(final String status) {
        this.status = status;
    }
}
