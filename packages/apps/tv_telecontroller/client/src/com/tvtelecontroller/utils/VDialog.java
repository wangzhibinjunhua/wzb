package com.tvtelecontroller.utils;

import com.tvtelecontroller.R;

import android.app.Activity;
import android.app.Dialog;
import android.widget.TextView;

public class VDialog {
	
	private Dialog progressDialog;
	private static VDialog instance;
	private Activity mActivity;

	public VDialog(Activity activity) {
		mActivity = activity;
	}

	public static VDialog getInstnce(Activity activity) {
		if (instance == null) {
			instance = new VDialog(activity);
		}
		return instance;
	}

	public Dialog getDialog() {
		if (progressDialog == null) {
			progressDialog = new Dialog(mActivity, R.style.dialog);
			progressDialog.setContentView(R.layout.progress_layout);
		}
		return progressDialog;
	}

	public void showDialog(String title, String message, boolean cancelable) {
		if (mActivity.isFinishing()) {
			return;
		}

		try {
			progressDialog = getDialog();
			TextView dialogMsg = (TextView) progressDialog.findViewById(R.id.tv_dialog_msg);
			dialogMsg.setText(message);
			progressDialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void showDialog(String title, String message) {
		showDialog(title, message, true);
	}

	public void showDialog() {
		if(mActivity!=null){
			if (mActivity.isFinishing()) {
				return;
			}
		}
		try {
			progressDialog = getDialog();
			progressDialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void dismisssDialog() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}

}
