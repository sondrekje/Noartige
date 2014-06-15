package no.artige.handlers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import no.artige.Artige;

public class SQLHandler {


	private String username;
	private String password;
	private String host;
	private String databaseName;
	private String url;
	private Connection conn;
	private Artige plugin;

	public SQLHandler(Artige instance, String host, String databaseName, String username, String password) {
		this.host = host;
		this.databaseName = databaseName;
		this.username = username;
		this.password = password;
		this.plugin = instance;
	}

	// Create the default table Players
	public void createPlayersTable() throws SQLException {
		Statement s = conn.createStatement();
		s.executeUpdate("CREATE TABLE IF NOT EXISTS `players` (`id` int(11) NOT NULL AUTO_INCREMENT, `name` varchar(64) NOT NULL, `rank` int(11) NOT NULL, `regtime` datetime NOT NULL, " +
				"`regip` varchar(64) NOT NULL, `ip` varchar(64) NOT NULL, `lastonline` datetime NOT NULL, `uuid` varchar(70) NOT NULL, PRIMARY KEY (`id`))");
	}

	public synchronized boolean tableExist(String table) throws Exception {
		Statement s = this.conn.createStatement();
		s.executeQuery("SHOW TABLES");
		ResultSet rs = s.getResultSet();
		boolean exist = false;
		while (rs.next()) {
			if (rs.getString(1).equalsIgnoreCase(table)) {
				exist = true;
			}
		}
		return exist;
	}

	public ResultSet query(final String query) throws SQLException {
		Statement statement = null;
		ResultSet result = null;
		try
		{
			statement = conn.createStatement();
			result = statement.executeQuery(query);
			return result;
		} catch (final SQLException e)
		{
			if (e.getMessage().equals("Can not issue data manipulation statements with executeQuery()."))
			{
				try
				{
					statement.executeUpdate(query);
				} catch (final SQLException ex)
				{
					if (e.getMessage().startsWith("You have an error in your SQL syntax;"))
					{
						String temp = (e.getMessage().split(";")[0].substring(0, 36) + e.getMessage().split(";")[1].substring(91));
						temp = temp.substring(0, temp.lastIndexOf("'"));
						throw new SQLException(temp);
					} else
					{
						ex.printStackTrace();
					}
				}
			} else if (e.getMessage().startsWith("You have an error in your SQL syntax;"))
			{
				String temp = (e.getMessage().split(";")[0].substring(0, 36) + e.getMessage().split(";")[1].substring(91));
				temp = temp.substring(0, temp.lastIndexOf("'"));
				throw new SQLException(temp);
			} else
			{
				e.printStackTrace();
			}
		}
		return null;
	}


	public boolean update(String query) {
		Statement s = null;
		try {
			s = this.conn.createStatement();
			s.executeUpdate(query);
			s.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public synchronized void runConnection() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		this.url = ("jdbc:mysql://" + this.host + "/" + this.databaseName + "?autoReconnect=true");
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		this.conn = DriverManager.getConnection(this.url, this.username, this.password);
		plugin.print("Koblet til MySQL serveren med suksess");
	}

	public Connection getConnection() {
		return this.conn;
	}

	public synchronized void closeConnection() throws Exception	{
		this.conn.close();
		plugin.print("Ser ut som koblingen til MySQL serveren ble lukket.");
	}

}