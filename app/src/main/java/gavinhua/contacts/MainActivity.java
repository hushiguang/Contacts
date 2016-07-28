package gavinhua.contacts;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {


    @Bind(R.id.container)
    ViewPager mViewPager;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.tabLayout)
    TabLayout tabLayout;

    @Bind(R.id.fab)
    FloatingActionButton fab;

    private int tabHeight;

    private int fabSize;

    ContactsFragment contactsFragment;


    private final int GET_WRITE_CONTACTS_PERMISSION = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        contactsFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 冷启动优化
        setTheme(R.style.AppTheme_NoActionBar);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && PackageManager.PERMISSION_DENIED == ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS)) {
            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS}, GET_WRITE_CONTACTS_PERMISSION);
        } else {
            init();
        }
    }

    private void init() {
        contactsFragment = new ContactsFragment();
        mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(mViewPager);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this, AddContactActivity.class), ContactsFragment.REQUEST_CODE);
                overridePendingTransition(R.anim.dialpad_slide_in_bottom, android.R.anim.fade_out);
            }
        });

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabHeight = tabLayout.getHeight();
            }
        });

        fab.post(new Runnable() {
            @Override
            public void run() {
                fabSize = fab.getWidth();
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == GET_WRITE_CONTACTS_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                init();
            } else {
                Snackbar.make(mViewPager, "获取权限失败，无法使用", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    /**
     * 动画改变fab大小
     */
    private void animateFABVisibleChange(boolean isShow) {
        Animation animation;
        if (isShow) {
            animation = new ScaleAnimation(0f, 1f, 0f, 1f, fabSize / 2, fabSize / 2);
        } else {
            animation = new ScaleAnimation(1f, 0f, 1f, 0f, fabSize / 2, fabSize / 2);
        }
        animation.setDuration(100L);
        animation.setFillAfter(true);
        fab.startAnimation(animation);
    }

    /**
     * 动画改变tab高度
     */
    private void animateTabHeightChange(int oldValue, int newValue) {
        ValueAnimator localValueAnimator = ValueAnimator.ofInt(oldValue, newValue);
        localValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                ViewGroup.LayoutParams layoutParams = tabLayout.getLayoutParams();
                layoutParams.height = (int) valueAnimator.getAnimatedValue();
                tabLayout.setLayoutParams(layoutParams);
            }
        });
        localValueAnimator.setDuration(100L).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());

        MenuItem searchMenu = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchMenu.getActionView();
        searchView.setSearchableInfo(searchableInfo);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (onSearchViewQueryTextChangeListener != null)
                    onSearchViewQueryTextChangeListener.onQueryTextChange(newText);
                return false;
            }
        });

        // searchView.setOnCloseListener(); 无效；
        // 或者重写SearchView，甩出接扣
        MenuItemCompat.setOnActionExpandListener(searchMenu, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                mViewPager.setCurrentItem(1);
                animateFABVisibleChange(false);
                animateTabHeightChange(tabHeight, 0);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                animateTabHeightChange(0, tabHeight);
                animateFABVisibleChange(true);
                return true;
            }
        });
        return true;
    }

    interface onSearchViewQueryTextChangeListener {
        void onQueryTextChange(String newText);
    }

    private onSearchViewQueryTextChangeListener onSearchViewQueryTextChangeListener;

    public void setOnSearchViewQueryTextChangeListener(MainActivity.onSearchViewQueryTextChangeListener onSearchViewQueryTextChangeListener) {
        this.onSearchViewQueryTextChangeListener = onSearchViewQueryTextChangeListener;
    }


    class MyPagerAdapter extends FragmentPagerAdapter {

        String[] titles;

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
            titles = getResources().getStringArray(R.array.tab_titles);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new FrequentlyFragment();
                case 1:
                    return contactsFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

    }
}
