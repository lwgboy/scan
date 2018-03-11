package xxtt.scan.manager;

import xxtt.scan.model.KeyValueModel;

import android.content.Context;

import net.tsz.afinal.FinalDb;

public class KeyValueManager {
    FinalDb db;

    public KeyValueManager(Context context) {
        db = FinalDb.create(context);
    }

    public void save(String key, String value) {
        save(new KeyValueModel(key, value));
    }

    public void saveServiceUrl(String value) {
        save(new KeyValueModel(Constant.ServiceUrlKEY, value));
    }

    public void saveLoginName(String value) {
        save(new KeyValueModel(Constant.LoginNameKEY, value));
    }

    public void saveLoginPassword(String value) {
        save(new KeyValueModel(Constant.LoginPasswordKEY, value));
    }

    public void save(KeyValueModel model) {
        KeyValueModel temp = db.findById(model.getKey(), KeyValueModel.class);
        if (temp == null)
            db.save(model);
        else
            db.update(model);
    }

    public String getValueByKey(String key) {
        KeyValueModel temp = db.findById(key, KeyValueModel.class);
        if (temp == null)
            return null;
        else
            return temp.getValue();
    }
}
