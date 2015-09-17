package com.pedrocarrillo.expensetracker.ui;

import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.pedrocarrillo.expensetracker.R;
import com.pedrocarrillo.expensetracker.interfaces.IMainActivityListener;
import com.pedrocarrillo.expensetracker.ui.categories.CategoriesFragment;
import com.pedrocarrillo.expensetracker.ui.expenses.ExpensesFragment;
import com.pedrocarrillo.expensetracker.ui.reminders.ReminderFragment;
import com.pedrocarrillo.expensetracker.ui.statistics.StatisticsFragment;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, IMainActivityListener {

    @IntDef({NAVIGATION_MODE_STANDARD, NAVIGATION_MODE_TABS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface NavigationMode {}

    public static final int NAVIGATION_MODE_STANDARD = 0;
    public static final int NAVIGATION_MODE_TABS = 1;

    private int mCurrentMode = NAVIGATION_MODE_STANDARD;


    private DrawerLayout mainDrawerLayout;
    private NavigationView mainNavigationView;
    private TabLayout mainTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        setUpDrawer();
        setUpToolbar();
    }

    @NavigationMode
    public int getNavigationMode() {
        return mCurrentMode;
    };

    private void initUI() {
        mainDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mainTabLayout = (TabLayout)findViewById(R.id.tab_layout);
        mainNavigationView = (NavigationView)findViewById(R.id.nav_view);
    }

    private void setUpDrawer() {
        mainNavigationView.setNavigationItemSelectedListener(this);
        mainNavigationView.getMenu().performIdentifierAction(R.id.nav_expenses, 0);
    }

    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        menuItem.setChecked(true);
        mainDrawerLayout.closeDrawers();
        switchFragment(menuItem);
        return false;
    }

    @Override
    public void setTabs(List<String> tabList) {
        mainTabLayout.removeAllTabs();
        mainTabLayout.setVisibility(View.VISIBLE);
        for (String tab : tabList) {
            mainTabLayout.addTab(mainTabLayout.newTab().setText(tab));
        }
    }

    @Override
    public void setMode(@NavigationMode int mode) {
        if (mode == mCurrentMode) return;
        mCurrentMode = mode;
        switch (mode) {
            case NAVIGATION_MODE_STANDARD:
                setNavigationModeStandard();
                break;
            case NAVIGATION_MODE_TABS:
                setNavigationModeTabs();
                break;
        }
    }

    private void setNavigationModeTabs() {
        mainTabLayout.setVisibility(View.VISIBLE);
    }

    private void setNavigationModeStandard() {
        mainTabLayout.setVisibility(View.GONE);
    }

    private void switchFragment(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_expenses:
                replaceFragment(ExpensesFragment.newInstance(), false);
                break;
            case R.id.nav_categories:
                replaceFragment(CategoriesFragment.newInstance(), false);
                break;
            case R.id.nav_statistics:
                replaceFragment(StatisticsFragment.newInstance(), false);
                break;
            case R.id.nav_reminders:
                replaceFragment(ReminderFragment.newInstance(), false);
                break;

        }
    }
}