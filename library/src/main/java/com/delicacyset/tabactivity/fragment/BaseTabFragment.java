package com.delicacyset.tabactivity.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.delicacyset.tabactivity.activity.TabFragmentManager;
import com.delicacyset.tabactivity.activity.BaseTabActivity;

import java.util.List;

/**
 * Created by Dmitry Rusak on 6/16/15.
 * <p/>Special fragment for using in tabs.
 * Lifecycle:
 * <ul>use {@link #onTabStarted()} instead {@link Fragment#onStart()}
 * <ul>use {@link #onTabResumed()} ()} instead {@link Fragment#onResume()}
 * <ul>use {@link #onTabPaused()} instead {@link Fragment#onPause()}
 * <ul>use {@link #onTabStopped()} instead {@link Fragment#onStop()}
 */
public abstract class BaseTabFragment extends Fragment {
    // list in asc order with items as Fragments simple names
    public static final String EXTRA_NEXT_FRAGMENTS = "EXTRA_NEXT_FRAGMENTS";
    // bundle with <FragmentName, Bundle>
    public static final String EXTRA_NEXT_FRAGMENTS_ARGS = "EXTRA_NEXT_FRAGMENTS_ARGS";

    protected boolean mIsActive;

    protected int mNumberOfActiveLoadings = 0;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof BaseTabActivity)) {
            throw new ClassCastException(context.toString()
                    + " should be an extension of BaseTabActivity class");
        }
    }

    /**
     * send onActivityResult to nested fragment, because by default they won't receive such event
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FragmentManager nestedFragmentsManager = getChildFragmentManager();
        if (nestedFragmentsManager != null) {
            List<Fragment> nestedFragments = nestedFragmentsManager.getFragments();
            if (nestedFragments != null) {
                for (Fragment fragment : nestedFragments) {
                    fragment.onActivityResult(requestCode, resultCode, data);
                }
            }
        }
    }

    /**
     * don't use this callback for starting loading content, use #onVisibilityChanged method
     * */
    @Override
    public void onStart() {
        super.onStart();
        if (mIsActive) {
            onTabStarted();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mIsActive) {
            onTabResumed();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mIsActive) {
            onTabPaused();
        }
    }

    /**
     * don't use this callback for stopping content, use #onVisibilityChanged method
     * */
    @Override
    public void onStop() {
        super.onStop();
        if (mIsActive) {
            onTabStopped();
        }
    }

    /**
     * @return Manager for adding new fragments in tab
     */
    public TabFragmentManager getTabFragmentManager() {
        final Activity activity = getActivity();
        if (activity != null) {
            return ((BaseTabActivity) activity).getTabFragmentManager();
        }
        return null;
    }

    public void onTabStarted() {
        if (getChildFragmentManager() != null && getChildFragmentManager().getFragments() != null) {
            for (Fragment fragment : getChildFragmentManager().getFragments()) {
                if (fragment instanceof BaseTabChildFragment) {
                    ((BaseTabChildFragment) fragment).setActive(mIsActive);
                    if (fragment.isResumed()) {
                        ((BaseTabChildFragment) fragment).onTabStarted();
                    }
                }
            }
        }
    }
    public void onTabResumed() {
        if (getChildFragmentManager() != null && getChildFragmentManager().getFragments() != null) {
            for (Fragment fragment : getChildFragmentManager().getFragments()) {
                if (fragment instanceof BaseTabChildFragment) {
                    ((BaseTabChildFragment) fragment).setActive(mIsActive);
                    if (fragment.isResumed()) {
                        ((BaseTabChildFragment) fragment).onTabResumed();
                    }
                }
            }
        }
    }
    public void onTabPaused() {
        if (getChildFragmentManager() != null && getChildFragmentManager().getFragments() != null) {
            for (Fragment fragment : getChildFragmentManager().getFragments()) {
                if (fragment instanceof BaseTabChildFragment) {
                    ((BaseTabChildFragment) fragment).setActive(mIsActive);
                    if (fragment.isResumed()) {
                        ((BaseTabChildFragment) fragment).onTabPaused();
                    }
                }
            }
        }
    }
    public void onTabStopped() {
        if (getChildFragmentManager() != null && getChildFragmentManager().getFragments() != null) {
            for (Fragment fragment : getChildFragmentManager().getFragments()) {
                if (fragment instanceof BaseTabChildFragment) {
                    ((BaseTabChildFragment) fragment).setActive(mIsActive);
                    if (fragment.isResumed()) {
                        ((BaseTabChildFragment) fragment).onTabStopped();
                    }
                }
            }
        }
    }

    /**
     *
     * @param isActive true if fragment's tab is active and this fragment is on top of stack
     */
    public void setActive(boolean isActive) {
        mIsActive = isActive;
        if (isAdded()) {
            if (getChildFragmentManager() != null && getChildFragmentManager().getFragments() != null) {
                for (Fragment fragment : getChildFragmentManager().getFragments()) {
                    if (fragment instanceof BaseTabChildFragment) {
                        ((BaseTabChildFragment) fragment).setActive(mIsActive);
                    }
                }
            }
        }
    }
    /**
     * @return string id of the fragment for using in Back stack and in Support Fragment Manager
     */
    public abstract String getFragmentId();
}
