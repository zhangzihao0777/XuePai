package com.wangyi.UIview.activity;

import com.wangyi.UIview.widget.dialog.LoadingDialog;
import org.xutils.view.annotation.*;

import com.wangyi.UIview.BaseActivity;
import com.wangyi.define.EventName;
import com.wangyi.function.HttpsFunc;
import com.wangyi.reader.R;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

@ContentView(R.layout.login)
public class LoginActivity extends BaseActivity {
	@ViewInject(R.id.editUserID)
	private EditText userID;
	@ViewInject(R.id.editPassWords)
	private EditText passWords;
	@ViewInject(R.id.confirm)
	private Button confirm;
	private LoadingDialog loading;
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			switch(msg.what){
				case EventName.UI.START:
					loading.show();
					break;
				case EventName.UI.FAULT:
					loading.dismiss();
					break;
				case EventName.UI.SUCCESS:
					loading.dismiss();
					handler.postDelayed(new Runnable() {
						public void run() {
							LoginActivity.this.finish();
						}
					}, 500);
					break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loading = new LoadingDialog(this);
	}

	@Event(R.id.close)
	private void onCloseClick(View view){
		LoginActivity.this.finish();
	}

	@Event(R.id.register)
	private void onRegisterClick(View view){
		Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
		startActivity(intent);
	}

	@Event(R.id.confirm)
	private void onConfirmClick(View view){
		HttpsFunc.getInstance().connect(handler).Login(userID.getText().toString(), passWords.getText().toString());
	}
}
