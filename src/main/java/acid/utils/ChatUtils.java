package acid.utils;

import acid.Client;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

public class ChatUtils
{
    private final ChatComponentText message;
    
    private ChatUtils(final ChatComponentText message) {
        this.message = message;
    }
    
    public static String addFormat(final String message, final String regex) {
        return message.replaceAll("(?i)" + regex + "([0-9a-fklmnor])", "¡ì$1");
    }
    
    public void displayClientSided() {
        Minecraft.getMinecraft().thePlayer.addChatMessage((IChatComponent)this.message);
    }
    
    private ChatComponentText getChatComponent() {
        return this.message;
    }
    
    ChatUtils(final ChatComponentText chatComponentText, final ChatUtils chatUtils) {
        this(chatComponentText);
    }
    
    public static class ChatMessageBuilder
    {
        private static final EnumChatFormatting defaultMessageColor;
        private ChatComponentText theMessage;
        private boolean useDefaultMessageColor;
        private ChatStyle workingStyle;
        private ChatComponentText workerMessage;
        
        public ChatMessageBuilder(final boolean prependDefaultPrefix, final boolean useDefaultMessageColor) {
            this.theMessage = new ChatComponentText("");
            this.useDefaultMessageColor = false;
            this.workingStyle = new ChatStyle();
            this.workerMessage = new ChatComponentText("");
            if (prependDefaultPrefix) {
                Client.instance.getClass();
                this.theMessage.appendSibling((IChatComponent)new ChatMessageBuilder(false, false).appendText(String.valueOf(EnumChatFormatting.AQUA + "ETB ")).setColor(EnumChatFormatting.RED).build().getChatComponent());
            }
            this.useDefaultMessageColor = useDefaultMessageColor;
        }
        
        public ChatMessageBuilder() {
            this.theMessage = new ChatComponentText("");
            this.useDefaultMessageColor = false;
            this.workingStyle = new ChatStyle();
            this.workerMessage = new ChatComponentText("");
        }
        
        public ChatMessageBuilder appendText(final String text) {
            this.appendSibling();
            this.workerMessage = new ChatComponentText(text);
            this.workingStyle = new ChatStyle();
            if (this.useDefaultMessageColor) {
                this.setColor(ChatMessageBuilder.defaultMessageColor);
            }
            return this;
        }
        
        public ChatMessageBuilder setColor(final EnumChatFormatting color) {
            this.workingStyle.setColor(color);
            return this;
        }
        
        public ChatMessageBuilder bold() {
            this.workingStyle.setBold(true);
            return this;
        }
        
        public ChatMessageBuilder italic() {
            this.workingStyle.setItalic(true);
            return this;
        }
        
        public ChatMessageBuilder strikethrough() {
            this.workingStyle.setStrikethrough(true);
            return this;
        }
        
        public ChatMessageBuilder underline() {
            this.workingStyle.setUnderlined(true);
            return this;
        }
        
        public ChatUtils build() {
            this.appendSibling();
            return new ChatUtils(this.theMessage, null);
        }
        
        private void appendSibling() {
            this.theMessage.appendSibling(this.workerMessage.setChatStyle(this.workingStyle));
        }
        
        static {
            defaultMessageColor = EnumChatFormatting.WHITE;
        }
    }
}
