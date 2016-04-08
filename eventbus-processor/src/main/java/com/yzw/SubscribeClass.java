package com.yzw;

import com.yzw.annotations.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubscribeClass {
	List<String> tags;
	Map<String, TagInfo> tagInfoMap;

	SubscribeClass() {
		this.tags = new ArrayList<String>();
		this.tagInfoMap = new HashMap<String, TagInfo>();
	}

	public void addTag(String tag, int priority, ThreadMode threadmode) {
		tags.add(tag);
		tagInfoMap.put(tag, new TagInfo(priority,threadmode));
	}

	public static class TagInfo {
		int priority;
		ThreadMode threadmode;

		TagInfo(int p, ThreadMode tm) {
			this.priority = p;
			this.threadmode = tm;
		}
	}

	@Override
	public String toString() {
		return "SubscribeClass [tags=" + tags + ", tagInfoMap=" + tagInfoMap
				+ "]";
	}
	
	
}
