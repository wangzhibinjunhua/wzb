package anti.drop.device;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class BaseActivity extends Activity{

	public static BaseActivity instance;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		instance = this;
	}
	
	public static class StaticHandler<T> extends Handler {
		public WeakReference<T> mWeakReference;

		public StaticHandler(T t) {
			mWeakReference = new WeakReference<T>(t);
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
