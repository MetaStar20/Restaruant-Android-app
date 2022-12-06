package com.babar.restaurantkiosk.adapters;
/**
 * Created by Artur on 2021/3/24 )
 */


import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.babar.restaurantkiosk.R;
import com.babar.restaurantkiosk.items.Dir;
import com.babar.restaurantkiosk.util.DebugLog;

import tellh.com.recyclertreeview_lib.TreeNode;
import tellh.com.recyclertreeview_lib.TreeViewBinder;

public class DirectoryNodeBinder extends TreeViewBinder<DirectoryNodeBinder.ViewHolder> {
    @Override
    public ViewHolder provideViewHolder(View itemView) {
        return new ViewHolder(itemView);
    }

    @Override
    public void bindView(ViewHolder holder, int position, TreeNode node) {

        DebugLog.console("bindview..................");
//        boolean isCheck = node.isExpand() ? true : false;
        holder.ivArrow.setChecked(false);
        holder.ivArrow.setClickable(false);
        holder.ivArrow.setLongClickable(false);

        Dir dirNode = (Dir) node.getContent();
        holder.tvName.setText(dirNode.dirName);

    }

    @Override
    public int getLayoutId() {
        return R.layout.item_dir;
    }

    public static class ViewHolder extends TreeViewBinder.ViewHolder {
        private CheckBox ivArrow;
        private TextView tvName;

        public ViewHolder(View rootView) {
            super(rootView);
            this.ivArrow = (CheckBox) rootView.findViewById(R.id.storageCheckbox);
            this.tvName = (TextView) rootView.findViewById(R.id.tv_name);
        }

        public CheckBox getIvArrow() {
            return ivArrow;
        }

        public TextView getTvName() {
            return tvName;
        }
    }
}
