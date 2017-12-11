package fr.lazik.economie;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
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
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

	public static Inventory inventory;
	public Logger log = (Logger) java.util.logging.Logger.getLogger("Minecraft");
	FileConfiguration config = getConfig();
	int taskId;

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
		config.addDefault("prix.CAKE.material", "CAKE_BLOCK");
		config.addDefault("prix.CAKE.name", "gateau");
		config.addDefault("prix.CAKE.prix", 20);
		config.addDefault("prix.CAKE.quantite", 1);
		config.addDefault("prix.POTATO.material", "POTATO");
		config.addDefault("prix.POTATO.name", "patate");
		config.addDefault("prix.POTATO.prix", 16);
		config.addDefault("prix.POTATO.quantite", 32);
		config.addDefault("prix.experience.material", "experience");
		config.addDefault("prix.experience.name", "experience");
		config.addDefault("prix.experience.prix", 500);
		config.addDefault("prix.experience.quantite", 1395);
		config.createSection("sign");
		config.options().copyDefaults(true);
		saveConfig();

		ArrayList<String> locations = new ArrayList<String>();
		ConfigurationSection cs = this.getConfig().getConfigurationSection("sign");
		for (String key : cs.getKeys(false)) {
			locations.add(config.getString("sign." + key + ".location"));
			for (String location : locations) {
				Integer x = Integer.parseInt(location.split(";")[0]);
				Integer y = Integer.parseInt(location.split(";")[1]);
				Integer z = Integer.parseInt(location.split(";")[2]);
				String author = config.getString("sign." + key + ".author");
				Block bloc = this.getServer().getWorld("world").getBlockAt(x, y, z);
				bloc.setMetadata("E-conomie", new FixedMetadataValue(this, author));
			}
		}

		log.info(locations.size() + " panneaux charge(s)");
		log.info("[E-conomie] Chargement des markets termine!");

		log.info("[E-conomie] Le plugin est correctement charge !");
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
	}

	public void onDisable() {
	}

	@EventHandler // Ceci vas annoncer l'event ! (OBLIGATOIRE !)
	public void onPlayerJoin(PlayerJoinEvent e) { // Nous annoncons le type d'event ! Par exemple : PlayerChatEvent...
		Player p = e.getPlayer();
		config.addDefault("compte." + p.getName() + ".solde", 0);
		config.addDefault("compte." + p.getName() + ".pseudo", p.getName());
		config.options().copyDefaults(true);
		saveConfig();

		p.sendMessage("§8[§CE-conomie§8] §ABienvenue sur le serveur, vous avez "
				+ config.getInt("compte." + p.getName() + ".solde") + "€ sur votre compte");
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
				Player player = (Player) nEvent.getDamager();
				if (config.contains("gain." + e.getType().toString())) {

					double gain = config.getInt("gain." + e.getType().toString());
					player.sendMessage(
							"§8[§CE-conomie§8] §AVous avez tué un(e) " + e.getName() + " et gagné " + gain + "€");
					int montant = config.getInt("compte." + player.getName() + ".solde");
					config.set("compte." + player.getName() + ".solde", montant + gain);
					saveConfig();
				} else if (config.getString("prime.recherche").equals(e.getName())) {
					Bukkit.getScheduler().cancelTask(taskId);
					config.set("compte." + player.getName() + ".solde",
							config.getDouble("compte." + player.getName() + ".solde")
									+ config.getDouble("prime.montant"));

					for (Player unJoueur : getServer().getOnlinePlayers()) {
						unJoueur.sendMessage("La prime de " + config.getDouble("prime.montant") + " pour la tête de "
								+ config.getString("prime.recherche") + " a été gagné par " + player.getName());
					}

					config.set("prime.demandeur", "");
					config.set("prime.recherche", "");
					config.set("prime.montant", "");
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

				event.getBlock().setMetadata("E-conomie", new FixedMetadataValue(this, player.getName()));

				config.set("sign." + event.getBlock().getX() + ";" + event.getBlock().getY() + ";"
						+ event.getBlock().getZ() + ".author", player.getName());
				config.set(
						"sign." + event.getBlock().getX() + ";" + event.getBlock().getY() + ";"
								+ event.getBlock().getZ() + ".location",
						event.getBlock().getX() + ";" + event.getBlock().getY() + ";" + event.getBlock().getZ());
				saveConfig();
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
				if (b.hasMetadata("E-conomie")) {
					Sign s = (Sign) b.getState();
					String item = s.getLine(1);
					if (config.contains("prix." + item + ".material")) {
						if (config.getInt("prix." + item + ".prix") <= config
								.getInt("compte." + player.getName() + ".solde")) {
							Material material = Material.getMaterial(config.getString("prix." + item + ".material"));
							ItemStack is = new ItemStack(material, config.getInt("prix." + item + ".quantite"));
							double montant = config.getInt("compte." + player.getName() + ".solde");
							config.set("compte." + player.getName() + ".solde",
									(montant - config.getDouble("prix." + item + ".prix")));
							saveConfig();
							player.getInventory().addItem(is);
							player.sendMessage("§8[§CE-conomie§8] §AVous avez acheter "
									+ config.getString("prix." + item + ".name") + " pour "
									+ config.getString("prix." + item + ".prix") + " €");
						} else {
							player.sendMessage("§8[§CE-conomie§8] §CVous n'avez pas assez d'argent");
						}
					}
				}

			}

		}

	}

	// les commandes

	public boolean onCommand(CommandSender sender, Command cmd, String label, String args[]) {
		Player player = (Player) sender;

		switch (label.toLowerCase()) {
		case "moncompte":
			player.sendMessage(
					"vous avez " + config.getInt("compte." + player.getName() + ".solde") + "€ sur votre compte");
			return true;
		case "showprime":
			if (!config.getString("prime.demandeur").equals("")) {
				player.sendMessage("Une prime de " + config.getString("prime.montant")
						+ "€ est disponible pour la tête de " + config.getString("prime.recherche"));
				return true;
			} else {
				player.sendMessage("Aucune prime n'est actuellement déposer!");
				return true;
			}

		case "moremoney":
			if (args.length != 0) {
				try {
					double somme = Integer.parseInt(args[0]);
					String joueurGive;
					if (args.length == 1) {
						joueurGive = player.getName();
					} else if (args.length == 2) {
						joueurGive = args[1];
						if (getServer().getPlayer(joueurGive) == null
								|| config.getString("compte." + joueurGive) == null) {
							player.sendMessage("§8[§CE-conomie§8] §Cle joueur n'est pas connecte");
							return true;
						}
					} else {
						return false;
					}

					if (somme != 0) {
						config.set("compte." + joueurGive + ".solde",
								config.getDouble("compte." + joueurGive + ".solde") + somme);
						saveConfig();
						player.sendMessage("§8[§CE-conomie§8] §C" + joueurGive + " à reçue " + somme + "€");
						return true;
					} else {
						player.sendMessage("§8[§CE-conomie§8] §CLa valeur de la somme doit etre plus grand que 0");
						return false;
					}

				} catch (NumberFormatException e) {
					return false;
				}
			} else {
				log.info(args.toString());
				return false;
			}

		case "epay":
			if (args.length == 2) {
				try {
					double montant = Integer.parseInt(args[1]);

					String pseudo = args[0];
					Player player1 = getServer().getPlayer(pseudo);

					if (!player.getName().equals(pseudo)) {
						if (getServer().getPlayer(pseudo) != null && config.getString("compte." + pseudo) != null) {
							if (Double.compare(montant,
									config.getDouble("compte." + player.getName() + ".solde")) <= 0) {

								config.set("compte." + player.getName() + ".solde",
										config.getDouble("compte." + player.getName() + ".solde") - montant);
								config.set("compte." + pseudo + ".solde",
										config.getDouble("compte." + pseudo + ".solde") + montant);
								saveConfig();
								player.sendMessage("§8[§CE-conomie§8] §EVous avez envoyer " + Double.toString(montant)
										+ "€ à " + pseudo);
								player1.sendMessage("§8[§CE-conomie§8] §EVous avez reçu " + Double.toString(montant)
										+ "€ de " + player.getName());
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
				} catch (NumberFormatException e) {
					return false;
				}
			} else {
				return false;
			}

			return true;
		case "newprime":
			if (args.length == 2) {
				try {
					double montant = Integer.parseInt(args[1]);

					String pseudo = args[0];
					Player player1 = getServer().getPlayer(pseudo);
					log.info("ici");

					// verifie que le joueur ne se met pas une prime
					if (!player.getName().equals(pseudo)) {

						// verifie que le joueur existe
						if (getServer().getPlayer(pseudo) != null && config.getString("compte." + pseudo) != null) {
							// si il a de l'argent
							if (Double.compare(montant,
									config.getDouble("compte." + player.getName() + ".solde")) <= 0) {
								if (config.getString("prime.demandeur").equals("")) {

									config.set("prime.demandeur", player.getName());
									config.set("prime.recherche", player1.getName());
									config.set("prime.montant", montant);
									config.set("compte." + config.getString("prime.demandeur") + ".solde",
											config.getDouble("compte." + config.getString("prime.demandeur") + ".solde")
													- config.getDouble("prime.montant"));
									saveConfig();
									taskId = getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

										public void run() {
											for (Player unJoueur : getServer().getOnlinePlayers()) {
												unJoueur.sendMessage("La prime à expirée!");
											}
											player1.sendMessage(
													"Vous avez gagné la prime de " + config.getDouble("prime.montant")
															+ "€ pour avoir survécu durant les 3 jours");
											config.set("compte." + config.getString("prime.recherche") + ".solde",
													config.getDouble(
															"compte." + config.getString("prime.recherche") + ".solde")
															+ config.getDouble("prime.montant"));
											config.set("prime.demandeur", "");
											config.set("prime.recherche", "");
											config.set("prime.montant", "");
											saveConfig();
										}
									}, 72000L);
									for (Player unJoueur : getServer().getOnlinePlayers()) {
										unJoueur.sendMessage(
												"Une prime de " + montant + "€ à été placé sur " + player1.getName());
									}
								} else {
									player.sendMessage("§8[§CE-conomie§8] §CUne prime existe déjà!");
								}

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
				} catch (NumberFormatException e) {
					return false;
				}
			} else {
				return false;
			}

		default:
			return false;
		}

	}

}