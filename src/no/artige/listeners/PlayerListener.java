package no.artige.listeners;

import java.util.Random;

import no.artige.Artige;
import no.artige.handlers.UserHandler;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

	private final Artige plugin;

	private static UserHandler userHandler;

	public PlayerListener(Artige instance) {
		this.plugin = instance;
		userHandler = plugin.getUserHandler();
	}

	public void initialize() {
		userHandler = plugin.getUserHandler();
	}

	@EventHandler
	public void onPlayerJoin(final PlayerJoinEvent e) {
		Player p = e.getPlayer();

		String[] joinMsg = new String[3];
		joinMsg[0] = " fant visst veien hjem.";
		joinMsg[1] = " kom seg visst på igjen.";
		joinMsg[2] = " var jo velkommen tilbake han også.";

		int r = new Random().nextInt(joinMsg.length);

		this.userHandler.addPlayer(p);
		
		e.setJoinMessage(userHandler.getRankName(p) + ChatColor.GREEN + joinMsg[r]);
	}

	@EventHandler
	public void onPlayerChatEvent(final AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		e.setFormat(userHandler.getChatName(p) + ChatColor.WHITE + e.getMessage());
	}

	@EventHandler
	public void onPlayerQuitEvent(final PlayerQuitEvent e) {
		Player p = e.getPlayer();
		e.setQuitMessage(userHandler.getRankName(p) + ChatColor.RED + " logget av.");
		userHandler.logout(p);
	}

}
