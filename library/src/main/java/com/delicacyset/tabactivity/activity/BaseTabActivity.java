package com.delicacyset.tabactivity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import com.delicacyset.tabactivity.fragment.BaseTabFragment;
import com.delicacyset.tabactivity.holder.ITabId;

/**
 * Created by Dmitry Rusak on 6/25/15.
 * <p/>Base activity with tabs with tab fragment manager to access and handle tab fragments
 */
public abstract class BaseTabActivity extends AppCompatActivity
        implements TabLayout.OnTabSelectedListener {

    public static final String EXTRA_TAB_TO_OPEN_FIRST = "TAB_TO_OPEN_FIRST";
    public static final String EXTRA_TAB_CLEAR_STACK = "TAB_CLEAR_STACK";
    public static final String EXTRA_TAB_ARGUMENTS = "TAB_ARGUMENTS";

    private final TabFragmentManager mTabFragmentManager = new TabFragmentManager();

    /**
     * contains extras for fragments in the tab (for deep-linking)
     */
    protected Bundle mTabArguments;

    public TabFragmentManager getTabFragmentManager() {
        return mTabFragmentManager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTabFragmentManager.onActivityAttached(this);
        Intent intent = getIntent();
        processIntent(intent, savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        processIntent(intent, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTabFragmentManager.onActivityDetached();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mTabFragmentManager.saveCurrentState(outState);
    }

    /**
     * set content view as usual and init tabs
     * */
    private void setTabContentView(@LayoutRes int layoutResID, Bundle savedState) {
        super.setContentView(layoutResID);
        initUi();
        ITabId[] tabIds  = initTabs();
        getTabLayout().setOnTabSelectedListener(this);
        mTabFragmentManager.onTabsInitialized(tabIds, savedState);
    }

    private void processIntent(Intent intent, Bundle savedInstanceState) {
        boolean clearStack = false;
        ITabId startTab = getDefaultTabId(); // default tab if not specified in arguments
        if (intent != null) {
            startTab = getTabIdByUniqueTabIdName(intent.getStringExtra(EXTRA_TAB_TO_OPEN_FIRST), startTab);
            clearStack = intent.getBooleanExtra(EXTRA_TAB_CLEAR_STACK, false);
            mTabArguments = intent.getParcelableExtra(EXTRA_TAB_ARGUMENTS);
        } else {
            mTabArguments = null;
        }
        if (getTabLayout() == null) {
            // activity was not created or need to be recreated
            setTabContentView(getContentLayoutId(), savedInstanceState);
            if (savedInstanceState != null) {
                // restoring state: don't need to clear and select default tab
                if (getTabFragmentManager().getCurrentTab() != null) {
                    startTab = getTabFragmentManager().getCurrentTab();
                }
                clearStack = false;
            }
        }
        if (getIntent() != null) {
            getIntent().replaceExtras(intent);
        }
        selectTab(startTab, clearStack);
        if (getIntent() != null) {
            getIntent().removeExtra(EXTRA_TAB_TO_OPEN_FIRST);
            getIntent().putExtra(EXTRA_TAB_CLEAR_STACK, false);
        }
    }

    @Override
    public void onBackPressed() {
        mTabFragmentManager.popFragmentInCurrentTab();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        Object tag = tab.getTag();
        if (tag != null && tag instanceof ITabId) {
            mTabFragmentManager.onChangeTab((ITabId) tag);
        }
    }

    public void selectTab(ITabId tabId) {
        selectTab(tabId, false);
    }

    public void selectTab(ITabId tabId, boolean clearStack) {
        mTabFragmentManager.selectTab(tabId, clearStack);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {}

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        Object tag = tab.getTag();
        if (tag != null && tag instanceof ITabId) {
            mTabFragmentManager.onReselectTab((ITabId) tag);
        }
    }

    /**
     * @return TabLayout object that have to be on activity's layout
     */
    protected abstract TabLayout getTabLayout();

    /**
     * called right after {@link #setContentView} method and before {@link #initTabs}<br>
     * Good place to init ui components through findViewById
     */
    protected abstract void initUi();

    /**
     * @return array of tab ids in the order they should be displayed on tabs
     */
    protected abstract ITabId[] initTabs();

    /**
     * method to specify root tab fragment for each tab returned from {@link #initTabs}
     * @param tabId for which one We ask for fragment
     * @return fragment instance when need to create or recreate tab root fragment in TabActivity
     */
    protected abstract BaseTabFragment getRootFragmentForTab(ITabId tabId);

    protected abstract ITabId getTabIdByUniqueTabIdName(String name, ITabId defaultValue);

    @LayoutRes
    protected abstract int getContentLayoutId();

    @NonNull
    protected abstract ITabId getDefaultTabId();
}
