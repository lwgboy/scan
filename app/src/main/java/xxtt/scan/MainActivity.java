package xxtt.scan;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.hdhe.uhf.reader.Tools;
import com.android.hdhe.uhf.reader.UhfReader;

import xxtt.scan.manager.MateManager;
import xxtt.scan.model.EPCModel;
import xxtt.scan.MoreHandleActivity;
import xxtt.scan.ScreenStateReceiver;
import xxtt.scan.SettingPower;
import xxtt.scan.lib.Utils;
import xxtt.scan.MainActivity.InventoryThread;

import xxtt.scan.MainActivity;
import xxtt.scan.model.MateModel;
import xxtt.scan.service.ScanService;

import android.R.array;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton.OnCheckedChangeListener;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.http.AjaxCallBack;

import xxtt.scan.manager.Constant;
import xxtt.scan.manager.KeyValueManager;
import xxtt.scan.manager.OrderManager;
import xxtt.scan.manager.StoreManager;
import xxtt.scan.manager.UserManager;
import xxtt.scan.model.GoodModel;
import xxtt.scan.model.StoreModel;
import xxtt.scan.model.UserModel;
import xxtt.scan.R;

public class MainActivity extends Activity implements OnClickListener,
        OnItemClickListener {

    private Button buttonClear;
    private Button buttonConnect;
    private Button buttonStart;
    private TextView textVersion;
    private ListView listViewData;
    private ListEditorAdapter mAdapter;
    private ArrayList<Map<String, Object>> listMap;
    private boolean runFlag = true;
    private boolean startFlag = false;
    private boolean connectFlag = false;
    private UhfReader reader; // 超高频读写器

    ProgressBar pbWidget;

    private ScreenStateReceiver screenReceiver;

    private StoreManager storeManager;
    List<StoreModel> allStores;
    private ArrayList<String> listStoreName = new ArrayList<String>();
    // private ArrayList<int> listStoreId=new ArrayList<int>();
    PickDialog pickDialog;
    private OrderManager orderManager;
    MateManager mateManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setOverflowShowingAlways();
        setContentView(R.layout.main);

        pbWidget = (ProgressBar) this.findViewById(R.id.pbWidget);
        pbWidget.setVisibility(View.INVISIBLE);
        utils = new Utils(this);
        orderManager = new OrderManager(this);

        mateManager = new MateManager(this);
        mateManager.setCallBack(callBackMate);

        initStore();
        initView();

        List<EPCModel> list = new ArrayList<EPCModel>();
        mAdapter = new ListEditorAdapter(MainActivity.this, list);
        listViewData.setAdapter(mAdapter);

        // 获取读写器实例，若返回Null,则串口初始化失败
        reader = UhfReader.getInstance();
        if (reader == null) {
            textVersion.setText("serialport init fail");
            setButtonClickable(buttonClear, false);
            setButtonClickable(buttonStart, false);
            setButtonClickable(buttonConnect, false);
            return;
        }
        try {
            int value = getSharedValue();
            if (value < 16) value = 16;
            if (value > 26) value = 26;

            if (reader.setOutputPower(value)) {
                Toast.makeText(getApplicationContext(), "设置成功", 0).show();
            } else {
                utils.toastShow("设置参数：" + String.valueOf(value));
            }

            Thread.sleep(100);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // 获取用户设置功率,并设置
        SharedPreferences shared = getSharedPreferences("power", 0);
        int value = shared.getInt("value", 26);
        Log.e("", "value" + value);
        reader.setOutputPower(value);

        // 添加广播，默认屏灭时休眠，屏亮时唤醒
        screenReceiver = new ScreenStateReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(screenReceiver, filter);

        /**************************/

        // String serialNum = "";
        // try {
        // Class<?> classZ = Class.forName("android.os.SystemProperties");
        // Method get = classZ.getMethod("get", String.class);
        // serialNum = (String) get.invoke(classZ, "ro.serialno");
        // } catch (Exception e) {
        // }
        // Log.e("serialNum", serialNum);

        /*************************/

        Thread thread = new InventoryThread();
        thread.start();
        // 初始化声音池
        Utils.initSoundPool(this);
    }

    private void initStore() {
        storeManager = new StoreManager(this);
        allStores = storeManager.getStores();
        for (int i = 0; i < allStores.size(); i++) {
            StoreModel model = allStores.get(i);
            // utils.toastShow(model.getStoreId()+"");
            listStoreName.add(model.getStoreName());
        }
    }

    private void initView() {
        buttonStart = (Button) findViewById(R.id.button_start);
        buttonConnect = (Button) findViewById(R.id.button_connect);
        buttonClear = (Button) findViewById(R.id.button_clear);
        listViewData = (ListView) findViewById(R.id.listView_data);
        textVersion = (TextView) findViewById(R.id.textView_version);
        buttonStart.setOnClickListener(this);
        buttonConnect.setOnClickListener(this);
        buttonClear.setOnClickListener(this);
        setButtonClickable(buttonStart, false);
        listViewData.setOnItemClickListener(this);
        listMap = new ArrayList<Map<String, Object>>();
    }

    @Override
    protected void onPause() {
        startFlag = false;
        super.onPause();
    }

    /**
     * 盘存线程
     *
     * @author Administrator
     */
    class InventoryThread extends Thread {
        private List<byte[]> epcList;

        @Override
        public void run() {
            super.run();
            while (runFlag) {
                if (startFlag) {
                    // reader.stopInventoryMulti()
                    epcList = reader.inventoryRealTime(); // 实时盘存
                    if (epcList != null && !epcList.isEmpty()) {
                        // 播放提示音
                        Utils.play(1, 0);
                        for (byte[] epc : epcList) {
                            epcCode = Tools.Bytes2HexString(epc, epc.length);
                            //mAdapter.addEpc(epcStr);
                            Message msg = new Message();
                            //给message对象赋值
                            msg.what = 1;
                            //发送message值给Handler接收
                            mHandler.sendMessage(msg);
                        }
                    }
                    epcList = null;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private String epcCode = "";
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            // 更新UI
            switch (msg.what) {
                case 1://addItem
                    if (msg != null) {
                        mAdapter.addEpc(epcCode);
                    }
                    break;
                case 2://clear
                    mAdapter.clear();
                    break;
            }
        }
    };

    // 设置按钮是否可用
    private void setButtonClickable(Button button, boolean flag) {
        button.setClickable(flag);
        if (flag) {
            button.setTextColor(Color.BLACK);
        } else {
            button.setTextColor(Color.GRAY);
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(screenReceiver);
        runFlag = false;
        if (reader != null) {
            reader.close();
        }
        super.onDestroy();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_start:
                if (!startFlag) {
                    startFlag = true;
                    buttonStart.setText(R.string.stop_inventory);
                } else {
                    startFlag = false;
                    buttonStart.setText(R.string.inventory);
                }
                break;
            case R.id.button_connect:
                try {
                    int value = getSharedValue();
                    if (value < 16) value = 16;
                    if (value > 26) value = 26;

                    if (reader.setOutputPower(value)) {
                        Toast.makeText(getApplicationContext(), "设置成功", 0).show();
                    } else {
                        utils.toastShow("设置参数：" + String.valueOf(value));
                    }

                    byte[] versionBytes = reader.getFirmware();
                    if (versionBytes != null) {
                        // reader.setWorkArea(3);//设置成欧标
                        Utils.play(1, 0);
                        String version = new String(versionBytes);
                        // textVersion.setText(new String(versionBytes));
                        setButtonClickable(buttonConnect, false);
                        setButtonClickable(buttonStart, true);
                    }
                    setButtonClickable(buttonConnect, false);
                    setButtonClickable(buttonStart, true);

                } catch (Exception e) {
                    //utils.toastShow(e.getMessage());
                    utils.toastShow("无法启动扫描设备，请重试或者重启手持机！");
                }
                break;
            case R.id.button_clear:
                Message msg = new Message();
                //给message对象赋值
                msg.what = 2;
                //发送message值给Handler接收
                mHandler.sendMessage(msg);
                break;
            default:
                break;
        }
    }

    boolean isCallbackMate = false;

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
                    utils.toastShow("获取物资编码成功");
                }
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


    // 获取存储Value
    private int getSharedValue() {
        SharedPreferences shared = getSharedPreferences("power", 0);
        return shared.getInt("value", 26);
    }

    private int value = 2600;

    // private int values = 432 ;
    // private int mixer = 0;
    // private int if_g = 0;

    public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
        EPCModel model = mAdapter.getItem(position);

        Intent intent = new Intent(this, SetNumberActivity.class);
        intent.putExtra("position", position);
        intent.putExtra("number", model.getCount());
        this.startActivityForResult(intent, 10000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 10000://设置数量
                    int position = data.getIntExtra("position", -1);
                    int number = data.getIntExtra("number", 0);
                    mAdapter.setNumber(position, number);
                    break;
            }
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        // Log.e("", "adfasdfasdf");
        // Intent intent = new Intent(this, SettingActivity.class);
        // startActivity(intent);
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingPower.class);
                startActivity(intent);
                break;
            case R.id.order_dist:
                saveDistOrder();
                break;
            case R.id.order_allot:
                saveAllotOrder();
                break;
            case R.id.order_count:
                saveCountOrder();
                break;
            case R.id.mate_get:
                if (isCallbackMate == false) {
                    isCallbackMate = true;
                    mateManager.getMates(0);
                } else {
                    utils.toastShow("正在刷新物资编码，请稍后...");
                }
                break;
            // case R.id.reboot:
            // Intent intent2 = new Intent(Intent.ACTION_REBOOT);
            // intent2.putExtra("nowait", 1);
            // intent2.putExtra("interval", 1);
            // intent2.putExtra("window", 0);
            // sendBroadcast(intent2);
            // break;
            case R.id.quit_app:
                Utils.exitApp(this);
                break;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    void showProgressBar() {
        pbWidget.setVisibility(View.VISIBLE);
    }

    void hideProgressBar() {
        pbWidget.setVisibility(View.GONE);
        isLoding = false;
    }

    AjaxCallBack<String> callBack = new AjaxCallBack<String>() {
        @Override
        public void onLoading(long count, long current) {
            // textView.setText(current + "/" + count);
        }

        @Override
        public void onSuccess(String t) {
            super.onSuccess(t);
            isCallbackMate = false;
            try {
                JSONObject json = new JSONObject(t);
                if (json.isNull("Result")) {
                    utils.toastShow("保存信息失败，请稍后重试");
                } else {
                    utils.toastShow(json.getString("Message"));
                    switch (json.getInt("Result")) {
                        case 0:
                            break;
                        case -1:
                            break;
                        case -2:
                            break;
                        case -3:
                            break;
                        case -4:
                            break;
                        default:
                            mAdapter.clear();
                            break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                utils.toastShow(e.getMessage());
                utils.toastShow("保存信息失败，请稍后重试");
            }
            hideProgressBar();
        }

        @Override
        public void onFailure(Throwable t, int errorNo, String strMsg) {
            super.onFailure(t, errorNo, strMsg);
            hideProgressBar();
            utils.toastShow(strMsg);
            utils.toastShow("保存信息失败，请稍后重试");
            isCallbackMate = false;
        }
    };

    Utils utils;
    int storeId = 0;
    Boolean isLoding = false;

    void saveEPCInfo(final int orderid) {
        try {
            if (isLoding) {
                Toast.makeText(MainActivity.this, "条码信息正在提交中，请稍后......",
                        Toast.LENGTH_LONG).show();
                return;
            }
            if (Constant.getLoginUser(this).getUserId() <= 0) {
                Toast.makeText(MainActivity.this, "无用户登录信息！", Toast.LENGTH_LONG)
                        .show();
                return;
            }

            pickDialog = new PickDialog(MainActivity.this, getString(R.string.title), new PickDialogListener() {

                public void onRightBtnClick() {
                    // TODO Auto-generated method stub
                }

                public void onListItemLongClick(int position,
                                                String string) {
                    // TODO Auto-generated method stub
                }

                public void onListItemClick(int position, String string) {
                    try {
                        storeId = allStores.get(position).getStoreId();
                        Toast.makeText(
                                MainActivity.this,
                                "选择仓库："
                                        + allStores.get(position)
                                        .getStoreName()
                                        + " 仓库id：" + storeId + "",
                                Toast.LENGTH_SHORT).show();

                        showProgressBar();
                        orderManager.setCallBack(callBack);
                        switch (orderid) {
                            case R.id.order_dist:
                                orderManager.saveDistOrder(Constant
                                        .getLoginUser(MainActivity.this)
                                        .getUserId(), storeId, mAdapter.getData());
                                break;
                            case R.id.order_allot:
                                orderManager.saveAllotOrder(Constant
                                        .getLoginUser(MainActivity.this)
                                        .getUserId(), storeId, mAdapter.getData());
                                break;
                            case R.id.order_count:
                                orderManager.saveCountOrder(Constant
                                        .getLoginUser(MainActivity.this)
                                        .getUserId(), storeId, mAdapter.getData());
                                break;
                        }
                    } catch (Exception ex) {
                        Toast.makeText(MainActivity.this,
                                ex.getMessage(), Toast.LENGTH_LONG)
                                .show();
                    }
                }

                public void onLeftBtnClick() {
                    // TODO Auto-generated method stub
                }

                public void onCancel() {
                    // TODO Auto-generated method stub
                }
            });
            pickDialog.show();

            // 延迟0.5秒加载
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    pickDialog.initListViewData(listStoreName);
                }
            }, 500);
        } catch (Exception ex) {
            Toast.makeText(MainActivity.this, ex.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    void saveDistOrder() {
        if (mAdapter.getCount() > 0) {
            saveEPCInfo(R.id.order_dist);
        } else {
            noEPCInfo("暂无物资条码可生成出库单！");
        }
    }

    void saveAllotOrder() {
        if (mAdapter.getCount() > 0) {
            saveEPCInfo(R.id.order_allot);
        } else {
            noEPCInfo("暂无物资条码可生成调拨单！");
        }
    }

    void saveCountOrder() {
        if (mAdapter.getCount() > 0) {
            saveEPCInfo(R.id.order_count);
        } else {
            noEPCInfo("暂无物资条码可生成盘点单！");
        }
    }

    void noEPCInfo(CharSequence message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setCancelable(false)
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    /**
     * 在actionbar上显示菜单按钮
     */
    private void setOverflowShowingAlways() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class
                    .getDeclaredField("sHasPermanentMenuKey");
            menuKeyField.setAccessible(true);
            menuKeyField.setBoolean(config, false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}