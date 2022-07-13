package org.secuso.privacyfriendlywerwolf.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener;
import androidx.core.app.TaskStackBuilder;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import org.secuso.privacyfriendlywerwolf.R;
import org.secuso.privacyfriendlywerwolf.dialog.PlayerInputDialog;

/**
 * Base activity is the template for all other activities to inherite
 * <p>
 * Based on Chris from 04.07.2016
 *
 * @author Florian Staubach <florian.staubach@stud.tu-darmstadt.de>
 */
public abstract class BaseActivity extends AppCompatActivity implements OnNavigationItemSelectedListener {

    // delay to launch nav drawer item, to allow close animation to play
    static final int NAVDRAWER_LAUNCH_DELAY = 250;
    // fade in and fade out durations for the main content when switching between
    // different Activities of the app through the Nav Drawer
    static final int MAIN_CONTENT_FADEOUT_DURATION = 150;
    static final int MAIN_CONTENT_FADEIN_DURATION = 250;

    // Navigation drawer:
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;

    protected Toolbar toolbar;

    // Helper
    private Handler mHandler;
    protected SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mHandler = new Handler();

        overridePendingTransition(0, 0);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    protected int getNavigationDrawerID() {
        return 0;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        final int itemId = item.getItemId();

        return goToNavigationItem(itemId);
    }

    protected boolean goToNavigationItem(final int itemId) {

        if (itemId == getNavigationDrawerID()) {
            // just close drawer because we are already in this activity
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }

        // delay transition so the drawer can close
        mHandler.postDelayed(() -> callDrawerItem(itemId), NAVDRAWER_LAUNCH_DELAY);

        mDrawerLayout.closeDrawer(GravityCompat.START);

        selectNavigationItem(itemId);

        // fade out the active activity
        View mainContent = findViewById(R.id.main_content);
        if (mainContent != null) {
            mainContent.animate().alpha(0).setDuration(MAIN_CONTENT_FADEOUT_DURATION);
        }
        return true;
    }

    // set active navigation item
    private void selectNavigationItem(int itemId) {
        for (int i = 0; i < mNavigationView.getMenu().size(); i++) {
            boolean b = itemId == mNavigationView.getMenu().getItem(i).getItemId();
            mNavigationView.getMenu().getItem(i).setChecked(b);
        }
    }

    /**
     * Enables back navigation for activities that are launched from the NavBar. See
     * {@code AndroidManifest.xml} to find out the parent activity names for each activity.
     *
     * @param intent
     */
    private void createBackStack(Intent intent) {
        TaskStackBuilder builder = TaskStackBuilder.create(this);
        builder.addNextIntentWithParentStack(intent);
        builder.startActivities();
    }

    /**
     * handles the items of the main navigation drawer
     *
     * @param itemId
     */
    private void callDrawerItem(final int itemId) {

        Intent intent;

        switch (itemId) {
            case R.id.nav_main:
                intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.nav_new_game:
                PlayerInputDialog playerInputDialog = new PlayerInputDialog();
                playerInputDialog.setCancelable(false);
                playerInputDialog.show(getSupportFragmentManager(), "dialog_from_drawer");
                break;
            case R.id.nav_join_game:
                intent = new Intent(this, StartClientActivity.class);
                createBackStack(intent);
                break;
            case R.id.nav_about:
                intent = new Intent(this, AboutActivity.class);
                createBackStack(intent);
                break;
            case R.id.nav_help:
                intent = new Intent(this, HelpActivity.class);
                createBackStack(intent);
                break;
            case R.id.nav_tutorial:
                intent = new Intent(this, TutorialActivity.class);
                intent.putExtra("force", true);
                createBackStack(intent);
                break;
            case R.id.nav_settings:
                intent = new Intent(this, SettingsActivity.class);
                intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, SettingsActivity.GeneralPreferenceFragment.class.getName());
                intent.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);
                createBackStack(intent);
                break;
            default:
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (getSupportActionBar() == null) {
            setSupportActionBar(toolbar);
        }

        if (!this.getClass().equals(GameActivity.class)) {
            mDrawerLayout = findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            mDrawerLayout.addDrawerListener(toggle);
            toggle.syncState();

            mNavigationView = findViewById(R.id.nav_view);
            mNavigationView.setNavigationItemSelectedListener(this);

            selectNavigationItem(getNavigationDrawerID());
        }


        View mainContent = findViewById(R.id.main_content);
        if (mainContent != null) {
            mainContent.setAlpha(0);
            mainContent.animate().alpha(1).setDuration(MAIN_CONTENT_FADEIN_DURATION);
        }
    }


}
