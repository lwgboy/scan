package xxtt.scan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xxtt.scan.manager.Constant;
import xxtt.scan.manager.MateManager;
import xxtt.scan.manager.UserManager;
import xxtt.scan.model.EPCModel;
import xxtt.scan.model.MateModel;
import xxtt.scan.model.StoreModel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ListEditorAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    Activity activity;
    List<EPCModel> mData;
    xxtt.scan.lib.Utils utils;
    MateManager mateManager;

    String name;
    Boolean isok;

    public ListEditorAdapter(Activity activity, List<EPCModel> data) {
        this.activity = activity;
        mData = data;
        mInflater = LayoutInflater.from(activity);
        //init();
        utils = new xxtt.scan.lib.Utils();

        mateManager = new MateManager(activity);
        mateManager.setCallBack(callBack);
    }

    public List<EPCModel> getData() {
        return mData;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public EPCModel getItem(int arg0) {
        return arg0 < getCount() ? mData.get(arg0) : null;
    }

    @Override
    public long getItemId(int arg0) {
        return arg0 < getCount() ? mData.get(arg0).getId() : 0;
    }

    public void setNumber(int position, int number) {

        if (position > -1 && position < getCount()) {
            getItem(position).setCount(number);
            notifyDataSetChanged();
        }
    }

    public void addEpc(String oepc) {

        String showEpc = oepc;
        if (oepc.length() == 32) {
            String[] arr = oepc.split("A");
            if (arr.length == 4)
                showEpc = arr[1];
            if (arr.length == 5)
                showEpc = arr[1] + "A" + arr[2];
        }

        for (EPCModel item : mData) {
            if (showEpc.equals(item.getEpc())) {
                return;
            }
        }

        isok = false;
        name = "（无）";
        if (Constant.getLoginUser(this.activity).getUserId() <= 0) {
            Toast.makeText(this.activity, "无用户登录信息！", Toast.LENGTH_LONG).show();
            isok = true;
        } else {
            /*
            mateManager.getMateName(showEpc);
            while (!isok) {
            }
            */
            name = mateManager.getName(showEpc);
        }

        EPCModel m = new EPCModel();
        m.setEpc(showEpc);
        m.setOldEpc(oepc);
        m.setName(name);
        m.setCount(1);
        mData.add(m);
        notifyDataSetChanged();
    }

    public void addItem(EPCModel model) {
        if (model != null) {
            mData.add(model);
            notifyDataSetChanged();
        }
    }

    public void clear() {
        mData.clear();
        notifyDataSetChanged();
    }

    private Integer index = -1;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final EPCModel m = getItem(position);
        final ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listview_item2, null);
            holder = new ViewHolder();

            holder.count = (TextView) convertView.findViewById(R.id.textView_count);
            holder.epc = (TextView) convertView.findViewById(R.id.textView_epc);
            holder.id = (TextView) convertView.findViewById(R.id.textView_id);
            holder.name = (TextView) convertView.findViewById(R.id.textView_name);
            holder.llImg = (LinearLayout) convertView.findViewById(R.id.ll_Img);

            convertView.setTag(holder); //绑定ViewHolder对象
        } else {
            holder = (ViewHolder) convertView.getTag(); //取出ViewHolder对象
        }

        holder.id.setText(position + 1 + "");
        holder.epc.setText(m.getEpc());
        holder.name.setText(m.getName());
        holder.count.setText(m.getCount() + "");

        holder.llImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String oepc = m.getOldEpc();
                //Toast.makeText(activity, oepc, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(activity, MoreHandleActivity.class);
                intent.putExtra("epc", oepc);
                activity.startActivity(intent);
            }
        });

        return convertView;
    }

    public final class ViewHolder {
        public TextView id;
        public TextView count;// ListView中的输入
        public TextView epc;// 用来定义的标志性主键,可不用关心
        public TextView name;
        public LinearLayout llImg;
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
                    utils.toastShow("未有获取到材料信息，请稍后重试");
                } else {
                    // 循环入库
                    MateModel model;
                    JSONObject item;
                    List<MateModel> models = new ArrayList<MateModel>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        item = jsonArray.getJSONObject(i);
                        model = new MateModel();
                        model.setId(item.getInt("Id"));
                        model.setName(item.getString("Name"));
                        name = model.getName();
                        model.setCode(item.getString("Code"));
                        // utils.toastShow(item.getString("Id"));
                        models.add(model);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                utils.toastShowErrofInfo("获取材料信息失败，请稍后重试");
            }
            isok = true;
        }

        @Override
        public void onFailure(Throwable t, int errorNo, String strMsg) {
            super.onFailure(t, errorNo, strMsg);
            utils.toastShowErrofInfo("获取仓库信息失败，请稍后重试");
        }
    };
}