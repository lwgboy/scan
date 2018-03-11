package xxtt.scan.manager;

import java.util.List;

import android.content.Context;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import xxtt.scan.model.EPCModel;
import xxtt.scan.model.StoreModel;

public class OrderManager {
    Context context;
    FinalDb db;

    public OrderManager(Context context) {
        this.context = context;
        db = FinalDb.create(context);
    }

    public void clear() {
        db.deleteAll(EPCModel.class);
    }

    public void save(EPCModel model) {
        if (db.findById(model.getId(), EPCModel.class) == null) {
            db.save(model);
        } else {
            db.update(model);
        }
    }

    private AjaxCallBack<String> callBack;

    public AjaxCallBack<String> getCallBack() {
        return callBack;
    }

    public void setCallBack(AjaxCallBack<String> callBack) {
        this.callBack = callBack;
    }

    public void saveDistOrder(int userid, int storeid, List<EPCModel> list) {
        if (callBack != null) {
            AjaxParams params = new AjaxParams();
            params.put("userid", userid + "");
            params.put("storeid", storeid + "");

            String barcodes = "", numbers = "";
            for (EPCModel model : list) {
                barcodes = barcodes.length() == 0 ? model.getEpc()
                        : barcodes + "," + model.getEpc();
                numbers = numbers.length() == 0 ? model.getCount() + ""
                        : numbers + "," + model.getCount() + "";
            }
            params.put("barcodeary", barcodes);
            params.put("numberary", numbers);

            FinalHttp fh = new FinalHttp();
            fh.post(Constant.saveDistOrderServiceUrl(context), params,
                    callBack);
        }
    }

    public void saveAllotOrder(int userid, int storeid, List<EPCModel> list) {
        if (callBack != null) {
            AjaxParams params = new AjaxParams();
            params.put("userid", userid + "");
            params.put("storeid", storeid + "");

            String barcodes = "", numbers = "";
            for (EPCModel model : list) {
                barcodes = barcodes.length() == 0 ? model.getEpc()
                        : barcodes + "," + model.getEpc();
                numbers = numbers.length() == 0 ? model.getCount() + ""
                        : numbers + "," + model.getCount() + "";
            }
            params.put("barcodeary", barcodes);
            params.put("numberary", numbers);

            FinalHttp fh = new FinalHttp();
            fh.post(Constant.saveAllotOrderServiceUrl(context), params,
                    callBack);
        }
    }

    public void saveCountOrder(int userid, int storeid, List<EPCModel> list) {
        if (callBack != null) {
            AjaxParams params = new AjaxParams();
            params.put("userid", userid + "");
            params.put("storeid", storeid + "");

            String barcodes = "", numbers = "";
            for (EPCModel model : list) {
                barcodes = barcodes.length() == 0 ? model.getEpc()
                        : barcodes + "," + model.getEpc();
                numbers = numbers.length() == 0 ? model.getCount() + ""
                        : numbers + "," + model.getCount() + "";
            }
            params.put("barcodeary", barcodes);
            params.put("numberary", numbers);

            FinalHttp fh = new FinalHttp();
            fh.post(Constant.saveCountOrderServiceUrl(context), params, callBack);
        }
    }
}
