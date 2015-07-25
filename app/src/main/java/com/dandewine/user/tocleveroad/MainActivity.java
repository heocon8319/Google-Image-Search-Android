package com.dandewine.user.tocleveroad;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.dandewine.user.tocleveroad.adapters.FixedFragmentStatePagerAdapter;
import com.dandewine.user.tocleveroad.fragments.Favourite;
import com.dandewine.user.tocleveroad.fragments.ResultOfSearch;
import com.dandewine.user.tocleveroad.other.SlidingTabLayout;
import com.quinny898.library.persistentsearch.SearchBox;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.toolbar) Toolbar toolbar;
    @InjectView(R.id.searchbox) SearchBox searchBox;
    @InjectView(R.id.tabs) SlidingTabLayout tabs;
    @InjectView(R.id.viewpager) ViewPager pager;
    public ViewPagerAdapter pagerAdapter;
    public  boolean resultsAsListview,favoriteAsLisview;
    public Menu menu;
    private MenuItem item;
    private ResultOfSearch resultFragment;
    private Favourite favouriteFragment;
    int count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resultsAsListview = true;
        favoriteAsLisview = false;
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        initTabs();//set the tabs
        resultFragment = ResultOfSearch.getInstance();
        favouriteFragment = Favourite.getInstance();

    }
    private void toggle(boolean isFromSearch){
        MenuItem item = menu.findItem(R.id.action_toggle);
        if(isFromSearch) {
            if (resultsAsListview) {
                item.setIcon(R.mipmap.grid);
                item.setTitle("Show as grid");
                resultsAsListview = false;
                resultFragment.toggle(1);

            } else {
                item.setIcon(R.mipmap.listiview);
                item.setTitle("Show as list");
                resultsAsListview = true;
                resultFragment.toggle(0);
            }
        }else{
            if(favoriteAsLisview){
                item.setIcon(R.mipmap.grid);
                favouriteFragment.toggle(1);
                favoriteAsLisview = false;
            }else{
                item.setIcon(R.mipmap.listiview);
                favouriteFragment.toggle(0);
                favoriteAsLisview = true;
            }
        }

    }

    private void initTabs(){
        CharSequence titles[]={
                getResources().getText(R.string.main_activity_search),
                getResources().getText(R.string.main_activity_favourite),
        };
        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),titles);
        pager.setAdapter(pagerAdapter);
        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (count != 0)//fix this crap
                    changeIcon(position);
                count++;
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabs.setDistributeEvenly(true);
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.accent);
            }
        });
        tabs.setViewPager(pager);
    }
    private void changeIcon(int position){
        if (position == 1) {
            if (favouriteFragment.mLayoutManager.getSpanCount() == 1)
                item.setIcon(R.mipmap.grid);
            else
                item.setIcon(R.mipmap.listiview);
        } else {
            if (resultFragment.mLayoutManager.getSpanCount() == 1)
                item.setIcon(R.mipmap.grid);
            else
                item.setIcon(R.mipmap.listiview);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("searchState", resultsAsListview);
        outState.putBoolean("favoriteState", favoriteAsLisview);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        resultsAsListview = savedInstanceState.getBoolean("searchState");
        favoriteAsLisview = savedInstanceState.getBoolean("favoriteState");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        item = menu.findItem(R.id.action_toggle);
        favouriteFragment = Favourite.getInstance();
        changeIcon(pager.getCurrentItem());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            openSearch();
            return true;
        }else{
            if(pager.getCurrentItem()==0) toggle(true);
            else toggle(false);
            return true;
        }
    }
    public void openSearch() {//события связанные с поисковым вводом
        toolbar.setTitle("");
        searchBox.revealFromMenuItem(R.id.action_search, this);
       /* for (int x = 0; x < 10; x++) {
            SearchResult option = new SearchResult("Result "
                    + Integer.toString(x), getResources().getDrawable(
                    R.drawable.ic_up));
            searchBox.addSearchable(option);
        }*/
        searchBox.setMenuListener(new SearchBox.MenuListener() {

            @Override
            public void onMenuClick() {
                // Hamburger has been clicked
                Toast.makeText(MainActivity.this, "Menu click",
                        Toast.LENGTH_LONG).show();
            }

        });
        searchBox.setSearchListener(new SearchBox.SearchListener() {

            @Override
            public void onSearchOpened() {
                // Use this to tint the screen
            }

            @Override
            public void onSearchClosed() {
                searchBox.hideCircularly(MainActivity.this);
                searchBox.setSearchString("");
               toolbar.setTitle("toCleveroad");
            }

            @Override
            public void onSearchTermChanged() {
                // React to the search term changing
                // Called after it has updated results
            }

            @Override
            public void onSearch(String searchTerm){
                toolbar.setTitle("Google Image Searcher");
                if(isConnected()) {
                   ResultOfSearch fragment = ((ResultOfSearch) getSupportFragmentManager().findFragmentByTag(pagerAdapter.getTag(0)));
                    fragment.sendRequest(searchTerm,true);
                }else
                    Toast.makeText(MainActivity.this,
                            "Sorry, seems we are haven't connection with network",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSearchCleared() {

            }

        });

    }
    //проверка наличие соеденения с сетью
    private boolean isConnected(){
        ConnectivityManager manager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo!=null && networkInfo.isConnected();
    }
    public static class ViewPagerAdapter extends FixedFragmentStatePagerAdapter{

        CharSequence titles[];
        public ViewPagerAdapter(FragmentManager fm, CharSequence mTitles[]) {
            super(fm);
            this.titles = mTitles;
        }

        @Override
        public Fragment getItem(int position) {

            switch (position){
                case 0:
                    return ResultOfSearch.getInstance();
                case 1:
                    return Favourite.getInstance();
                default:
                    throw new IllegalArgumentException("Cannot recognize what" +
                        " fragment should be returned on this position "+position);

            }

        }

        @Override
        public String getTag(int position) {
            return (String) titles[position];
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return titles.length;
        }
    }


}
