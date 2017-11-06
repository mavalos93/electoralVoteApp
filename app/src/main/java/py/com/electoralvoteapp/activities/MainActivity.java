package py.com.electoralvoteapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.IntentCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import py.com.electoralvoteapp.R;
import py.com.electoralvoteapp.entities.Notifications;
import py.com.electoralvoteapp.entities.TableVotes;
import py.com.electoralvoteapp.fragments.MainFragment;
import py.com.electoralvoteapp.fragments.NotificationFragment;
import py.com.electoralvoteapp.fragments.TabletVotesFragment;
import py.com.electoralvoteapp.models.Sync;
import py.com.electoralvoteapp.utiles.AppPreferences;
import py.com.electoralvoteapp.utiles.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity implements NotificationFragment.OnItemNotificationsListenerSelected,
        TabletVotesFragment.OnItemTableVoteListenerSelected {

    private TextView mTextMessage;
    private ViewPager mViewPager;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mViewPager.setCurrentItem(0);
                    mTextMessage.setText(R.string.title_home);
                    break;
                case R.id.navigation_dashboard:
                    mViewPager.setCurrentItem(1);
                    mTextMessage.setText(R.string.title_dashboard);
                    break;
                case R.id.navigation_notifications:
                    mViewPager.setCurrentItem(2);
                    mTextMessage.setText(R.string.title_notifications);
                    break;
            }
            return true;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        setupViewPager(mViewPager);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.sync) {
            Intent intent = new Intent(this, SyncActivity.class);
            intent.putExtra(Sync.TABLE_VOTES, true);
            intent.putExtra(Sync.CANDIDATES, true);
            startActivity(intent);
            return true;
        }

        if (id == R.id.out) {
            startActivity(new Intent(this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK));
            AppPreferences.getAppPreferences(this).edit().clear().apply();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_main_menu, menu);
        return true;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(MainFragment.newInstance());
        adapter.addFrag(TabletVotesFragment.newInstance());
        adapter.addFrag(NotificationFragment.newInstance());
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onItemTableVoteListenerSelected(TableVotes tableVotes) {
    }

    @Override
    public void onItemNotificationsListenerSelected(Notifications transactions) {

    }
}
