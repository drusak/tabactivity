package com.delicacyset.tabactivity.fragment;

import android.os.Bundle;

/**
 * Created by Dmitry Rusak on 8/31/15.
 * <p/>This is base fragment for the child inside tab fragment (like view pager fragments inside tab fragment).
 * It is better to use this fragment in conjunction with {@link android.support.v4.app.Fragment#getChildFragmentManager()}
 */
public abstract class BaseTabChildFragment extends BaseTabFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // child is created and added to its Active parent - should be active by default
        mIsActive = true;
    }

    @Override
    public String getFragmentId() {
        // for the child We don't need it
        return null;
    }

}
