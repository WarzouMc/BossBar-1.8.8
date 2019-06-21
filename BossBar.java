package fr.WarzouMc.Extra_Natural_Duel.grafic.bossBar;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class BossBar {

    private Player player;
    private String message;
    private EntityWither wither;
    private float live;

    public BossBar(Player player, String message, float live){
        this.player = player;
        this.message = message;
        this.live = live;
        update(message, live);
    }

    public void update(String message, float live) {
        Vector vector = new Vector().toLocation(player.getLocation().getWorld(), player.getLocation().getYaw(), player.getLocation().getPitch()).getDirection();
        Location location = player.getLocation().add(vector.multiply(20));
        removWither();
        WorldServer world = ((CraftWorld) location.getWorld()).getHandle();
        wither = new EntityWither(world);
        wither.setLocation(location.getX(), location.getY(), location.getZ(), location.getPitch(), location.getYaw());
        wither.setCustomName(message);
        wither.setCustomNameVisible(true);
        wither.setHealth(live * 100 / wither.getMaxHealth());
        wither.setInvisible(true);
        wither.r(10000000);
        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(wither);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    public void removWither() {
        if (wither != null){
            PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(wither.getId());
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }
    }

}
