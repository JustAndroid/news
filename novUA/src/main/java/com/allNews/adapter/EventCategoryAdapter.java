package com.allNews.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.allNews.data.Event;
import com.allNews.managers.MyPreferenceManager;

import java.util.List;

import gregory.network.rss.R;

public class EventCategoryAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private final List<Event> values;

    public EventCategoryAdapter(Context context, List<Event> values) {
        this.inflater = LayoutInflater.from(context);
        this.values = values;
    }

    @Override
    public int getCount() {
        return this.values.size();
    }

    @Override
    public Event getItem(int position) {
        return this.values.get(position);
    }

    @Override
    public long getItemId(int position) {
        return this.values.get(position).getEventId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder;
        if (convertView == null) {
            holder = new Holder();

            convertView = inflater.inflate(R.layout.event_category_item, null);

            holder.tvCategory = (TextView) convertView
                    .findViewById(R.id.event_filter_category_item);

            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        if (position == 0)
            populateSelectAllField(holder);
        else
            populate(holder, this.values.get(position));

        return convertView;
    }

    private void populateSelectAllField(Holder holder) {
        if (MyPreferenceManager.isAllEventCategorySelected(inflater
                .getContext())) {
            holder.tvCategory.setText(inflater.getContext().getResources()
                    .getString(R.string.filter_none));
            holder.tvCategory.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                    R.drawable.unselected_checkbox, 0);
        } else {
            holder.tvCategory.setText(inflater.getContext().getResources()
                    .getString(R.string.filter_all));
            holder.tvCategory.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                    R.drawable.selected_checkbox, 0);
        }
    }

    private void populate(Holder holder, Event event) {
        holder.tvCategory.setText(event.getCategory());
        if (MyPreferenceManager.eventCategoryFilter(inflater.getContext(),
                event.getCategory()))
            holder.tvCategory.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                    R.drawable.selected_checkbox, 0);
        else
            holder.tvCategory.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                    R.drawable.unselected_checkbox, 0);
    }

    class Holder {

        TextView tvCategory;

    }
}
