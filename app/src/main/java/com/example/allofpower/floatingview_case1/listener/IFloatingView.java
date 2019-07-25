package com.example.allofpower.floatingview_case1.listener;

import android.support.annotation.DrawableRes;
import com.example.allofpower.floatingview_case1.FloatView;
import com.example.allofpower.floatingview_case1.FloatingManage;


public interface IFloatingView {

    FloatingManage icon(@DrawableRes int resId);

    FloatingManage toast(String str);

    FloatingManage add();

    FloatingManage visibility();//显示/隐藏

    FloatingManage remove();

    FloatView getView();

}
