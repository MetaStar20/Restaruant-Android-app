package com.babar.restaurantkiosk.adapters;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

import com.babar.restaurantkiosk.R;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class StorageFolderRecyclerViewAdapter extends RecyclerView.Adapter<StorageFolderRecyclerViewAdapter.ViewHolder> {

    private final List<String> mValues;
    private int selectedPosition = -1;// no selection by default
    private CheckBoxListener checkBoxListener;
    public StorageFolderRecyclerViewAdapter(List<String> items,CheckBoxListener checkBoxListener) {
        mValues = items;
        this.checkBoxListener = checkBoxListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_device, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String folder = mValues.get(position);
        holder.mIdView.setText(folder);
        //holder.mContentView.setText(mValues.get(position).getDeviceId());
        //holder.storageCheckbox.setChecked(selectedPosition == position);
      /*  holder.storageCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                this.selectedPosition = holder.getAdapterPosition();

                checkBoxListener.onChecked(selectedPosition,isChecked);
            }
        });*/
        holder.storageCheckbox.setOnClickListener((v -> {
            this.selectedPosition = holder.getAdapterPosition();

            checkBoxListener.onChecked(selectedPosition,true);
        }));
        /*holder.storageCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            this.selectedPosition = holder.getAdapterPosition();


            checkBoxListener.onChecked(selectedPosition,isChecked);
        });*/
        if(selectedPosition == position){
            holder.storageCheckbox.setChecked(true);
        }else{
            holder.storageCheckbox.setChecked(false);
        }
    }



    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final CheckBox storageCheckbox;
        //public final TextView mContentView;
        //public String mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.folderName);
            storageCheckbox = view.findViewById(R.id.storageCheckbox);
           // mContentView = (TextView) view.findViewById(R.id.deviceID);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mIdView.getText() + "'";
        }
    }

    public interface  CheckBoxListener{
        public void onChecked(int position,boolean isChecked);


    }
}