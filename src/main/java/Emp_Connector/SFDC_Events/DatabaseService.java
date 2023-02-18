package Emp_Connector.SFDC_Events;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;



public class DatabaseService {
	
	
	public void dataProcess(String key,Object val) {
			
			HashMap<String,String> map = new HashMap<String,String>();
			if(key.contains("payload")) {
				((HashMap<String,String>) val).forEach((k,v)->map.put(k,v));
				map.forEach((k,v) -> System.out.println("key : "+k+" value : "+v));
			}
			if(!map.isEmpty()) {
				dbTransaction(map);
			}
			
	}
	
	public void dbTransaction(HashMap<String,String> map) {
		
		System.out.println("in dbc");	
		try {
			Session session = getCurrentSession();
			session.beginTransaction();
			GuestUser user = new GuestUser(map.get("App__c"),map.get("UserIP__c"),map.get("Image_Type__c"),map.get("LoginType__c"),map.get("CreatedById"));
			session.persist(user);
			session.getTransaction().commit();
			session.close();
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	public static Session getCurrentSession() {
		List<String> dbproperties = ServiceCredential.dbConnection();
		Map<String, String> settings = new HashMap<>();
		settings.put("connection.driver_class", "org.postgresql.Driver");
		settings.put("dialect", "org.hibernate.dialect.PostgreSQLDialect");
		settings.put("hibernate.connection.url",dbproperties.get(2));
		settings.put("hibernate.connection.username",dbproperties.get(0));
		settings.put("hibernate.connection.password", dbproperties.get(1));
		settings.put("hibernate.current_session_context_class", "thread");
		settings.put("hibernate.show_sql", "true");
		settings.put("hibernate.format_sql", "true");

		ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
		                                    .applySettings(settings).build();

		MetadataSources metadataSources = new MetadataSources(serviceRegistry);
		metadataSources.addAnnotatedClass(GuestUser.class);
		Metadata metadata = metadataSources.buildMetadata();
		SessionFactory sessionFactory = metadata.getSessionFactoryBuilder().build();
		Session session = sessionFactory.openSession();
		
		return session;
		
		}

}
