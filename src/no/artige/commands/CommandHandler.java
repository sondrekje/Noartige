package no.artige.commands;

import java.util.List;

import no.artige.Artige;
import no.artige.handlers.UserHandler;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class CommandHandler implements CommandExecutor {

	protected final Artige plugin;
	protected UserHandler userHandler;

	private int status;

	public CommandHandler(Artige instance) {
		this.plugin = instance;
		this.userHandler = this.plugin.getUserHandler();
		setStatus(15);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if ((sender instanceof Player)) {
			Player p = (Player)sender;
			if ((this.userHandler.getRank(p) >= getStatus()) || (p.isOp())) {
				return onPlayerCommand(p, cmd, label, args);
			} else {
				p.sendMessage(ChatColor.RED + "Du har ikke rettigheter til å utføre denne kommadoen ("+ userHandler.getRank(p) + " - " + getStatus()+".");
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Denne kommandoen er ikke støttet i konsoll.");
			return true;
		}
		return true;
	}

	public String getWorldNames(List<World> worlds) {
		String worldNames = "";
		for (World w : worlds) {
			worldNames = worldNames + w.getName() + ", ";
		}
		return worldNames;
	}

	@SuppressWarnings("unused")
	public boolean isInt(String s) {
		boolean b = true;
		try {
			int x = Integer.parseInt(s);
		}
		catch (NumberFormatException nFE)
		{
			int x;
			b = false;
		}
		return b;
	}

	public void print(String msg) {
		this.plugin.print(msg);
	}
	
	public String adminPrefix() {
		return ChatColor.DARK_RED + "Admin: ";
	}
	
	public ChatColor fineColor() {
		return ChatColor.GREEN;
	}
	
	public ChatColor errorColor() {
		return ChatColor.RED;
	}
	
	public ChatColor neutralColor() {
		return ChatColor.YELLOW;
	}
	
	public ChatColor gold() {
		return ChatColor.GOLD;
	}

	public String rankName(Player p) {
		return userHandler.getRankName(p);
	}
	
	public String highlight(String highlight, ChatColor reset) {
		return ChatColor.BLUE + highlight + reset;
	}
	
	public String replaceColors (String message) {
		return message.replaceAll("(?i)&([a-f0-9])", "\u00A7$1");
	}

	public abstract boolean onPlayerCommand(Player p, Command cmd, String label, String[] args);

	public void setStatus(int rank) {
		this.status = rank;
	}
	
	public int getStatus() {
		return this.status;
	}
	
}