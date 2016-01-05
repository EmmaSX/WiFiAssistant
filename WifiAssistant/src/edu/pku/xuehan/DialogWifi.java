package edu.pku.xuehan;

import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class DialogWifi extends Dialog {
    
    private ImageButton dialog_connect, dialog_cancel;
    private TextView title;
    private String wifiname;
    private EditText dialog_password;
    private View.OnClickListener connectListener;
    
    public DialogWifi(Context context, int theme, String wifiname ) {
        super(context, theme);
        this.wifiname = wifiname;
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_wifi, null);
        dialog_connect = (ImageButton) mView.findViewById(R.id.dialog_connect);
    }
 
    private void setDialogWifi() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_wifi, null);
        title = (TextView) mView.findViewById(R.id.dialog_detail_wifi_name);
        title.setText(wifiname);
        //dialog_connect = (ImageButton) mView.findViewById(R.id.dialog_connect);
        dialog_cancel = (ImageButton) mView.findViewById(R.id.dialog_cancel);
        dialog_password = (EditText)mView.findViewById(R.id.dialog_password);
        dialog_cancel.setOnClickListener(connectListener);
        dialog_connect.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				DialogWifi.this.dismiss();
			}
		});
        super.setContentView(mView);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDialogWifi();
    }
     
    public EditText getEditText(){
        return dialog_password;
    }
    
    /**
     * 确定键监听器
     * @param listener
     */ 
    public void setOnPositiveListener(View.OnClickListener listener){ 
    	this.connectListener = listener;
    	//Toast.makeText( DialogWifi.this, "正在连接，请稍后... " ,Toast.LENGTH_LONG).show();
    } 
    /**
     * 取消键监听器
     * @param listener
     */ 
    public void setOnNegativeListener(View.OnClickListener listener){
    	dialog_cancel.setOnClickListener(listener); 
    }
}