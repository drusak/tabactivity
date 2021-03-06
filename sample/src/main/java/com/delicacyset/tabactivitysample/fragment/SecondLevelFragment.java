package com.delicacyset.tabactivitysample.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.delicacyset.tabactivity.fragment.BaseTabFragment;
import com.delicacyset.tabactivitysample.MainTabActivity;
import com.delicacyset.tabactivitysample.R;

/**
 * Created by Dmitry Rusak on 12/23/15.
 * <p/>
 */
public class SecondLevelFragment extends BaseTabFragment {

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
                                (ThirdLevelFragment) Fragment.instantiate(getActivity(),
                                        ThirdLevelFragment.class.getName(),
                                        args));
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_second_level, container, false);
    }

    @Override
    public void onTabResumed() {
        super.onTabResumed();
        // just for sample purpose do it here
        Activity parent = getActivity();
        if (parent instanceof AppCompatActivity) {
            ActionBar ab = ((AppCompatActivity) parent).getSupportActionBar();
            if (ab != null) {
                ab.setDisplayHomeAsUpEnabled(true);
            }
        }
    }
}
