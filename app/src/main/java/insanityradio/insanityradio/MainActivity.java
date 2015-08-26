package insanityradio.insanityradio;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.socialize.CommentUtils;
import com.socialize.entity.Entity;

public class MainActivity extends FragmentActivity {
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

        MenuItem commentItem = menu.findItem(R.id.action_comment);
        commentItem.setVisible(DataModel.getEnableComment(this));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_comment:
                Entity entity = Entity.newInstance("insanityradio", null);

                CommentUtils.showCommentView(this, entity);

                return true;
            case R.id.action_share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, DataModel.getShareText(this));

                startActivity(intent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateUI() {
        invalidateOptionsMenu();
    }
}
