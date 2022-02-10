package fr.bidoumax.rush.listener;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.Bed;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import fr.bidoumax.rush.MainRush;
import fr.bidoumax.rush.RushPlayer;
import fr.bidoumax.rush.enums.BState;
import fr.bidoumax.rush.enums.TEAM;

public class BPlayerListeners implements Listener {

	private MainRush main;

	public BPlayerListeners(MainRush main) {
		this.main = main;
	}
//-CLIQUE SUR PANNEAU [RUSH] POUR ETRE TP AU HUB ET ATTENDRE LA PARTIE -------------------------------------------------------------
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (event == null) return;
		Player player = event.getPlayer();
		Action action = event.getAction();

		if (event.getClickedBlock() != null
				&& (action == Action.RIGHT_CLICK_BLOCK || action == Action.LEFT_CLICK_BLOCK)) {

			BlockState bs = event.getClickedBlock().getState();
			if (bs instanceof Sign) {
				Sign sign = (Sign) bs;
				if (sign.getLine(1).equalsIgnoreCase("[Rush]")) {
					boolean ajoutRéussit = main.addPlayer(player);
					if (ajoutRéussit == true) {
						player.teleport(new Location(player.getWorld(), 130.5, 49, -47.5, -90f, 10f));
						player.sendTitle("§f§lFREE GAME !", " ", 1, 20, 1);
						player.addPotionEffect(
								new PotionEffect(PotionEffectType.SATURATION, 9999999, 9999999, false, false));
						player.addPotionEffect(
								new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 9999999, 9999999, false, false));
						player.setGameMode(GameMode.ADVENTURE);
						for (RushPlayer rushPlayer : main.getPlayers()) {
							rushPlayer.getPlayer().sendMessage("§7[" + "§b§lRUSH" + "§7] " + player.getName()
									+ " a rejoint la partie §a(" + main.getPlayers().size() + "§a/8)");
						}
						player.getInventory().clear();
						setupInventaireJoueurRush(player);
					}
//-------------------------------------------------------------------------------------------------------------------------------------					
				}

			} else if (bs instanceof Bed) {
				Bed bed = (Bed) bs;
				if (main.getPlayer(player) != null) {
					event.setCancelled(true);
				}
			}

		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event == null) return;
		Player player = event.getPlayer();

		if (event.getItem() == null && event.getAction() != null && main.getPlayer(player) == null)
			return;

		switch (event.getItem().getItemMeta().getDisplayName()) {

		case "§cRetour Spawn":
			if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				this.main.leaveGame(player);
				getInventoryLeaveGame(event.getPlayer());
				event.setCancelled(true);
			}
			break;
		case "§bChoisir mon équipe !":
			if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				this.main.createMenuChoiceTeams(player, true);
				event.setCancelled(true);
			}
			break;
		default:
			break;
		}
	}

	@EventHandler
	public void onClickInventory(InventoryClickEvent event) {
		if (event == null) return;
		Player player = (Player) event.getWhoClicked();

		if (event.getCurrentItem() == null && event.getAction() != null || main.getPlayer(player) == null)
			return;
		if (main.getState() == BState.WAITING) {
			switch (event.getCurrentItem().getItemMeta().getDisplayName()) {
			case "§bEquipe §cRouge":
				this.main.joinTeam(TEAM.ROUGE, player);
				break;
			case "§bEquipe §3Cyan":
				this.main.joinTeam(TEAM.CYAN, player);
				break;
			case "§bEquipe §eJaune":
				this.main.joinTeam(TEAM.JAUNE, player);
				break;
			case "§bEquipe §9Bleue":
				this.main.joinTeam(TEAM.BLEU, player);
				break;
			case "§bEquipe §fNeutre":
				this.main.joinTeam(TEAM.NEUTRE, player);
				break;
			default:
				break;
			}
			player.closeInventory();
		}

	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event == null) return;
		Player player = event.getPlayer();
		Block block = event.getBlockPlaced();

		if (block.getBlockData().getMaterial().equals(Material.TNT) && main.getPlayer(player) != null) {
			Entity tnt = player.getWorld().spawnEntity(block.getLocation(), EntityType.PRIMED_TNT);
			tnt.setCustomName(main.getPlayer(player).getTeam().toString());
			block.setType(Material.AIR);
		}
	}
	
	@EventHandler
	public void damage(EntityDamageEvent event) //Listens to EntityDamageEvent
	{
		if (event == null) return;
		Entity ent = event.getEntity();
		Bukkit.broadcastMessage(event.getHandlers().toString());
	    if(ent instanceof Player) {
			Player player = (Player) ent;
			if(((player.getHealth() - event.getFinalDamage()) <= 0)) {
				event.setCancelled(true);
				main.playerDead(player);
			}
			
		}
	}

	@EventHandler
	public void onExplosion(EntityExplodeEvent event) {
		if (event == null) return;
		String entityName = event.getEntity().getCustomName();
		for (Block block : event.blockList()) {
			if (!main.canDestroyBed(block, entityName)) {
				event.setCancelled(true);
			} else {
				block.setType(Material.AIR);
			}
		}
	}

	// SETUP de l'inventaire du joueur pour le jeu Rush
	public void setupInventaireJoueurRush(Player player) {
		if (main.getPlayer(player) != null) {
			ItemStack kit = new ItemStack(Material.BOW, 1);
			ItemMeta kit_meta = kit.getItemMeta();
			kit_meta.setDisplayName("§eKits " + "§7(Clique droit)");
			List<String> lorekit = new ArrayList<String>();
			lorekit.add("§7Choisi un kit");
			kit_meta.setLore(lorekit);
			kit.setItemMeta(kit_meta);
			player.getInventory().setItem(0, kit);

			ItemStack team = new ItemStack(Material.NETHER_STAR, 1);
			ItemMeta team_meta = team.getItemMeta();
			team_meta.setDisplayName("§bChoisir mon équipe !");
			List<String> loreteam = new ArrayList<String>();
			loreteam.add("§7Choisi ton équipe !");
			team_meta.setLore(loreteam);
			team.setItemMeta(team_meta);
			player.getInventory().setItem(4, team);

			ItemStack retour = new ItemStack(Material.RED_BED, 1);
			ItemMeta retour_meta = retour.getItemMeta();
			retour_meta.setDisplayName("§cRetour Spawn");
			List<String> loreretour = new ArrayList<String>();
			loreretour.add("§7Cliques pour retourner au spawn");
			retour_meta.setLore(loreretour);
			retour.setItemMeta(retour_meta);
			player.getInventory().setItem(8, retour);
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		if (event == null) return;
		if (main.getPlayer(event.getPlayer()) != null) {
			main.leaveGame(event.getPlayer());
			for (RushPlayer rushPlayer : main.getPlayers()) {
				rushPlayer.getPlayer().sendMessage("§7[" + "§b§lRUSH" + "§7] " + event.getPlayer().getDisplayName()
						+ " a quitter la partie §a(" + main.getPlayers().size() + "§a/8)");
			}
		}

	}

	public void getInventoryLeaveGame(Player player) {
		ItemStack compass = new ItemStack(Material.COMPASS, 1);
		ItemMeta compass_meta = compass.getItemMeta();
		compass_meta.setDisplayName(ChatColor.GOLD + "§e§l✪ Navigateur");
		List<String> lorecompass = new ArrayList<String>();
		lorecompass.add("§7Ouvre le menu Principal");
		compass_meta.setLore(lorecompass);
		compass.setItemMeta(compass_meta);
		player.getInventory().setItem(0, compass);

		ItemStack profiltete = new ItemStack(Material.PLAYER_HEAD, 1);
		SkullMeta profiltete_meta = (SkullMeta) profiltete.getItemMeta();
		profiltete_meta.setOwningPlayer(player);
		profiltete.setItemMeta(profiltete_meta);
		player.getInventory().addItem(profiltete);
		profiltete_meta.setDisplayName(ChatColor.BOLD + "§b§l😃 " + player.getName());
		List<String> loreprofil = new ArrayList<String>();
		loreprofil.add("§7Ouvre le menu de ton profil");
		profiltete_meta.setLore(loreprofil);
		profiltete.setItemMeta(profiltete_meta);
		player.getInventory().setItem(1, profiltete);

		ItemStack shop = new ItemStack(Material.EMERALD, 1);
		ItemMeta shop_meta = shop.getItemMeta();
		shop_meta.setDisplayName(ChatColor.GREEN + "§d§lS§e§lh§b§lo§c§lp");
		List<String> loreshop = new ArrayList<String>();
		loreshop.add("§7Ouvre le menu du Shop");
		shop_meta.setLore(loreshop);
		shop.setItemMeta(shop_meta);
		player.getInventory().setItem(7, shop);

		ItemStack cosmétique = new ItemStack(Material.ENDER_CHEST, 1);
		ItemMeta cosmétique_meta = cosmétique.getItemMeta();
		cosmétique_meta.setDisplayName(ChatColor.GREEN + "§d§l♫ Cosmétiques");
		List<String> lorecosmétique = new ArrayList<String>();
		lorecompass.add("§7Ouvre le menu Cosmétique");
		cosmétique_meta.setLore(lorecosmétique);
		cosmétique.setItemMeta(cosmétique_meta);
		player.getInventory().setItem(8, cosmétique);
	}
	


}
