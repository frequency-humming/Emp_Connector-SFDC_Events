package Emp_Connector.SFDC_Events;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
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
	
	public static List<String> loginProperties() {
		List<String> lc = new ArrayList<>();
		try {
			String preProp = decrypt();
			StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
			encryptor.setPassword(preProp);
			encryptor.setAlgorithm("PBEWithHMACSHA512AndAES_256");
			encryptor.setIvGenerator(new RandomIvGenerator());
			Properties props = new EncryptableProperties(encryptor);
			props.load(new FileInputStream("resources/application.properties"));
			Url = props.getProperty("salesforce.url");
			Username = props.getProperty("salesforce.username");
			Password = props.getProperty("salesforce.password");
			Collections.addAll(lc,Url,Username,Password,Event,Replay);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lc;
	}
	
	public static String decrypt() {
		
		String prop = null;
		try {
			PropertiesConfiguration config = new PropertiesConfiguration();
			config.load("resources/application.properties");
			String key = config.getString("salesforce.secret");
			String env = config.getString("salesforce.env.key");
			AES256TextEncryptor textEncryptor = new AES256TextEncryptor();
			textEncryptor.setPassword(key);
			prop = textEncryptor.decrypt(env);
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		return prop;
	}

}
