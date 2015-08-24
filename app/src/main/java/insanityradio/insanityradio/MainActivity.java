package insanityradio.insanityradio;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ShareActionProvider;

public class MainActivity extends FragmentActivity {
    private ShareActionProvider shareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);

        MenuItem shareMenuItem = menu.findItem(R.id.action_share);

        shareActionProvider = (ShareActionProvider) shareMenuItem.getActionProvider();

        setShareIntent();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_comment:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setShareIntent() {
        if (shareActionProvider != null) {
            String shareText = DataModel.getShareText(this);

            Intent shareIntent = ShareCompat.IntentBuilder.from(this).setType("text/plain").setText(shareText).getIntent();

            shareActionProvider.setShareIntent(shareIntent);
        }
    }
}
