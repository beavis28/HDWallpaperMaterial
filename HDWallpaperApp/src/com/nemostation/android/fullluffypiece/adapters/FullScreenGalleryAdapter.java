package com.nemostation.android.fullluffypiece.adapters;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class FullScreenGalleryAdapter extends FragmentPagerAdapter {
	private List<Fragment> fragments;

	public FullScreenGalleryAdapter(FragmentManager fm, List<Fragment> fragments) {
		super(fm);
		this.fragments = fragments;
	}

	@Override
	public Fragment getItem(int position) {
		return fragments.get(position);
	}

	@Override
	public int getCount() {
		return this.fragments.size();
	}
}
