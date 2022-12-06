package com.babar.restaurantkiosk.ui.dialogues;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;

import com.babar.restaurantkiosk.R;

public class ChangeTouchWaitingTimeDialog extends Dialog implements
        android.view.View.OnClickListener {
    public Activity c;
    public Dialog d;
    public Button yes;
    private NumberPicker numberPicker;
    private DisplayTimeListener displayTimeListener;

    public ChangeTouchWaitingTimeDialog(Activity a, DisplayTimeListener displayTimeListener) {
        super(a);
        this.c = a;
        this.displayTimeListener = displayTimeListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.change_touch_waiting_dialog);
        yes = (Button) findViewById(R.id.changeTouchWaitingBtn);
        numberPicker = findViewById(R.id.numberPicker1);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(30);
        //numberPicker.setTextSize(50);
        //numberPicker.setTextColor(c.getResources().getColor(R.color.white));
        yes.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        System.out.println(333);
        Log.i("touchwaiting", "333");
        switch (v.getId()) {
            case R.id.changeTouchWaitingBtn:
                // c.finish();
                displayTimeListener.onTouchWaitingChange(numberPicker.getValue());
                break;
//            case R.id.btn_no:
//                dismiss();
//                break;
            default:
                break;
        }
        dismiss();
    }

    public interface DisplayTimeListener{
        public void onTouchWaitingChange(int dvalue);
    }
}
