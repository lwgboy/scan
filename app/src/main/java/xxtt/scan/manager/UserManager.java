package xxtt.scan.manager;

import xxtt.scan.model.UserModel;

import java.util.List;

import android.content.Context;
import android.widget.Toast;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

public class UserManager {
    Context context;
    FinalDb db;

    public UserManager(Context context) {
        this.context = context;
        db = FinalDb.create(context);
    }

    public void save(UserModel model) {
        if (db.findById(model.getUserId(), UserModel.class) == null) {
            db.save(model);
        } else {
            db.update(model);
        }
    }

    public UserModel getLoginUser() {
        UserModel model = new UserModel();
        List<UserModel> allModels = db.findAll(UserModel.class);
        if (allModels.size() > 0) {
            model = allModels.get(0);
        }
        return model;
    }

    private AjaxCallBack<String> callBack;

    public AjaxCallBack<String> getCallBack() {
        return callBack;
    }

    public void setCallBack(AjaxCallBack<String> callBackLogin) {
        this.callBack = callBackLogin;
    }

    public void login(String userName, String password, String serviceUrl) {
        if (callBack != null) {
            AjaxParams params = new AjaxParams();
            params.put("username", userName);
            params.put("password", password);

            FinalHttp fh = new FinalHttp();
            String url = Constant.getLoginServiceUrl(context, serviceUrl);
            //Toast.makeText(context, url, 0).show();
            fh.post(url, params, callBack);
        }
    }
}
