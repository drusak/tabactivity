package com.delicacyset.tabactivitysample;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.delicacyset.tabactivity.activity.BaseTabActivity;
import com.delicacyset.tabactivity.fragment.BaseTabFragment;
import com.delicacyset.tabactivity.holder.ITabId;
import com.delicacyset.tabactivitysample.fragment.FirstLevelFragment;
import com.delicacyset.tabactivitysample.fragment.FragmentTab1;
import com.delicacyset.tabactivitysample.fragment.FragmentTab2;
import com.delicacyset.tabactivitysample.fragment.FragmentTab3;
import com.delicacyset.tabactivitysample.fragment.FragmentTab4;
import com.delicacyset.tabactivitysample.fragment.SecondLevelFragment;
import com.delicacyset.tabactivitysample.fragment.ThirdLevelFragment;
import com.delicacyset.tabactivitysample.model.HomeTabId;

public class MainTabActivity extends BaseTabActivity {

    public static final String EXTRA_OPEN_NEXT_FRAGMENT = "EXTRA_OPEN_NEXT_FRAGMENT";   // int value

    private TabLayout mTabLayout;

    /**
     * @param startTab tab to open first when activity shown
     * @param argsToTabFragment bundle with data if need to pass it into startTab fragment when started
     * @return
     */
    public static Intent getStartIntent(Context context,
                                        ITabId startTab,
                                        boolean clearStack,
                                        Bundle argsToTabFragment) {
        Intent intent = new Intent(context, MainTabActivity.class);
        intent.putExtra(EXTRA_TAB_TO_OPEN_FIRST, startTab.getUniqueTabIdName());
        intent.putExtra(EXTRA_TAB_CLEAR_STACK, clearStack);
        if (argsToTabFragment != null) {
            intent.putExtra(EXTRA_TAB_ARGUMENTS, argsToTabFragment);
        }
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
            return (BaseTabFragment) Fragment.instantiate(this, FragmentTab1.class.getName(), mTabArguments);
        } else if (HomeTabId.TAB_2.getUniqueTabIdName().equals(tabId.getUniqueTabIdName())) {
            return (BaseTabFragment) Fragment.instantiate(this, FragmentTab2.class.getName(), mTabArguments);
        } else if (HomeTabId.TAB_3.getUniqueTabIdName().equals(tabId.getUniqueTabIdName())) {
            return (BaseTabFragment) Fragment.instantiate(this, FragmentTab3.class.getName(), mTabArguments);
        } else if (HomeTabId.TAB_4.getUniqueTabIdName().equals(tabId.getUniqueTabIdName())) {
            return (BaseTabFragment) Fragment.instantiate(this, FragmentTab4.class.getName(), mTabArguments);
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Fragment currentFragment = getTabFragmentManager().getCurrentSelectedTabFragment();
        // enable / disable adding fragment (because SecondLevelFragment has same fragmentId
        if (currentFragment instanceof ThirdLevelFragment) {
            menu.findItem(R.id.action_add_in_tab).setEnabled(false);
        } else {
            menu.findItem(R.id.action_add_in_tab).setEnabled(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_add_in_tab:
                Fragment currentFragment = getTabFragmentManager().getCurrentSelectedTabFragment();
                BaseTabFragment fragmentToAdd = null;
                if (currentFragment instanceof FirstLevelFragment) {
                    fragmentToAdd = new SecondLevelFragment();
                } else if (currentFragment instanceof SecondLevelFragment) {
                    fragmentToAdd = new ThirdLevelFragment();
                }
                if (fragmentToAdd != null) {
                    getTabFragmentManager().addFragmentInCurrentTab(fragmentToAdd);
                }
                return true;
            case R.id.action_clear_current_tab:
                Intent startIntent = MainTabActivity
                        .getStartIntent(this, getTabFragmentManager().getCurrentTab(), true, null);
                startActivity(startIntent);
                return true;
            case R.id.action_clear_current_tab_and_go_deep:
                Bundle extraArgs = new Bundle();
                extraArgs.putInt(EXTRA_OPEN_NEXT_FRAGMENT, 2);
                startIntent = MainTabActivity
                        .getStartIntent(this, getTabFragmentManager().getCurrentTab(), true, extraArgs);
                startActivity(startIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
