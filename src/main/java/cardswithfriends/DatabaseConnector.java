package cardswithfriends;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

/**
 * 
 * @author Sarah and Thomas
 *
 */
public class DatabaseConnector {
	// Should get this value from a config file for real production.
	//format is:  driver://username:password@hostname.com:PORT/db_name
	private static final String MONGO_DATABASE_ENVIRONMENT_VARIABLE = "PRODUCTION_MONGO";
	
	private static MongoClient client;
	private static DB db; 
	
    public static DB getMongoDB(){
    	if(db == null){
    		ProcessBuilder processBuilder = new ProcessBuilder();
    		String environmentURI = processBuilder.environment().get(MONGO_DATABASE_ENVIRONMENT_VARIABLE);
    		MongoClientURI uri  = new MongoClientURI(environmentURI); 
    		MongoClient client = new MongoClient(uri);
        	db = client.getDB(uri.getDatabase());
    	}
    	return db;
    }
    
    //Should be closed on app shutdown
    public static void closeConnection(){
    	client.close();
    }
}
