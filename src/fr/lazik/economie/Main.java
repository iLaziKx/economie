package fr.lazik.economie;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener { // Nous rajoutons ceci !

	public Logger log = (Logger) java.util.logging.Logger.getLogger("Minecraft");

	public void onEnable() {
		log.info("[GuideEpicube] Le plugin est correctement demarrer !");
		Bukkit.getServer().getPluginManager().registerEvents(this, this); // Ici nous chargons les events !
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
		if (e.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent nEvent = (EntityDamageByEntityEvent) e.getLastDamageCause();
			if (nEvent.getDamager() instanceof Player) {
				Player p = (Player)nEvent.getDamager();
				p.sendMessage("J'ai tué " + e.getName());
			}
		}
	}	
}