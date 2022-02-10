package fr.bidoumax.rush;

import org.bukkit.entity.Player;


import fr.bidoumax.rush.enums.TEAM;

public class RushPlayer {

	private Player player;
	private TEAM team;

	public RushPlayer(Player newPlayer) {
		this.player = newPlayer;
		this.team = TEAM.NEUTRE;
	}

	public RushPlayer(Player newPlayer, TEAM newTeam) {
		this.player = newPlayer;
		this.team = newTeam;
	}

	public Player getPlayer() {
		return this.player;
	}

	public TEAM getTeam() {
		return this.team;
	}

	public void setTeam(TEAM newTeam) {
		this.team = newTeam;
	}
}
