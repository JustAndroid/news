package com.allNews.utils;

import android.content.Context;
import android.content.res.Resources;

import java.util.HashSet;
import java.util.Set;

public class ResourcesImpl extends Resources {
    private Set<Integer> mActionBarEmbedTabsIds = new HashSet<>();

	public ResourcesImpl(Context context, Resources resources) {
		super(resources.getAssets(), resources.getDisplayMetrics(),
				resources.getConfiguration());

        Resources mResources = resources;

		String packageName = context.getPackageName();
		mActionBarEmbedTabsIds.add(mResources.getIdentifier(
                "abc_action_bar_embed_tabs", "bool", packageName));
		mActionBarEmbedTabsIds.add(mResources.getIdentifier(
                "abc_action_bar_embed_tabs_pre_jb", "bool", packageName));
		mActionBarEmbedTabsIds.add(mResources.getIdentifier(
                "action_bar_embed_tabs", "bool", "android"));
		mActionBarEmbedTabsIds.add(mResources.getIdentifier(
                "action_bar_embed_tabs_pre_jb", "bool", "android"));
		mActionBarEmbedTabsIds.remove(0);
	}

	@Override
	public boolean getBoolean(int id) throws NotFoundException {
		if (mActionBarEmbedTabsIds.contains(id)) {
			return false; // stacked ot embed goes here
		}
		return super.getBoolean(id);
	}
}