package fr.bidoumax.rush.tasks;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import fr.bidoumax.rush.MainRush;

public class TimerIngot extends BukkitRunnable {
	private int timer;
	private MainRush main;

	public TimerIngot(MainRush main, int seconde) {
		this.timer = seconde;
		this.main = main;
	}

	@Override
	public void run() {
		if (timer <= 0) {
			this.main.spawnIngot();
			cancel();
		}
		timer--;
	}
}
