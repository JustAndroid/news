package com.allNews.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.allNews.activity.AllNewsActivity;
import com.allNews.managers.ManagerUpdates;

import gregory.network.rss.R;
public class ProgressView {
	// private Context context;
	private LinearLayout progressLayout;
	private ProgressBar progress;
	private TextView progressText;
	private int maxProgress = 0;

	public ProgressView(View v) {
		// this.context = context;
		initWidget(v);
 
	}

	private void initWidget(View v) {
		progressLayout = (LinearLayout) v.findViewById(R.id.downloadNewsLayout);
		progress = (ProgressBar) v.findViewById(R.id.downloadNewsProgressBar);
		progressText = (TextView) v.findViewById(R.id.downloadNewsText);
		Button stopBtn = (Button) v.findViewById(R.id.downloadNewsStopBtn);
		stopBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showStopUpdateDialog(v.getContext());

			}
		});
	}

	public void showProgress() {
		progressLayout.setVisibility(View.VISIBLE);
		progressText.setText(progress.getProgress() + "/" + maxProgress);
	}

	public void hideProgress() {
		progressLayout.setVisibility(View.GONE);
	}

	public void setMaxProgress(int max) {
		progress.setProgress(0);
		max = max + 20; // 20 - добавляем новости из ТОП-20 
		progress.setMax(max);
		this.maxProgress = max;
	}

	public void setProgress(int value) {
		progress.setProgress(value);
		progressText.setText(value + "/" + maxProgress);
	}

	public void addProgress(int value) {
		int newProgress = progress.getProgress() + value;
		if (newProgress > maxProgress)
			newProgress = maxProgress;
		progress.setProgress(newProgress);
		progressText.setText(newProgress + "/" + maxProgress);
	}

	public void showStopUpdateDialog(final Context context) {

		new AlertDialog.Builder(context)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle(R.string.stop_update_title)
				.setMessage(context.getString(R.string.stop_update_msg))
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {

								ManagerUpdates.removeAllUrl(context);
								hideProgress();
								
								((AllNewsActivity)context).nowUpdated = false;
							}

						}).setNegativeButton(R.string.no, null).show();
	}
}
