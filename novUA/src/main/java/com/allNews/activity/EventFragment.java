package com.allNews.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.allNews.adapter.EventAdapter;
import com.allNews.data.Event;
import com.allNews.managers.ManagerEvents;
import com.allNews.managers.MyPreferenceManager;
import com.allNews.web.Statistic;

import java.util.List;

import gregory.network.rss.R;

public class EventFragment extends Fragment {

	private View rootView;
	private ListView eventFeedListView;

	private TextView txtMsg;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.eventslist_layout, container,
				false);
		eventFeedListView = (ListView) rootView
				.findViewById(R.id.eventsListView);
		txtMsg = (TextView) rootView.findViewById(R.id.txtMsg);
		setMsg();

		refreshEvents(null);
		return rootView;
	}

	@SuppressLint("NewApi")
	private void setMsg() {
		final LinearLayout eventMsgLaoyt = (LinearLayout) rootView
				.findViewById(R.id.eventMsgLayout);
		if (!MyPreferenceManager.showEventMsg(getActivity()))
			eventMsgLaoyt.setVisibility(View.GONE);

		Button eventMsgBtn = (Button) rootView.findViewById(R.id.eventMsgBtn);
		eventMsgBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				eventMsgLaoyt.setVisibility(View.GONE);
				MyPreferenceManager.disableEventMsg(getActivity());
			}
		});
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(getActivity());

		int curTheme =  MyPreferenceManager.getCurrentTheme(getActivity());
		if (curTheme == AllNewsActivity.THEME_DARK) {
			if (Build.VERSION.SDK_INT >= 16) {
				eventMsgLaoyt.setBackground(getResources().getDrawable(
						R.drawable.event_msg_bg));
			} else {
				eventMsgLaoyt.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.event_msg_bg));
			}

			eventMsgBtn.setBackgroundColor(getResources().getColor(
					R.color.eventMsgBg));

		}

	}

	public void refreshEvents(final String str) {
		showProgress();
		final Handler handler = new Handler();
		Runnable runnable = new Runnable() {
			@Override
			public void run() {

				final List<Event> values = getEvents(str);

				handler.post(new Runnable() {
					@Override
					public void run() {
						if (getActivity() != null)
							setAdapter(values);

					}

				});

			}

		};
		new Thread(runnable).start();
	}

	protected List<Event> getEvents(String str) {
		List<Event> values = null;
		if (str != null) {
			values = ManagerEvents.getEvents(getActivity(), str);
		} else {
			values = ManagerEvents.getAllEventsFromDb(getActivity());
		}
		return values;
	}

	public void refreshEvents(final String filterFrom, final String filterTo) {
		showProgress();
		final Handler handler = new Handler();
		Runnable runnable = new Runnable() {
			@Override
			public void run() {

				final List<Event> values = ManagerEvents.getAllEventsFromDb(
						getActivity(), filterFrom, filterTo);

				handler.post(new Runnable() {
					@Override
					public void run() {
						if(getActivity()!=null)
						setAdapter(values);

					}

				});

			}

		};
		new Thread(runnable).start();

	}

	private void setAdapter(final List<Event> eventsList) {
		hideProgress();
		if (eventsList == null || eventsList.size() == 0   ) {
			txtMsg.setVisibility(View.VISIBLE);
			eventFeedListView.setAdapter(null);
			Log.e("Set adapter", "eventList 0");
			return;
		}
		txtMsg.setVisibility(View.GONE);
		EventAdapter eventAdapter = new EventAdapter(getActivity(), eventsList);
		eventFeedListView.setAdapter(eventAdapter);
		eventFeedListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Event event = eventsList.get(arg2);

				Statistic.sendStatistic(getActivity(),
						Statistic.CATEGORY_EVENTS,
						Statistic.ACTION_CLICK_EVENT,
						event.getName(getActivity()), 0L);

				Intent intent = new Intent(getActivity(), EventActivity.class);
				intent.putExtra("event_id", event.getEventId());
				startActivity(intent);
			}
		});
		
	}

	private void hideProgress() {
		if (getActivity() != null)
			((AllNewsActivity) getActivity()).hideProgressBar();
	}

	private void showProgress() {
		if (getActivity() != null)
			((AllNewsActivity) getActivity()).showProgressBar();
	}
	
	public void scrollToNext() {  
        int currentPosition = eventFeedListView.getFirstVisiblePosition();  
        if (currentPosition == eventFeedListView.getCount() - 1)   
            return;  
        eventFeedListView.setSelection(currentPosition + 1);  
        eventFeedListView.clearFocus();  
    }  
          
    public void scrollToPrevious() {  
        int currentPosition = eventFeedListView.getFirstVisiblePosition();  
        if (currentPosition == 0)   
            return;  
        eventFeedListView.setSelection(currentPosition - 1);  
        eventFeedListView.clearFocus();  
    }  
}
