package domain;

public class User {

	private String id;
	private String password;
	private String type;
	private String photoRoute;
	
	public User(String id, String password, String type, String photoRoute) {
		super();
		this.id = id;
		this.password = password;
		this.type = type;
		this.photoRoute = photoRoute;
	}

	public User(String id, String password, String type) {
		super();
		this.id = id;
		this.password = password;
		this.type = type;
		//this.photoRoute = "";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPhotoRoute() {
		return photoRoute;
	}

	public void setPhotoRoute(String photoRoute) {
		this.photoRoute = photoRoute;
	}
	
	
}
