package anti.drop.device.view;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import anti.drop.device.BaseActivity;
import anti.drop.device.BaseApplication;
import anti.drop.device.R;
import anti.drop.device.adapter.TapeListAdapter;
import anti.drop.device.pojo.TapeBean;
import anti.drop.device.utils.BluetoothLeClass;
import anti.drop.device.utils.BluetoothLeClass.media_data_listener;
import anti.drop.device.utils.SharedPreferencesUtils;

public class TapeActivity extends BaseActivity {

	private ImageView backView;
	private TextView titleView;
	private ImageView tapeImage;
	private ListView tapeList;

	private TapeListAdapter mAdapter;
	private List<TapeBean> tapeData;

	private MediaRecorder myRecorder;
	private MediaPlayer myPlayer;
	private String path;
	private String paths = path;
	private File saveFilePath;
	// 所录音的文件
	String[] listFile = null;
	boolean flag;
	private TextView tape_tip;
	String temp_date;
	private float recodeTime = 0.0f;
	private static final float MIN_RECORD_TIME = 0.3f; // 最短录音时间，单位秒
	private static final int RECORD_OFF = 0; // 不在录音
	private static final int RECORD_ON = 1; // 正在录音
	private int recordState = 0; // 录音状态
	Thread mRecordThread;
	BaseApplication app;
	BluetoothLeClass mBLE_media;
	static boolean tape_flag = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tape);
		initView();

		myPlayer = new MediaPlayer();

		flag = true;
		MyItemOnLongClick();

		app = (BaseApplication) getApplication();
		mBLE_media = app.get_ble();
		if (!mBLE_media.initialize()) {
			Log.d("wzb", "error");
		}
		mBLE_media.set_media_data_listener(m_media_data_listener);
		SharedPreferencesUtils.getInstanse(this).setIsEnter(true);
	}

	@Override
	protected void onResume() {
		super.onResume();
		tape_flag = true;
		SharedPreferencesUtils.getInstanse(this).setIsEnter(true);
	}

	protected void onPause() {
		super.onPause();
		tape_flag = false;
		SharedPreferencesUtils.getInstanse(this).setIsEnter(false);
	}
	
	protected void onStop() {
		super.onStop();
		SharedPreferencesUtils.getInstanse(this).setIsEnter(false);	
	}

	
	
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				if(recordState!=RECORD_ON){
					start_record();
				}else{
					stop_record();
				}
				break;
			default:
				break;
			}
		}
	};

	private BluetoothLeClass.media_data_listener m_media_data_listener = new media_data_listener() {

		@Override
		public void media_data(String value) {
			// TODO Auto-generated method stub
			Log.d("wzb", "media data=" + value+"tape_flag="+tape_flag);
			if (tape_flag) {
				if (value.equals("b1") || value.equals("b2")) {
					mHandler.sendEmptyMessage(0);
				}
			}
		}
	};

	private void findViewById() {

		backView = (ImageView) findViewById(R.id.title_back);
		titleView = (TextView) findViewById(R.id.title_text);
		tapeImage = (ImageView) findViewById(R.id.tape_image);
		tapeList = (ListView) findViewById(R.id.tape_listview);
		tape_tip = (TextView) findViewById(R.id.tape_tip);

	}

	// 录音线程
	private Runnable recordThread = new Runnable() {

		@Override
		public void run() {
			recodeTime = 0.0f;
			while (recordState == RECORD_ON) {
				{
					try {
						Thread.sleep(100);
						recodeTime += 0.1;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	};

	// 开启录音计时线程
	private void callRecordTimeThread() {
		mRecordThread = new Thread(recordThread);
		mRecordThread.start();
	}

	void start_record() {
		if (recordState != RECORD_ON) {
			recordState = RECORD_ON;
			tapeList.setVisibility(View.GONE);
			tape_tip.setText("正在录音中...");
			tape_tip.setVisibility(View.VISIBLE);
			try {
				myRecorder = new MediaRecorder();
				temp_date = new SimpleDateFormat("yyyyMMddHHmmss")
						.format(System.currentTimeMillis());
				File rootfiles = new File(path);
				if (!rootfiles.exists()) {
					rootfiles.mkdir();
				}
				paths = path + "/" + "w" + temp_date + ".amr";
				Log.d("wzb", "paths=" + paths);
				saveFilePath = new File(paths);

				saveFilePath.createNewFile();

				myRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
				myRecorder
						.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
				myRecorder.setOutputFile(saveFilePath.getAbsolutePath());

				myRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
				myRecorder.prepare();
				// 开始录音
				myRecorder.start();
				callRecordTimeThread();
				// 重新读取 文件
				File files = new File(path);
				listFile = files.list();

				Log.d("wzb", "listFile=" + listFile);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	void deleteFailRecord(File file) {
		if (file.isFile() && file.exists()) {
			file.delete();
		}
	}

	void stop_record() {
		if (recordState == RECORD_ON) {
			recordState = RECORD_OFF;

			Log.d("wzb", "recodeTime=" + recodeTime);
			if (saveFilePath.exists() && saveFilePath != null) {
				if (myRecorder != null) {
					if (recodeTime < MIN_RECORD_TIME) {
						myRecorder.setOnErrorListener(null);
						myRecorder.setPreviewDisplay(null);
						// myRecorder.stop();
						myRecorder.release();
						myRecorder = null;
						deleteFailRecord(saveFilePath);
						Toast.makeText(TapeActivity.this, "时间太短,录音失败!",
								Toast.LENGTH_LONG).show();
					} else {
						myRecorder.stop();
						myRecorder.release();
						myRecorder = null;
						tapeData.add(new TapeBean("w" + temp_date, paths, ""));
					}
				}
			}

			if (tapeData != null && tapeData.size() > 0) {
				tapeList.setVisibility(View.VISIBLE);
				tape_tip.setVisibility(View.GONE);
			} else {
				tapeList.setVisibility(View.GONE);
				tape_tip.setText("当前没有录音");
				tape_tip.setVisibility(View.VISIBLE);

			}
			mAdapter.notifyDataSetChanged();
		}

	}

	private void setListener() {

		backView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		tapeImage.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
				switch (action) {
				case MotionEvent.ACTION_DOWN:// 按下录音
					Log.d("wzb", "down");
					start_record();
					break;
				case MotionEvent.ACTION_UP:// 松开结束
					Log.d("wzb", "up");
					stop_record();
					break;
				case MotionEvent.ACTION_CANCEL:
					Log.d("wzb", "cancel");
					stop_record();
					break;
				}
				return true;
			}
		});

		// click for play
		tapeList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String play_path = mAdapter.getPath(arg2);
				Log.d("wzb", "play_path" + play_path);

				// 调用系统播放器
				Intent intent = new Intent(Intent.ACTION_VIEW);
				Uri uri = Uri.parse("file://" + play_path);
				intent.setDataAndType(uri, "audio/*");
				startActivity(intent);
			}
		});
	}

	// long click for delete or rename
	private void MyItemOnLongClick() {

		tapeList.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				menu.add(0, 0, 0, "重命名");
				menu.add(0, 1, 0, "删除");
				menu.add(0, 2, 0, "删除全部");

			}
		});
	}

	void rename(final String str1, final int id) {
		final EditText filename = new EditText(this);
		Builder alerBuidler = new Builder(this);
		alerBuidler.setTitle("请输入要保存的文件名").setView(filename)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String text = filename.getText().toString();
						Log.d("wzb", "text.length:" + text.length());
						if (text.length() == 0) {
							// 保持不变
						} else {
							tapeData.remove(id);
							File file = new File(str1);
							file.renameTo(new File(path + "/" + text));
							tapeData.add(new TapeBean(text, path + "/" + text,
									""));
							mAdapter.notifyDataSetChanged();
						}
					}
				}).show();

	}

	public boolean onContextItemSelected(MenuItem item) {

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		int MID = (int) info.id;// 这里的info.id对应的就是数据库中_id的值

		String filePath = mAdapter.getPath(MID);
		String fileName = mAdapter.getName(MID);
		Log.d("wzb", "MID=" + MID + "file=" + filePath);
		switch (item.getItemId()) {
		case 0:// rename
				// tapeData.remove(MID);
			rename(filePath, MID);

			break;

		case 1:// delete
			tapeData.remove(MID);
			Log.d("wzb", "**" + fileName + " " + filePath);
			File file = new File(filePath);
			if (file.isFile() && file.exists()) {
				file.delete();
			}

			break;

		case 2:// delete all
			tapeData.clear();
			RecursionDeleteFile(new File(path));
			break;

		default:
			break;
		}
		mAdapter.notifyDataSetChanged();

		return super.onContextItemSelected(item);

	}

	public static void RecursionDeleteFile(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}
		if (file.isDirectory()) {
			File[] childFile = file.listFiles();
			if (childFile == null || childFile.length == 0) {
				file.delete();
				return;
			}
			for (File f : childFile) {
				RecursionDeleteFile(f);
			}
			file.delete();
		}
	}

	private void initView() {

		findViewById();
		titleView.setText("录音");
		tapeData = new ArrayList<TapeBean>();// TODO 此处需替换成获取录音的操作
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			try {
				path = Environment.getExternalStorageDirectory()
						.getCanonicalPath().toString()
						+ "/wrecorders";
				File files = new File(path);
				if (!files.exists()) {
					files.mkdir();
				}
				listFile = files.list();
				if (listFile != null) {
					for (int i = 0; i < listFile.length; i++) {
						Log.d("wzb", "i=" + i + " " + "listfile=" + listFile[i]);
						tapeData.add(new TapeBean(listFile[i], path + "/"
								+ listFile[i], ""));
					}

				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		mAdapter = new TapeListAdapter(this, tapeData);
		tapeList.setAdapter(mAdapter);
		setListener();
		if (tapeData != null && tapeData.size() > 0) {
			tapeList.setVisibility(View.VISIBLE);
			tape_tip.setVisibility(View.GONE);
		} else {
			tapeList.setVisibility(View.GONE);
			tape_tip.setVisibility(View.VISIBLE);

		}

	}

	@Override
	protected void onDestroy() {

		if (myRecorder != null)
			myRecorder.release();
		SharedPreferencesUtils.getInstanse(this).setIsEnter(false);
		super.onDestroy();
	}

}
