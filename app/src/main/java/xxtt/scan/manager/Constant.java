package xxtt.scan.manager;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.UUID;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import xxtt.scan.model.UserModel;

public class Constant {
    // public static final boolean DEBUG = true;
    public static final boolean DEBUG = false;
    public static final String IsSelectPole = "IsSelectPole";
    public static final String DATABASE_NAME = "GoodManagerSystem.db";
    public static final int DATABASE_VERSION = 1;
    public static final String LOG_TAGNAME = "gmsDbLogs";
    public static final int KSize = 1024;
    public static final int MSize = KSize * 1024;
    public static final int GSize = MSize * 1024;
    public static final NumberFormat formatter2 = new DecimalFormat("0.##");

    public static UserModel User;

    public static UserModel getLoginUser(Context context) {
        if (User == null) {
            UserManager userManager = new UserManager(context);
            User = userManager.getLoginUser();
        }
        return User;
    }

    public static boolean checkLogined(Context context) {
        if (User == null) {
            UserManager userManager = new UserManager(context);
            User = userManager.getLoginUser();
        }
        return Constant.User.getUserId() > 0;
    }

    /**
     * 取ＧＵＩＤ，32位长度
     */
    public static String getGuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static void setChildEnabledFalse(LinearLayout v) {
        View c;
        for (int i = 0; i < v.getChildCount(); i++) {
            c = v.getChildAt(i);
            if (c instanceof LinearLayout) {
                setChildEnabledFalse((LinearLayout) c);
            } else {
                c.setEnabled(false);
            }
        }
    }

    public static String getFileSizeStr(int bytesLength) {
        double temp = bytesLength;
        String strType;
        if (bytesLength > GSize) {
            temp = temp / GSize;
            strType = "G";
        } else if (bytesLength > MSize) {
            temp = temp / MSize;
            strType = "M";
        } else {
            temp = temp / KSize;
            strType = "K";
        }
        return formatter2.format(temp) + strType;
    }

    private static String ServiceUrl = null;// "http://182.92.99.34/cjpower/RwMgrService.asmx/";
    private static String LoginName = null;
    private static String LoginPassword = null;
    private static String DefaultUrl = "192.168.0.102:1000";

    public static final String ServiceUrlKEY = "ServiceUrl";
    public static final String LoginNameKEY = "LoginName";
    public static final String LoginPasswordKEY = "LoginPassword";

    public static final String LoginMethodUrl = "/AndroidServices/SysUser.asmx/Login";
    public static final String GetStoreMethodUrl = "/AndroidServices/GmsStore.asmx/GetStores";
    public static final String SaveAllotOrderMethodUrl = "/AndroidServices/AllotOrder.asmx/SaveOrder";
    public static final String SaveDistOrderMethodUrl = "/AndroidServices/DistOrder.asmx/SaveOrder";
    public static final String SaveCountOrderMethodUrl = "/AndroidServices/CountOrder.asmx/SaveOrder";
    public static final String GetMateNameMethodUrl = "/AndroidServices/Mate.asmx/GetName";
    public static final String GetMatesMethodUrl = "/AndroidServices/Mate.asmx/GetMates";

//    public static final String LoginMethodUrl = "/api/SysUser/Login";
//    public static final String GetStoreMethodUrl = "/api/GmsStore/GetStores";
//    public static final String SaveAllotOrderMethodUrl = "/api/AllotOrder/SaveOrder";
//    public static final String SaveDistOrderMethodUrl = "/api/DistOrder/SaveOrder";
//    public static final String SaveCountOrderMethodUrl = "/api/CountOrder/SaveOrder";

    public static String getServiceUrl(Context context) {
        if (ServiceUrl == null) {
            KeyValueManager m = new KeyValueManager(context);
            ServiceUrl = m.getValueByKey(ServiceUrlKEY);

            if (ServiceUrl == null) {
                // Toast.makeText(context, "系统没有配置服务地址，请打开登录界面设置主服务地址",
                // Toast.LENGTH_LONG).show();
            }
        }
        return ServiceUrl == null ? DefaultUrl : ServiceUrl;
    }

    public static String getLoginName(Context context) {
        if (LoginName == null) {
            KeyValueManager m = new KeyValueManager(context);
            LoginName = m.getValueByKey(LoginNameKEY);

            if (LoginName == null) {
                // Toast.makeText(context, "系统没有配置服务地址，请打开登录界面设置主服务地址",
                // Toast.LENGTH_LONG).show();
            }
        }
        return LoginName == null ? "xxadmin" : LoginName;
    }

    public static String getLoginPassword(Context context) {
        if (LoginPassword == null) {
            KeyValueManager m = new KeyValueManager(context);
            LoginPassword = m.getValueByKey(LoginPasswordKEY);

            if (LoginPassword == null) {
                // Toast.makeText(context, "系统没有配置服务地址，请打开登录界面设置主服务地址",
                // Toast.LENGTH_LONG).show();
            }
        }
        return LoginPassword == null ? "" : LoginPassword;
    }

    public static String getLoginServiceUrl(Context context) {
        String url = getServiceUrl(context);
        url = "http://" + url + LoginMethodUrl;
        return url;
    }

    public static String getLoginServiceUrl(Context context,
                                            String serviceUrl) {
        return "http://" + serviceUrl + LoginMethodUrl;
    }

    public static String getStoreServiceUrl(Context context) {
        String url = getServiceUrl(context);
        url = "http://" + url + GetStoreMethodUrl;
        return url;
    }

    public static String saveAllotOrderServiceUrl(Context context) {
        String url = getServiceUrl(context);
        url = "http://" + url + SaveAllotOrderMethodUrl;
        return url;
    }

    public static String saveDistOrderServiceUrl(Context context) {
        String url = getServiceUrl(context);
        url = "http://" + url + SaveDistOrderMethodUrl;
        return url;
    }

    public static String saveCountOrderServiceUrl(Context context) {
        String url = getServiceUrl(context);
        url = "http://" + url + SaveCountOrderMethodUrl;
        return url;
    }

    public static  String getMateNameServiceUrl(Context context){
        String url = getServiceUrl(context);
        url = "http://" + url + GetMateNameMethodUrl;
        return url;
    }

    public static  String getMatesServiceUrl(Context context){
        String url = getServiceUrl(context);
        url = "http://" + url + GetMatesMethodUrl;
        return url;
    }
}
