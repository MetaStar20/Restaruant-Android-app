package com.asura.library.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;

import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;


import com.asura.library.R;
import com.asura.library.events.IVideoPlayListener;
import com.asura.library.events.OnPosterClickListener;
import com.asura.library.posters.Poster;
import com.asura.library.posters.RemoteVideo;
import com.asura.library.views.fragments.PosterFragment;
import com.asura.library.views.indicators.IndicatorShape;
import com.google.android.exoplayer2.C;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import static androidx.core.view.ViewCompat.LAYOUT_DIRECTION_LTR;

/**
 * Created by yaminilohitha on 14-03-2018.
 */

public class PosterSlider extends FrameLayout implements ViewPager.OnPageChangeListener,
        IAttributeChange, IVideoPlayListener {

    private final String TAG = "PosterSlider";

    public volatile List<Poster> posters = new ArrayList<>();

    private AppCompatActivity hostActivity;
    private CustomViewPager viewPager;

    //CustomAttributes

    private Drawable selectedSlideIndicator;
    private Drawable unSelectedSlideIndicator;
    private int defaultIndicator;
    private int indicatorSize;
    private boolean mustAnimateIndicators;
    private boolean mustLoopSlides;
    private int defaultPoster = 0;
    private int imageSlideInterval = 5000;

    private boolean hideIndicators = false;

    private boolean mustWrapContent;

    private SlideIndicatorsGroup slideIndicatorsGroup;

    private static HandlerThread handlerThread;

    static {
        handlerThread = new HandlerThread("TimerThread");
        handlerThread.start();
    }

    private Handler handler = new Handler(handlerThread.getLooper());

    private boolean setupIsCalled = false;
    List<Poster> posterQueue = new ArrayList<>();

    private OnPosterClickListener onPosterClickListener;

    private Timer timer;

    private PosterAdapter posterAdapter;

    private boolean videoStartedinAutoLoop = false;

    private volatile int COUNTER = 0;


    @LayoutRes
    private int emptyView;


    public PosterSlider(@NonNull Context context) {
        super(context);
    }

    public PosterSlider(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        parseCustomAttributes(attrs);
    }

    public PosterSlider(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        parseCustomAttributes(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PosterSlider(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        parseCustomAttributes(attrs);
    }

    private void parseCustomAttributes(AttributeSet attributeSet) {
        if (attributeSet != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.PosterSlider);
            try {
                selectedSlideIndicator = typedArray.getDrawable(R.styleable.PosterSlider_selectedSlideIndicator);
                unSelectedSlideIndicator = typedArray.getDrawable(R.styleable.PosterSlider_unSelectedSlideIndicator);
                defaultIndicator = typedArray.getInteger(R.styleable.PosterSlider_defaultIndicator, IndicatorShape.DASH);
                indicatorSize = typedArray.getDimensionPixelSize(R.styleable.PosterSlider_indicatorSize, getResources().getDimensionPixelSize(R.dimen.default_indicator_size));
                mustAnimateIndicators = typedArray.getBoolean(R.styleable.PosterSlider_animateIndicators, true);
                mustLoopSlides = typedArray.getBoolean(R.styleable.PosterSlider_loopSlides, false);
                defaultPoster = typedArray.getInt(R.styleable.PosterSlider_defaultPoster, 0);
                imageSlideInterval = typedArray.getInt(R.styleable.PosterSlider_imageSlideInterval, 0);
                hideIndicators = typedArray.getBoolean(R.styleable.PosterSlider_hideIndicators, false);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                typedArray.recycle();
            }
        }
        if (!isInEditMode()) {
            setup();
        }
    }

    private void setup() {
        if (!isInEditMode()) {
            post(new Runnable() {
                @Override
                public void run() {
                    if (getContext() instanceof AppCompatActivity) {
                        hostActivity = (AppCompatActivity) getContext();
                    } else {
                        throw new RuntimeException("Host activity must extend AppCompatActivity");
                    }
                    boolean mustMakeViewPagerWrapContent = getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT;

                    viewPager = new CustomViewPager(getContext(), mustMakeViewPagerWrapContent);
                    viewPager.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        viewPager.setId(View.generateViewId());
                    } else {
                        int id = Math.abs(new Random().nextInt((5000 - 1000) + 1) + 1000);
                        viewPager.setId(id);
                    }
                    viewPager.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    viewPager.addOnPageChangeListener(PosterSlider.this);
                    addView(viewPager);
                    slideIndicatorsGroup = new SlideIndicatorsGroup(getContext(), selectedSlideIndicator, unSelectedSlideIndicator, defaultIndicator, indicatorSize, mustAnimateIndicators);
                    if (!hideIndicators) {
                        addView(slideIndicatorsGroup);
                    }
                    stopTimer();
                    setupTimer2();
                    setupIsCalled = true;
                    if (posters.size() > 0){
                        setPosters(posters);
                    }
                    //renderRemainingPosters();
                }
            });
        }

    }

    private void renderRemainingPosters() {
        setPosters(posterQueue);
    }

    public void setPosters(List<Poster> posters) {
        Log.d(TAG, "setPosters: setupIsCalled : "+setupIsCalled);
        this.posters = posters;
        if (setupIsCalled) {

            for (int i = 0; i < posters.size()-1; i++) {
                posters.get(i).setPosition(i);
                posters.get(i).setOnPosterClickListener(onPosterClickListener);
                posters.get(i).setOnTouchListener(new OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                            stopTimer();
                        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                            stopTimer();
                            setupTimer2();
                        }
                        return false;
                    }
                });
                slideIndicatorsGroup.onSlideAdd();
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                Log.d(TAG, "setPosters: JELLY_BEAN_MR1 : "+"123");
                posterAdapter = new PosterAdapter(hostActivity.getSupportFragmentManager(), mustLoopSlides, getLayoutDirection(), posters, viewPager);
            } else {
                Log.d(TAG, "setPosters: JELLY_BEAN_MR1 : "+"12345");
                posterAdapter = new PosterAdapter(hostActivity.getSupportFragmentManager(), mustLoopSlides, posters, viewPager);
            }
            posterAdapter.setVideoPlayListener(this);

            viewPager.setAdapter(posterAdapter);

            if (mustLoopSlides) {
                if (Build.VERSION.SDK_INT >= 17) {
                    if (getLayoutDirection() == LAYOUT_DIRECTION_LTR) {
                        Log.d(TAG, "setPosters: JELLY_BEAN_MR1 : "+"1234");
                        viewPager.setCurrentItem(1, false);
                        slideIndicatorsGroup.onSlideChange(0);
                    } else {
                        Log.d(TAG, "setPosters: JELLY_BEAN_MR1 : "+"123456");
                        viewPager.setCurrentItem(posters.size(), false);
                        slideIndicatorsGroup.onSlideChange(posters.size() - 1);
                    }
                } else {
                    Log.d(TAG, "setPosters: JELLY_BEAN_MR1 : "+"123123");
                    viewPager.setCurrentItem(posters.size(), false);
                    slideIndicatorsGroup.onSlideChange(posters.size() - 1);
                }
            }
            COUNTER = 0;
            stopTimer();
            setupTimer2();
        } else {
            posterQueue.addAll(posters);
        }


    }

    private void setupTimer() {
        Log.d(TAG, "setupTimer: ");
        if (imageSlideInterval > 0 && mustLoopSlides) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Log.d(TAG, "run: setUpTimer");
                    ((AppCompatActivity) getContext()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!mustLoopSlides) {
                                if (viewPager.getCurrentItem() == posters.size() - 1) {
                                    viewPager.setCurrentItem(0, true);
                                } else {
                                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                                }
                            } else {

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                    if (getLayoutDirection() == LAYOUT_DIRECTION_LTR) {
                                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                                    } else {
                                        viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
                                    }
                                } else {
                                    viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
                                }
                            }
                        }
                    });
                }
            }, imageSlideInterval, imageSlideInterval);
        }
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }


    @Override
    public void onPageSelected(int position) {
        if (mustLoopSlides) {
            if (position == 0) {
//                postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        viewPager.setCurrentItem(posters.size(), false);
//                    }
//                }, 400);
                if (slideIndicatorsGroup != null) {
                    slideIndicatorsGroup.onSlideChange(posters.size() - 1);
                }
            } else if (position == posters.size() + 1) {
//                postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        viewPager.setCurrentItem(1, false);
//                    }
//                }, 400);
                if (slideIndicatorsGroup != null) {
                    slideIndicatorsGroup.onSlideChange(1);
                }
            } else {
                if (slideIndicatorsGroup != null) {
                    slideIndicatorsGroup.onSlideChange(position - 1);
                }
            }
        } else {
            slideIndicatorsGroup.onSlideChange(position);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        Log.d(TAG, "onPageScrollStateChanged: ");

        switch (state) {
            case ViewPager.SCROLL_STATE_DRAGGING:
                stopTimer();
                break;
            case ViewPager.SCROLL_STATE_IDLE:
                if (timer == null && !videoStartedinAutoLoop) {
                    stopTimer();
                    setupTimer2();
                }

                break;
        }
    }


    public void setOnPosterClickListener(OnPosterClickListener onPosterClickListener) {
        this.onPosterClickListener = onPosterClickListener;
        for (Poster poster : posters) {
            poster.setOnPosterClickListener(onPosterClickListener);
        }
    }

    public void setDefaultIndicator(final int indicator) {
        post(new Runnable() {
            @Override
            public void run() {
                defaultIndicator = indicator;
                slideIndicatorsGroup.changeIndicator(indicator);
                if (mustLoopSlides) {
                    if (viewPager.getCurrentItem() == 0) {
                        postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                viewPager.setCurrentItem(posters.size(), false);
                            }
                        }, 400);
                        if (slideIndicatorsGroup != null) {
                            slideIndicatorsGroup.onSlideChange(posters.size() - 1);
                        }
                    } else if (viewPager.getCurrentItem() == posters.size() + 1) {
                        postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                viewPager.setCurrentItem(1, false);
                            }
                        }, 400);
                        if (slideIndicatorsGroup != null) {
                            slideIndicatorsGroup.onSlideChange(0);
                        }
                    } else {
                        if (slideIndicatorsGroup != null) {
                            slideIndicatorsGroup.onSlideChange(viewPager.getCurrentItem() - 1);
                        }
                    }
                } else {
                    slideIndicatorsGroup.onSlideChange(viewPager.getCurrentItem());
                }
            }
        });
    }

    public void setCustomIndicator(Drawable selectedSlideIndicator, Drawable unSelectedSlideIndicator) {
        this.selectedSlideIndicator = selectedSlideIndicator;
        this.unSelectedSlideIndicator = unSelectedSlideIndicator;
        slideIndicatorsGroup.changeIndicator(selectedSlideIndicator, unSelectedSlideIndicator);
        if (mustLoopSlides) {
            if (viewPager.getCurrentItem() == 0) {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        viewPager.setCurrentItem(posters.size(), false);
                    }
                }, 400);
                if (slideIndicatorsGroup != null) {
                    slideIndicatorsGroup.onSlideChange(posters.size() - 1);
                }
            } else if (viewPager.getCurrentItem() == posters.size() + 1) {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        viewPager.setCurrentItem(1, false);
                    }
                }, 400);
                if (slideIndicatorsGroup != null) {
                    slideIndicatorsGroup.onSlideChange(0);
                }
            } else {
                if (slideIndicatorsGroup != null) {
                    slideIndicatorsGroup.onSlideChange(viewPager.getCurrentItem() - 1);
                }
            }
        } else {
            slideIndicatorsGroup.onSlideChange(viewPager.getCurrentItem());
        }
    }

    public void setCurrentSlide(final int position) {
        post(new Runnable() {
            @Override
            public void run() {
                if (viewPager != null) {
                    viewPager.setCurrentItem(position);
                }
            }
        });
    }

    public void setInterval(int interval) {
        this.imageSlideInterval = interval;
        onIntervalChange();
    }

    public void setIndicatorSize(int indicatorSize) {
        this.indicatorSize = indicatorSize;
        onIndicatorSizeChange();
    }

    public void setLoopSlides(boolean loopSlides) {
        this.mustLoopSlides = loopSlides;
    }

    public void setMustAnimateIndicators(boolean mustAnimateIndicators) {
        this.mustAnimateIndicators = mustAnimateIndicators;
        onAnimateIndicatorsChange();
    }

    public void setHideIndicators(boolean hideIndicators) {
        this.hideIndicators = hideIndicators;
        onHideIndicatorsValueChanged();
    }

    public int getCurrentSlidePosition() {
        if (viewPager == null)
            return -1;
        return viewPager.getCurrentItem();
    }

    public boolean getMustLoopSlides() {
        return mustLoopSlides;
    }

    // Events
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public void onIndicatorSizeChange() {
        if (!hideIndicators) {
            if (slideIndicatorsGroup != null) {
                removeView(slideIndicatorsGroup);
            }
            slideIndicatorsGroup = new SlideIndicatorsGroup(getContext(), selectedSlideIndicator, unSelectedSlideIndicator, defaultIndicator, indicatorSize, mustAnimateIndicators);
            addView(slideIndicatorsGroup);
            for (int i = 0; i < posters.size(); i++) {
                slideIndicatorsGroup.onSlideAdd();
            }
        }
    }

    @Override
    public void onSelectedSlideIndicatorChange() {

    }

    @Override
    public void onUnselectedSlideIndicatorChange() {

    }

    @Override
    public void onDefaultIndicatorsChange() {

    }

    @Override
    public void onAnimateIndicatorsChange() {
        if (slideIndicatorsGroup != null) {
            slideIndicatorsGroup.setMustAnimateIndicators(mustAnimateIndicators);
        }
    }

    @Override
    public void onIntervalChange() {
        Log.d(TAG, "onIntervalChange: ");
        if (handler != null) {
            stopTimer();
        }
        setupTimer2();
    }

    @Override
    public void onLoopSlidesChange() {
        Log.d(TAG, "onLoopSlidesChange: ");
    }

    @Override
    public void onDefaultBannerChange() {
        Log.d(TAG, "onDefaultBannerChange: ");
    }

    @Override
    public void onEmptyViewChange() {
        Log.d(TAG, "onEmptyViewChange: ");
    }

    @Override
    public void onHideIndicatorsValueChanged() {
        if (slideIndicatorsGroup != null) {
            removeView(slideIndicatorsGroup);
        }
        if (!hideIndicators) {
            slideIndicatorsGroup = new SlideIndicatorsGroup(getContext(), selectedSlideIndicator, unSelectedSlideIndicator, defaultIndicator, indicatorSize, mustAnimateIndicators);
            addView(slideIndicatorsGroup);
            for (int i = 0; i < posters.size(); i++) {
                slideIndicatorsGroup.onSlideAdd();
            }
        }
    }

    public void removeAllPosters() {
        this.posters.clear();
        this.slideIndicatorsGroup.removeAllViews();
        this.slideIndicatorsGroup.setSlides(0);
        invalidate();
        requestLayout();
    }


    @Override
    public void onVideoStarted() {
        Log.d(TAG, "onVideoStarted: ");
        videoStartedinAutoLoop = true;
        stopTimer();
    }

    @Override
    public void onVideoStopped() {
        Log.d(TAG, "onVideoStopped: ");
        //setupTimerWithNoDelay2();
        stopTimer();
        timer = new Timer();
        timer.schedule(new MyTimerTask(),  0);
        videoStartedinAutoLoop = false;
    }

    private void setupTimerWithNoDelay() {
        Log.d(TAG, "setupTimerWithNoDelay: ");
        if (imageSlideInterval > 0) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Log.d(TAG, "run: setupTimerWithNoDelay()");
                    ((AppCompatActivity) getContext()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!mustLoopSlides) {
                                if (viewPager.getCurrentItem() == posters.size() - 1) {
                                    viewPager.setCurrentItem(0, true);
                                } else {
                                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                                }
                            } else {

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                    if (getLayoutDirection() == LAYOUT_DIRECTION_LTR) {
                                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                                    } else {
                                        viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
                                    }
                                } else {
                                    viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
                                }
                            }
                        }
                    });
                }
            }, 0, imageSlideInterval);
        }
    }

    private void setupTimerWithNoDelay2() {
        if (imageSlideInterval > 0) {
            if(posters!=null && posters.size()>0) {
                timer = new Timer();
                if (COUNTER >= posters.size())
                    COUNTER = 0;
                Log.d("TAG_DEGUB", "setupTimerWithNoDelay2 duration: " + posters.get(COUNTER).getDuration());
                Log.d("TAG_DEGUB", "setupTimerWithNoDelay2 pos: " + posters.get(COUNTER).getPosition());
                Log.d("TAG_DEGUB", "setupTimerWithNoDelay2 COUNTER: " + COUNTER);
                if(posters.get(COUNTER) instanceof RemoteVideo){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        if (getLayoutDirection() == LAYOUT_DIRECTION_LTR) {
                            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                        } else {
                            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
                        }
                    } else {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
                    }
                    COUNTER++;
                   // timer.schedule(new MyTimerTaskWithNoDelay(), imageSlideInterval*1000);
                }else {
                    timer.schedule(new MyTimerTaskWithNoDelay(), posters.get(COUNTER).getDuration());
                }
            }
        }
    }

    private void setupTimer2() {
        Log.d(TAG, "setupTimer: ");
        Log.d(TAG, "setupTimer2: posters Size : "+posters.size());
        if (imageSlideInterval > 0 && mustLoopSlides) {
            if(posters!=null && posters.size()>0) {
                timer = new Timer();
                if (COUNTER >= posters.size())
                    COUNTER = 0;


                Log.d("TAG_DEGUB", "setupTimer2 size: " + posters.size());
                Log.d("TAG_DEGUB", "setupTimer2 duration: " + posters.get(COUNTER).getDuration());
                Log.d("TAG_DEGUB", "setupTimer2 pos: " + posters.get(COUNTER).getPosition());
                Log.d("TAG_DEGUB", "setupTimer2 COUNTER: " + COUNTER);



                if(posters.get(COUNTER) instanceof RemoteVideo){
                    Log.d("TAG_DEGUB", "setupTimer2 size: " + "if_RemoteVideo");
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//                        if (getLayoutDirection() == LAYOUT_DIRECTION_LTR) {
//                            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
//                        } else {
//                            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
//                        }
//                    } else {
//                        viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
//                    }
//                    if(COUNTER == 0){
//                        timer.schedule(new MyTimerTask(),  posters.get(COUNTER).getDuration());
//                    }else if (COUNTER==posters.size()) {
//                        timer.schedule(new MyTimerTask(), posters.get(COUNTER).getDuration());
////                        COUNTER = 0;
//                    }

                    timer.schedule(new MyTimerTask(),  posters.get(COUNTER).getDuration());

                     //timer.schedule(new MyTimerTask(), posters.get(COUNTER).getDuration()*1000, posters.get(COUNTER).getDuration()*1000*1000);
//                    timer.schedule(new MyTimerTask(),  posters.get(COUNTER).getDuration());

                }else {
                    if(COUNTER == 0){
                        timer.schedule(new MyTimerTask(),  posters.get(COUNTER).getDuration());
                    }else if (COUNTER==posters.size()-1){
                        timer.schedule(new MyTimerTask(),  posters.get(COUNTER).getDuration());
                        COUNTER = -1;
                    }else {
                        Log.d("TAG_DEGUB", "setupTimer2 size: " + "else_if_RemoteVideo");
                        if(posters.get(COUNTER) instanceof RemoteVideo){
                            timer.schedule(new MyTimerTask(), 0);
                        }else {
                            timer.schedule(new MyTimerTask(), posters.get(COUNTER).getDuration());
                        }
                    }
                }
            }
        }
    }

    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            Log.d(TAG, "run: setupTimerDelay()");
            ((AppCompatActivity) getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!mustLoopSlides) {
                        if (viewPager.getCurrentItem() == posters.size() - 1) {
                            viewPager.setCurrentItem(0, true);
                        } else {
                            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            if (getLayoutDirection() == LAYOUT_DIRECTION_LTR) {
                                if (viewPager.getCurrentItem() + 1 > posters.size()){
                                    viewPager.setCurrentItem(1, true);
                                }else{
                                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                                }
                            } else {
                                if (viewPager.getCurrentItem() == 0){
                                    viewPager.setCurrentItem(posters.size(), true);
                                }
                                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
                            }
                        } else {
                            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
                        }
                    }
                    stopTimer();
                    COUNTER++;
                    setupTimer2();
                }
            });
        }
    }

    class MyTimerTaskWithNoDelay extends TimerTask {
        @Override
        public void run() {
            Log.d(TAG, "run: setupTimerWithNoDelay()");
            ((AppCompatActivity) getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!mustLoopSlides) {
                        if (viewPager.getCurrentItem() == posters.size() - 1) {
                            viewPager.setCurrentItem(0, true);
                        } else {
                            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            if (getLayoutDirection() == LAYOUT_DIRECTION_LTR) {
                                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                            } else {
                                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
                            }
                        } else {
                            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
                        }
                    }
                    stopTimer();
                    COUNTER++;
                    setupTimerWithNoDelay2();
                }
            });
        }
    }
}
