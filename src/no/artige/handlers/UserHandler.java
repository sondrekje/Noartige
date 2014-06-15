package no.artige.handlers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import no.artige.Artige;
import no.artige.handlers.data.PlayerData;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class UserHandler {

	private static Artige plugin;
	
	private SQLHandler sqlHandler;
	
	private Connection conn;
	
	// Data for online players
	public HashMap<Player, PlayerData> playerData = new HashMap<Player, PlayerData>();
	
	public UserHandler(Artige instance) {
		plugin = instance;
		sqlHandler = plugin.getSQLHandler();
	}
	
	public void initialize() {
		sqlHandler = plugin.getSQLHandler();
		conn = plugin.getSQLHandler().getConnection();
	}
	
	public void updateOnline() {
		for (Player online: Bukkit.getOnlinePlayers()) {
			addPlayer(online);
		}
	}
	
	public void updatePlayer(Player p) {
		addPlayer(p);
	}
	
	public void addPlayer(Player p) {
		String name = p.getName();
		if (playerData.get(p) != null) {
			logout(p);
		}
		
		if (!hasPlayer(name)) {
			insertPlayer(name);
			p.sendMessage(ChatColor.YELLOW + "Du logget på for første gang, " + ChatColor.WHITE + name + ". " + ChatColor.YELLOW + "Velkommen!");
			login(p, true);
		} else {
			p.sendMessage(ChatColor.YELLOW + "Velkommen tilbake!");
			login(p, false);
		}
	}
	
	public void login(Player p, boolean first) {
		if (first) {
			String ip = this.getIP(p.getName());
			PlayerData pd = new PlayerData(1, ip);
			playerData.put(p, pd);
		} else {
			String ip = this.getIP(p.getName());
			int rank = this.getRankFromDatabase(p.getName());
			PlayerData pd = new PlayerData(rank, ip);
			playerData.put(p, pd);
		}
		setTabName(p);
	}
		
	public void logout(Player p) {
		saveInfo(p);
		playerData.remove(p);
	}
	
	public void setTabName(Player p) {
		String pn = p.getName();
		if (pn.length() >= 14) {
			p.setPlayerListName(getNameColor(p) + pn.substring(14) + "..");
		} else {
			p.setPlayerListName(getNameColor(p) + pn);
		}
	}
	
	public ChatColor getNameColor(Player p) {
		int x = getRank(p);
		String pn = p.getName();
		switch (x) {
		case 1: 
			return ChatColor.WHITE;
		case 2:
			return ChatColor.DARK_PURPLE;
		case 3:
			return ChatColor.DARK_PURPLE;
		case 4:
			return ChatColor.DARK_PURPLE;
		case 5:
			return ChatColor.AQUA;
		case 6:
			return ChatColor.BLUE;
		case 7:
			return ChatColor.GOLD;
		default:
			return ChatColor.RED;
		}
	}
	
	public String getChatName(Player p) {
		int x = getRank(p);
		String pn = p.getName();
		switch (x) {
		case 1:
			return "§f"+pn+": "; 
		case 2:
			return ChatColor.DARK_PURPLE + "[I] " + pn + ": ";
		case 3:
			return ChatColor.DARK_PURPLE + "[II] " + pn + ": ";
		case 4: 
			return ChatColor.DARK_PURPLE + "[III] " + pn + ": ";
		case 5:
			return ChatColor.AQUA + "[Pensjonist] " + pn + ": ";
		case 6:
			return ChatColor.BLUE + "[Mod] " + pn + ": ";
		case 7:
			return ChatColor.GOLD + "[Admin] " + pn + ": ";
		default:
			return "§c[Error] " + pn + ": ";
		}
	}
	
	public String getRankName(Player p) {
		int x= getRank(p);
		String pn = p.getName();
		switch (x) {
		case 1:
			return ChatColor.WHITE + pn;
		case 2:
			return ChatColor.DARK_PURPLE + "[I] " + pn;
		case 3:
			return ChatColor.DARK_PURPLE + "[II] " + pn;
		case 4: 
			return ChatColor.DARK_PURPLE + "[III] " + pn;
		case 5:
			return ChatColor.AQUA + "[Pensjonist] " + pn;
		case 6: 
			return ChatColor.BLUE + "[Mod] " + pn;
		case 7:
			return ChatColor.GOLD + "[Admin] " + pn;
		default:
			return "§[Error] " + pn;
		}
	}
	
	public int getRank(Player p) {
		PlayerData pd = (PlayerData)this.playerData.get(p);
		if (pd != null) {
			return pd.getRank();
		}
		return 0;
	}
	
	public synchronized int insertPlayer(String name) {
		int ret = -1;
		String uuid = Bukkit.getPlayer(name).getUniqueId().toString();
		String ip = getIP(name);
		try {
			Statement s = conn.createStatement();
			String values = "('" + name + "', '1', NOW(), '"+ip+"','"+ip+"', NOW(), '"+uuid+"')";
			ret = s.executeUpdate("INSERT INTO players (name, rank, regtime, regip, ip, lastonline, uuid) VALUES "+values);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public void setRank(Player p, int rank) {
		try {
			setRank(p.getName(), rank);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void saveInfo(Player p) {
		try {
			setLastOnline(p.getName());
			setLastIP(p.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public synchronized int setRank(String name, int rank) throws Exception {
		Statement s = null;
		Player p = Bukkit.getPlayer(name);
		try {
			s = this.conn.createStatement();
			s.executeUpdate("UPDATE Players SET rank='" + rank + "' WHERE name='" + name + "'");
			s.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		updatePlayer(p);
		return rank;
	}
	
	public synchronized int setLastOnline(String name) throws Exception {
		Statement s = null;
		int i = -1;
		try {
			s = this.conn.createStatement();
			i = s.executeUpdate("UPDATE Players SET lastonline=NOW() WHERE name='"+name+"'");
			s.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return i;
	}
	
	public synchronized int setLastIP(String name) throws Exception {
		Statement s = null;
		int i = -1;
		try {
			s = this.conn.createStatement();
			i = s.executeUpdate("UPDATE players SET ip='"+this.getIP(name)+"' WHERE name='"+name+"'");
			s.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return i;
	}
	
	public synchronized int getRankFromDatabase(String name) {
		int rank = 1;
		try {
			ResultSet rs = sqlHandler.query("SELECT rank FROM players WHERE name='" + name + "'");
			while (rs.next()) {
				rank = rs.getInt("rank");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rank;
	}
	
	public synchronized boolean hasPlayer(String name) {
		try {
			ResultSet rs = sqlHandler.query("SELECT * FROM players WHERE name='" + name + "'");
			if (rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public String getIP(String name) {
		return Bukkit.getPlayer(name).getAddress().getAddress().getHostAddress();
	}
}
