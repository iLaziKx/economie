package fr.lazik.economie;

import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

	public static Inventory inventory;
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
		inventory = Bukkit.createInventory(null, 36, "§4ceci est un tuto");
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
				if (config.contains("gain." + e.getType().toString())) {
					gain = config.getInt("gain." + e.getType().toString());
					player.sendMessage("Vous avez tué un(e) " + e.getName() + " et gagné " + gain + "€");
				}

			}

		}
	}

	// intraction avec les panneaux
	// creer un panneau si un text particulier et écrie
	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		Player player = event.getPlayer();
		String[] lines = event.getLines();
		String item = "";
		if(item != "") {
			item = lines[0];
		if (config.contains("prix." + item)) {
			event.setLine(0, "");
			event.setLine(1, item);
			event.setLine(2, "§3Acheter du " + item);
			event.setLine(3, "pour"+ config.getInt("prix." + item));
			player.sendMessage("§8[§7Epicube§8] §3Panneau correctement creer !");
		}
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getClickedBlock().getState() instanceof Sign) {
			Sign sign = (Sign) event.getClickedBlock().getState();
			String[] lines = sign.getLines();
			if (lines[1].equalsIgnoreCase("§8Boulangerie")) {
				if (lines[2].equalsIgnoreCase("§4Prend du pain")) {
					Player player = event.getPlayer();
					ItemStack spe = new ItemStack(Material.BREAD, 1);
					player.getInventory().addItem(spe);
					/*ItemMeta spem = spe.getItemMeta();
					spem.setDisplayName("§8[§7Epicube§8] §4Tuto Epicube");
					spe.setItemMeta(spem);
					inventory.addItem(new ItemStack[] { spe });
					inventory.setItem(10, spe);
					inventory.addItem(new ItemStack[] { spe });
					player.openInventory(inventory);*/

				}
			}
		}
	}

}