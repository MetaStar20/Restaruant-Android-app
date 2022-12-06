package com.babar.restaurantkiosk.ui.dialogues;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.babar.restaurantkiosk.R;
import com.babar.restaurantkiosk.util.DebugLog;
import com.babar.restaurantkiosk.util.LocalCache;

import java.util.Timer;
import java.util.TimerTask;

/***********************************
 * Created by Babar on 12/15/2020.  *
 ***********************************/
public class AdminPasswordDialog extends Dialog implements
        View.OnClickListener {

    public Activity c;
    public Dialog d;
    public Button yes,returnDisplayBtn;
    private EditText pass1;
    private Timer timer;

    private AdminPassListener displayPassListener;

    public AdminPasswordDialog(Activity a, AdminPassListener displayPassListener) {
        super(a);
        this.c = a;
        this.displayPassListener = displayPassListener;
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.admin_pass_dialog);
        yes = (Button) findViewById(R.id.changeDisplayBtn);
        returnDisplayBtn = (Button) findViewById(R.id.returnDisplayBtn);
        pass1 = findViewById(R.id.pass1);

        yes.setOnClickListener(this);
        returnDisplayBtn.setOnClickListener(this);
        Reminder(2);
    }

    public void Reminder(int seconds) {
        timer = new Timer();
        timer.schedule(new RemindTask(), seconds*1000);
    }

    class RemindTask extends TimerTask {
        public void run() {
            if(pass1.getText().toString().isEmpty()){
                dismiss();
                timer.cancel();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.changeDisplayBtn:
               // c.finish();
                if(!pass1.getText().toString().isEmpty()) {
                    String pass = pass1.getText().toString();
                    if (pass.equalsIgnoreCase(LocalCache.getPassword())) {
                        displayPassListener.onPassword(pass1.getText().toString());
                        dismiss();
                    }else{
                        Toast.makeText(c,"Wrong password attempted",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Log.d("TAG_DEGUB", "OKOK ");
                    Toast.makeText(c,"Password should not be empty",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.returnDisplayBtn:
                dismiss();
                break;
            default:
                break;
        }

    }

    public interface AdminPassListener{
        public void onPassword(String value);
    }
}
