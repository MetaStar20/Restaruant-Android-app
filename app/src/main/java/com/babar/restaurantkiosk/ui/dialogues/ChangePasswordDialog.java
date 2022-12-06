package com.babar.restaurantkiosk.ui.dialogues;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.babar.restaurantkiosk.R;

/***********************************
 * Created by Babar on 12/15/2020.  *
 ***********************************/
public class ChangePasswordDialog extends Dialog implements
        View.OnClickListener {

    public Activity c;
    public Dialog d;
    public Button yes,returnDisplayBtn;
    private EditText pass1,pass2;

    private PassListener displayPassListener;

    public ChangePasswordDialog(Activity a, PassListener displayPassListener) {
        super(a);
        this.c = a;
        this.displayPassListener = displayPassListener;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.change_pass_dialog);
        yes = (Button) findViewById(R.id.changeDisplayBtn);
        returnDisplayBtn = (Button) findViewById(R.id.returnDisplayBtn);
        pass1 = findViewById(R.id.pass1);
        pass2 = findViewById(R.id.pass2);
        yes.setOnClickListener(this);
        returnDisplayBtn.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.changeDisplayBtn:
               // c.finish();
                if(!pass1.getText().toString().isEmpty()&& !pass1.getText().toString().isEmpty()) {
                    if (pass1.getText().toString().equalsIgnoreCase(pass2.getText().toString())) {
                        displayPassListener.onPassChange(pass1.getText().toString());
                        dismiss();
                    }else{
                        Toast.makeText(c,"Password should be same",Toast.LENGTH_SHORT).show();
                    }
                }else{
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

    public interface PassListener{
        public void onPassChange(String value);
    }
}
