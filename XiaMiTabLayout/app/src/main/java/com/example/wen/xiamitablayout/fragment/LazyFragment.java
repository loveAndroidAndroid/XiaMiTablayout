package com.example.wen.xiamitablayout.fragment;

import android.support.v4.app.Fragment;
import android.view.View;

public abstract class LazyFragment extends Fragment{

    public abstract void initNet();
    public View getTargetView(){
        return getView() ;
    }

}
