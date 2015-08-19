package anti.drop.device.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import anti.drop.device.BaseActivity;
import anti.drop.device.R;

public class LoadingActivity extends BaseActivity{

	private static final int GO_HOME = 0xFFFFFFFF;
	private static final int SPLASH_TIME = 2 * 1000;
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case GO_HOME:
				goHome();
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading);
		mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_TIME);
	}
	
	private void goHome() {
		Intent intent = new Intent(this,HomeActivity.class);
		startActivity(intent);
		finish();
	}
	
}
