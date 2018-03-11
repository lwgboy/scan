package xxtt.scan.manager;

import java.util.List;

import android.content.Context;

import net.tsz.afinal.FinalDb;

import xxtt.scan.model.ConfigModel;

public class ConfigManager {
    Context context;
    FinalDb db;

    public ConfigManager(Context context) {
        this.context = context;
        db = FinalDb.create(context);
    }

    public void save(ConfigModel model) {
        if (db.findById(model.getId(), ConfigModel.class) == null) {
            db.save(model);
        } else {
            db.update(model);
        }
    }

    public ConfigModel getConfigModel(int id) {
        List<ConfigModel> list = db.findAllByWhere(ConfigModel.class, "id=0");
        return list.size() > 0 ? list.get(0) : new ConfigModel();
    }

    public void delete(int id) {
        db.deleteById(ConfigModel.class, id);
    }

    public void delete(ConfigModel model) {
        db.delete(model);
    }
}
