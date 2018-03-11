package xxtt.scan.lib;

import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import xxtt.scan.R;

public class Utils {

    Context vview;

    public Utils() {
    }

    public Utils(Context view) {
	vview = view;
    }

    public void toastShow(Context view, String text) {
	Toast.makeText(view, text, Toast.LENGTH_SHORT).show();
    }

    public void toastShow(String text) {
	Toast.makeText(vview, text, Toast.LENGTH_LONG).show();
    }
    
    public void toastShowErrofInfo(String text)
    {
	Toast.makeText(vview, text, Toast.LENGTH_SHORT).show();
    }

    public static void showProgressBar(ProgressBar pb) {
	pb.setVisibility(View.VISIBLE);
    }

    public static void hideProgressBar(ProgressBar pb) {
	pb.setVisibility(View.GONE);
    }

    public static SoundPool sp;
    public static Map<Integer, Integer> suondMap;
    public static Context context;

    // 初始化声音池
    public static void initSoundPool(Context context) {
	Utils.context = context;
	sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 1);
	suondMap = new HashMap<Integer, Integer>();
	suondMap.put(1, sp.load(context, R.raw.msg, 1));
    }

    // 播放声音池声音
    public static void play(int sound, int number) {
	AudioManager am = (AudioManager) Utils.context
		.getSystemService(Context.AUDIO_SERVICE);
	// 返回当前AlarmManager最大音量
	float audioMaxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

	// 返回当前AudioManager对象的音量值
	float audioCurrentVolume = am
		.getStreamVolume(AudioManager.STREAM_MUSIC);
	float volumnRatio = audioCurrentVolume / audioMaxVolume;
	sp.play(suondMap.get(sound), // 播放的音乐Id
		audioCurrentVolume, // 左声道音量
		audioCurrentVolume, // 右声道音量
		1, // 优先级，0为最低
		number, // 循环次数，0无不循环，-1无永远循环
		1);// 回放速度，值在0.5-2.0之间，1为正常速度
    }

    public static void exitApp(Context context) {
	AlertDialog.Builder builder = new AlertDialog.Builder(context);
	builder.setMessage("确定要退出物资管理系统吗？").setCancelable(false)
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int id) {
			android.os.Process
				.killProcess(android.os.Process.myPid());
			System.exit(0);
		    }
		})
		.setNegativeButton("取消", new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int id) {
			dialog.cancel();
		    }
		});
	AlertDialog alert = builder.create();
	alert.show();
    }
}