package com.mobitv.ott.adapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import com.mobitv.ott.fragment.CommonPageFragment;

import java.util.List;

/**
 * Created by sonth on 3/2/2017.
 */

public class PersonalPagerAdapter extends FragmentStatePagerAdapter {
    private List<CommonPageFragment> fragmentList;
    private List<String> titleList;

    public PersonalPagerAdapter(FragmentManager fm, List<CommonPageFragment> fragmentList, List<String> titleList) {
        super(fm);
        this.fragmentList = fragmentList;
        this.titleList = titleList;
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }
}
