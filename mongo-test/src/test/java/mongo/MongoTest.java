package mongo;
import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.ListDatabasesIterable;

public class MongoTest {
	public static void main(String[] args) {
		//Mongo mongo = new Mongo("192.168.126.19");
		MongoClient client = new MongoClient("192.168.126.19");
		ListDatabasesIterable<Document> databases = client.listDatabases();
		for (Document document : databases) {
			System.out.println(document);
			System.out.println("github");
		}
	}
}
