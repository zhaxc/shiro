package mongo;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.EntityInterceptor;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.mapping.MappedClass;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.mapping.cache.EntityCache;
import org.mongodb.morphia.query.Criteria;
import org.mongodb.morphia.query.FieldEnd;
import org.mongodb.morphia.query.MorphiaIterator;
import org.mongodb.morphia.query.MorphiaKeyIterator;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.omg.PortableInterceptor.USER_EXCEPTION;

import com.entity.User;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;

public class MorphiaTest {
	
	private MongoClient mongo;
	private Morphia morphia;
	private Datastore ds;

	@Before
	public void init(){
		mongo = new MongoClient("192.168.126.19");
		morphia = new Morphia();
		ds = morphia.createDatastore(mongo, "temps");
	}

	private void print(Object o) {
		if (o != null) {
			System.out.println(o.toString());
		}
	}
	
	@After
	public void destory(){
		if (mongo != null) {
			mongo.close();
		}
		System.gc();
	}
	
	@Test
	public void testMorphia(){
		// 创建一个Datastore，过时的方法不推荐使用
		//morphia.createDatastore("Morphia");
		// 创建ds
		Datastore ds = morphia.createDatastore(mongo, "Morphia");
		print("createDatastore: " + ds);
		 // 设置操作资源对象，这里设置User.class 就可以完成对User的一系列操作
		morphia.map(User.class);
		//morphia.mapPackage("com.entity");//会把整个包下面的类都加载进来
		// 将对象转成DBObject
		//print("toDBObject: " + morphia.toDBObject(new User(System.currentTimeMillis(),"查",true,18,"北京")));
		print("toDBObject:"+ morphia.toDBObject(new User(1,"查",true,18,"北京")));
		// 将参数2转换成参数1的类型
		print("fromDBObject: " + morphia.fromDBObject(ds, User.class, BasicDBObjectBuilder.start("_id",2).append("name", "wangni").add("age", "17").get()));
        print("getMapper: " + morphia.getMapper());
        print("isMapped: " + morphia.isMapped(User.class));
	}
	
	@Test
	public void testMapper(){
		Mapper mapper = morphia.getMapper();
		 // 添加对象映射
		print("addMappedClass: " + mapper.addMappedClass(User.class));
		// 创建实体缓存
		EntityCache createEntityCache = mapper.createEntityCache();
		print(createEntityCache);
		String collectionName = mapper.getCollectionName("test");
		print(collectionName);
		print(mapper.getConverters());
		User user = new User(System.currentTimeMillis(), "jackson", true, 22, "北京");
		user.setId(1306814012734L);
		print(mapper.getId(user));
		for (EntityInterceptor ei : mapper.getInterceptors()) {
            System.out.println("EntityInterceptor: " + ei);
        }
		 // 查询主键
        print("getKey: " + mapper.getKey(user));
        // 所有已经映射的class
        for (MappedClass mc : mapper.getMappedClasses()) {
            System.out.println("getMappedClasses: " + mc);
        }
        
        print("mcMap: " + mapper.getMCMap());
        
        print("getOptions: " + mapper.getOptions());
        print("keyToRef: " + mapper.keyToDBRef(mapper.getKey(user)));
        print("refToKey: " + mapper.refToKey(mapper.keyToDBRef(mapper.getKey(user))));
	}
	
	/**
	 * 查询所有
	 */
	private void query() {
		Iterable<User> it = ds.createQuery(User.class).fetch();
		while (it.iterator().hasNext()) {
			print("fetch: " + it.iterator().next());
		}
	}
	
	@Test
	public void testCUD(){
		// 添加测试数据
	    for (int i = 0; i < 50; i++) {
	        User u = new User( i, "test-" + i, ((i % 2 == 0)? true: false), 18 + i, "china-gz#" + i);
	        print(ds.save(u));
	    }
		
//		this.query();
//		Key<User> key = ds.find(User.class,"id",1).getKey();
//		print(key);
		
		//修改操作
//		UpdateOperations<User> us = ds.createUpdateOperations(User.class);
//		us.set("name", "zha").add("address", "dd");
//		int updatedCount = ds.update(ds.find(User.class,"id",1).getKey(), us).getUpdatedCount();
//		print(updatedCount);
	}
	
	@Test
	public void testSave(){
		// 添加数据
		//save object
//	   User user = new User(1L,"zha",true,18,"北京");
//	   Key<User> save = ds.save(user);
//	   print(save);
		//WriteResult delete = ds.delete(ds.createQuery(User.class));
		
		//save list
//		ArrayList<User> users = new ArrayList<User>();
//		users.add(new User(1306907246518L, "zhangsan", true, 22, "china-gz"));
//		User user = new User(System.currentTimeMillis() + 3, "zhaoliu", true, 29, "china-beijin");
//		users.add(user);
//		users.add(new User(System.currentTimeMillis() + 6, "wangwu", true, 24, "china-shanghai"));
//		users.add(new User(System.currentTimeMillis() + 9, "lisi", true, 26, "china-wuhan"));
//		Iterable<Key<User>> save = ds.save(users);
//		print(save);
		
//		WriteResult delete = ds.delete(ds.createQuery(User.class));
//		print(delete);
	}
	
	/**
	 * Find查询操作
	 */
	@Test
	public void testFind(){
		 
		 List<User> asList = ds.find(User.class).asList();
		 print("find: " + asList);
		 List<User> asList2 = ds.find(User.class).field("name").contains("test-1").asList();
		 print(asList2);
		 List<User> asList3 = ds.find(User.class).field("name").endsWith("22").asList();
		 print(asList3);
		 List<User> asList4 = ds.find(User.class).field("name").equalIgnoreCase("CK").asList();
		 print(asList4);
		 print("find-doesNotExist: " + ds.find(User.class).field("name").doesNotExist().asList());
		 print("find-doesNotExist: " + ds.find(User.class).field("name").exists().asList());
		 List<User> asList5 = ds.find(User.class).field("age").greaterThan(30).asList();
		 print(asList5);
		 User user = ds.find(User.class,"id",1).get();
		 print(user);
		 print("find: " + ds.find(User.class, "age", 28).asList());
	}
	
	/**
	 * query查询操作
	 */
	@Test
	public void testQuery(){
		// 查询所有
		List<User> all = ds.createQuery(User.class).asList();
		print("queryAll: " + all);
		print("==========================================================================");
		// 查询主键
		List<Key<User>> asKeyList = ds.createQuery(User.class).asKeyList();
		for (Key<User> key : asKeyList) {
			Object id = key.getId();//key值
			Class<? extends User> type = key.getType();//类型
			String collection = key.getCollection();//聚合集合名
		}
		print("query key: " + asKeyList);
		print("==========================================================================");
		// 结果集数量
		long countAll = ds.createQuery(User.class).countAll();
		print("countAll: " + countAll);
		print("==========================================================================");
		// 抓取查询所有记录（id=49, name=test-49, sex=false, age=67, address=china-gz#49）
		MorphiaIterator<User,User> fetch = ds.createQuery(User.class).fetch();
		while(fetch.hasNext()){
			User next = fetch.next();
			print(next);
		}
		print("==========================================================================");
		// 抓取查询所有记录的id(其他字段为默认值（id=49, name=null, sex=false, age=0, address=null）)
		MorphiaIterator<User,User> emptyEntities = ds.createQuery(User.class).fetchEmptyEntities();
		while(emptyEntities.hasNext()){
			print(emptyEntities.next());
		}
		print("==========================================================================");
		// 抓取所有的key
		MorphiaKeyIterator<User> fetchKeys = ds.createQuery(User.class).fetchKeys();
		while (fetchKeys.hasNext()) {
			 Key<User> next = fetchKeys.next();
			 print("fetchKeys: " + next);
		}
		print("==========================================================================");
		// age > 24
		List<User> asList = ds.createQuery(User.class).filter("age >", 24).asList();
		print("filter age>24: " +asList);
		print("==========================================================================");
		// age in (20, 28)
		print("==========================================================================");
		List<User> asList2 = ds.createQuery(User.class).filter("age in", new int[]{20,28}).asList();
		print("filter age in 20,28: " + asList2);
		print("==========================================================================");
	    List<User> asList3 = ds.createQuery(User.class).field("age").greaterThan(28).asList();
	    print("field age greaterThan 28: "+ asList3);
		print("==========================================================================");
		List<User> asList4 = ds.createQuery(User.class).field("age").greaterThan(20).field("age").lessThan(28).asList();
		print("field 20<age<28: " + asList4);
		print("==========================================================================");
		List<User> asList5 = ds.createQuery(User.class).field("age").greaterThanOrEq(28).field("age").lessThanOrEq(30).asList();
		print("28<=age<=30: "+asList5);
		print("==========================================================================");
		Criteria criteria;
		// limit 3
		List<User> asList6 = ds.createQuery(User.class).limit(3).asList();
		print("limit: " + asList6);
		print("==========================================================================");
		// 分页类似MySQL
		List<User> asList7 = ds.createQuery(User.class).offset(11).limit(5).asList();
		print(asList7);
		print("==========================================================================");
		// order排序，默认asc
		List<User> asList8 = ds.createQuery(User.class).order("age").asList();
		print("order asc: " + asList8);
		print("==========================================================================");
		//desc
		List<User> asList9 = ds.createQuery(User.class).order("-age").asList();
		print("order desc: " + asList9);
		print("==========================================================================");
		// 组合排序 order by age, name
		List<User> asList10 = ds.createQuery(User.class).order("age,name").asList();
		print("组合排序： " + asList10);
		print("query: " + ds.createQuery(User.class).queryNonPrimary().asList());
		print("query: " + ds.createQuery(User.class).queryPrimaryOnly().asList());
		print("==========================================================================");
		//如果include 为true就表示取该属性的值，其他的默认null，反之为false则该属性为null，取其他的值
	    print("query: " + ds.createQuery(User.class).retrievedFields(false, "age","name").asList());
	}
	
	/**
	 * get查询
	 */
	@Test
	public void testGet(){
		User user = new User();
		user.setId(1);
		User user2 = ds.get(user);
		print(user);
		print(user2);
		List<Long> ids = new ArrayList<Long>();
		ids.add(1L);
		ids.add(2L);
		List<User> asList = ds.get(User.class,ids).asList();
		print(asList);
		User user3 = ds.get(User.class,10);
		print(user3);
	}
	
	/**
	 * count查询
	 */
	@Test
	public void testGetCount(){
		User user = new User();
		user.setId(1L);
		print("getCount: " + ds.getCount(user));
		print("getCount: " + ds.getCount(User.class));
		List<Long> ids = new ArrayList<Long>();
	    ids.add(1L);
	    ids.add(2L);
	    print("getCount: " + ds.getCount(ds.get(User.class, ids)));
	    print("getCount: " + ds.getCount(ds.createQuery(User.class).filter("age > ", 22)));
	    print("countAll: " + ds.get(User.class, ids).countAll());
	    print("countAll: " + ds.find(User.class).countAll());
	}
}
