package fr.WarzouMc.Extra_Natural_Duel.grafic;

import fr.WarzouMc.Extra_Natural_Duel.eventsManager.PluginManager;
import fr.WarzouMc.Extra_Natural_Duel.grafic.bossBar.BossBar;
import fr.WarzouMc.Extra_Natural_Duel.main.Main;
import fr.WarzouMc.Extra_Natural_Duel.statue.GStatue;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public class Updaters extends BukkitRunnable {
    private Main main;
    public Updaters(Main main) {this.main = main;}

    @Override
    public void run() {
        for (BossBar bar : main.getBar().values()) {
            if(main.getgStatue() == GStatue.WAITING || main.getgStatue() == GStatue.STARTING){
                bar.update("§cWaiting more player : §e" + main.getPlayerInGame().size() + "/8", 100 * main.getPlayerInGame().size() / 8);
            }else {
                bar.update(main.getBossMsg(), main.getBossInt());
            }
        }
    }
}
