package com.allNews.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.allNews.activity.AllNewsActivity;
import com.allNews.data.Event;
import com.allNews.managers.EWLoader;
import com.allNews.managers.MyPreferenceManager;
import com.allNews.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import gregory.network.rss.R;

public class EventAdapter extends BaseAdapter {
    private final SimpleDateFormat formatDate = new SimpleDateFormat("HH:mm dd MMM");

    private final LayoutInflater inflater;
    private final List<Event> values;
    private final int curTheme;

    public EventAdapter(Context context, List<Event> values) {
        this.inflater = LayoutInflater.from(context);
        this.values = values;

        curTheme = MyPreferenceManager.getCurrentTheme(context);
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
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            holder = new Holder();

            convertView = inflater.inflate(R.layout.list_item_event, null);
            holder.ivAvatar = (ImageView) convertView
                    .findViewById(R.id.ivAvatar);
            holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            holder.tvAddress = (TextView) convertView
                    .findViewById(R.id.tvAddress);
            holder.tvPrice = (TextView) convertView.findViewById(R.id.tvPrice);
            holder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
            holder.isMark = (ImageView) convertView
                    .findViewById(R.id.eventTop);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        populate(holder, this.values.get(position));
        return convertView;
    }

    private void populate(Holder holder, Event event) {
        holder.ivAvatar.setImageResource(R.drawable.ic_placeholder);
        if (event.getLogoSmallUrl() != null
                && Utils.isUrlValid(event.getLogoSmallUrl())) {
            EWLoader.loadAvatarEvent(inflater.getContext(),
                    event.getLogoSmallUrl(), holder.ivAvatar);
        }

        holder.tvName.setText(event.getName(inflater.getContext()));

        if (!event.getAddress().equals("")) {
            holder.tvAddress.setVisibility(View.VISIBLE);
            holder.tvAddress.setText(event.getAddress());
        } else
            holder.tvAddress.setVisibility(View.INVISIBLE);

        holder.tvPrice.setVisibility(View.VISIBLE);
        if (event.isFree())
            holder.tvPrice.setText("" + event.getPrice());
        else if (event.getPrice() > 0 && event.getCurrency() != null)
            holder.tvPrice
                    .setText(event.getPrice() + " " + event.getCurrency());
        else
            holder.tvPrice.setVisibility(View.INVISIBLE);


        holder.tvDate.setText(formatDate.format(new Date(
                event.getStartDate() * 1000)));
        if (event.isTop())
            holder.isMark.setVisibility(View.VISIBLE);
        else
            holder.isMark.setVisibility(View.GONE);

        if (curTheme == AllNewsActivity.THEME_DARK) {
            holder.tvAddress.setTextColor(inflater.getContext().getResources()
                    .getColor(R.color.white));
            holder.tvName.setTextColor(inflater.getContext().getResources()
                    .getColor(R.color.white));
            holder.tvDate.setTextColor(inflater.getContext().getResources()
                    .getColor(R.color.white));

            holder.tvAddress.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_small_locationr_dark, 0, 0, 0);
            holder.tvDate.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_small_calendar_dark, 0, 0, 0);
            holder.tvPrice.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_small_ticket_dark, 0, 0, 0);
        }
    }

    class Holder {
        ImageView ivAvatar;
        TextView tvName;
        TextView tvAddress;
        TextView tvDate;
        TextView tvPrice;
        ImageView isMark;
    }
}
