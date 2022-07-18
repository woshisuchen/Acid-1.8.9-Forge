package acid.ui.login;

import java.io.IOException;
import acid.ui.font.FontLoaders;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.GuiScreen;

public class GuiRenameAlt extends GuiScreen
{
    private final GuiAltManager manager;
    private GuiTextField nameField;
    private String status;
    private GuiPasswordField pwField;
    
    public GuiRenameAlt(final GuiAltManager manager) {
        this.status = "¡ìeWaiting...";
        this.manager = manager;
    }
    
    public void actionPerformed(final GuiButton button) {
        switch (button.id) {
            case 1: {
                this.mc.displayGuiScreen((GuiScreen)this.manager);
                break;
            }
            case 0: {
                this.manager.selectedAlt.setMask(this.nameField.getText());
                if (!this.pwField.getText().isEmpty()) {
                    this.manager.selectedAlt.setPassword(this.pwField.getText());
                }
                this.status = "¡ìaEdited!";
                break;
            }
        }
    }
    
    public void drawScreen(final int par1, final int par2, final float par3) {
        this.drawDefaultBackground();
        FontLoaders.kiona18.drawCenteredString("Edit Alt", this.width / 2, 10.0f, -1);
        FontLoaders.kiona18.drawCenteredString(this.status, this.width / 2, 20.0f, -1);
        this.nameField.drawTextBox();
        this.pwField.drawTextBox();
        if (this.nameField.getText().isEmpty()) {
            FontLoaders.kiona18.drawStringWithShadow("New E-Mail", this.width / 2 - 96, 66.0, -7829368);
        }
        if (this.pwField.getText().isEmpty()) {
            FontLoaders.kiona18.drawStringWithShadow("New Password", this.width / 2 - 96, 106.0, -7829368);
        }
        super.drawScreen(par1, par2, par3);
    }
    
    public void initGui() {
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 92 + 12, "Edit"));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 116 + 12, "Cancel"));
        this.nameField = new GuiTextField(1, this.mc.fontRendererObj, this.width / 2 - 100, 60, 200, 20);
        this.pwField = new GuiPasswordField(this.mc.fontRendererObj, this.width / 2 - 100, 100, 200, 20);
    }
    
    protected void keyTyped(final char par1, final int par2) {
        this.nameField.textboxKeyTyped(par1, par2);
        this.pwField.textboxKeyTyped(par1, par2);
        if (par1 == '\t' && (this.nameField.isFocused() || this.pwField.isFocused())) {
            this.nameField.setFocused(!this.nameField.isFocused());
            this.pwField.setFocused(!this.pwField.isFocused());
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
        this.nameField.mouseClicked(par1, par2, par3);
        this.pwField.mouseClicked(par1, par2, par3);
    }
}
