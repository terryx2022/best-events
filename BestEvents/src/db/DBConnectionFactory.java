package db;

import db.mysql.MySQLConnection;
import db.mongodb.MongoDBConnection;

/**
 * Creates different database instances
 * @author tianqix
 *
 */
public class DBConnectionFactory {
	// This should change based on the database.
	private static final String DEFAULT_DB = "mysql";
	
	public static DBConnection getConnection(String db) {
		switch (db) {
		case "mysql":
			// return new MySQLConnection();
			return new MySQLConnection();
		case "mongodb":
			// return new MongoDBConnection();
			return null;
		default:
			throw new IllegalArgumentException("Invalid db:" + db);
		}

	}

	public static DBConnection getConnection() {
		return getConnection(DEFAULT_DB);
	}

	

}
