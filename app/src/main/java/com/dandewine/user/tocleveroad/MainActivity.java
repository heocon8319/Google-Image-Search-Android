package com.dandewine.user.tocleveroad;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.dandewine.user.tocleveroad.fragments.ResultOfSearch;
import com.dandewine.user.tocleveroad.other.SlidingTabLayout;
import com.dandewine.user.tocleveroad.adapters.ViewPagerAdapter;
import com.quinny898.library.persistentsearch.SearchBox;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.toolbar) Toolbar toolbar;
    @InjectView(R.id.searchbox) SearchBox searchBox;
    public ViewPagerAdapter pagerAdapter;
    private SlidingTabLayout tabs;
    public ViewPager pager;
    public static boolean isListView;
    private Menu menu;
    public int page = 1;
    public String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isListView = true;
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        initTabs();//устанавливаем вкладки


    }
    private void toggle(){
        ResultOfSearch resultFragment = ((ResultOfSearch) getSupportFragmentManager().findFragmentByTag(pagerAdapter.getTag(0)));
        MenuItem item = menu.findItem(R.id.action_toggle);
        if(isListView){
            item.setIcon(R.mipmap.grid);
            item.setTitle("Show as grid");
            isListView=false;
            resultFragment.toggle(1);
        }else{
            item.setIcon(R.mipmap.listiview);
            item.setTitle("Show as list");
            isListView = true;
            resultFragment.toggle(0);
        }
    }
    private void initTabs(){
        CharSequence Titles[]={
                getResources().getText(R.string.search),
                getResources().getText(R.string.favourite),
        };
        tabs = (SlidingTabLayout)findViewById(R.id.tabs);
        pager =(ViewPager) findViewById(R.id.pager);
        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),Titles,2);
        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        pager.setAdapter(pagerAdapter);
        tabs.setDistributeEvenly(true);
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.accent);
            }
        });
        tabs.setViewPager(pager);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            openSearch();
            return true;
        }else{
            toggle();
        }
        return super.onOptionsItemSelected(item);
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
                query=searchTerm;
                toolbar.setTitle("Google Image Searcher");
                if(isConnected()) {
                   ResultOfSearch fragment = ((ResultOfSearch) getSupportFragmentManager().findFragmentByTag(pagerAdapter.getTag(0)));
                    fragment.sendRequest(searchTerm,11);
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
    //слушатель ответа нашего запроса

}
