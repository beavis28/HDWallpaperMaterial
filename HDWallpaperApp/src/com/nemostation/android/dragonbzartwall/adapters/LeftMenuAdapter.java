package com.nemostation.android.dragonbzartwall.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nemostation.android.dragonbzartwall.BaseActivity;
import com.nemostation.android.dragonbzartwall.R;
import com.nemostation.android.dragonbzartwall.models.Category;

public class LeftMenuAdapter extends BaseAdapter {

	private List<Category> mCategories;
	private LayoutInflater mInflater;

	private static final int NUMBER_OF_FUNCTION_CAT = 5;

	public LeftMenuAdapter(Context context, List<Category> categories) {
		mCategories = categories;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return mCategories.size() + NUMBER_OF_FUNCTION_CAT; // + Recent and Favourites + Rate this app
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private class ViewHolder {
		public View divider;
		public View separator;
		public TextView label;
		public TextView categoriesLabel;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.drawer_list_item, null);
			holder = new ViewHolder();
			holder.divider = convertView
					.findViewById(R.id.drawer_list_item_divider);
			holder.separator = convertView
					.findViewById(R.id.drawer_list_separator);
			holder.label = (TextView) convertView
					.findViewById(R.id.drawer_list_item_text_label);
			holder.categoriesLabel = (TextView) convertView
					.findViewById(R.id.drawer_list_categories_label);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (position < NUMBER_OF_FUNCTION_CAT) {
			if (position == 0) {
				holder.label.setText(R.string.recent);
				holder.categoriesLabel.setVisibility(View.GONE);
				holder.divider.setVisibility(View.GONE);
				holder.separator.setVisibility(View.GONE);
			} else if (position == 1) {
				holder.label.setText(R.string.favourites);
				holder.categoriesLabel.setVisibility(View.GONE);
				holder.divider.setVisibility(View.GONE);
				holder.separator.setVisibility(View.GONE);
			} else if (position == 2) {
				holder.label.setText(R.string.about_us);
				holder.categoriesLabel.setVisibility(View.GONE);
				holder.divider.setVisibility(View.GONE);
				holder.separator.setVisibility(View.GONE);
			} else if (position == 3) {
				holder.label.setText(R.string.rate_app);
				holder.categoriesLabel.setVisibility(View.GONE);
				holder.divider.setVisibility(View.GONE);
				holder.separator.setVisibility(View.GONE);
			} else if (position == 4) {
				holder.label.setText(R.string.more_apps);
				holder.categoriesLabel.setVisibility(View.VISIBLE);
				holder.divider.setVisibility(View.GONE);
				holder.separator.setVisibility(View.VISIBLE);
			}
		} else {
			Category category = mCategories.get(position - NUMBER_OF_FUNCTION_CAT);
			holder.label.setText(category.getName());
			holder.divider.setVisibility(View.VISIBLE);
			holder.separator.setVisibility(View.GONE);
			holder.categoriesLabel.setVisibility(View.GONE);
		}
		holder.label.setTypeface(BaseActivity.sRobotoLight);
		holder.categoriesLabel.setTypeface(BaseActivity.sRobotoBlack);

		return convertView;
	}
}
