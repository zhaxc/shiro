package mongo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;

public class TestMongoCRUD {
	private Mongo mongo = null;
	private DB db;
	private DBCollection users;
	
	@Before
	public void init(){
		mongo = new MongoClient("192.168.126.19");
		db = mongo.getDB("temp");
		users = db.getCollection("users");
	}
	
	@After
	public void destory(){
		if (mongo != null) {
			mongo.close();
		}
		System.gc();
	}
	
	@Test
	public void queryAll(){
		DBCursor find = users.find();
		while(find.hasNext()){
			print(find.next());
		}	
		
	}
	
	@Test
	public void add(){
		 print("count: " + users.count());
		 DBObject user = new BasicDBObject();
	     user.put("name", "hoojo");
	     user.put("age", 24);
	     users.save(user);
	     print("count: " + users.count());
	}
	
	public void remove(){
		queryAll();
		
	}
	
	public void print(Object o) {
        System.out.println(o);
    }
}
