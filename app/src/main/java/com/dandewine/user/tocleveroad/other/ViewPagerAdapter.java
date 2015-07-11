package com.dandewine.user.tocleveroad.other;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.dandewine.user.tocleveroad.fragments.Favourite;
import com.dandewine.user.tocleveroad.fragments.ResultOfSearch;


public class ViewPagerAdapter extends FixedFragmentStatePagerAdapter {

    CharSequence Titles[];
    int NumbOfTabs;

    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(FragmentManager fm,CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);
        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;
    }


    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                return ResultOfSearch.getInstance();
            case 1:
                return Favourite.getInstance();
            default:
               return null;
        }

    }


    @Override
    public String getTag(int position) {
        return (String)Titles[position];
    }

    // This method return the titles for the Tabs in the Tab Strip

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount() {
        return NumbOfTabs;
    }
}
