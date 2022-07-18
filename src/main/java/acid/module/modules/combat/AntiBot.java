package acid.module.modules.combat;

import acid.api.EventHandler;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.Collection;
import acid.api.events.world.EventPreUpdate;
import acid.management.ModuleManager;
import acid.Client;
import java.util.Arrays;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.Entity;
import java.util.Iterator;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.EntityLivingBase;
import acid.api.value.Value;
import acid.module.ModuleType;
import java.util.ArrayList;
import acid.api.value.Mode;
import acid.module.Module;

public class AntiBot extends Module
{
    public static Mode<Enum> mode;
    private static final ArrayList<Integer> ground;
    
    public AntiBot() {
        super("AntiBot", new String[] { "AntiBot" }, ModuleType.Combat);
        this.addValues(AntiBot.mode);
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
        AntiBot.ground.clear();
    }
    
    @Override
    public void onDisable() {
        super.onDisable();
        AntiBot.ground.clear();
    }
    
    private boolean inTab(final EntityLivingBase entity) {
        for (final Object item : this.mc.getNetHandler().getPlayerInfoMap()) {
            final NetworkPlayerInfo playerInfo = (NetworkPlayerInfo)item;
            if (playerInfo != null && playerInfo.getGameProfile() != null && playerInfo.getGameProfile().getName().contains(entity.getName())) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean isHypixelNPC(final EntityLivingBase entity) {
        final String formatted = entity.getDisplayName().getFormattedText();
        return (!formatted.startsWith("¡ì") && formatted.endsWith("¡ìr")) || AntiBot.ground.contains(entity.getEntityId()) || formatted.contains("¡ì8[NPC]");
    }
    
    private static boolean isHypixelNPC(final Entity entity) {
        final String formatted = entity.getDisplayName().getFormattedText();
        return (!formatted.startsWith("¡ì") && formatted.endsWith("¡ìr")) || AntiBot.ground.contains(entity.getEntityId()) || formatted.contains("¡ì8[NPC]");
    }
    
    private boolean removeHypixelBot(final EntityLivingBase entity) {
        if (entity instanceof EntityWither && entity.isInvisible()) {
            return true;
        }
        if (!this.inTab(entity) && !isHypixelNPC(entity) && entity.isEntityAlive() && entity != this.mc.thePlayer) {
            this.mc.theWorld.removeEntity((Entity)entity);
            return true;
        }
        return false;
    }
    
    private ArrayList<EntityPlayer> getLivingPlayers() {
        return (ArrayList<EntityPlayer>)(ArrayList)Arrays.asList((EntityPlayer[])this.mc.theWorld.loadedEntityList.stream().filter(entity -> entity instanceof EntityPlayer).filter(entity -> entity != this.mc.thePlayer).map(entity -> entity).toArray(EntityPlayer[]::new));
    }
    
    public boolean isBot(final EntityLivingBase entity) {
        final Client instance = Client.instance;
        Client.getModuleManager();
        return ModuleManager.getModuleByClass(AntiBot.class).isEnabled() && entity != this.mc.thePlayer && (AntiBot.mode.getValue() == bot.Watchdog && isHypixelNPC(entity));
    }
    
    @EventHandler
    public void onUpdate(final EventPreUpdate e) {
        this.setSuffix("Watchdog");
        if (this.mc.thePlayer.ticksExisted <= 1) {
            AntiBot.ground.clear();
        }
        for (final EntityPlayer entity : this.mc.theWorld.playerEntities) {
            this.removeHypixelBot((EntityLivingBase)entity);
        }
        ground.addAll((Collection)this.getLivingPlayers().stream().filter(entityPlayer -> entityPlayer.onGround && !ground.contains((Object)entityPlayer.getEntityId())).map(Entity::getEntityId).collect(Collectors.toList()));
    }
    
    public boolean isBot(final Entity entity) {
        final Client instance = Client.instance;
        Client.getModuleManager();
        return ModuleManager.getModuleByClass(AntiBot.class).isEnabled() && entity != this.mc.thePlayer && (AntiBot.mode.getValue() == bot.Watchdog && isHypixelNPC(entity));
    }
    
    static {
        AntiBot.mode = new Mode<Enum>("Priority", bot.values(), bot.Watchdog);
        ground = new ArrayList<Integer>();
    }
    
    enum bot
    {
        Watchdog;
    }
}
