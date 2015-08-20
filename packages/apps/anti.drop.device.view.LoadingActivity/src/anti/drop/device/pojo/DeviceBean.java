package anti.drop.device.pojo;

public class DeviceBean {

	public int _id;
	public String address;// 蓝牙地址
	public String name;// 蓝牙名称
	public int status;// 连接状态[12 (0x0000000c):已经匹配，11 (0x0000000b):匹配正在进行中;10
	public String bell = "铃声1";// 默认铃声

	// (0x0000000a):未被匹配]
	public DeviceBean() {
	}

	public DeviceBean(String address, String name, int status,String bell) {
		this.address = address;
		this.name = name;
		this.status = status;
		this.bell = bell;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getBell() {
		return bell;
	}

	public void setBell(String bell) {
		this.bell = bell;
	}

}
