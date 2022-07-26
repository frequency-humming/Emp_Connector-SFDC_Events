package Emp_Connector.SFDC_Events;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="`GuestUser`",schema="public")
public class GuestUser {
	
	@Id
    @Column(name="`Id`")
    private String Id;
	
	@Column(name="`App`")
	private String App;
	
	@Column(name="`User__c`")
	private String User__c;
	
	@Column(name="`Image_Type`")
	private String Image_Type;
	
	@Column(name="`Login_Type`")
	private String Login_Type;
	
	public GuestUser(String app,String user,String image,String login,String id) {
		this.App = app;
		this.User__c = user;
		this.Image_Type = image;
		this.Login_Type = login;
		this.Id = id;
	}

}
