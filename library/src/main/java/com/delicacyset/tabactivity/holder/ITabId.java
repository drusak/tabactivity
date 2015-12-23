package com.delicacyset.tabactivity.holder;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;

/**
 * Created by Dmitry Rusak on 12/22/15.
 * <p/>
 */
public interface ITabId {
    @IdRes int getContainerTabResId();
    @NonNull String getUniqueTabIdName();
}
