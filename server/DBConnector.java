package server;

import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnector {
	
	private final static String database = "localhost";
	private final static int port = 3306;
	private MariaDbDataSource conn;
	private Logger logger;
	
	public DBConnector(String pUser, String pPassword){
		conn = new MariaDbDataSource();
		logger = Logger.getLogger("databse");
		try{
			conn.setServerName(database);
			conn.setUser(pUser);
			conn.setPort(port);
			conn.setPassword(pPassword);
			conn.getConnection();
			logger.log(Level.INFO, String.format("Connecting to resource jdbc://%s:%s@%s:%s", database, port, pUser, pPassword));
		}
		catch(SQLException se){se.printStackTrace();}
		finally{logger.log(Level.INFO, "Connected to resource.");}
	}
}
