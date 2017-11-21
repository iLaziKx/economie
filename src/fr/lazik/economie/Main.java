package fr.lazik.economie;

import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

	public Logger log = (Logger) java.util.logging.Logger.getLogger("Minecraft");
	FileConfiguration config = getConfig();

	public void onEnable() {
		config.addDefault("gain.ZOMBIE", 1);
		config.addDefault("gain.SKELETON", 2);
		config.addDefault("gain.SPIDER", 1);
		config.addDefault("gain.CREEPER", 3);
		config.addDefault("gain.SLIME", 1);
		config.addDefault("gain.GHAST", 5);
		config.addDefault("gain.PIG_ZOMBIE", 3);
		config.addDefault("gain.ENDERMAN", 2);
		config.addDefault("gain.BLAZE", 2);
		config.addDefault("gain.WITCH", 5);
		config.addDefault("gain.WITHER", 50);
		config.addDefault("gain.WITHER_SKELETON", 10);

		config.addDefault("prix.ironBlock", 20);
		config.addDefault("prix.goldBlock", 50);
		config.addDefault("prix.DiamondBlock", 150);
		config.addDefault("prix.coalBlock", 5);
		config.addDefault("prix.bread", 2);
		config.addDefault("prix.cake", 20);
		config.addDefault("prix.potatos", 16);
		config.addDefault("prix.experience", 500);
		config.options().copyDefaults(true);
		saveConfig();
		log.info("[E-conomie] Le plugin est correctement charge !");
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
	}

	public void onDisable() {
	}

	@EventHandler // Ceci vas annoncer l'event ! (OBLIGATOIRE !)
	public void onPlayerJoin(PlayerJoinEvent e) { // Nous annoncons le type d'event ! Par exemple : PlayerChatEvent...
		e.setJoinMessage(ChatColor.GRAY + "[" + ChatColor.DARK_RED + "+" + ChatColor.GRAY + "]" + ChatColor.RED
				+ e.getPlayer().getName()); // Nous modifions le message de connexion !
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
		e.setQuitMessage(ChatColor.GRAY + "[" + ChatColor.DARK_GREEN + "-" + ChatColor.GRAY + "]" + ChatColor.RED
				+ e.getPlayer().getName()); // Nous modifions le message de deconnexion !
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {

		Entity e = event.getEntity();
		float gain = 0;
		
		if (e.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent nEvent = (EntityDamageByEntityEvent) e.getLastDamageCause();
			Player player = (Player) nEvent.getDamager();
			if (nEvent.getDamager() instanceof Player) {
				player.sendMessage(e.getType().toString() + config.getStringList("gain.CREEPER"));
				if (config.getStringList("gain").contains(e.getType().toString())) {
					player.sendMessage("Vous avez tué un(e) " + e.getName() + " et gagné " + gain + "€");
				}
			}

		}
	}

}