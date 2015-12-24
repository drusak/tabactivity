package com.delicacyset.tabactivity.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.delicacyset.tabactivity.holder.ITabId;
import com.delicacyset.tabactivity.fragment.BaseTabFragment;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * Created by Dmitry Rusak on 7/10/15.
 * <p/>Manager for handling tab events like add fragment to current tab, select new tab, etc.
 * Designed to use with {@link BaseTabFragment} instances. Every fragment added on top of current (not replacing).
 * Therefore lifecycle fragment methods are not called when adding fragment or selecting new tab in activity.
 * Therefore you should use specially created methods for that like {@link BaseTabFragment#onTabStarted()} etc.
 * They will be called in similar way general methods onStart onStop etc called.
 */
public class TabFragmentManager {

    // stores all data
    static final String EXTRA_TAB_MANAGER_KEY = "extra:tabFragmentManager";
    // Bundle with keys as tab ids
    static final String EXTRA_TABS_STACKS = "extra:tabFragmentManager:tabs_stacks";
    // String Array with tabIds in ordered way
    static final String EXTRA_TABS_ORDERED_LIST = "extra:tabFragmentManager:tabs_list_ids";
    // String tab enum name
    static final String EXTRA_TAB_CURRENT = "extra:tabFragmentManager:tab_current";

    private BaseTabActivity mBaseTabActivity;

    // LinkedHashMap - in order to save order of tabs
    private Map<ITabId, TabHolder> mTabIds;
    private ITabId mCurrentTab;

    public void onActivityAttached(BaseTabActivity tabActivity) {
        mBaseTabActivity = tabActivity;
    }

    public void onActivityDetached() {
        mBaseTabActivity = null;
        mTabIds = null;
    }

    /**
     * called right after received tabs for activity - init tabs meta info and stacks of fragments in each
     * @param tabIds ids of tabs that should be on activity's TabLayout
     * @param savedState data for restoring tabs states while recreating activity
     */
    protected void onTabsInitialized(ITabId[] tabIds, Bundle savedState) {
        if (savedState == null) {
            mTabIds = new LinkedHashMap<>(tabIds.length);
            for (ITabId tabId : tabIds) {
                TabHolder tabHolder = new TabHolder(tabId);
                tabHolder.mContainer = getContainerViewForTab(tabId);
                mTabIds.put(tabId, tabHolder);
            }
        } else {
            restoreSavedState(savedState);
        }
    }

    public void addFragmentInCurrentTab(BaseTabFragment fragment) {
        this.addFragmentInTab(mCurrentTab, fragment);
    }

    public void addFragmentInTab(ITabId tabId, BaseTabFragment fragment) {
        // stop the current fragment
        onHideOrShowCurrentFragmentInTab(tabId, true);

        mTabIds.get(tabId).mFragmentsStack.push(fragment.getFragmentId());
        fragment.setActive(true);

        mBaseTabActivity.getSupportFragmentManager().beginTransaction()
                .add(tabId.getContainerTabResId(), fragment, fragment.getFragmentId())
                .commitAllowingStateLoss();
    }

    public void popFragmentInCurrentTab() {
        this.popFragmentInTab(mCurrentTab);
    }

    public void popFragmentInTab(ITabId tabId) {
        Stack<String> stack = mTabIds.get(tabId).mFragmentsStack;
        if (stack.size() > 1) {
            String currentFragmentTag = stack.peek();
            Fragment fragment = mBaseTabActivity.getSupportFragmentManager()
                    .findFragmentByTag(currentFragmentTag);
            if (fragment != null) {
                // stop the current fragment
                onHideOrShowCurrentFragmentInTab(tabId, true);
                stack.pop();

                mBaseTabActivity.getSupportFragmentManager().beginTransaction()
                        .remove(fragment)
                        .commit();
                // start next fragment
                onHideOrShowCurrentFragmentInTab(tabId, false);
            }

        } else {
            mBaseTabActivity.finish();
        }
    }

    /**
     * simply set current tab object, for physical selecting new tab use {@link #selectTab(ITabId)}
     * @param currentTab current tab to set
     */
    public void setCurrentTab(ITabId currentTab) {
        mCurrentTab = currentTab;
    }

    public ITabId getCurrentTab() {
        return mCurrentTab;
    }

    public int getCountOfFragmentsInCurrentTab() {
        Stack<String> stack = mTabIds.get(mCurrentTab).mFragmentsStack;
        return stack == null ? 1 : stack.size();
    }

    /**
     * @return top fragment in current selected tab
     */
    public Fragment getCurrentSelectedTabFragment() {
        Fragment currentFragment = null;
        if (mCurrentTab != null) {
            String currentFragmentTag = null;
            Stack<String> currentTabStack = mTabIds.get(mCurrentTab).mFragmentsStack;
            if (currentTabStack != null && currentTabStack.size() > 0) {
                currentFragmentTag = currentTabStack.peek();
            }
            if (!TextUtils.isEmpty(currentFragmentTag)) {
                currentFragment = mBaseTabActivity.getSupportFragmentManager().findFragmentByTag(currentFragmentTag);
            }
        }
        return currentFragment;
    }
    /**
     * @return index of current tab or -1 if there is no any selection yet
     * */
    public int getTabPosition(ITabId tabId) {
        int position = -1;
        if (mTabIds != null && tabId != null) {
            List<ITabId> tabs = new ArrayList<>(mTabIds.keySet());
            for (int i = 0; i < tabs.size(); i++) {
                if (tabs.get(i) == tabId) {
                    position = i;
                    break;
                }
            }
        }
        return position;
    }

    public void selectTab(ITabId tabId) {
        selectTab(tabId, false);
    }

    /**
     * select new tab
     * @param tabId new tab to select
     */
    public void selectTab(ITabId tabId, boolean clearStack) {
        int tabIndex = getTabPosition(tabId);
        if (tabIndex != -1) {
            if (clearStack) {
                clearTabStackToRootFragmentOnly(tabId);
            }
            if (mBaseTabActivity != null && mBaseTabActivity.getTabLayout() != null) {
                TabLayout.Tab tab = mBaseTabActivity.getTabLayout().getTabAt(tabIndex);
                if (tab != null) {
                    tab.select();
                }
            }
        }
    }

    public void onReselectTab(ITabId tabId) {
        if (mTabIds != null) {
            if (mTabIds.containsKey(tabId) && mTabIds.get(tabId).mFragmentsStack.isEmpty()) {
                // case when tab is selected but not displayed
                // e.g. when tabs added to tab layout it selects first tab by default
                onChangeTab(tabId);
            } else if (tabId == mCurrentTab) {
                onHideOrShowTab(mCurrentTab, false, false);
            }
        }
    }

    /**
     * called when new tab selected event received.
     * Hide previously selected tab and show new tab with its previous fragments in stack
     * @param newTabId new tab to select
     */
    protected void onChangeTab(ITabId newTabId) {
        if (mTabIds == null) {
            // not initialized yet
            return;
        }
        Fragment fragment = null;
        if (mTabIds.get(newTabId).mFragmentsStack != null && mTabIds.get(newTabId).mFragmentsStack.size() > 0) {
            fragment = mBaseTabActivity.getSupportFragmentManager().findFragmentByTag(newTabId.getUniqueTabIdName());
        }
        boolean initTab = false;
        if (fragment == null) {
            initTab = true;
            fragment = mBaseTabActivity.getRootFragmentForTab(newTabId);
            if (fragment != null) {
                mTabIds.get(newTabId).mFragmentsStack.push(newTabId.getUniqueTabIdName());
                ((BaseTabFragment) fragment).setActive(true);
                mBaseTabActivity.getSupportFragmentManager().beginTransaction()
                        .replace(newTabId.getContainerTabResId(), fragment, newTabId.getUniqueTabIdName())
                        .commit();
            }
        }
        if (!(fragment instanceof BaseTabFragment)) {
            throw new RuntimeException("If you add fragment to tab It must extends BaseTabFragment");
        }

        onHideOrShowTab(newTabId, false, initTab);
        if (mCurrentTab != null && newTabId != mCurrentTab) {
            onHideOrShowTab(mCurrentTab, true, false);
        }
        mCurrentTab = newTabId;
    }

    private void clearTabStackToRootFragmentOnly(ITabId tabId) {
        TabHolder holder = mTabIds.get(tabId);
        if (holder != null) {
            if (holder.mFragmentsStack != null) {
                while (holder.mFragmentsStack.size() > 0) {
                    String currentFragmentTag = holder.mFragmentsStack.peek();
                    Fragment fragment = mBaseTabActivity.getSupportFragmentManager()
                            .findFragmentByTag(currentFragmentTag);
                    if (fragment != null) {
                        // stop the current fragment
                        onHideOrShowCurrentFragmentInTab(tabId, true);
                        holder.mFragmentsStack.pop();
                        mBaseTabActivity.getSupportFragmentManager().beginTransaction()
                                .remove(fragment)
                                .commit();
                    }
                }
            }
        }
    }

    /**
     * every tab is a layout and every root fragment added to corresponding layout container
     * @param tabId to look for the container for it
     * @return root layout where tab fragments added
     */
    private <T extends ITabId> ViewGroup getContainerViewForTab(T tabId) {
        ViewGroup containerForTab =
                (ViewGroup) mBaseTabActivity.findViewById(tabId.getContainerTabResId());
        if (containerForTab == null) {
            throw new RuntimeException("Tab Activity layout should contain container with " +
                    tabId.getContainerTabResId() + " for the tab " + tabId);
        }
        return containerForTab;
    }

    /**
     * when tab hided or shown methods onTabPaused onTabStopped and onTabStarted onTabResumed of fragments called respectively
     * @param tabId tab id to hide/show
     * @param hide true if need to hide fragment in tab
     */
    private void onHideOrShowCurrentFragmentInTab(ITabId tabId, boolean hide) {
        Stack<String> stack = mTabIds.get(tabId).mFragmentsStack;
        if (stack == null || stack.size() == 0) {
            return;
        }
        String topFragmentId = stack.peek();
        Fragment fragment = mBaseTabActivity.getSupportFragmentManager().findFragmentByTag(topFragmentId);
        if (fragment != null && fragment instanceof BaseTabFragment) {
            if (hide) {
                if (fragment.isResumed()) {
                    ((BaseTabFragment) fragment).onTabPaused();
                    ((BaseTabFragment) fragment).onTabStopped();
                }
                ((BaseTabFragment) fragment).setActive(false);
            } else {
                ((BaseTabFragment) fragment).setActive(true);
                if (fragment.isResumed()) {
                    ((BaseTabFragment) fragment).onTabStarted();
                    ((BaseTabFragment) fragment).onTabResumed();
                }
            }
        }
    }

    /**
     * notify the top fragment of the tab only
     * */
    private void onHideOrShowTab(ITabId tabId, boolean hide, boolean initTab) {
        mTabIds.get(tabId).mContainer.setVisibility(hide ? View.INVISIBLE : View.VISIBLE);
        onHideOrShowCurrentFragmentInTab(tabId, hide);
    }

    /**
     * contains all info about tab (id, container where fragments added, fragment stack)
     */
    class TabHolder {
        ITabId mTabId;
        ViewGroup mContainer;
        /**
         * stores fragment tags in order to find in fragment manager byTag
         */
        Stack<String> mFragmentsStack;

        public TabHolder(ITabId tabId) {
            mTabId = tabId;
            mFragmentsStack = new Stack<>();
        }
    }

    protected void restoreSavedState(Bundle savedState) {
        Bundle data = savedState.getBundle(EXTRA_TAB_MANAGER_KEY);
        if (data != null) {
            List<String> orderedTabs = data.getStringArrayList(EXTRA_TABS_ORDERED_LIST);
            mTabIds = new LinkedHashMap<>();
            Bundle tabsData = data.getBundle(EXTRA_TABS_STACKS);
            if (tabsData != null && orderedTabs != null) {
                for (String tabIdName : orderedTabs) {
                    ITabId tabId = mBaseTabActivity
                            .getTabIdByUniqueTabIdName(tabIdName, mBaseTabActivity.getDefaultTabId());
                    TabHolder tabHolder = new TabHolder(tabId);

                    tabHolder.mContainer = getContainerViewForTab(tabId);

                    ArrayList<String> stack = tabsData.getStringArrayList(tabIdName);
                    tabHolder.mFragmentsStack = new Stack<>();
                    if (stack != null) {
                        tabHolder.mFragmentsStack.addAll(stack);
                    }

                    mTabIds.put(tabId, tabHolder);
                }

                String currentTabName = data.getString(EXTRA_TAB_CURRENT);
                mCurrentTab = getTabIdByUniqueTabIdName(currentTabName, mTabIds.keySet());

                Stack<String> stack = mTabIds.get(mCurrentTab).mFragmentsStack;
                String topFragmentId = stack.peek();
                Fragment fragment = mBaseTabActivity.getSupportFragmentManager().findFragmentByTag(topFragmentId);
                if (fragment != null && fragment instanceof BaseTabFragment) {
                    ((BaseTabFragment) fragment).setActive(true);
                }
            }
        }
    }

    protected void saveCurrentState(Bundle outState) {
        Bundle data = new Bundle();
        if (mCurrentTab != null) {
            data.putString(EXTRA_TAB_CURRENT, mCurrentTab.getUniqueTabIdName());
        }
        if (mTabIds != null) {
            ArrayList<String> orderedTabs = new ArrayList<>();
            Bundle tabsData = new Bundle();
            for (ITabId tabId : mTabIds.keySet()) {
                orderedTabs.add(tabId.getUniqueTabIdName());
                tabsData.putStringArrayList(tabId.getUniqueTabIdName(),
                        new ArrayList<>(mTabIds.get(tabId).mFragmentsStack));
            }
            data.putStringArrayList(EXTRA_TABS_ORDERED_LIST, orderedTabs);
            data.putBundle(EXTRA_TABS_STACKS, tabsData);
        }
        outState.putBundle(EXTRA_TAB_MANAGER_KEY, data);
    }

    private ITabId getTabIdByUniqueTabIdName(String uniqueTabIdName, Set<ITabId> tabIds) {
        for (ITabId tab : tabIds) {
            if (tab.getUniqueTabIdName().equals(uniqueTabIdName)) {
                return tab;
            }
        }
        return mBaseTabActivity.getDefaultTabId();
    }

}
