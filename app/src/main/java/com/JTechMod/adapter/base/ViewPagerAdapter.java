package com.JTechMod.adapter.base;

import java.util.ArrayList;

import com.JTechMod.fragment.base.BaseFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

/**
 * viewpager适配器
 * 
 * @author wuxubaiyang
 *
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {
	private ArrayList<BaseFragment> fragmentBases = new ArrayList<BaseFragment>();;

	public void setFragmentBases(ArrayList<BaseFragment> fragmentBases) {
		this.fragmentBases = fragmentBases;
	}

	public ViewPagerAdapter(FragmentManager fragmentManager) {
		super(fragmentManager);
	}

	public ViewPagerAdapter(FragmentManager fragmentManager, ArrayList<BaseFragment> fragmentBases) {
		super(fragmentManager);
		this.fragmentBases = fragmentBases;
	}

	@Override
	public Fragment getItem(int position) {
		return fragmentBases.get(position);
	}

	@Override
	public int getCount() {
		return fragmentBases.size();
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// super.destroyItem(container, position, object);
	}
}