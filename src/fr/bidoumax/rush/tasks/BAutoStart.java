package fr.bidoumax.rush.tasks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.bidoumax.rush.MainRush;
import fr.bidoumax.rush.RushPlayer;
import fr.bidoumax.rush.enums.BState;

public class BAutoStart extends BukkitRunnable {

	private MainRush main;
	private int timer;

	public BAutoStart(MainRush main, int seconde) {
		this.main = main;
		this.timer = seconde;
	}

	@Override
	public void run() {

		for (RushPlayer rushPlayer : main.getPlayers()) {
			rushPlayer.getPlayer().setLevel(timer);

			if (this.main.getPlayers().size() < this.main.getSizeGame()) {
				rushPlayer.getPlayer().sendMessage("Interuption !");
				rushPlayer.getPlayer().sendTitle("§c§lInteruption !", "", 1, 20, 1);
				this.main.setState(BState.WAITING);
				timer = 0;
				rushPlayer.getPlayer().setLevel(0);
				cancel();
			} else {
				if (timer == 10 || timer == 5 || timer == 4 || timer == 3 || timer == 2 || timer == 1 || timer == 0) {
					rushPlayer.getPlayer()
							.sendMessage("§7[" + "§b§lRUSH" + "§7] " + "§6Début du jeu dans §c" + timer + " sec");
					rushPlayer.getPlayer().sendTitle("§c§l" + timer, "§ePréparez-vous !", 1, 20, 1);
				}

				if (timer == 0) {
					rushPlayer.getPlayer()
							.sendMessage("§a▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄ " + "\n" + "" + "\n" + "Jeu - §b§lRUSH" + "\n"
									+ " " + "\n" + "  §fUtilise les ressources pour acheter de l'équipement." + "\n"
									+ "  §fTon lit permet à l'équipede respawn." + "\n"
									+ "  Seul une TNT peut le détruire" + "\n"
									+ "  Soit dans la dernière équipe en vie pour gagner." + "\n" + " " + "\n"
									+ "§aMap - §e/ " + "\n" + "§a▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄ " + "\n" + " ");
					rushPlayer.getPlayer().sendTitle("§b§lGO !", "", 1, 20, 1);
					this.main.startGame();
					cancel();
				}
			}

		}
		if (timer <= 0) {
			cancel();
		} else {
			timer--;
		}
		
	}

}
