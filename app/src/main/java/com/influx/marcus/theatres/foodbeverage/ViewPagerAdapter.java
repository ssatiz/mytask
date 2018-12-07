package com.influx.marcus.theatres.foodbeverage;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.influx.marcus.theatres.R;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentPagerAdapter implements PagerSlidingTabStrip.CustomTabProvider {
    ArrayList<String> tabsName = new ArrayList<>();
    FoodCategoryList filtermodel;
    TextView tabTitle;

    public ViewPagerAdapter(FragmentManager manager, ArrayList<String> tabsList, FoodCategoryList model) {
        super(manager);
        this.tabsName = tabsList;
        this.filtermodel = model;
    }

    @Override
    public int getCount() {
        return tabsName.size();
    }


    @Override
    public View getCustomTabView(ViewGroup viewGroup, int position) {
        RelativeLayout tabLayout = (RelativeLayout)
                LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.tab_layout, viewGroup, false);
        tabTitle = tabLayout.findViewById(R.id.tab_title);

        String tab = tabsName.get(position);
        tabTitle.setText(tab.toUpperCase());

        return tabLayout;
    }

    @Override
    public void tabSelected(View tab) {

    }

    @Override
    public void tabUnselected(View tab) {

    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                return fragment = new RecommandedFrag(filtermodel.recommanedlist);
            case 1:
                return fragment = new Combofrag(filtermodel.combolist);
            case 2:
                return fragment = new PopcornFrag(filtermodel.popcornlist);

        }
        return fragment;
    }
}