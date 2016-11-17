package com.heliomug.utils;

import java.util.logging.Logger;

public class GlobalStatusDisplayer {
	private static StatusDisplay displayer = new StatusDisplay() {
		@Override
		public void displayStatus(Object status) {
			Logger.getGlobal().info(status.toString());
		}
	};
	
	public static void setStatusDisplayer(StatusDisplay displayer) {
		GlobalStatusDisplayer.displayer = displayer;
	}
	
	public static StatusDisplay get() {
		return displayer;
	}
}
