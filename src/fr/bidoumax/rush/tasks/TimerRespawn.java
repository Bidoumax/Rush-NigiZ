package fr.bidoumax.rush.tasks;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import fr.bidoumax.rush.MainRush;
import fr.bidoumax.rush.RushPlayer;

public class TimerRespawn extends BukkitRunnable {
	private int timer;
	private MainRush main;
	private RushPlayer rushPlayer;

	public TimerRespawn(MainRush main, int seconde, RushPlayer player) {
		this.timer = seconde;
		this.main = main;
		this.rushPlayer = player;
	}

	@Override
	public void run() {
		rushPlayer.getPlayer().sendTitle("§cTU ES MORT", "§7Respawn dans §e"+timer,1,20,1);
		if (timer <= 0) {
			this.main.respawn(this.rushPlayer);
			cancel();
		}
		timer--;
	}
}
