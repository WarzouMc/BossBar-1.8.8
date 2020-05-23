package your.package;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BossBar {

    private static Map<UUID, BossBar> bossBarMap = new HashMap<>();

    private int animationDuration = -1;
    private int currentDuration = 0;

    private String message = "";
    private boolean right = true;
    private boolean isSend = false;
    private EntityWither entityWither;

    private boolean autoDestroy = true;

    private boolean pause = false;

    private Player player;
    public BossBar(Player player) {
        this.player = player;
        bossBarMap.put(this.player.getUniqueId(), this);
    }

    public void update() {
        update(this.message);
    }

    public void update(String message) {
        if (!Bukkit.getOnlinePlayers().contains(this.player)) {
            reset();
            return;
        }
        if (this.entityWither == null)
            return;
        sendPacket(new PacketPlayOutEntityDestroy(this.entityWither.getId()));
        setMessage(message);

        Location location = getLocation();
        this.entityWither.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

        applyMessage();
        if (!updateHealth() && autoDestroy) {
            reset();
            return;
        }
        sendPacket(new PacketPlayOutSpawnEntityLiving(entityWither));
    }

    private boolean updateHealth() {
        if (this.pause)
            return true;

        if (this.animationDuration < 0)
            return true;

        if (this.animationDuration - this.currentDuration < 0)
            return false;

        float percentGeneration = (this.currentDuration + 0.0f) / this.animationDuration;
        float health = this.right ? getMaxHealth() * percentGeneration : getMaxHealth() - getMaxHealth() * percentGeneration;
        this.entityWither.setHealth(health);
        this.currentDuration++;
        return true;
    }

    public void send() {
        send("Wither boss bar message");
    }

    public void send(String message) {
        send(message, -1);
    }

    public void send(String message, int duration) {
        send(message, duration, true);
    }

    public void send(String message, int duration, boolean right) {
        send(message, duration, right, true);
    }

    public void send(String message, int duration, boolean right, boolean overwrite) {
        if (this.isSend && !overwrite)
            return;
        this.message = message;
        this.animationDuration = duration;
        this.right = right;

        this.autoDestroy = duration > 0;

        spawn();
    }

    public void setAutoDestroy(boolean autoDestroy) {
        this.autoDestroy = autoDestroy;
    }

    private void setMessage(String message) {
        if (pause) return;
        this.message = message;
    }

    public void pause() {
        this.pause = true;
    }

    public void resume() {
        this.pause = false;
    }

    public void reset() {
        if (this.entityWither != null)
            sendPacket(new PacketPlayOutEntityDestroy(this.entityWither.getId()));
        bossBarMap.put(this.player.getUniqueId(), new BossBar(this.player));
    }

    private float getMaxHealth() {
        return this.entityWither.getMaxHealth();
    }

    private float getHealth() {
        return this.entityWither.getHealth();
    }

    public void spawn() {
        Location location = this.getLocation();
        this.entityWither = new EntityWither(this.getWorldServer());
        this.entityWither.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        this.entityWither.setInvisible(true);
        this.entityWither.r(900); //wither shield
        this.entityWither.setHealth((this.animationDuration < 0 || !right) ? getMaxHealth() : 0.0f);
        applyMessage();
        this.entityWither.setCustomNameVisible(true);
        sendPacket(new PacketPlayOutSpawnEntityLiving(entityWither));
    }

    private void applyMessage() {
        this.entityWither.setCustomName(this.message);
    }

    private Location getLocation() {
        Vector direction = this.player.getLocation().getDirection();
        return this.player.getLocation().add(direction.multiply(20));
    }

    private WorldServer getWorldServer() {
        return ((CraftWorld) this.player.getWorld()).getHandle();
    }

    private void sendPacket(Packet packet) {
        ((CraftPlayer) this.player).getHandle().playerConnection.sendPacket(packet);
    }

    public Player getPlayer() {
        return this.player;
    }

    public String getMessage() {
        return this.message;
    }

    public static Map<UUID, BossBar> getBossBarMap() {
        Map<UUID, BossBar> tempBossBarMap = new HashMap<>(bossBarMap);
        tempBossBarMap.forEach((uuid, bossBar) -> {
            if (!Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(uuid))) {
                bossBar.reset();
                bossBarMap.remove(uuid);
            }
        });
        return bossBarMap;
    }

    public int getCurrentDuration() {
        return this.currentDuration;
    }
}
