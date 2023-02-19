package Emp_Connector.SFDC_Events;

import java.util.HashMap;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;



public class DatabaseService {

	
	public void dataProcess(String key,Object val) {
			
			HashMap<String,String> map = new HashMap<String,String>();
			if(key.contains("payload")) {
				((HashMap<String,String>) val).forEach((k,v)->map.put(k,v));
				map.forEach((k,v) -> System.out.println("key : "+k+" value : "+v));
			}
			if(!map.isEmpty()) {
				getCurrentSession(map);
			}
			
	}
	
	public void getCurrentSession(HashMap<String,String> map) {
		
		List<String> dbproperties = ServiceCredential.dbConnection();
		
		Configuration configuration = new Configuration();
		configuration.setProperty("connection.driver_class", "org.postgresql.Driver");
		configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
		configuration.setProperty("hibernate.connection.url",dbproperties.get(2));
		configuration.setProperty("hibernate.connection.username",dbproperties.get(0));
		configuration.setProperty("hibernate.connection.password",dbproperties.get(1));
		configuration.setProperty("hibernate.current_session_context_class", "thread");
		configuration.setProperty("hibernate.connection.pool_size","1");
		configuration.setProperty("hibernate.show_sql", "true");
		configuration.setProperty("hibernate.format_sql", "true");
		configuration.addAnnotatedClass(GuestUser.class);
		SessionFactory sessionFactory = configuration.buildSessionFactory();
		Session session = sessionFactory.getCurrentSession();
		Transaction tx = session.beginTransaction();
		GuestUser user = new GuestUser(map.get("App__c"),map.get("UserIP__c"),map.get("Image_Type__c"),map.get("LoginType__c"),map.get("CreatedById"));
		session.persist(user);
		tx.commit();
		session.close();
		sessionFactory.close();	
		}

}
