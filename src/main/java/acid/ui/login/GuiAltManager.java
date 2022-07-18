package acid.ui.login;

import net.minecraft.client.gui.ScaledResolution;
import java.io.IOException;
import java.util.Iterator;
import org.lwjgl.input.Keyboard;
import acid.utils.render.RenderUtil;
import org.lwjgl.opengl.GL11;
import acid.ui.font.FontLoaders;
import org.lwjgl.input.Mouse;
import java.util.Random;
import acid.Client;
import acid.management.FileManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class GuiAltManager extends GuiScreen
{
    private static Minecraft mc;
    private GuiButton login;
    private GuiButton remove;
    private GuiButton rename;
    private AltLoginThread loginThread;
    private int offset;
    public Alt selectedAlt;
    private String status;
    
    public GuiAltManager() {
        this.selectedAlt = null;
        this.status = "\247eWaiting...";
        FileManager.saveAlts();
    }
    
    public void actionPerformed(final GuiButton button) {
        switch (button.id) {
            case 0: {
                if (this.loginThread == null) {
                    GuiAltManager.mc.displayGuiScreen((GuiScreen)null);
                    break;
                }
                if (!this.loginThread.getStatus().equals("Logging in...") && !this.loginThread.getStatus().equals("Do not hit back! Logging in...")) {
                    GuiAltManager.mc.displayGuiScreen((GuiScreen)null);
                    break;
                }
                this.loginThread.setStatus("Do not hit back! Logging in...");
                break;
            }
            case 1: {
                final String user = this.selectedAlt.getUsername();
                final String pass = this.selectedAlt.getPassword();
                (this.loginThread = new AltLoginThread(user, pass)).start();
                break;
            }
            case 2: {
                if (this.loginThread != null) {
                    this.loginThread = null;
                }
                Client.instance.getAltManager();
                AltManager.getAlts().remove(this.selectedAlt);
                this.status = "¡ìcRemoved.";
                this.selectedAlt = null;
                FileManager.saveAlts();
                break;
            }
            case 3: {
                GuiAltManager.mc.displayGuiScreen((GuiScreen)new GuiAddAlt(this));
                break;
            }
            case 4: {
                GuiAltManager.mc.displayGuiScreen((GuiScreen)new GuiAltLogin(this));
                break;
            }
            case 5: {
                Client.instance.getAltManager();
                Client.instance.getAltManager();
                final Alt randomAlt = AltManager.alts.get(new Random().nextInt(AltManager.alts.size()));
                final String user2 = randomAlt.getUsername();
                final String pass2 = randomAlt.getPassword();
                (this.loginThread = new AltLoginThread(user2, pass2)).start();
                break;
            }
            case 6: {
                GuiAltManager.mc.displayGuiScreen((GuiScreen)new GuiRenameAlt(this));
                break;
            }
            case 7: {
                Client.instance.getAltManager();
                final Alt lastAlt = AltManager.lastAlt;
                if (lastAlt != null) {
                    final String user3 = lastAlt.getUsername();
                    final String pass3 = lastAlt.getPassword();
                    (this.loginThread = new AltLoginThread(user3, pass3)).start();
                    break;
                }
                if (this.loginThread == null) {
                    this.status = "?cThere is no last used alt!";
                    break;
                }
                this.loginThread.setStatus("?cThere is no last used alt!");
                break;
            }
        }
    }
    
    public void drawScreen(final int par1, final int par2, final float par3) {
        this.drawDefaultBackground();
        if (Mouse.hasWheel()) {
            final int wheel = Mouse.getDWheel();
            if (wheel < 0) {
                this.offset += 26;
                if (this.offset < 0) {
                    this.offset = 0;
                }
            }
            else if (wheel > 0) {
                this.offset -= 26;
                if (this.offset < 0) {
                    this.offset = 0;
                }
            }
        }
        this.drawDefaultBackground();
        FontLoaders.kiona18.drawStringWithShadow(GuiAltManager.mc.getSession().getUsername(), 10.0, 10.0, -7829368);
        Client.instance.getAltManager();
        FontLoaders.kiona18.drawCenteredString("Account Manager - " + AltManager.getAlts().size() + " alts", this.width / 2, 10.0f, -1);
        FontLoaders.kiona18.drawCenteredString((this.loginThread == null) ? this.status : this.loginThread.getStatus(), this.width / 2, 20.0f, -1);
        GL11.glPushMatrix();
        this.prepareScissorBox(0.0f, 33.0f, this.width, this.height - 50);
        GL11.glEnable(3089);
        int y = 38;
        Client.instance.getAltManager();
        for (final Alt alt : AltManager.getAlts()) {
            if (!this.isAltInArea(y)) {
                continue;
            }
            final String name = alt.getMask().equals("") ? alt.getUsername() : alt.getMask();
            final String pass = alt.getPassword().equals("") ? "¡ìcCracked" : alt.getPassword().replaceAll(".", "*");
            if (alt == this.selectedAlt) {
                if (this.isMouseOverAlt(par1, par2, y - this.offset) && Mouse.isButtonDown(0)) {
                    RenderUtil.drawBorderedRect(52.0f, y - this.offset - 4, this.width - 52, y - this.offset + 20, 1.0f, -16777216, -2142943931);
                }
                else if (this.isMouseOverAlt(par1, par2, y - this.offset)) {
                    RenderUtil.drawBorderedRect(52.0f, y - this.offset - 4, this.width - 52, y - this.offset + 20, 1.0f, -16777216, -2142088622);
                }
                else {
                    RenderUtil.drawBorderedRect(52.0f, y - this.offset - 4, this.width - 52, y - this.offset + 20, 1.0f, -16777216, -2144259791);
                }
            }
            else if (this.isMouseOverAlt(par1, par2, y - this.offset) && Mouse.isButtonDown(0)) {
                RenderUtil.drawBorderedRect(52.0f, y - this.offset - 4, this.width - 52, y - this.offset + 20, 1.0f, -16777216, -2146101995);
            }
            else if (this.isMouseOverAlt(par1, par2, y - this.offset)) {
                RenderUtil.drawBorderedRect(52.0f, y - this.offset - 4, this.width - 52, y - this.offset + 20, 1.0f, -16777216, -2145180893);
            }
            FontLoaders.kiona18.drawCenteredString(name, this.width / 2, y - this.offset, -1);
            FontLoaders.kiona18.drawCenteredString(pass, this.width / 2, y - this.offset + 10, 5592405);
            y += 26;
        }
        GL11.glDisable(3089);
        GL11.glPopMatrix();
        super.drawScreen(par1, par2, par3);
        if (this.selectedAlt == null) {
            this.login.enabled = false;
            this.remove.enabled = false;
            this.rename.enabled = false;
        }
        else {
            this.login.enabled = true;
            this.remove.enabled = true;
            this.rename.enabled = true;
        }
        if (Keyboard.isKeyDown(200)) {
            this.offset -= 26;
            if (this.offset < 0) {
                this.offset = 0;
            }
        }
        else if (Keyboard.isKeyDown(208)) {
            this.offset += 26;
            if (this.offset < 0) {
                this.offset = 0;
            }
        }
    }
    
    public void initGui() {
        this.buttonList.add(new GuiButton(0, this.width / 2 + 4 + 76, this.height - 24, 75, 20, "Cancel"));
        this.login = new GuiButton(1, this.width / 2 - 154, this.height - 48, 70, 20, "Login");
        this.buttonList.add(this.login);
        this.remove = new GuiButton(2, this.width / 2 - 74, this.height - 24, 70, 20, "Remove");
        this.buttonList.add(this.remove);
        this.buttonList.add(new GuiButton(3, this.width / 2 + 4 + 76, this.height - 48, 75, 20, "Add"));
        this.buttonList.add(new GuiButton(4, this.width / 2 - 74, this.height - 48, 70, 20, "Direct Login"));
        this.buttonList.add(new GuiButton(5, this.width / 2 + 4, this.height - 48, 70, 20, "Random"));
        this.rename = new GuiButton(6, this.width / 2 + 4, this.height - 24, 70, 20, "Edit");
        this.buttonList.add(this.rename);
        this.rename = new GuiButton(7, this.width / 2 - 154, this.height - 24, 70, 20, "Last Alt");
        this.buttonList.add(this.rename);
        this.login.enabled = false;
        this.remove.enabled = false;
        this.rename.enabled = false;
    }
    
    private boolean isAltInArea(final int y) {
        return y - this.offset <= this.height - 50;
    }
    
    private boolean isMouseOverAlt(final int x, final int y, final int y1) {
        return x >= 52 && y >= y1 - 4 && x <= this.width - 52 && y <= y1 + 20 && x >= 0 && y >= 33 && x <= this.width && y <= this.height - 50;
    }
    
    protected void mouseClicked(final int par1, final int par2, final int par3) {
        if (this.offset < 0) {
            this.offset = 0;
        }
        int y = 38 - this.offset;
        Client.instance.getAltManager();
        for (final Alt alt : AltManager.getAlts()) {
            if (this.isMouseOverAlt(par1, par2, y)) {
                if (alt == this.selectedAlt) {
                    this.actionPerformed(this.buttonList.get(1));
                    return;
                }
                this.selectedAlt = alt;
            }
            y += 26;
        }
        try {
            super.mouseClicked(par1, par2, par3);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void prepareScissorBox(final float x, final float y, final float x2, final float y2) {
        final int factor = new ScaledResolution(GuiAltManager.mc).getScaleFactor();
        GL11.glScissor((int)(x * factor), (int)((new ScaledResolution(GuiAltManager.mc).getScaledHeight() - y2) * factor), (int)((x2 - x) * factor), (int)((y2 - y) * factor));
    }
    
    static {
        GuiAltManager.mc = Minecraft.getMinecraft();
    }
}
