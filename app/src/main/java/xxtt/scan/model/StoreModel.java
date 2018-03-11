package xxtt.scan.model;

import net.tsz.afinal.annotation.sqlite.Id;

public class StoreModel {

    public StoreModel() {
    }

    public StoreModel(int storeId, String storeName) {
        this.storeId = storeId;
        this.storeName = storeName;
    }

    int storeId;

    @Id
    int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    String storeName;

}
