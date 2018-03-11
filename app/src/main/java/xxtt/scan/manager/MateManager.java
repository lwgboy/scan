package xxtt.scan.manager;

import android.content.Context;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import java.util.List;

import xxtt.scan.model.EPCModel;
import xxtt.scan.model.MateModel;
import xxtt.scan.model.StoreModel;

/**
 * Created by zhushuyan on 2018/3/3.
 */

public class MateManager {
    Context context;
    FinalDb db;

    public MateManager(Context context) {
        this.context = context;
        db = FinalDb.create(context);
    }

    private AjaxCallBack<String> callBack;

    public AjaxCallBack<String> getCallBack() {
        return callBack;
    }

    public void setCallBack(AjaxCallBack<String> callBack) {
        this.callBack = callBack;
    }

    public void getMateName(String epc) {
        if (callBack != null) {
            AjaxParams params = new AjaxParams();
            params.put("code", epc + "");

            FinalHttp fh = new FinalHttp();
            fh.post(Constant.getMateNameServiceUrl(context), params, callBack);
        }
    }

    public String getName(String epc) {
        List<MateModel> findes = db.findAllByWhere(MateModel.class, "Code = '" + epc + "'");
        if (findes.size() >0 ){
            return findes.get(0).getName();
        }
        return "";
    }

    public void clear() {
        db.deleteAll(MateModel.class);
    }

    public void initMates() {
        clear();
    }

    public void getMates(int orgId) {
        if (callBack != null) {
            AjaxParams params = new AjaxParams();
            FinalHttp fh = new FinalHttp();

            fh.post(Constant.getMatesServiceUrl(context), params, callBack);
        }
    }


    public List<MateModel> getMates() {
        return db.findAll(MateModel.class);
    }

    public void save(List<MateModel> models) {
        for (MateModel model : models) {
            save(model);
        }
    }

    public void save(MateModel model) {
        if (db.findById(model.getId(), MateModel.class) == null) {
            db.save(model);
        } else {
            db.update(model);
        }
    }

}
