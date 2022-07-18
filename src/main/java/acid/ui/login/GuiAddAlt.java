package acid.ui.login;

import com.mojang.authlib.exceptions.AuthenticationException;
import acid.management.FileManager;
import acid.Client;
import com.mojang.authlib.Agent;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.net.Proxy;
import java.io.IOException;
import org.lwjgl.input.Keyboard;
import acid.ui.font.FontLoaders;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.GuiScreen;

public class GuiAddAlt extends GuiScreen
{
    private final GuiAltManager manager;
    private GuiPasswordField password;
    private String status;
    private GuiTextField username;
    private GuiTextField combined;
    
    public GuiAddAlt(final GuiAltManager manager) {
        this.status = "\247eWaiting...";
        this.manager = manager;
    }
    
    protected void actionPerformed(final GuiButton button) {
        switch (button.id) {
            case 0: {
                AddAltThread login;
                if (this.combined.getText().isEmpty()) {
                    login = new AddAltThread(this.username.getText(), this.password.getText());
                }
                else if (!this.combined.getText().isEmpty() && this.combined.getText().contains(":")) {
                    final String u = this.combined.getText().split(":")[0];
                    final String p = this.combined.getText().split(":")[1];
                    login = new AddAltThread(u.replaceAll(" ", ""), p.replaceAll(" ", ""));
                }
                else {
                    login = new AddAltThread(this.username.getText(), this.password.getText());
                }
                login.start();
                break;
            }
            case 1: {
                this.mc.displayGuiScreen((GuiScreen)this.manager);
                break;
            }
        }
    }
    
    public void drawScreen(final int i, final int j, final float f) {
        this.drawDefaultBackground();
        FontLoaders.kiona18.drawCenteredString("Add Alt", this.width / 2, 20.0f, -1);
        this.username.drawTextBox();
        this.password.drawTextBox();
        this.combined.drawTextBox();
        if (this.username.getText().isEmpty()) {
            FontLoaders.kiona18.drawStringWithShadow("Username / E-Mail", this.width / 2 - 96, 66.0, -7829368);
        }
        if (this.password.getText().isEmpty()) {
            FontLoaders.kiona18.drawStringWithShadow("Password", this.width / 2 - 96, 106.0, -7829368);
        }
        if (this.combined.getText().isEmpty()) {
            FontLoaders.kiona18.drawStringWithShadow("Email:Password", this.width / 2 - 96, 146.0, -7829368);
        }
        FontLoaders.kiona18.drawCenteredString(this.status, this.width / 2, 30.0f, -1);
        super.drawScreen(i, j, f);
    }
    
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 92 + 12, "Login"));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 116 + 12, "Back"));
        this.username = new GuiTextField(1, this.mc.fontRendererObj, this.width / 2 - 100, 60, 200, 20);
        this.password = new GuiPasswordField(this.mc.fontRendererObj, this.width / 2 - 100, 100, 200, 20);
        (this.combined = new GuiTextField(1, this.mc.fontRendererObj, this.width / 2 - 100, 140, 200, 20)).setMaxStringLength(200);
    }
    
    protected void keyTyped(final char par1, final int par2) {
        this.username.textboxKeyTyped(par1, par2);
        this.password.textboxKeyTyped(par1, par2);
        this.combined.textboxKeyTyped(par1, par2);
        if (par1 == '\t' && (this.username.isFocused() || this.combined.isFocused() || this.password.isFocused())) {
            this.username.setFocused(!this.username.isFocused());
            this.password.setFocused(!this.password.isFocused());
            this.combined.setFocused(!this.combined.isFocused());
        }
        if (par1 == '\r') {
            this.actionPerformed(this.buttonList.get(0));
        }
    }
    
    protected void mouseClicked(final int par1, final int par2, final int par3) {
        try {
            super.mouseClicked(par1, par2, par3);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        this.username.mouseClicked(par1, par2, par3);
        this.password.mouseClicked(par1, par2, par3);
        this.combined.mouseClicked(par1, par2, par3);
    }
    
    static void access$0(final GuiAddAlt guiAddAlt, final String string) {
        guiAddAlt.status = string;
    }
    
    private class AddAltThread extends Thread
    {
        private final String password;
        private final String username;
        
        public AddAltThread(final String username, final String password) {
            this.username = username;
            this.password = password;
            GuiAddAlt.access$0(GuiAddAlt.this, "¡ì7Waiting...");
        }
        
        private final void checkAndAddAlt(final String username, final String password) {
            final YggdrasilAuthenticationService service = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
            final YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication)service.createUserAuthentication(Agent.MINECRAFT);
            auth.setUsername(username);
            auth.setPassword(password);
            try {
                auth.logIn();
                Client.instance.getAltManager();
                AltManager.getAlts().add(new Alt(username, password));
                FileManager.saveAlts();
                GuiAddAlt.access$0(GuiAddAlt.this, "¡ìaAlt added. (" + username + ")");
            }
            catch (AuthenticationException e) {
                GuiAddAlt.access$0(GuiAddAlt.this, "¡ìcAlt failed!");
                e.printStackTrace();
            }
        }
        
        @Override
        public void run() {
            if (this.password.equals("")) {
                Client.instance.getAltManager();
                AltManager.getAlts().add(new Alt(this.username, ""));
                FileManager.saveAlts();
                GuiAddAlt.access$0(GuiAddAlt.this, "¡ìaAlt added. (" + this.username + " - offline name)");
                return;
            }
            GuiAddAlt.access$0(GuiAddAlt.this, "¡ìeTrying alt...");
            this.checkAndAddAlt(this.username, this.password);
        }
    }
}
