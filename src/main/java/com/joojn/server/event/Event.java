package com.joojn.server.event;

import java.util.ArrayList;

public class Event {

	public void call() {
		final ArrayList<EventData> dataList = EventManager.get(this.getClass());
		
		if(dataList != null) {
			for(EventData data : dataList) {
				try {
					data.target.invoke(data.source, this);
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
}
