package no.artige;

import java.io.File;
import java.io.IOException;

import no.artige.commands.RankCommand;
import no.artige.handlers.SQLHandler;
import no.artige.handlers.UserHandler;
import no.artige.listeners.PlayerListener;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Artige extends JavaPlugin {

	// Config and other useful things
	private FileConfiguration config;
	
	// Listeners and handlers
	private final PlayerListener playerListener = new PlayerListener(this);

	private final UserHandler userHandler = new UserHandler(this);
	
	private SQLHandler sqlHandler;
	
	public void onEnable() {
		config = getConfig();
		
		getDataFolder().mkdir();

		try {
			loadConfig();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		PluginManager pm = getServer().getPluginManager();
		
		pm.registerEvents(playerListener, this);
		
		connectSql();
		
		runInitializations();
		
		print("Aktivert Artige.no plugin!");
	
		userHandler.updateOnline();
		getCommands();
	}

	public void onDisable() {
		try {
			File config = new File(getDataFolder() + "/config.yml");
			if (!config.exists()) {
				this.config.save(getDataFolder() + "/" + "config.yml");
			}
		} catch (IOException e) {
			e.printStackTrace(); 
		} 
		print("Deaktivert Artige.no plugin!");
	}
	
	public void loadConfig() throws Exception {
		this.config.options().header("Artigeno database konfigurasjon.");
		this.config.addDefault("MySQL.Host", "localhost");
		this.config.addDefault("MySQL.DatabaseName", "ArtigeMC");
		this.config.addDefault("MySQL.Username", "root");
		this.config.addDefault("MySQL.Password", "root");
		this.config.options().copyDefaults(true);
	}
	
	// Register commands 
	public void getCommands() {
		getCommand("rank").setExecutor(new RankCommand(this));
		print("Kommandoer har blitt registrert.");
	}
	
	// Initialize classes easily
	public void runInitializations() {
		playerListener.initialize();
		userHandler.initialize();
	}
	
	// Getter for handlers
	public UserHandler getUserHandler() {
		return userHandler;
	}
	
	public SQLHandler getSQLHandler() {
		return sqlHandler;
	}
	
	// Custom print function which I find useful
	public void print(String message) {
		System.out.println("[Artigeno] " + message);
	}
	
	
	private void connectSql() {
		String host = this.config.getString("MySQL.Host");
		String databaseName = this.config.getString("MySQL.DatabaseName");
		String username = this.config.getString("MySQL.Username");
		String password = this.config.getString("MySQL.Password");
		this.sqlHandler = new SQLHandler(this, host, databaseName, username, password);
		try {
			this.sqlHandler.runConnection();
			this.sqlHandler.createPlayersTable();
		} catch (Exception e) {
			print("Kunne ikke koble til database med: " + host + ", " + databaseName + ", " + username + ", " + password + ".");
			e.printStackTrace();
		}
	}
	
}
