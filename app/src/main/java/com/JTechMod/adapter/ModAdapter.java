package com.JTechMod.adapter;

import com.JTechMod.custom.jrecyclerview.RecyclerAdapter;
import com.JTechMod.custom.jrecyclerview.RecyclerHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 模型
 * 
 * @author JTech
 *
 */
public class ModAdapter extends RecyclerAdapter<String> {

	public ModAdapter(Context context) {
		super(context);
	}

	@Override
	public View createView(LayoutInflater inflater, ViewGroup parent, int viewType) {
		return inflater.inflate(android.R.layout.simple_expandable_list_item_1, parent, false);
	}

	@Override
	public void convert(RecyclerHolder holder, String item, int position) {
		holder.setText(android.R.id.text1, item);
	}

}