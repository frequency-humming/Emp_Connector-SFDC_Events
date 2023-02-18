package Emp_Connector.SFDC_Events;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.chainsaw.Main;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.RandomIvGenerator;
import org.jasypt.properties.EncryptableProperties;
import org.jasypt.util.text.AES256TextEncryptor;

public class ServiceCredential {
	
	static String Url;
	static String Username;
	static String Password;
	static String Event = "/event/User_Event__e";
	static String Replay = "-1";
	static String database;
	
	public static List<String> loginProperties() {
		List<String> lc = new ArrayList<>();
		try {
			String preProp = decrypt(false);
			StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
			encryptor.setPassword(preProp);
			encryptor.setAlgorithm("PBEWithHMACSHA512AndAES_256");
			encryptor.setIvGenerator(new RandomIvGenerator());
			Properties props = new EncryptableProperties(encryptor);
			props.load(Main.class.getClassLoader().getResourceAsStream("application.properties"));
			Url = props.getProperty("salesforce.url");
			Username = props.getProperty("salesforce.username");
			Password = props.getProperty("salesforce.password");
			Collections.addAll(lc,Url,Username,Password,Event,Replay);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lc;
	}
	
	public static String decrypt(boolean db) {
		
		String prop = null;
		try {
			PropertiesConfiguration config = new PropertiesConfiguration();
			config.load(Main.class.getClassLoader().getResourceAsStream("application.properties"));
			if(db) {
				database = config.getString("db.postgres.database");
				String key = config.getString("db.postgres.key");
				return key;
			} else {
				String key = config.getString("salesforce.secret");
				String env = config.getString("salesforce.env.key");
				AES256TextEncryptor textEncryptor = new AES256TextEncryptor();
				textEncryptor.setPassword(key);
				prop = textEncryptor.decrypt(env);
			}
			
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		return prop;
	}
	
	public static List<String> dbConnection() {
		List<String> dblc = new ArrayList<>();
		try {
			String dbkey = decrypt(true);
			StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
			encryptor.setPassword(dbkey);
			encryptor.setAlgorithm("PBEWithHMACSHA512AndAES_256");
			encryptor.setIvGenerator(new RandomIvGenerator());
			Properties props = new EncryptableProperties(encryptor);
			props.load(Main.class.getClassLoader().getResourceAsStream("application.properties"));
			String dbUsername = props.getProperty("db.postgres.username");
			String dbPasword = props.getProperty("db.postgres.password");
			Collections.addAll(dblc,dbUsername,dbPasword,database);
		} catch (IOException e) {
			e.printStackTrace();
		}  
        return dblc;
	}
}