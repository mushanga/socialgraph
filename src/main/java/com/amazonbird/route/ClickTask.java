package com.amazonbird.route;

import com.amazonbird.db.data.Click;

public class ClickTask implements Runnable{

	private Click click;
	
	public ClickTask(Click click){
		this.click = click;
	}
	@Override
	public void run() {
		ClickMgrImpl.getInstance().addClick(click);
		
	}

}
