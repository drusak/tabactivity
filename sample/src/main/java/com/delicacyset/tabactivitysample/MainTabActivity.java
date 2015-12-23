package com.delicacyset.tabactivitysample;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;

import com.delicacyset.tabactivity.activity.BaseTabActivity;
import com.delicacyset.tabactivity.fragment.BaseTabFragment;
import com.delicacyset.tabactivity.holder.ITabId;
import com.delicacyset.tabactivitysample.fragment.FragmentTab1;
import com.delicacyset.tabactivitysample.fragment.FragmentTab2;
import com.delicacyset.tabactivitysample.fragment.FragmentTab3;
import com.delicacyset.tabactivitysample.fragment.FragmentTab4;
import com.delicacyset.tabactivitysample.model.HomeTabId;

public class MainTabActivity extends BaseTabActivity {

    private TabLayout mTabLayout;

    /**
     * @param startTab tab to open first when activity shown
     * @param argsToTabFragment bundle with data if need to pass it into startTab fragment when started
     * @return
     */
    public static Intent getStartIntent(Context context,
                                        HomeTabId startTab,
                                        boolean clearStack,
                                        Bundle argsToTabFragment) {
        Intent intent = new Intent(context, MainTabActivity.class);
        intent.putExtra(EXTRA_TAB_TO_OPEN_FIRST, startTab.name());
        intent.putExtra(EXTRA_TAB_CLEAR_STACK, clearStack);
//        if (argsToTabFragment != null) {
//            intent.putExtra(EXTRA_TAB_ARGUMENTS, argsToTabFragment);
//        }
        return intent;
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_main_tab;
    }

    @Override
    public TabLayout getTabLayout() {
        return mTabLayout;
    }

    @Override
    protected void initUi() {
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
    }

    @Override
    public ITabId[] initTabs() {
        mTabLayout.addTab(mTabLayout.newTab()
                .setText(HomeTabId.TAB_1.getUniqueTabIdName())
                .setTag(HomeTabId.TAB_1));
        mTabLayout.addTab(mTabLayout.newTab()
                .setText(HomeTabId.TAB_2.getUniqueTabIdName())
                .setTag(HomeTabId.TAB_2));
        mTabLayout.addTab(mTabLayout.newTab()
                .setText(HomeTabId.TAB_3.getUniqueTabIdName())
                .setTag(HomeTabId.TAB_3));
        mTabLayout.addTab(mTabLayout.newTab()
                .setText(HomeTabId.TAB_4.getUniqueTabIdName())
                .setTag(HomeTabId.TAB_4));
        return getTabs();
    }

    public ITabId[] getTabs() {
        return new HomeTabId[]{HomeTabId.TAB_1, HomeTabId.TAB_2, HomeTabId.TAB_3, HomeTabId.TAB_4};
    }

    @Override
    public BaseTabFragment getRootFragmentForTab(ITabId tabId) {
        if (HomeTabId.TAB_1.getUniqueTabIdName().equals(tabId.getUniqueTabIdName())) {
            return (BaseTabFragment) Fragment.instantiate(this, FragmentTab1.class.getName());
        } else if (HomeTabId.TAB_2.getUniqueTabIdName().equals(tabId.getUniqueTabIdName())) {
            return (BaseTabFragment) Fragment.instantiate(this, FragmentTab2.class.getName());
        } else if (HomeTabId.TAB_3.getUniqueTabIdName().equals(tabId.getUniqueTabIdName())) {
            return (BaseTabFragment) Fragment.instantiate(this, FragmentTab3.class.getName());
        } else if (HomeTabId.TAB_4.getUniqueTabIdName().equals(tabId.getUniqueTabIdName())) {
            return (BaseTabFragment) Fragment.instantiate(this, FragmentTab4.class.getName());
        }
        // default
        return (BaseTabFragment) Fragment.instantiate(this, FragmentTab1.class.getName());
    }

    @NonNull
    @Override
    protected ITabId getDefaultTabId() {
        return HomeTabId.TAB_1;
    }

    @Override
    public ITabId getTabIdByUniqueTabIdName(String name, ITabId defaultValue) {
        ITabId tabId = defaultValue;
        if (name != null) {
            tabId = HomeTabId.valueOf(name);
        }
        return tabId;
    }
}
