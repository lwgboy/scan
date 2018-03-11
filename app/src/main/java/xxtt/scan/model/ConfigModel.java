package xxtt.scan.model;

import net.tsz.afinal.annotation.sqlite.Id;

public class ConfigModel {
    String address;

    @Id
    int id;

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public String getAddress() {
	return address;
    }

    public void setAddress(String address) {
	this.address = address;
    }
}
