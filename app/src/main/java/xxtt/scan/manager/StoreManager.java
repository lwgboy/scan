package xxtt.scan.manager;

import java.util.List;

import android.content.Context;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import xxtt.scan.model.StoreModel;

public class StoreManager {
    Context context;
    FinalDb db;

    public StoreManager(Context context) {
        this.context = context;
        db = FinalDb.create(context);
    }

    public void initStores() {
        clear();
        int[] integers = {1, 2, 3, 4, 5, 6, 7, 8};
        for (int j = 0; j < integers.length; j++) {
            int i = integers[j];
            save(new StoreModel(i, "仓库" + i + ""));
        }
    }

    public void clear() {
        db.deleteAll(StoreModel.class);
    }

    public void save(StoreModel model) {
        if (db.findById(model.getId(), StoreModel.class) == null) {
            db.save(model);
        } else {
            db.update(model);
        }
    }

    public void save(List<StoreModel> models) {
        for (StoreModel model : models) {
            save(model);
        }
    }

    private AjaxCallBack<String> callBack;

    public AjaxCallBack<String> getCallBack() {
        return callBack;
    }

    public void setCallBack(AjaxCallBack<String> callBack) {
        this.callBack = callBack;
    }

    public void getStores(int orgid) {
        if (callBack != null) {
            AjaxParams params = new AjaxParams();
            params.put("orgid", orgid + "");

            FinalHttp fh = new FinalHttp();
            fh.post(Constant.getStoreServiceUrl(context), params, callBack);
        }
    }

    public List<StoreModel> getStores() {
        return db.findAll(StoreModel.class);
    }
}
