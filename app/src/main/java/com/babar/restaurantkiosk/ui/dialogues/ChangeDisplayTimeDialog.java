package com.babar.restaurantkiosk.ui.dialogues;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.NumberPicker;

import com.babar.restaurantkiosk.R;

/***********************************
 * Created by Babar on 12/15/2020.  *
 ***********************************/
public class ChangeDisplayTimeDialog extends Dialog implements
        android.view.View.OnClickListener {

    public Activity c;
    public Dialog d;
    public Button yes;
    private NumberPicker numberPicker;
    private DisplayTimeListener displayTimeListener;

    public ChangeDisplayTimeDialog(Activity a,DisplayTimeListener displayTimeListener) {
        super(a);
        this.c = a;
        this.displayTimeListener = displayTimeListener;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.change_display_dialog);
        yes = (Button) findViewById(R.id.changeDisplayBtn);
        numberPicker = findViewById(R.id.numberPicker);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(30);
        //numberPicker.setTextSize(50);
        //numberPicker.setTextColor(c.getResources().getColor(R.color.white));
        yes.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        System.out.println(4);
        switch (v.getId()) {
            case R.id.changeDisplayBtn:
               // c.finish();
                displayTimeListener.onChange(numberPicker.getValue());
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
        public void onChange(int dvalue);
    }
}
