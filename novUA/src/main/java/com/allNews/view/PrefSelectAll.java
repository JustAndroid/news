package com.allNews.view;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.allNews.managers.ManagerCategories;
import com.allNews.managers.ManagerSources;

import gregory.network.rss.R;

public class PrefSelectAll extends Preference {
	public static final int TYPE_CATEGORY = 0;
	public static final int TYPE_SOURCE = 1;

	private TextView title;
	private LayoutInflater mInflater;
	private Context context;
	private int type;

	public PrefSelectAll(Context context, int type) {
		super(context);
		mInflater = LayoutInflater.from(context);
		this.context = context;
		this.type = type;
	}

	public PrefSelectAll(Context context, AttributeSet attr) {
		super(context, attr);

	}

	public PrefSelectAll(Context context, AttributeSet attr, int style) {
		super(context, attr, style);
	}

	@Override
	protected View onCreateView(ViewGroup parent) {

        return mInflater.inflate(R.layout.pref_button_select_all_layout,
                parent, false);
	}

	@Override
	protected void onBindView(View view) {
		super.onBindView(view);
		TextView txt = (TextView) view.findViewById(R.id.hintText);
		title = (TextView) view.findViewById(R.id.btnSelectAll);
		if (type == TYPE_SOURCE) {
			txt.setVisibility(View.GONE);
			setSourceTitle();
		} else if (type == TYPE_CATEGORY)
			setCategoryTitle();
	}

	public void setSourceTitle() {
		
		if (ManagerSources.isAllSourcesChecked(context) == 1)
			title.setText(context.getResources()
					.getString(R.string.filter_none));
		else
			title.setText(context.getResources().getString(R.string.filter_all));

	}

	public void setCategoryTitle() {
	
		if (ManagerCategories.isAllCategoriesChecked(context) == 1)
			title.setText(context.getResources()
					.getString(R.string.filter_none));
		else
			title.setText(context.getResources().getString(R.string.filter_all));

	}
}
