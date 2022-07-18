package acid.ui.astolfo;

import org.lwjgl.opengl.GL11;
import org.lwjgl.input.Mouse;
import acid.ui.font.FontLoaders;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.Minecraft;
import acid.utils.render.RenderUtil;
import java.awt.Color;
import java.util.Iterator;
import acid.module.Module;
import acid.management.ModuleManager;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import acid.module.ModuleType;

public class Window
{
    public ModuleType category;
    public ArrayList<Button> buttons;
    public boolean drag;
    public boolean extended;
    public int x;
    public int y;
    public int expand;
    public int dragX;
    public int dragY;
    public int max;
    public int scroll;
    public int scrollTo;
    public double angel;
    int staticColor;
    public int totalY;
    int offset;
    
    public Window(final ModuleType category, final int x, final int y) {
        this.buttons =  Lists.newArrayList();
        this.category = category;
        this.x = x;
        this.y = y;
        this.max = 120;
        int y2 = y + 25;
        for (final Module c : ModuleManager.getModules()) {
            if (c.getType() != category) {
                continue;
            }
            this.buttons.add(new Button(category, c, x + 5, y2));
            y2 += 15;
        }
        for (final Button b2 : this.buttons) {
            b2.setParent(this);
        }
    }
    
    public void render(final int mouseX, final int mouseY) {
        int current = 0;
        int iY = this.y + 20;
        this.totalY = 15;
        for (final Button b3 : this.buttons) {
            b3.y = iY - this.offset;
            iY += 15;
            this.totalY += 15;
            if (b3.expand) {
                for (final ValueButton v : b3.buttons) {
                    current += 15;
                    this.totalY += 15;
                }
            }
            current += 15;
        }
        int height = 15 + current;
        if (height > 316) {
            height = 316;
        }
        if (this.extended) {
            this.expand = height;
            this.angel = 180.0;
        }
        else {
            this.expand = 0;
            this.angel = 0.0;
        }
        final boolean isOnPanel = mouseX > this.x - 2 && mouseX < this.x + 92 && mouseY > this.y - 2 && mouseY < this.y + this.expand;
        if (isOnPanel) {
            this.runWheel(height);
        }
        if (this.category.name().equals("Combat")) {
            this.staticColor = new Color(231, 76, 60).getRGB();
        }
        else if (this.category.name().equals("Render")) {
            this.staticColor = new Color(54, 1, 205).getRGB();
        }
        else if (this.category.name().equals("Movement")) {
            this.staticColor = new Color(45, 203, 113).getRGB();
        }
        else if (this.category.name().equals("Player")) {
            this.staticColor = new Color(141, 68, 173).getRGB();
        }
        else if (this.category.name().equals("World")) {
            this.staticColor = new Color(38, 154, 255).getRGB();
        }
        if (this.expand > 0) {
            RenderUtil.rectangleBordered(this.x - 0.5, this.y - 0.5, this.x + 90.5, this.y + 1.5 + this.expand, 1.0, this.staticColor, this.staticColor);
            RenderUtil.rectangleBordered(this.x, this.y, this.x + 90, this.y + 1.0 + this.expand, 1.0, new Color(25, 25, 25).getRGB(), new Color(25, 25, 25).getRGB());
        }
        RenderUtil.drawGradientRect(this.x, this.y, this.x + 90, this.y + 17, new Color(25, 25, 25).getRGB(), new Color(25, 25, 25).getRGB());
        final ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
        if (this.category.name().equals("Combat")) {
            FontLoaders.kiona16.drawString("combat", this.x + 61, this.y + 6, new Color(220, 220, 220).getRGB());
        }
        if (this.category.name().equals("Render")) {
            FontLoaders.kiona16.drawString("visual", this.x + 69, this.y + 6, new Color(220, 220, 220).getRGB());
        }
        if (this.category.name().equals("Movement")) {
            FontLoaders.kiona16.drawString("movement", this.x + 50, this.y + 6, new Color(220, 220, 220).getRGB());
        }
        if (this.category.name().equals("Player")) {
            FontLoaders.kiona16.drawString("player", this.x + 67, this.y + 6, new Color(220, 220, 220).getRGB());
        }
        if (this.category.name().equals("World")) {
            FontLoaders.kiona16.drawString("other", this.x + 69, this.y + 6, new Color(220, 220, 220).getRGB());
        }
        if (this.expand > 0) {
            for (final Button b4 : this.buttons) {
                b4.render(mouseX, mouseY, new Limitation(this.x, this.y + 16, this.x + 90, this.y + this.expand));
            }
        }
        if (this.drag) {
            if (!Mouse.isButtonDown(0)) {
                this.drag = false;
            }
            this.x = mouseX - this.dragX;
            this.y = mouseY - this.dragY;
            this.buttons.get(0).y = this.y + 22 - this.offset;
            for (final Button b5 : this.buttons) {
                b5.x = this.x + 5;
            }
        }
    }
    
    public static void doGlScissor(final int x, final int y, final int width, final int height) {
        final Minecraft mc = Minecraft.getMinecraft();
        int scaleFactor = 1;
        int k = mc.gameSettings.guiScale;
        if (k == 0) {
            k = 1000;
        }
        while (scaleFactor < k && mc.displayWidth / (scaleFactor + 1) >= 320 && mc.displayHeight / (scaleFactor + 1) >= 240) {
            ++scaleFactor;
        }
        GL11.glScissor(x * scaleFactor, mc.displayHeight - (y + height) * scaleFactor, width * scaleFactor, height * scaleFactor);
    }
    
    protected void runWheel(final int height) {
        if (Mouse.hasWheel()) {
            final int wheel = Mouse.getDWheel();
            if (this.totalY - height <= 0) {
                return;
            }
            if (wheel < 0) {
                if (this.offset < this.totalY - height) {
                    this.offset += 20;
                    if (this.offset < 0) {
                        this.offset = 0;
                    }
                }
            }
            else if (wheel > 0) {
                this.offset -= 20;
                if (this.offset < 0) {
                    this.offset = 0;
                }
            }
        }
    }
    
    public void key(final char typedChar, final int keyCode) {
        this.buttons.forEach(b2 -> b2.key(typedChar, keyCode));
    }
    
    public void mouseScroll(final int mouseX, final int mouseY, final int amount) {
        if (mouseX > this.x - 2 && mouseX < this.x + 92 && mouseY > this.y - 2 && mouseY < this.y + 17 + this.expand) {
            this.scrollTo -= amount / 120 * 28;
        }
    }
    
    public void click(int mouseX, int mouseY, int button) {
        if (mouseX > this.x - 2 && mouseX < this.x + 92 && mouseY > this.y - 2 && mouseY < this.y + 17) {
            if (button == 1) {
                this.extended = !this.extended;
                boolean bl = this.extended;
            }
            if (button == 0) {
                this.drag = true;
                this.dragX = mouseX - this.x;
                this.dragY = mouseY - this.y;
            }
        }
        if (this.extended) {
            this.buttons.stream().filter(b2 -> b2.y < this.y + this.expand).forEach(b2 -> b2.click(mouseX, mouseY, button));
        }
    }
}
