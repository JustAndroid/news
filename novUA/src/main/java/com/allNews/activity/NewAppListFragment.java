package com.allNews.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

//import com.allNews.adapter.ArticleAdapter;
import com.allNews.adapter.NewAppListAdapter;
import com.allNews.data.NewApp.NewApp;
import com.allNews.managers.ManagerNewAppNewApi;

import java.util.ArrayList;
import java.util.List;

import gregory.network.rss.R;

public class NewAppListFragment extends Fragment {
	public static final String TAB_ALL = "All";
	private ListView newsFeedListView;

	private TextView txtMsg;
	private String tag;


//	private ArticleAdapter articleAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		// Log.e("onCreateView", getArguments().getString(TAB));
		View rootView = inflater
				.inflate(R.layout.news_tab_content, container, false);
		newsFeedListView = (ListView) rootView.findViewById(R.id.newsListView);
		SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) rootView
				.findViewById(R.id.refresh);
		mSwipeRefreshLayout.setEnabled(false);
		txtMsg = (TextView) rootView.findViewById(R.id.txtMsg);
		txtMsg.setVisibility(View.VISIBLE);
		refresh();
		return rootView;
	}

	public void setCustomTag(String tag) {
		this.tag = tag;
	}

	public String getCustomTag() {
		if (tag != null)
			return tag;
		return getArguments().getString(TabFragment.TAB);
	}

	public void refresh() {
		// txtMsg.setText(getResources().getString(R.string.msg_updating));
		showProgress();
		final Handler handler = new Handler();
		Runnable runnable = new Runnable() {
			@Override
			public void run() {

				final List<NewApp> values = getNewAppList();

				handler.post(new Runnable() {
					@Override
					public void run() {
						if (getActivity() != null) {
							//TODO
							setAdapter(values);
						}

					}

				});

			}

		};
		new Thread(runnable).start();

	}

	private List<NewApp> getNewAppList() {

		return ManagerNewAppNewApi.getNewApp(getActivity());

	}

//	private void _setAdapter(List<Article> articles)
//	{
//		hideProgress();
//		if (articles.size() == 0) {
//			newsFeedListView.setAdapter(null);
//			return;
//		}
//		txtMsg.setVisibility(View.GONE);
//
//		articleAdapter = new ArticleAdapter(getActivity(), 0, articles);
//
//		newsFeedListView.setAdapter(articleAdapter);
//		newsFeedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				//TODO send statistic, open Article Activity
//				Article article = articleAdapter.getItem(position);
//				ServerController.postArticleWasRead(getActivity(), article.getArticleID(), new Response.Listener<JSONObject>() {
//					@Override
//					public void onResponse(JSONObject response) {
//						try {
//							Log.e("postArticleWasRead", response.toString());
//						} catch (Exception e){
//							e.printStackTrace();
//						}
//					}
//				}, AllNewsActivity.getEmptyErrorListener());
//
//				((AllNewsActivity) getActivity()).hideProgressBar();
//				Intent intent = new Intent(getActivity(),
//						NewsCollectionActivity.class);
//				intent.putExtra(NewsCollectionActivity.NEW_APP_NODE_ID,
//						article.getArticleID());
//				intent.putExtra(
//						NewsCollectionActivity.NEW_APP_LIST_IDS_KEY,
//						getNewAppNodeID());
//				intent.putExtra(
//						NewsCollectionActivity.NEW_APP_POSITION_KEY,
//						position);
//				getActivity().startActivityForResult(intent, 1);
//			}
//		});
//
//	}

//	private ArrayList<Integer> getNewAppNodeID(){
//		ArrayList<Integer> list = new ArrayList<>(articleAdapter.getCount());
//		for (int i = 0; i < articleAdapter.getCount(); i++){
//			try {
//				list.add(Integer.parseInt(articleAdapter.getItem(i).getArticleID()));
//			} catch (Exception e){
//				e.printStackTrace();
//			}
//		}
//		return list;
//	}
	private void setAdapter(List<NewApp> newAppList) {
		hideProgress();
		if (newAppList.size() == 0) {
			// txtMsg.setText(getActivity().getResources().getString(R.string.no_news));
			// txtMsg.setVisibility(View.VISIBLE);
			newsFeedListView.setAdapter(null);
			return;
		}
		txtMsg.setVisibility(View.GONE);

		NewAppListAdapter adapter = new NewAppListAdapter(getActivity(),
				newAppList);

		newsFeedListView.setAdapter(adapter);
//		newsFeedListView.setOnScrollListener(new AbsListView.OnScrollListener() {
//			@Override
//			public void onScrollStateChanged(AbsListView listView, int scrollState) {
//				if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
//					Glide.with(NewAppListFragment.this).pauseRequests();
//				} else {
//					Glide.with(NewAppListFragment.this).resumeRequests();
//				}
//			}
//			@Override
//			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//			}
//		});
		adapter.notifyDataSetChanged();

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
		int currentPosition = newsFeedListView.getFirstVisiblePosition();
		if (currentPosition == newsFeedListView.getCount() - 1)
			return;
		newsFeedListView.setSelection(currentPosition + 1);
		newsFeedListView.clearFocus();
	}

	public void scrollToPrevious() {
		int currentPosition = newsFeedListView.getFirstVisiblePosition();
		if (currentPosition == 0)
			return;
		newsFeedListView.setSelection(currentPosition - 1);
		newsFeedListView.clearFocus();
	}
}
