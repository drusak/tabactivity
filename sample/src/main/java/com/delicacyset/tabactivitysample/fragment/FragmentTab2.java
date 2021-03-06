package com.delicacyset.tabactivitysample.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.delicacyset.tabactivity.fragment.BaseTabFragment;
import com.delicacyset.tabactivitysample.R;

/**
 * Created by Dmitry Rusak on 12/23/15.
 * <p/>
 */
public class FragmentTab2 extends FirstLevelFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab_2, container, false);
    }

    @Override
    public String getFragmentId() {
        return this.getClass().getSimpleName();
    }
}
