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
import com.dandewine.user.tocleveroad.model.GoogleSearchResponse;
import com.dandewine.user.tocleveroad.networking.SampleRetrofitSpiceRequest;
import com.dandewine.user.tocleveroad.networking.SampleRetrofitSpiceService;
import com.dandewine.user.tocleveroad.other.SlidingTabLayout;
import com.dandewine.user.tocleveroad.adapters.ViewPagerAdapter;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.quinny898.library.persistentsearch.SearchBox;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.toolbar) Toolbar toolbar;
    @InjectView(R.id.searchbox) SearchBox searchBox;
    public ViewPagerAdapter pagerAdapter;
    private SlidingTabLayout tabs;
    ViewPager pager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        initTabs();//устанавливаем вкладки
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
                return getResources().getColor(R.color.ColorAccent);
            }
        });
        tabs.setViewPager(pager);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar result_item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            openSearch();
            return true;
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
                toolbar.setTitle("toCleveroad");
                if(isConnected()) {
                    ((ResultOfSearch)getSupportFragmentManager().findFragmentByTag(pagerAdapter.getTag(0))).sendRequest(searchTerm);
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
