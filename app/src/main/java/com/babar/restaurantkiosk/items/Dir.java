package com.babar.restaurantkiosk.items;


import com.babar.restaurantkiosk.R;

import tellh.com.recyclertreeview_lib.LayoutItemType;

/**
 * Created by Artur on 2021/3/24 )
 */

public class Dir implements LayoutItemType {
    public String dirName;

    public Dir(String dirName) {
        this.dirName = dirName;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_dir;
    }
}
