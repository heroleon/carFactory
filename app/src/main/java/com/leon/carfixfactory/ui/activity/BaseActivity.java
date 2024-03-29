package com.leon.carfixfactory.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.leon.carfixfactory.presenter.BasePresenter;
import com.leon.carfixfactory.utils.ZToast;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import butterknife.ButterKnife;

/**
 * Created by leon.
 */

public abstract class BaseActivity<P extends BasePresenter> extends RxAppCompatActivity {

    private ConnectivityManager manager;
    //未指定类型的Presenter
    protected P mPresenter;
    //初始化Presenter

    protected boolean isTrans;
    protected abstract void initPresenter(Intent intent);
    //设置布局
    protected abstract int getLayout();
    //初始化布局
    protected abstract void initView();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        setContentView(getLayout());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            initStatusBar(true);
        }
        //初始化ButterKnife
        ButterKnife.bind(this);
        initPresenter(getIntent());
        initView();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void initStatusBar(boolean isTransparent) {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if(isTransparent){
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }else{
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        isTrans = isTransparent;
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //设置打印方法
    public void showToast(String text){
        ZToast.makeText(this, text,1000).show();
        //Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    /***
     * 获取屏幕宽度
     * @return 屏幕宽度（px）
     */
    public int getMobileWidth(){
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        return  width;
    }

    /**
     * 获取屏幕高度
     * @return 屏幕高度(px)
     */
    public int getMobileHeight(){
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;
        return  height;
    }

    /**
     * 获取状态栏高度
     * @return 高度（px）
     */
    public int getStatusBarHeight(){
        int result = 0;
        int resultId = getResources().getIdentifier("status_bar_height","dimen","android");
        if(resultId>0){
            result =getResources().getDimensionPixelSize(resultId);
        }
        return result;
    }

    /**
     * 检查网络
     * @return 是否有网络
     */
    public boolean checkNetworkState(){
        boolean flag = false;
        manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if(manager.getActiveNetworkInfo() != null){
            flag = manager.getActiveNetworkInfo().isAvailable();
        }
        return flag;
    }

}
