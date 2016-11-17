package com.heliomug.utils;

import java.util.logging.Logger;

public class GlobalStatusDisplayer {
	private static StatusDisplayer displayer = new StatusDisplayer() {
		@Override
		public void displayStatus(Object status) {
			Logger.getGlobal().info(status.toString());
		}
	};
	
	public static void setStatusDisplayer(StatusDisplayer displayer) {
		GlobalStatusDisplayer.displayer = displayer;
	}
	
	public static StatusDisplayer get() {
		return displayer;
	}
}
