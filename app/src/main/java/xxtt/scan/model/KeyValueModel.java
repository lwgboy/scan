package xxtt.scan.model;

import net.tsz.afinal.annotation.sqlite.Id;

public class KeyValueModel {

    @Id
    private String key;
    private String value;

    public KeyValueModel() {
    }

    public KeyValueModel(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
