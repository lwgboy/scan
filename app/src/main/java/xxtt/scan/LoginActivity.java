package xxtt.scan;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.http.AjaxCallBack;

import xxtt.scan.manager.Constant;
import xxtt.scan.R;
import xxtt.scan.lib.Utils;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import xxtt.scan.manager.KeyValueManager;
import xxtt.scan.manager.MateManager;
import xxtt.scan.manager.StoreManager;
import xxtt.scan.manager.UserManager;
import xxtt.scan.model.MateModel;
import xxtt.scan.model.StoreModel;
import xxtt.scan.model.UserModel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

public class LoginActivity extends Activity implements View.OnClickListener {
    private static final int loginCode = 212002;
    Boolean isLogining = false;

    String userName;
    String password;
    String address;

    EditText etAddress;
    EditText etUserName;
    EditText etPassword;
    Button btnLogin;
    Button btnExit;

    ProgressBar pbWidget;

    xxtt.scan.lib.Utils utils;

    FinalDb db;
    UserManager userManager;
    StoreManager storeManager;
    MateManager mateManager;

    KeyValueManager keyValueManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        keyValueManager = new KeyValueManager(this);
        userManager = new UserManager(this);
        storeManager = new StoreManager(this);
        mateManager = new MateManager(this);

        userManager.setCallBack(callBackLogin);
        storeManager.setCallBack(callBack);
        mateManager.setCallBack(callBackMate);

        etAddress = (EditText) this.findViewById(R.id.etAddress);
        etUserName = (EditText) this.findViewById(R.id.etUserName);
        etPassword = (EditText) this.findViewById(R.id.etPassword);
        btnLogin = (Button) this.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
        btnExit = (Button) this.findViewById(R.id.btnExit);
        btnExit.setOnClickListener(this);

        pbWidget = (ProgressBar) this.findViewById(R.id.pbWidget);
        pbWidget.setVisibility(View.INVISIBLE);

        utils = new xxtt.scan.lib.Utils(this);
        db = FinalDb.create(this);

        etAddress.setText(Constant.getServiceUrl(this));
        etUserName.setText(Constant.getLoginUser(this).getUsername());

        //todo：设置默认账号与密码
        etUserName.setText(Constant.getLoginName(this));
        etPassword.setText(Constant.getLoginPassword(this));

        etAddress.setSelection(etAddress.getText().length());// 将光标移至文字末尾
    }

    AjaxCallBack<String> callBackLogin = new AjaxCallBack<String>() {
        @Override
        public void onLoading(long count, long current) {
            // utils.toastShow(current + "/" + count);
        }

        @Override
        public void onSuccess(String t) {
            super.onSuccess(t);
            try {
                // utils.toastShow(t);
                JSONObject json = new JSONObject(t);
                if (json.isNull("responseText")) {
                    switch (json.getInt("ResultInt")) {
                        case 0: // 登录成功----保存用户信息
                            JSONObject resultUser = json
                                    .getJSONObject("ResultUser");
                            UserModel userModel = new UserModel();
                            int orgid = -1;
                            if (resultUser.getString("DId").length() > 0) {
                                orgid = resultUser.getInt("DId");
                            }
                            userModel.setUserId(resultUser.getInt("UserId"));
                            userModel.setUsername(userName);
                            userModel.setRealName(resultUser.getString("RealName"));
                            userManager.save(userModel);
                            Constant.User = userModel;
                            // 保存服务地址
                            keyValueManager.saveServiceUrl(address);
                            //todo:2018-03-03 保存登录账号
                            keyValueManager.saveLoginName(userName);
                            //todo:2018-03-03 保存登录密码
                            keyValueManager.saveLoginPassword(password);
                            // 获取仓库列表
                            storeManager.getStores(orgid);
                            //todo:2018-03-04 朱书彦 获取材料列表
                            //mateManager.getMates(orgid);
                            //gotoMainView();
                            break;
                        default:
                            hideProgressBar();
                            utils.toastShow("帐号或密码错误，请修改后重试");
                            break;
                    }
                } else {
                    hideProgressBar();
                    utils.toastShow("帐号或密码错误，请修改后重试");
                }
            } catch (JSONException e) {
                hideProgressBar();
                e.printStackTrace();
                utils.toastShow("帐号或密码错误，请修改后重试");
            }
        }

        @Override
        public void onFailure(Throwable t, int errorNo, String strMsg) {
            super.onFailure(t, errorNo, strMsg);
            utils.toastShow("系统出现异常：" + t.getMessage());
            hideProgressBar();
        }
    };

    void showProgressBar() {
        pbWidget.setVisibility(View.VISIBLE);
    }

    void hideProgressBar() {
        pbWidget.setVisibility(View.GONE);
        isLogining = false;
    }

    AjaxCallBack<String> callBack = new AjaxCallBack<String>() {
        @Override
        public void onLoading(long count, long current) {
            // textView.setText(current + "/" + count);
        }

        @Override
        public void onSuccess(String t) {
            super.onSuccess(t);
            try {
                JSONArray jsonArray = new JSONArray(t);
                if (jsonArray.length() <= 0) {
                    hideProgressBar();
                    utils.toastShow("未有获取到仓库信息，请稍后重试");
                } else {
                    // 首先清除仓库信息
                    storeManager.clear();
                    // 循环入库
                    StoreModel model;
                    JSONObject item;
                    List<StoreModel> models = new ArrayList<StoreModel>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        item = jsonArray.getJSONObject(i);
                        model = new StoreModel();
                        model.setId(item.getInt("StoreId"));
                        model.setStoreId(item.getInt("StoreId"));
                        model.setStoreName(item.getString("StoreName"));
                        // utils.toastShow(item.getString("Id"));
                        models.add(model);
                    }
                    storeManager.save(models);
                    hideProgressBar();
                }
                // 进入到主页面
                gotoMainView();
            } catch (JSONException e) {
                hideProgressBar();
                e.printStackTrace();
                utils.toastShowErrofInfo("获取仓库信息失败，请稍后重试");
            }
        }

        @Override
        public void onFailure(Throwable t, int errorNo, String strMsg) {
            super.onFailure(t, errorNo, strMsg);
            hideProgressBar();
            // utils.toastShowErrofInfo(strMsg);
            utils.toastShowErrofInfo("获取仓库信息失败，请稍后重试");
        }
    };


    AjaxCallBack<String> callBackMate = new AjaxCallBack<String>() {
        @Override
        public void onLoading(long count, long current) {
            // textView.setText(current + "/" + count);
        }

        @Override
        public void onSuccess(String t) {
            super.onSuccess(t);
            try {
                JSONArray jsonArray = new JSONArray(t);
                if (jsonArray.length() <= 0) {
                    hideProgressBar();
                    utils.toastShow("未有获取到信息，请稍后重试");
                } else {
                    // 首先清除信息
                    mateManager.clear();
                    // 循环入库
                    MateModel model;
                    JSONObject item;
                    List<MateModel> models = new ArrayList<MateModel>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        item = jsonArray.getJSONObject(i);
                        model = new MateModel();
                        model.setId(item.getInt("Id"));
                        model.setCode(item.getString("Code"));
                        model.setName(item.getString("Name"));
                        // utils.toastShow(item.getString("Id"));
                        models.add(model);
                    }
                    mateManager.save(models);
                    hideProgressBar();
                }
                // 进入到主页面
                gotoMainView();
            } catch (JSONException e) {
                hideProgressBar();
                e.printStackTrace();
                utils.toastShowErrofInfo("获取信息失败，请稍后重试");
            }
        }

        @Override
        public void onFailure(Throwable t, int errorNo, String strMsg) {
            super.onFailure(t, errorNo, strMsg);
            hideProgressBar();
            // utils.toastShowErrofInfo(strMsg);
            utils.toastShowErrofInfo("获取信息失败，请稍后重试");
        }
    };

    private void gotoMainView() {
        // 进入到主页面
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("UserName", userName);
        startActivity(intent);
        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("DefaultLocale")
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnExit:
                Utils.exitApp(this);
                break;
            case R.id.btnLogin:
                if (isLogining == false) {
                    isLogining = true;

                    etAddress.clearFocus();
                    etUserName.clearFocus();
                    etPassword.clearFocus();
                    address = etAddress.getText().toString().trim();
                    userName = etUserName.getText().toString().trim();
                    password = etPassword.getText().toString();

                    if (address.length() == 0) {
                        etAddress.requestFocus();
                        utils.toastShow("提示：服务地址不能为空");
                        isLogining = false;
                        return;
                    }
                    if (userName.length() == 0) {
                        etUserName.requestFocus();
                        utils.toastShow("提示：登录账号不允许为空");
                        isLogining = false;
                        return;
                    }
                    if (password.length() == 0) {
                        etPassword.requestFocus();
                        utils.toastShow(this, "提示：登录密码不允许为空");
                        isLogining = false;
                        return;
                    }

                    showProgressBar();
                    userManager.login(userName, password, address);
                    // storeManager.initStores();
                    // gotoMainView();
                } else {
                    utils.toastShow("正在登录中，请稍后...");
                }
                break;
        }
    }
}
