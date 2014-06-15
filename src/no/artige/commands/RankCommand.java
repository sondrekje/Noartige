package no.artige.commands;

import no.artige.Artige;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class RankCommand extends CommandHandler {

	public RankCommand(Artige instance) {
		super(instance);
		setStatus(7);
	}

	@Override
	public boolean onPlayerCommand(Player p, Command cmd, String label,
			String[] args) {
		if (args.length == 0) {
			p.sendMessage(this.adminPrefix() + this.errorColor() + "rank <spiller> <rank>");
			p.sendMessage(fineColor() + "1: bruker, 2: I, 3: II, 4: III, 5: Pensjonist, 6: mod, 7: admin");
			return true;
		} else if (args.length == 1) {
			p.sendMessage(ChatColor.RED + "Du har glemt rank!");
			p.performCommand("rank");
			return true;
		} else if (args.length == 2) {
			Player v = Bukkit.getPlayer(args[0]);
			if (v == null) {
				p.sendMessage(ChatColor.RED + "Fant ingen spiller med dette navnet.");
				return true;
			}

			switch (args[1]) {
			case "1":
				userHandler.setRank(v, 1);
				break;
			case "2":
				userHandler.setRank(v, 2);
				break;
			case "3":
				userHandler.setRank(v, 3);
				break;
			case "4":
				userHandler.setRank(v, 4);
				break;
			case "5":
				userHandler.setRank(v, 5);
				break;
			case "6":
				userHandler.setRank(v, 6);
				break;
			case "7":
				userHandler.setRank(v, 7);
				break;
			default:
				p.sendMessage(ChatColor.RED + "Ukjent rank!");
				return true;
			}
			Bukkit.broadcastMessage(rankName(v) + ChatColor.YELLOW + " sin nye spillerstatus ble satt av " + rankName(p) + ChatColor.YELLOW + ".");
			return true;
		} else {
			p.sendMessage(ChatColor.RED + "Ukjent argument.");
			return true;
		}
	}
}