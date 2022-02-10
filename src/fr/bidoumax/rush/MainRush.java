package fr.bidoumax.rush;

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.html.parser.Entity;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Bed;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.Vector;

import fr.bidoumax.rush.enums.BState;
import fr.bidoumax.rush.enums.TEAM;
import fr.bidoumax.rush.listener.BPlayerListeners;
import fr.bidoumax.rush.tasks.BAutoStart;
import fr.bidoumax.rush.tasks.TimerIngot;
import fr.bidoumax.rush.tasks.TimerRespawn;

public class MainRush extends JavaPlugin {

	private List<RushPlayer> players = new ArrayList<>();
	private BState state;
	private int sizeGame = 2;
	private int sizeTeam = 1;
	private float[] coordBaseROUGE = new float[] { 81.5f, 45, -47.5f, 90, 0 };
	private float[] coordBaseCYAN = new float[] { 181.5f, 45, -47.5f, -90, 0 };
	private float[] coordBaseJAUNE = new float[] { 130.5f, 45, -99.5f, -180, 0 };
	private float[] coordBaseBLEU = new float[] { 130.5f, 45, 3.5f, 0, 0 };
	private boolean bedBaseROUGE = true;
	private boolean bedBaseCYAN = true;
	private boolean bedBaseJAUNE = true;
	private boolean bedBaseBLEU = true;

	private float[] randomPourcent = new float[] { 60, 80, 100 };

	private boolean itemSpawnActivate = false;

	private Location[] itemDropLocation = new Location[] { new Location(Bukkit.getWorld("world"), 75f, 44.5f, -44),
			new Location(Bukkit.getWorld("world"), 75f, 44.5f, -46f),
			new Location(Bukkit.getWorld("world"), 75f, 44.5f, -50f),
			new Location(Bukkit.getWorld("world"), 75f, 44.5f, -52f),

			new Location(Bukkit.getWorld("world"), 134f, 44.5f, 9f),
			new Location(Bukkit.getWorld("world"), 132f, 44.5f, 9f),
			new Location(Bukkit.getWorld("world"), 128f, 44.5f, 9f),
			new Location(Bukkit.getWorld("world"), 126f, 44.5f, 9f),

			new Location(Bukkit.getWorld("world"), 134f, 44.5f, -106f),
			new Location(Bukkit.getWorld("world"), 132f, 44.5f, -106f),
			new Location(Bukkit.getWorld("world"), 128f, 44.5f, -106f),
			new Location(Bukkit.getWorld("world"), 126f, 44.5f, -106f),

			new Location(Bukkit.getWorld("world"), 187f, 44.5f, -52f),
			new Location(Bukkit.getWorld("world"), 187f, 44.5f, -50f),
			new Location(Bukkit.getWorld("world"), 187f, 44.5f, -46f),
			new Location(Bukkit.getWorld("world"), 187f, 44.5f, -44f), };

	@Override
	public void onEnable() {
		setState(BState.WAITING);

		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new BPlayerListeners(this), this);

		System.out.println("[NigiZ] RUSH ------> en marche");
	}

	public void setState(BState state) {
		this.state = state;
	}

	public BState getState() {
		return this.state;
	}

	public int getSizeGame() {
		return this.sizeGame;
	}

	public int getSizeTeam() {
		return this.sizeTeam;
	}

	public boolean isState(BState state) {
		return this.state == state;
	}

	public RushPlayer getPlayer(Player player) {
		for (RushPlayer rushPlayer : players) {
			if (rushPlayer.getPlayer().equals(player)) {
				return rushPlayer;
			}
		}
		return null;
	}

	public boolean canDestroyBed(Block bed, String nameEntity) {
		switch (bed.getType()) {
		case RED_BED:
			if (nameEntity.equals(TEAM.ROUGE.toString())) {
				return false;
			}
			break;
		case CYAN_BED:
			if (nameEntity.equals(TEAM.CYAN.toString())) {
				return false;
			}
			break;
		case YELLOW_BED:
			if (nameEntity.equals(TEAM.JAUNE.toString())) {
				return false;
			}
			break;
		case BLUE_BED:
			if (nameEntity.equals(TEAM.BLEU.toString())) {
				return false;
			}
			break;
		default:
			return true;
		}
		
		switch (bed.getType()) {
		case RED_BED:
			if (!nameEntity.equals(TEAM.ROUGE.toString())) {
				this.bedBaseROUGE = false;
			}
			break;
		case CYAN_BED:
			if (!nameEntity.equals(TEAM.CYAN.toString())) {
				this.bedBaseCYAN = false;
			}
			break;
		case YELLOW_BED:
			if (!nameEntity.equals(TEAM.JAUNE.toString())) {
				this.bedBaseJAUNE = false;
			}
			break;
		case BLUE_BED:
			if (!nameEntity.equals(TEAM.BLEU.toString())) {
				this.bedBaseBLEU = false;
			}
			break;
		default:
			return true;
		}
		return true;
	}

	public void spawnIngot() {
		this.itemSpawnActivate = true;
		double result = (Math.random() * (100 - 1));
		for (Location loc : itemDropLocation) {
			if (result > 0 && result <= randomPourcent[0]) {
				Item drop = Bukkit.getWorld("world").dropItemNaturally(loc, new ItemStack(Material.COPPER_INGOT, 1));
				drop.setVelocity(new Vector(0, 0, 0));
				drop.setPickupDelay(0);
			} else if (result > randomPourcent[0] && result <= randomPourcent[1]) {
				Item drop = Bukkit.getWorld("world").dropItemNaturally(loc, new ItemStack(Material.IRON_INGOT, 1));
				drop.setVelocity(new Vector(0, 0, 0));
				drop.setPickupDelay(0);
			} else if (result > randomPourcent[1] && result <= randomPourcent[2]) {
				Item drop = Bukkit.getWorld("world").dropItemNaturally(loc, new ItemStack(Material.GOLD_INGOT, 1));
				drop.setVelocity(new Vector(0, 0, 0));
				drop.setPickupDelay(0);
			}

		}

		if (this.state == BState.PLAYING) {
			TimerIngot newTimer = new TimerIngot(this, 1);
			newTimer.runTaskTimer(this, 0, 30);
		}
	}

	@Override
	public void onDisable() {
		System.out.println("Rush éteind");
	}

	public List<RushPlayer> getPlayers() {
		return players;
	}

	public boolean addPlayer(Player newPlayer) {

		for (RushPlayer rushPlayer : players) {
			if (rushPlayer.getPlayer().equals(newPlayer)) {
				newPlayer.sendMessage("§7[§bRUSH§7] §b>> §cVous êtes déja dans la partie");
				return false;
			}
		}
		if (players.size() >= this.sizeGame) {
			newPlayer.sendMessage("§7[§cRUSH§7] §b>> §cLa partie est pleine !");
			return false;
		} else if (this.state != BState.WAITING) {
			newPlayer.sendMessage("§7[§bRUSH§7] §b>> §cLa partie à déjà commencé !");
			return false;
		} else {
			players.add(new RushPlayer(newPlayer));
			for (RushPlayer rushPlayer : players) {
				this.createRushScoreBoard(rushPlayer);
			}
			this.startTimer();
			return true;
		}

	}

	public void startTimer() {

		if (players.size() == this.sizeGame && this.state == BState.WAITING) {
			BAutoStart newTimer = new BAutoStart(this, 20);
			newTimer.runTaskTimer(this, 0, 20);
		}
	}

	public void startGame() {
		this.state = BState.PLAYING;
		this.setupStartGame();
	}

	public void setupStartGame() {
		// join random team for ALL players
		if (!itemSpawnActivate) {
			this.spawnIngot();
		}
		for (RushPlayer rushPlayer : players) {
			if (rushPlayer.getTeam() == TEAM.NEUTRE) {
				joinRandomTeam(rushPlayer);
			}
			for (PotionEffect effect : rushPlayer.getPlayer().getActivePotionEffects()) {
				rushPlayer.getPlayer().removePotionEffect(effect.getType());
			}
			rushPlayer.getPlayer().setGameMode(GameMode.SURVIVAL);
			getArmorPlayer(rushPlayer);
			tpToBaseTeam(rushPlayer);

		}
	}

	public void getArmorPlayer(RushPlayer rushPlayer) {
		rushPlayer.getPlayer().getInventory().clear();
		rushPlayer.getPlayer().getInventory().addItem(new ItemStack(Material.WOODEN_PICKAXE, 1));

		ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
		LeatherArmorMeta metaChestplate = (LeatherArmorMeta) chestplate.getItemMeta();

		ItemStack leggins = new ItemStack(Material.LEATHER_LEGGINGS, 1);
		LeatherArmorMeta metaLeggins = (LeatherArmorMeta) chestplate.getItemMeta();

		ItemStack boots = new ItemStack(Material.LEATHER_BOOTS, 1);
		LeatherArmorMeta metaBoots = (LeatherArmorMeta) chestplate.getItemMeta();

		ItemStack helmet = new ItemStack(Material.LEATHER_HELMET, 1);
		LeatherArmorMeta metaHelmet = (LeatherArmorMeta) chestplate.getItemMeta();

		switch (rushPlayer.getTeam()) {
		case ROUGE:
			metaChestplate.setColor(Color.RED);
			metaLeggins.setColor(Color.RED);
			metaHelmet.setColor(Color.RED);
			metaBoots.setColor(Color.RED);
			break;
		case JAUNE:
			metaChestplate.setColor(Color.YELLOW);
			metaLeggins.setColor(Color.YELLOW);
			metaHelmet.setColor(Color.YELLOW);
			metaBoots.setColor(Color.YELLOW);
			break;
		case CYAN:
			metaChestplate.setColor(Color.AQUA);
			metaLeggins.setColor(Color.AQUA);
			metaHelmet.setColor(Color.AQUA);
			metaBoots.setColor(Color.AQUA);
			break;
		case BLEU:
			metaChestplate.setColor(Color.BLUE);
			metaLeggins.setColor(Color.BLUE);
			metaHelmet.setColor(Color.BLUE);
			metaBoots.setColor(Color.BLUE);
			break;
		default:
			break;
		}
		chestplate.setItemMeta(metaChestplate);
		leggins.setItemMeta(metaLeggins);
		boots.setItemMeta(metaBoots);
		helmet.setItemMeta(metaHelmet);
		rushPlayer.getPlayer().getInventory().setChestplate(chestplate);
		rushPlayer.getPlayer().getInventory().setLeggings(leggins);
		rushPlayer.getPlayer().getInventory().setBoots(boots);
		rushPlayer.getPlayer().getInventory().setHelmet(helmet);
	}

	public void tpToBaseTeam(RushPlayer rushPlayer) {

		switch (rushPlayer.getTeam()) {
		case ROUGE:
			rushPlayer.getPlayer().teleport(new Location(rushPlayer.getPlayer().getWorld(), coordBaseROUGE[0],
					coordBaseROUGE[1], coordBaseROUGE[2], coordBaseROUGE[3], coordBaseROUGE[4]));
			break;
		case CYAN:
			rushPlayer.getPlayer().teleport(new Location(rushPlayer.getPlayer().getWorld(), coordBaseCYAN[0],
					coordBaseCYAN[1], coordBaseCYAN[2], coordBaseCYAN[3], coordBaseCYAN[4]));
			break;
		case JAUNE:
			rushPlayer.getPlayer().teleport(new Location(rushPlayer.getPlayer().getWorld(), coordBaseJAUNE[0],
					coordBaseJAUNE[1], coordBaseJAUNE[2], coordBaseJAUNE[3], coordBaseJAUNE[4]));
			break;
		case BLEU:
			rushPlayer.getPlayer().teleport(new Location(rushPlayer.getPlayer().getWorld(), coordBaseBLEU[0],
					coordBaseBLEU[1], coordBaseBLEU[2], coordBaseBLEU[3], coordBaseBLEU[4]));
			break;
		default:
			break;
		}
	}

	public void leaveGame(Player player) {
		for (RushPlayer rushPlayer : players) {
			if (rushPlayer.getPlayer().equals(player)) {
				players.remove(rushPlayer);
			}
		}
		player.sendMessage("§aTéléportation au spawn...");
		player.teleport(new Location(player.getWorld(), -114.5, 90, 42.5, 180f, 24f));
		player.getInventory().clear();
		this.createSpawnScoreBoard(player);
	}

	public void createMenuChoiceTeams(Player player, boolean open) {

		Inventory inventory = Bukkit.createInventory(null, (9 * 1), "§8Choix de mon équipe");

		ItemStack teamRouge = new ItemStack(Material.RED_WOOL, 1);
		ItemMeta teamRougeItemMeta = teamRouge.getItemMeta();
		teamRougeItemMeta.setDisplayName("§bEquipe §cRouge");
		teamRouge.setAmount(getNumberPlaceFreeInTeam(TEAM.ROUGE));
		teamRouge.setItemMeta(teamRougeItemMeta);
		inventory.setItem(1, teamRouge);

		ItemStack teamCyan = new ItemStack(Material.CYAN_WOOL, 1);
		ItemMeta teamCyanItemMeta = teamCyan.getItemMeta();
		teamCyanItemMeta.setDisplayName("§bEquipe §3Cyan");
		teamCyan.setAmount(getNumberPlaceFreeInTeam(TEAM.CYAN));
		teamCyan.setItemMeta(teamCyanItemMeta);
		inventory.setItem(3, teamCyan);

		ItemStack teamJaune = new ItemStack(Material.YELLOW_WOOL, 1);
		ItemMeta teamJauneItemMeta = teamJaune.getItemMeta();
		teamJauneItemMeta.setDisplayName("§bEquipe §eJaune");
		teamJaune.setAmount(getNumberPlaceFreeInTeam(TEAM.JAUNE));
		teamJaune.setItemMeta(teamJauneItemMeta);
		inventory.setItem(2, teamJaune);

		ItemStack teamBleu = new ItemStack(Material.BLUE_WOOL, 1);
		ItemMeta teamBleuItemMeta = teamBleu.getItemMeta();
		teamBleuItemMeta.setDisplayName("§bEquipe §9Bleue");
		teamBleu.setAmount(getNumberPlaceFreeInTeam(TEAM.BLEU));
		teamBleu.setItemMeta(teamBleuItemMeta);
		inventory.setItem(0, teamBleu);

		ItemStack teamNeutre = new ItemStack(Material.WHITE_WOOL, 1);
		ItemMeta teamNeutreItemMeta = teamNeutre.getItemMeta();
		teamNeutreItemMeta.setDisplayName("§bEquipe §fNeutre");
		teamNeutre.setItemMeta(teamNeutreItemMeta);
		inventory.setItem(8, teamNeutre);

		if (open)
			player.openInventory(inventory);

	}

	public int getNumberOfTeam(TEAM team) {
		int rep = 0;
		for (RushPlayer rushPlayer : players) {
			if (rushPlayer.getTeam() == team) {
				rep++;
			}
		}
		return rep;
	}

	public int getNumberPlaceFreeInTeam(TEAM team) {
		int rep = 0;
		for (RushPlayer rushPlayer : players) {
			if (rushPlayer.getTeam() == team) {
				rep++;
			}
		}
		return sizeTeam - rep;
	}

	public void joinTeam(TEAM newTeam, Player player) {
		for (RushPlayer rushPlayer : players) {
			if (rushPlayer.getPlayer() == player) {
				rushPlayer.setTeam(newTeam);

				switch (newTeam) {
				case ROUGE:
					rushPlayer.getPlayer().sendMessage("§7[" + "§b§lRUSH" + "§7] " + "§b>> "
							+ "§bVous avez rejoint l'équipe " + "§cRouge " + "§f!");
					player.setPlayerListName(ChatColor.RED + getName());
					break;
				case CYAN:
					rushPlayer.getPlayer().sendMessage("§7[" + "§b§lRUSH" + "§7] " + "§b>> "
							+ "§bVous avez rejoint l'équipe " + "§3Cyan " + "§f!");
					break;
				case JAUNE:
					rushPlayer.getPlayer().sendMessage("§7[" + "§b§lRUSH" + "§7] " + "§b>> "
							+ "§bVous avez rejoint l'équipe " + "§eJaune " + "§f!");
					break;
				case BLEU:
					rushPlayer.getPlayer().sendMessage("§7[" + "§b§lRUSH" + "§7] " + "§b>> "
							+ "§bVous avez rejoint l'équipe " + "§9Bleu " + "§f!");
					break;
				case NEUTRE:
					rushPlayer.getPlayer()
							.sendMessage("§7[" + "§b§lRUSH" + "§7] " + "§b>> " + "§fVous n'êtes dans aucune équipe !");
					break;
				default:
					break;
				}
			}
		}
		for (RushPlayer rushPlayer : players) {
			if (rushPlayer.getPlayer().getOpenInventory().getTitle() == "§8Choix de mon équipe") {
				rushPlayer.getPlayer().closeInventory();
				createMenuChoiceTeams(rushPlayer.getPlayer(), true);
			}
			createMenuChoiceTeams(rushPlayer.getPlayer(), false);
		}

	}

	public void joinRandomTeam(RushPlayer rushPlayer) {
		TEAM[] Teams = new TEAM[] { TEAM.ROUGE, TEAM.CYAN, TEAM.JAUNE, TEAM.BLEU };
		for (TEAM team : Teams) {
			if (getNumberPlaceFreeInTeam(team) > 0 && rushPlayer.getTeam() == TEAM.NEUTRE) {
				this.joinTeam(team, rushPlayer.getPlayer());
			}
		}
	}

	public void playerDead(Player player) {
		RushPlayer rushPlayer = this.getPlayer(player);
		for (RushPlayer ForRushPlayer : players) {
			if (!ForRushPlayer.equals(rushPlayer)) {
				if (ForRushPlayer.getTeam() == rushPlayer.getTeam()) {
					ForRushPlayer.getPlayer().sendMessage("Votre allié " + player.getName() + " est mort");
				} else {
					ForRushPlayer.getPlayer().sendMessage("L'énemie " + player.getName() + " de la team "
							+ rushPlayer.getTeam().toString() + " est mort");
				}
			}
		}
		rushPlayer.getPlayer().setGameMode(GameMode.SPECTATOR);
		this.tpToBaseTeam(rushPlayer);
		if(canRespawn(rushPlayer)) {
			TimerRespawn newTimer = new TimerRespawn(this, 5, rushPlayer);
			newTimer.runTaskTimer(this, 0, 20);
		} else {
			rushPlayer.getPlayer().sendTitle("§cTU ES MORT", "§7FIN DEPARTIE POUR VOUS");
			this.leaveGame(player);
			this.tpToBaseTeam(rushPlayer);
			TEAM[] Teams = new TEAM[] { TEAM.ROUGE, TEAM.CYAN, TEAM.JAUNE, TEAM.BLEU };
			List<TEAM> listeTeamEnVue = new ArrayList<>();
			for (TEAM team : Teams) {
				Bukkit.broadcastMessage(team.name() +":" +getNumberOfTeam(team)+"");
				if(getNumberOfTeam(team) != 0) {
					listeTeamEnVue.add(team);
				}
			}
			Bukkit.broadcastMessage(listeTeamEnVue.size()+"tes");
			if(listeTeamEnVue.size() == 1) {
				Bukkit.broadcastMessage("FIN DE LA PARTIE");
			}
		}
		
		

	}

	public void respawn(RushPlayer rushPlayer) {
		this.tpToBaseTeam(rushPlayer);
		rushPlayer.getPlayer().setGameMode(GameMode.SURVIVAL);
		rushPlayer.getPlayer().setHealth(20);
		getArmorPlayer(rushPlayer);
	}

	public void createRushScoreBoard(RushPlayer rushPlayer) {
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard board = manager.getNewScoreboard();
		Objective obj = board.registerNewObjective("RushScoreboard-1", "dummy", "Rush");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);

		Score score1 = obj.getScore("++++++++++++++");
		score1.setScore(3);

		Score score2 = obj.getScore("Joueur restant: " + this.getPlayers().size() + "/" + this.getSizeGame());
		score2.setScore(1);

		Score score3 = obj.getScore(rushPlayer.getPlayer().getName());
		score3.setScore(2);

		Score score4 = obj.getScore("+++++++++++++");
		score4.setScore(0);

		rushPlayer.getPlayer().setScoreboard(board);

	}

	public boolean canRespawn(RushPlayer rushPlayer) {
		switch (rushPlayer.getTeam()) {
		case ROUGE:
			return bedBaseROUGE;
		case CYAN:
			return bedBaseCYAN;
		case JAUNE:
			return bedBaseJAUNE;
		case BLEU:
			return bedBaseBLEU;
		}
		return false;
	}

	public void createSpawnScoreBoard(Player player) {
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard board = manager.getNewScoreboard();
		Objective obj = board.registerNewObjective("HubScoreboard-1", "dummy", "≡≡≡≡≡ §6NigiZ ≡≡≡≡≡");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);

		Score score1 = obj.getScore(" \n");
		score1.setScore(15);

		Score score2 = obj.getScore("§d➵ Pseudo: §f" + player.getName());
		score2.setScore(14);

		Score score3 = obj.getScore(" \n");
		score3.setScore(13);

		Score score4 = obj.getScore("§3➵ Rang: §fDefault");
		score4.setScore(12);

		Score score5 = obj.getScore(" \n");
		score5.setScore(11);

		Score score6 = obj.getScore("§a➵ Gems: §f0§a◈");
		score6.setScore(10);

		Score score7 = obj.getScore(" \n");
		score7.setScore(9);

		Score score8 = obj.getScore("§9➵ Joueur en Ligne: §f" + Bukkit.getOnlinePlayers().size());
		score8.setScore(8);

		player.setScoreboard(board);

	}
	
	public void verifEnd() {
		Bukkit.broadcastMessage("isFinish");
		/*TEAM[] Teams = new TEAM[] { TEAM.ROUGE, TEAM.CYAN, TEAM.JAUNE, TEAM.BLEU };
		List<TEAM> listeTeamEnVue = new ArrayList<>();
		for (TEAM team : Teams) {
			Bukkit.broadcastMessage(team.name() +":" +getNumberOfTeam(team)+"");
			if(getNumberOfTeam(team) != 0) {
				listeTeamEnVue.add(team);
			}
		}
		Bukkit.broadcastMessage(listeTeamEnVue.size()+"tes");
		if(listeTeamEnVue.size() == 1) {
			Bukkit.broadcastMessage("FIN DE LA PARTIE");
		}*/
	}

}
