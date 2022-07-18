package acid.ui.login;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;
import acid.utils.render.RenderUtil;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;

public final class GuiPasswordField extends Gui
{
    private final int xPosition;
    private final int yPosition;
    private final FontRenderer fontRendererInstance;
    private boolean canLoseFocus;
    private boolean isFocused;
    private int cursorCounter;
    private boolean enableBackgroundDrawing;
    private String text;
    private int maxStringLength;
    private final int width;
    private final int height;
    private boolean visible;
    private int disabledColor;
    private int enabledColor;
    private int selectionEnd;
    private int cursorPosition;
    private int lineScrollOffset;
    private boolean isEnabled;
    
    public GuiPasswordField(final FontRenderer p_i1032_1_, final int p_i1032_2_, final int p_i1032_3_, final int p_i1032_4_, final int p_i1032_5_) {
        this.canLoseFocus = true;
        this.enableBackgroundDrawing = true;
        this.text = "";
        this.maxStringLength = 32;
        this.visible = true;
        this.disabledColor = 7368816;
        this.enabledColor = 14737632;
        this.isEnabled = true;
        this.fontRendererInstance = p_i1032_1_;
        this.xPosition = p_i1032_2_;
        this.yPosition = p_i1032_3_;
        this.width = p_i1032_4_;
        this.height = p_i1032_5_;
    }
    
    public void drawTextBox() {
        if (this.getVisible()) {
            if (this.getEnableBackgroundDrawing()) {
                Gui.drawRect(this.xPosition - 1, this.yPosition - 1, this.xPosition + this.width + 1, this.yPosition + this.height + 1, -6250336);
                Gui.drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, -16777216);
            }
            final int var1 = this.isEnabled ? this.enabledColor : this.disabledColor;
            final int var2 = this.cursorPosition - this.lineScrollOffset;
            int var3 = this.selectionEnd - this.lineScrollOffset;
            final String var4 = this.fontRendererInstance.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getWidth());
            final boolean var5 = var2 >= 0 && var2 <= var4.length();
            final boolean var6 = this.isFocused && this.cursorCounter / 6 % 2 == 0 && var5;
            final int var7 = this.enableBackgroundDrawing ? (this.xPosition + 4) : this.xPosition;
            final int var8 = this.enableBackgroundDrawing ? (this.yPosition + (this.height - 8) / 2) : this.yPosition;
            int var9 = var7;
            if (var3 > var4.length()) {
                var3 = var4.length();
            }
            if (var4.length() > 0) {
                final String var10 = var5 ? var4.substring(0, var2) : var4;
                var9 = this.fontRendererInstance.drawString(var10.replaceAll(".", "*"), var7, var8, var1);
            }
            final boolean var11 = this.cursorPosition < this.text.length() || this.text.length() >= this.getMaxStringLength();
            int var12 = var9;
            if (!var5) {
                var12 = ((var2 > 0) ? (var7 + this.width) : var7);
            }
            else if (var11) {
                var12 = var9 - 1;
                --var9;
            }
            if (var4.length() > 0 && var5 && var2 < var4.length()) {
                this.fontRendererInstance.drawString(var4.substring(var2).replaceAll(".", "*"), var9, var8, var1);
            }
            if (var6) {
                if (var11) {
                    Gui.drawRect(var12, var8 - 1, var12 + 1, var8 + 1 + this.fontRendererInstance.FONT_HEIGHT, -3092272);
                }
                else {
                    this.fontRendererInstance.drawString("_", var12, var8, var1);
                }
            }
            if (var3 != var2) {
                final int var13 = var7 + this.fontRendererInstance.getStringWidth(var4.substring(0, var3).replaceAll(".", "*"));
                this.drawCursorVertical(var12, var8 - 1, var13 - 1, var8 + 1 + this.fontRendererInstance.FONT_HEIGHT);
            }
        }
    }
    
    public void deleteFromCursor(final int p_146175_1_) {
        if (this.text.length() != 0) {
            if (this.selectionEnd != this.cursorPosition) {
                this.writeText("");
            }
            else {
                final boolean var2 = p_146175_1_ < 0;
                final int var3 = var2 ? (this.cursorPosition + p_146175_1_) : this.cursorPosition;
                final int var4 = var2 ? this.cursorPosition : (this.cursorPosition + p_146175_1_);
                String var5 = "";
                if (var3 >= 0) {
                    var5 = this.text.substring(0, var3);
                }
                if (var4 < this.text.length()) {
                    var5 = String.valueOf(var5) + this.text.substring(var4);
                }
                this.text = var5;
                if (var2) {
                    this.moveCursorBy(p_146175_1_);
                }
            }
        }
    }
    
    public boolean getVisible() {
        return this.visible;
    }
    
    public void deleteWords(final int p_146177_1_) {
        if (this.text.length() != 0) {
            if (this.selectionEnd != this.cursorPosition) {
                this.writeText("");
            }
            else {
                this.deleteFromCursor(this.getNthWordFromCursor(p_146177_1_) - this.cursorPosition);
            }
        }
    }
    
    public boolean getEnableBackgroundDrawing() {
        return this.enableBackgroundDrawing;
    }
    
    public void moveCursorBy(final int p_146182_1_) {
        this.setCursorPosition(this.selectionEnd + p_146182_1_);
    }
    
    public int getNthWordFromPos(final int p_146183_1_, final int p_146183_2_) {
        return this.func_146197_a(p_146183_1_, this.getCursorPosition(), true);
    }
    
    public void setEnabled(final boolean p_146184_1_) {
        this.isEnabled = p_146184_1_;
    }
    
    public void setEnableBackgroundDrawing(final boolean p_146185_1_) {
        this.enableBackgroundDrawing = p_146185_1_;
    }
    
    public int getSelectionEnd() {
        return this.selectionEnd;
    }
    
    public int getNthWordFromCursor(final int p_146187_1_) {
        return this.getNthWordFromPos(p_146187_1_, this.getCursorPosition());
    }
    
    private void drawCursorVertical(final int p_146188_1_, final int p_146188_2_, final int p_146188_3_, final int p_146188_4_) {
        RenderUtil.drawRect(p_146188_1_, p_146188_2_, p_146188_3_, p_146188_4_, 0);
    }
    
    public void setVisible(final boolean p_146189_1_) {
        this.visible = p_146189_1_;
    }
    
    public void setCursorPosition(final int p_146190_1_) {
        this.cursorPosition = p_146190_1_;
        final int var2 = this.text.length();
        if (this.cursorPosition < 0) {
            this.cursorPosition = 0;
        }
        if (this.cursorPosition > var2) {
            this.cursorPosition = var2;
        }
        this.setSelectionPos(this.cursorPosition);
    }
    
    public void writeText(final String p_146191_1_) {
        String var2 = "";
        final String var3 = ChatAllowedCharacters.filterAllowedCharacters(p_146191_1_);
        final int var4 = (this.cursorPosition < this.selectionEnd) ? this.cursorPosition : this.selectionEnd;
        final int var5 = (this.cursorPosition < this.selectionEnd) ? this.selectionEnd : this.cursorPosition;
        final int var6 = this.maxStringLength - this.text.length() - (var4 - this.selectionEnd);
        if (this.text.length() > 0) {
            var2 = String.valueOf(var2) + this.text.substring(0, var4);
        }
        int var7;
        if (var6 < var3.length()) {
            var2 = String.valueOf(var2) + var3.substring(0, var6);
            var7 = var6;
        }
        else {
            var2 = String.valueOf(var2) + var3;
            var7 = var3.length();
        }
        if (this.text.length() > 0 && var5 < this.text.length()) {
            var2 = String.valueOf(var2) + this.text.substring(var5);
        }
        this.text = var2;
        this.moveCursorBy(var4 - this.selectionEnd + var7);
    }
    
    public void setTextColor(final int p_146193_1_) {
        this.enabledColor = p_146193_1_;
    }
    
    public void setCursorPositionZero() {
        this.setCursorPosition(0);
    }
    
    public int func_146197_a(final int p_146197_1_, final int p_146197_2_, final boolean p_146197_3_) {
        int var4 = p_146197_2_;
        final boolean var5 = p_146197_1_ < 0;
        for (int var6 = Math.abs(p_146197_1_), var7 = 0; var7 < var6; ++var7) {
            if (var5) {
                do {
                    --var4;
                } while (!p_146197_3_ || var4 <= 0 || this.text.charAt(var4 - 1) == ' ');
                while (--var4 > 0) {
                    if (this.text.charAt(var4 - 1) == ' ') {
                        break;
                    }
                }
            }
            else {
                final int var8 = this.text.length();
                var4 = this.text.indexOf(32, var4);
                if (var4 == -1) {
                    var4 = var8;
                }
                else {
                    while (p_146197_3_ && var4 < var8 && this.text.charAt(var4) == ' ') {
                        ++var4;
                    }
                }
            }
        }
        return var4;
    }
    
    public int getCursorPosition() {
        return this.cursorPosition;
    }
    
    public void setSelectionPos(int p_146199_1_) {
        final int var2 = this.text.length();
        if (p_146199_1_ > var2) {
            p_146199_1_ = var2;
        }
        if (p_146199_1_ < 0) {
            p_146199_1_ = 0;
        }
        this.selectionEnd = p_146199_1_;
        if (this.fontRendererInstance != null) {
            if (this.lineScrollOffset > var2) {
                this.lineScrollOffset = var2;
            }
            final int var3 = this.getWidth();
            final String var4 = this.fontRendererInstance.trimStringToWidth(this.text.substring(this.lineScrollOffset), var3);
            final int var5 = var4.length() + this.lineScrollOffset;
            if (p_146199_1_ == this.lineScrollOffset) {
                this.lineScrollOffset -= this.fontRendererInstance.trimStringToWidth(this.text, var3, true).length();
            }
            if (p_146199_1_ > var5) {
                this.lineScrollOffset += p_146199_1_ - var5;
            }
            else if (p_146199_1_ <= this.lineScrollOffset) {
                this.lineScrollOffset -= this.lineScrollOffset - p_146199_1_;
            }
            if (this.lineScrollOffset < 0) {
                this.lineScrollOffset = 0;
            }
            if (this.lineScrollOffset > var2) {
                this.lineScrollOffset = var2;
            }
        }
    }
    
    public int getWidth() {
        return this.getEnableBackgroundDrawing() ? (this.width - 8) : this.width;
    }
    
    public void setCursorPositionEnd() {
        this.setCursorPosition(this.text.length());
    }
    
    public void setMaxStringLength(final int p_146203_1_) {
        this.maxStringLength = p_146203_1_;
        if (this.text.length() > p_146203_1_) {
            this.text = this.text.substring(0, p_146203_1_);
        }
    }
    
    public void setDisabledTextColour(final int p_146204_1_) {
        this.disabledColor = p_146204_1_;
    }
    
    public void setCanLoseFocus(final boolean p_146205_1_) {
        this.canLoseFocus = p_146205_1_;
    }
    
    public String getSelectedText() {
        final int var1 = (this.cursorPosition < this.selectionEnd) ? this.cursorPosition : this.selectionEnd;
        final int var2 = (this.cursorPosition < this.selectionEnd) ? this.selectionEnd : this.cursorPosition;
        return this.text.substring(var1, var2);
    }
    
    public int getMaxStringLength() {
        return this.maxStringLength;
    }
    
    public String getText() {
        return this.text;
    }
    
    public boolean isFocused() {
        return this.isFocused;
    }
    
    public void mouseClicked(final int p_146192_1_, final int p_146192_2_, final int p_146192_3_) {
        final boolean var4 = p_146192_1_ >= this.xPosition && p_146192_1_ < this.xPosition + this.width && p_146192_2_ >= this.yPosition && p_146192_2_ < this.yPosition + this.height;
        if (this.canLoseFocus) {
            this.setFocused(var4);
        }
        if (this.isFocused && p_146192_3_ == 0) {
            int var5 = p_146192_1_ - this.xPosition;
            if (this.enableBackgroundDrawing) {
                var5 -= 4;
            }
            final String var6 = this.fontRendererInstance.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getWidth());
            this.setCursorPosition(this.fontRendererInstance.trimStringToWidth(var6, var5).length() + this.lineScrollOffset);
        }
    }
    
    public void setFocused(final boolean p_146195_1_) {
        if (p_146195_1_ && !this.isFocused) {
            this.cursorCounter = 0;
        }
        this.isFocused = p_146195_1_;
    }
    
    public void setText(final String p_146180_1_) {
        if (p_146180_1_.length() > this.maxStringLength) {
            this.text = p_146180_1_.substring(0, this.maxStringLength);
        }
        else {
            this.text = p_146180_1_;
        }
        this.setCursorPositionEnd();
    }
    
    public boolean textboxKeyTyped(final char p_146201_1_, final int p_146201_2_) {
        if (!this.isFocused) {
            return false;
        }
        switch (p_146201_1_) {
            case '\u0001': {
                this.setCursorPositionEnd();
                this.setSelectionPos(0);
                return true;
            }
            case '\u0003': {
                GuiScreen.setClipboardString(this.getSelectedText());
                return true;
            }
            case '\u0016': {
                if (this.isEnabled) {
                    this.writeText(GuiScreen.getClipboardString());
                }
                return true;
            }
            case '\u0018': {
                GuiScreen.setClipboardString(this.getSelectedText());
                if (this.isEnabled) {
                    this.writeText("");
                }
                return true;
            }
            default: {
                switch (p_146201_2_) {
                    case 14: {
                        if (GuiScreen.isCtrlKeyDown()) {
                            if (this.isEnabled) {
                                this.deleteWords(-1);
                            }
                        }
                        else if (this.isEnabled) {
                            this.deleteFromCursor(-1);
                        }
                        return true;
                    }
                    case 199: {
                        if (GuiScreen.isShiftKeyDown()) {
                            this.setSelectionPos(0);
                        }
                        else {
                            this.setCursorPositionZero();
                        }
                        return true;
                    }
                    case 203: {
                        if (GuiScreen.isShiftKeyDown()) {
                            if (GuiScreen.isCtrlKeyDown()) {
                                this.setSelectionPos(this.getNthWordFromPos(-1, this.getSelectionEnd()));
                            }
                            else {
                                this.setSelectionPos(this.getSelectionEnd() - 1);
                            }
                        }
                        else if (GuiScreen.isCtrlKeyDown()) {
                            this.setCursorPosition(this.getNthWordFromCursor(-1));
                        }
                        else {
                            this.moveCursorBy(-1);
                        }
                        return true;
                    }
                    case 205: {
                        if (GuiScreen.isShiftKeyDown()) {
                            if (GuiScreen.isCtrlKeyDown()) {
                                this.setSelectionPos(this.getNthWordFromPos(1, this.getSelectionEnd()));
                            }
                            else {
                                this.setSelectionPos(this.getSelectionEnd() + 1);
                            }
                        }
                        else if (GuiScreen.isCtrlKeyDown()) {
                            this.setCursorPosition(this.getNthWordFromCursor(1));
                        }
                        else {
                            this.moveCursorBy(1);
                        }
                        return true;
                    }
                    case 207: {
                        if (GuiScreen.isShiftKeyDown()) {
                            this.setSelectionPos(this.text.length());
                        }
                        else {
                            this.setCursorPositionEnd();
                        }
                        return true;
                    }
                    case 211: {
                        if (GuiScreen.isCtrlKeyDown()) {
                            if (this.isEnabled) {
                                this.deleteWords(1);
                            }
                        }
                        else if (this.isEnabled) {
                            this.deleteFromCursor(1);
                        }
                        return true;
                    }
                    default: {
                        if (ChatAllowedCharacters.isAllowedCharacter(p_146201_1_)) {
                            if (this.isEnabled) {
                                this.writeText(Character.toString(p_146201_1_));
                            }
                            return true;
                        }
                        return false;
                    }
                }
            }
        }
    }
    
    public void updateCursorCounter() {
        ++this.cursorCounter;
    }
}
