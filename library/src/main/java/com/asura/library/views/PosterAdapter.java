package com.asura.library.views;


import android.util.LayoutDirection;

import com.asura.library.events.IVideoPlayListener;
import com.asura.library.posters.Poster;
import com.asura.library.views.fragments.EmptyViewFragment;
import com.asura.library.views.fragments.PosterFragment;

import java.util.Collections;
import java.util.List;

import androidx.annotation.LayoutRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class PosterAdapter extends FragmentStatePagerAdapter {

    private List<Poster> posters;
    private boolean isLooping;
    private CustomViewPager viewPager;
    @LayoutRes
    private int emptyView;

    private IVideoPlayListener videoPlayListener;

    
    public PosterAdapter(FragmentManager supportFragmentManager, boolean isLooping, List<Poster> posters, CustomViewPager viewPager) {
        super(supportFragmentManager);
        this.isLooping = isLooping;
        this.posters = posters;
        this.viewPager = viewPager;

    }

    public PosterAdapter(FragmentManager supportFragmentManager, boolean isLooping, int layoutDirection, List<Poster> posters, CustomViewPager viewPager) {
        super(supportFragmentManager);
        this.isLooping = isLooping;
        this.posters = posters;
        this.viewPager = viewPager;
        if (layoutDirection== LayoutDirection.RTL){
            Collections.reverse(posters);
        }
    }

    @Override
    public Fragment getItem(int position) {
        if (posters.isEmpty() && emptyView > 0) {
            return EmptyViewFragment.newInstance(emptyView);
        }
        boolean isCurrentItem = position == viewPager.getCurrentItem();
        if (isLooping) {
            if (position == 0) {
                return PosterFragment.newInstance(posters.get(posters.size() - 1), isCurrentItem,videoPlayListener);
            } else if (position == posters.size() + 1) {
                return PosterFragment.newInstance(posters.get(0), isCurrentItem,videoPlayListener);
            } else {
                return PosterFragment.newInstance(posters.get(position - 1), isCurrentItem,videoPlayListener);
            }
        } else {
            return PosterFragment.newInstance(posters.get(position), isCurrentItem,videoPlayListener);
        }
    }

    @Override
    public int getCount() {
        if (posters.isEmpty()) {
            if (emptyView > 0) {
                return 1;
            } else {
                return 0;
            }
        }
        if (isLooping) {
            return posters.size()+2;
        } else {
            return posters.size();
        }
    }

    public void setVideoPlayListener(IVideoPlayListener videoPlayListener) {
        this.videoPlayListener = videoPlayListener;
    }

    public void setEmptyView(int emptyView) {
        this.emptyView = emptyView;
        notifyDataSetChanged();
    }


}
