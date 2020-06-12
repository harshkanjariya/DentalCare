package com.harsh.dentalcare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

public class AppointmentActivity extends AppCompatActivity {
    ViewPager pager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        pager=findViewById(R.id.viewpager);
        MyPagerAdapter adapter= new MyPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);

        TabLayout tabLayout=findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(pager);
    }
    static class MyPagerAdapter extends FragmentStatePagerAdapter {
        String[]titles={"New","Old"};
        MyPagerAdapter(@NonNull FragmentManager fm){
            super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }
        @Override
        public int getCount() {return titles.length;}
        @NonNull
        @Override
        public Fragment getItem(int position) {
            Fragment fragment=null;
            switch (position){
                case 0:
                    fragment=new NewAppointmentFragment();
                    break;
                case 1:
                    fragment=new OldAppointmentFragment();
                    break;
            }
            assert fragment != null;
            return fragment;
        }
        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }
}
