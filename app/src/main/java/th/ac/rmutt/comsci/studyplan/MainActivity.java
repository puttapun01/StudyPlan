package th.ac.rmutt.comsci.studyplan;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import th.ac.rmutt.comsci.studyplan.Fragment.ClassTableFragment;
import th.ac.rmutt.comsci.studyplan.Fragment.HomeworkFragment;
import th.ac.rmutt.comsci.studyplan.Fragment.PlanFragment;
import th.ac.rmutt.comsci.studyplan.Fragment.ProfileFragment;
import th.ac.rmutt.comsci.studyplan.Fragment.SettingFragment;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*startControlData();*/


        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_check);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_table);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_home);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_time);
        tabLayout.getTabAt(4).setIcon(R.drawable.ic_menu);

        mViewPager.setCurrentItem(2);

    }


    @Override
    protected void attachBaseContext (Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position){
                case 0 :
                    return new HomeworkFragment();
                case 1 :
                    return new ClassTableFragment();
                case 2 :
                    return new ProfileFragment();
                case 3 :
                    return new PlanFragment();
                default :
                    return new SettingFragment();
            }
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }
}