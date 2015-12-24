TabActivity
======================

Library allowing to use Activity with TabLayout (support:design) and nested Fragments with tab's own stack.
For the fragments it is possible to handle their lifecycle like usual fragment.


Brief
----------

Use this library If your goal is to implement tabs navigation (iOS style), 
when each tab has its own collection of Fragments with its own back stack.
Some time ago there was quite effective class [TabActivity](http://developer.android.com/reference/android/app/TabActivity.html), 
but now it is deprecated.


How to use
----------

Main Tab activity should extend BaseTabActivity with several abstract methods.

If you want to add new "level" in current tab - use Fragment extended BaseTabFragment.

If you have fragments inside BaseTabFragment (like ViewPager) you should extend BaseTabChildFragment in conjunction with getChildFragmentManager

All fragments added to FragmentManager using _#add_ method (not _#replace_). It means that fragment's lifecycle methods won't be called in the way you expected in this Tab model.
E.g. When new fragment added in tab the previous one won't receive any event like onPause or onStop.
Because of that fact there are special methods that will be called in the "right" way.

```java

    public void onTabStarted()
    public void onTabResumed()
    public void onTabPaused()
    public void onTabStopped()
    
```

Taking example above when new fragment is added on top of the tab's stack 
methods _#onTabStarted()_ _#onTabResumed()_ will be called on new Fragment and _#onTabPaused()_ _#onTabStopped()_ on previous top Fragment.
Therefore it is the best place to put logic for saving / restoring content of fragments (in order to increase performance of the app) 
similar as you do it in usual Fragment's lifecycle methods.

For better experience you should add android:launchMode="singleTask" to your Main Tab Activity, 
in order to correctly handling deep linking and better user experience.

How it works
-----------------------

The benefit of library that it allows restoring and handling each fragments' stack using FragmentManager (it doesn't store Fragments' instances anywhere).

TabFragmentManager is the entry point for handling back stack, saving / restoring tabs, managing lifecycle of fragments.


Sample
------

Contains main parts of the library. It is quite understandable and can be used as manual for library.


Notes
-----

Developer is responsible for correct usage of memory and user experience (start, resume / pause, stop worker threads, services, tasks, free up memory, etc.) 

In generally you used _onStart_, _onResume_,... methods and now you should use _onTabStarted_, _onTabResumed_,... instead.



LICENSE
------- 

The Apache Software License, Version 2.0

http://www.apache.org/licenses/


