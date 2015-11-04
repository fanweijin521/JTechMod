package com.JTechMod.adapter.base;

import java.util.ArrayList;

import com.JTechMod.fragment.base.BaseFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * viewpager适配器
 * 
 * @author wuxubaiyang
 *
 */
public class ViewPagerStateAdapter extends FragmentStatePagerAdapter {
	private ArrayList<BaseFragment> fragmentBases = new ArrayList<BaseFragment>();

	public ArrayList<BaseFragment> getFragmentBases() {
		return fragmentBases;
	}

	public void setFragmentBases(ArrayList<BaseFragment> fragmentBases) {
		this.fragmentBases = fragmentBases;
	}

	public ViewPagerStateAdapter(FragmentManager fm) {
		super(fm);
	}

	public ViewPagerStateAdapter(FragmentManager fm, ArrayList<BaseFragment> fragmentBases) {
		super(fm);
		this.fragmentBases = fragmentBases;
	}

	@Override
	public Fragment getItem(int arg0) {
		return fragmentBases.get(arg0);
	}

	@Override
	public int getCount() {
		return fragmentBases.size();
	}
}