package com.ttkw.ui.adapters;

import java.util.ArrayList;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import com.ttkw.ui.fragments.RefreshableFragment;

public class PagerAdapter extends FragmentPagerAdapter {
	private final ArrayList<Fragment> mFragments = new ArrayList<Fragment>();

	public PagerAdapter(FragmentManager manager) {
		super(manager);
		// TODO Auto-generated constructor stub
	}

	public void addFragment(Fragment fragment) {
		mFragments.add(fragment);
		notifyDataSetChanged();
	}

	@Override
	public Fragment getItem(int position) {
		// TODO Auto-generated method stub
		return mFragments.get(position);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mFragments.size();
	}
	  /**
     * This method update the fragments that extends the {@link RefreshableFragment} class
     */
    public void refresh() {
        for (int i = 0; i < mFragments.size(); i++) {
            if( mFragments.get(i) instanceof RefreshableFragment ) {
                ((RefreshableFragment)mFragments.get(i)).refresh();
            }
        }
    }
}
