package com.delicacyset.tabactivitysample.fragment;

import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.delicacyset.tabactivity.fragment.BaseTabFragment;

/**
 * Created by Dmitry Rusak on 12/23/15.
 * <p/>
 */
public abstract class FirstLevelFragment extends BaseTabFragment {

    @Override
    public void onTabResumed() {
        super.onTabResumed();
        // just for sample purpose do it here
        Activity parent = getActivity();
        if (parent instanceof AppCompatActivity) {
            ActionBar ab = ((AppCompatActivity) parent).getSupportActionBar();
            if (ab != null) {
                ab.setDisplayHomeAsUpEnabled(false);
            }
        }
    }
}
