package anti.drop.device.pojo;

public class TapeBean {

	String name;
	String path;
	String duration;// Ê±³¤
	

	public TapeBean(String name, String path, String duration) {
		this.name = name;
		this.path = path;
		this.duration = duration;
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

}
