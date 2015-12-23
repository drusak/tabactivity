package com.delicacyset.tabactivitysample.model;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;

import com.delicacyset.tabactivity.holder.ITabId;
import com.delicacyset.tabactivitysample.R;

/**
 * Created by Dmitry Rusak on 6/24/15. <br>
 *     tab ids for tab layout in main activity
 */
public enum HomeTabId implements ITabId {
    TAB_1(R.id.container_tab_fragment_1),
    TAB_2(R.id.container_tab_fragment_2),
    TAB_3(R.id.container_tab_fragment_3),
    TAB_4(R.id.container_tab_fragment_4);

    /**
     * layout resource id where to put new fragments for the tab
     */
    @IdRes
    private final int mContainerTabResId;

    HomeTabId(@IdRes int containerTabResId) {
        mContainerTabResId = containerTabResId;
    }

    public static HomeTabId getTabIdByEnumName(String enumName, HomeTabId defaultValue) {
        HomeTabId tabId = defaultValue;
        if (enumName != null) {
            tabId = HomeTabId.valueOf(enumName);
        }
        return tabId;
    }

    @Override
    public int getContainerTabResId() {
        return mContainerTabResId;
    }

    @NonNull
    @Override
    public String getUniqueTabIdName() {
        return name();
    }


}
