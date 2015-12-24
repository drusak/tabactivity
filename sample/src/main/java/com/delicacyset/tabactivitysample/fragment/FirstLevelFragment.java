package com.delicacyset.tabactivitysample.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.delicacyset.tabactivity.fragment.BaseTabFragment;
import com.delicacyset.tabactivitysample.MainTabActivity;

/**
 * Created by Dmitry Rusak on 12/23/15.
 * <p/>
 */
public abstract class FirstLevelFragment extends BaseTabFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            int nextFragmentsCount = args.getInt(MainTabActivity.EXTRA_OPEN_NEXT_FRAGMENT, 0);
            if (nextFragmentsCount > 0) {
                args.putInt(MainTabActivity.EXTRA_OPEN_NEXT_FRAGMENT, nextFragmentsCount - 1);
                getTabFragmentManager()
                        .addFragmentInCurrentTab(
                                (SecondLevelFragment) Fragment.instantiate(getActivity(),
                                SecondLevelFragment.class.getName(),
                                args));
            }
        }
    }

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
