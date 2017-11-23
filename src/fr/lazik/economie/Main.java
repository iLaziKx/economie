package fr.lazik.economie;

import java.util.logging.Logger;



import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
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

		config.addDefault("prix.IRON_BLOCK.material", "IRON_BLOCK");
		config.addDefault("prix.IRON_BLOCK.name", "block iron");
		config.addDefault("prix.IRON_BLOCK.prix", 20);
		config.addDefault("prix.IRON_BLOCK.quantite", 1);
		config.addDefault("prix.GOLD_BLOCK.material", "GOLD_BLOCK");
		config.addDefault("prix.GOLD_BLOCK.name", "Block gold");
		config.addDefault("prix.GOLD_BLOCK.prix", 50);
		config.addDefault("prix.GOLD_BLOCK.quantite", 1);
		config.addDefault("prix.DIAMOND_BLOCK.material", "DIAMOND_BLOCK");
		config.addDefault("prix.DIAMOND_BLOCK.name", "Block diamant");
		config.addDefault("prix.DIAMOND_BLOCK.prix", 150);
		config.addDefault("prix.DIAMOND_BLOCK.quantite", 1);
		config.addDefault("prix.COAL_BLOCK.material", "COAL_BLOCK");
		config.addDefault("prix.COAL_BLOCK.name", "block charbon");
		config.addDefault("prix.COAL_BLOCK.prix", 5);
		config.addDefault("prix.COAL_BLOCK.quantite", 1);
		config.addDefault("prix.BREAD.material", "BREAD");
		config.addDefault("prix.BREAD.name", "pain");
		config.addDefault("prix.BREAD.prix", 2);
		config.addDefault("prix.BREAD.quantite", 1);
		config.addDefault("prix.CAKE_BLOCK.material", "CAKE_BLOCK");
		config.addDefault("prix.CAKE_BLOCK.name", "gateau");
		config.addDefault("prix.CAKE_BLOCK.prix", 20);
		config.addDefault("prix.CAKE_BLOCK.quantite", 1);
		config.addDefault("prix.POTATO.material", "POTATO");
		config.addDefault("prix.POTATO.name", "patate");
		config.addDefault("prix.POTATO.prix", 16);
		config.addDefault("prix.POTATO.quantite", 32);
		config.addDefault("prix.experience.material", "experience");
		config.addDefault("prix.experience.name", "experience");
		config.addDefault("prix.experience.prix", 500);
		config.addDefault("prix.experience.quantite", 1395);
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
		Player p = e.getPlayer();
		config.addDefault("compte." + p.getName(), 0);
		config.options().copyDefaults(true);
		saveConfig();

		p.sendMessage(
				"§8[§CE-conomie§8] §ABienvenue sur le serveur, vous avez " + config.getInt("compte." + p.getName()) + "€ sur votre compte");
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
		e.setQuitMessage(ChatColor.GRAY + "[" + ChatColor.DARK_GREEN + "-" + ChatColor.GRAY + "]" + ChatColor.RED
				+ e.getPlayer().getName()); // Nous modifions le message de deconnexion !
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {

		Entity e = event.getEntity();
		double gain = 0;

		if (e.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent nEvent = (EntityDamageByEntityEvent) e.getLastDamageCause();
			Player player = (Player) nEvent.getDamager();
			if (nEvent.getDamager() instanceof Player) {
				if (config.contains("gain." + e.getType().toString())) {
					gain = config.getInt("gain." + e.getType().toString());

					player.sendMessage("§8[§CE-conomie§8] §AVous avez tué un(e) " + e.getName() + " et gagné " + gain + "€");
					int montant = config.getInt("compte." + player.getName());
					config.set("compte." + player.getName(), montant + gain);
					saveConfig();
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
		String item = lines[0];
		if (!item.equals("") && player.isOp()) {
			if (config.contains("prix." + item)) {
				event.setLine(0, "§C[E-conomie]");
				event.setLine(1, item);
				event.setLine(2, "§1Du " + config.getString("prix." + item + ".name") + " pour");
				event.setLine(3, "§1" + config.getInt("prix." + item + ".prix") + " €");
				player.sendMessage("§8[§CE-conomie§8] §APanneau correctement creer !");
			}
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block b = event.getClickedBlock();
			if ((b.getType() == Material.SIGN_POST) || (b.getType() == Material.WALL_SIGN)) {
				Sign s = (Sign) b.getState();
				String item = s.getLine(1);
				if (config.contains("prix." + item + ".material")) {
					if (config.getInt("prix." + item + ".prix") <= config.getInt("compte." + player.getName())) {
						Material material = Material.getMaterial(config.getString("prix." + item + ".material"));
						ItemStack is = new ItemStack(material, config.getInt("prix." + item + ".quantite"));
						double montant = config.getInt("compte." + player.getName());

						config.set("compte." + player.getName(), (montant - config.getDouble("prix." + item + ".prix")));
						saveConfig();
						player.getInventory().addItem(is);
						player.sendMessage("§8[§CE-conomie§8] §AVous avez acheter " + config.getString("prix." + item + ".name") + " pour "
								+ config.getString("prix." + item + ".prix") + " €");
					} else {
						player.sendMessage("§8[§CE-conomie§8] §CVous n'avez pas assez d'argent");
					}
				}

			}

		}

	}

	// les commandes
	public boolean onCommand(CommandSender sender, Command cmd, String label, String args[]) { // Ceci est obligatoire !
																								// En tout cas si vous
																								// désirez exécuter 1
																								// commande ! Je ne
																								// serai pas vraiment
																								// vous expliquer ! J'en
																								// est oublie la
																								// véritable
																								// signification et par
																								// écrit c'est complique
																								// !
		Player player = (Player) sender; // Nous disons que lorsque nous marquerons : player.... ce seras le sender (Le
									// joueur qui exécute la commande) qui a exécuter la commande ! Et que c'est
											// un Player !
		// String name = player.getName(); //Nous récupérons le pseudo du joueur !
		// Lorsque nous écrirons "name" dans (Exemple) un message envoyé au joueur :
		// "name" seras remplacer par le nom du sender !

		if (label.equalsIgnoreCase("monCompte")) { // Nous disons que si le joueur écrit /bonjour il ce passe... Mais
													// equalsignoreCase veut dire que nous pouvons écrire /bonjour
													// /Bonjour /BoNjOuR etc çà marcheras ! Si nous avions marque :
													// if(label.equals("Bonjour")) /bonjour /BoNjOuR etc ne marcherai
													// pas ! Seul /Bonjour marcherai ! Complique a expliquer par écrit
													// mais pour faire simple : equalsIgnoreCase ignore les majuscule !
			player.sendMessage("vous avez " + config.getInt("compte." + player.getName()) + "€ sur votre compte");
			return true;
		} else if (label.equalsIgnoreCase("ePay")) {

			double montant = Integer.parseInt(args[1]);
			String pseudo = args[0];
			Player player1 =  getServer().getPlayer(pseudo);
			
			if (!player.getName().equals(pseudo)) {
				if (getServer().getPlayer(pseudo) != null) {
					if (Double.compare(montant, config.getDouble("compte." + player.getName())) <= 0) {
						config.set("compte." + player.getName(),
								config.getDouble("compte." + player.getName()) - montant);
						config.set("compte." + pseudo, config.getDouble("compte." + pseudo) + montant);
						saveConfig();

						player.sendMessage("vous avez envoyer " + Double.toString(montant) + "€ à " + pseudo);
						player1.sendMessage("§8[§CE-conomie§8] §EVous avez reçu " + Double.toString(montant) +"€ de " + player.getName());
					} else {
						player.sendMessage("§8[§CE-conomie§8] §CVous n'avez pas assez d'argent");
					}
				} else {
					player.sendMessage("§8[§CE-conomie§8] §Cle joueur n'est pas connecte");
				}
				return true;
			} else {
				player.sendMessage("§8[§CE-conomie§8] §CVous ne pouvez pas vous envoyer d'argent");
			}
			return true;
		} else {
			return false;
		}
	}
}