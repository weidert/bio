package com.heliomug.utils;

import java.util.logging.Logger;

public class StatusDisplayerSingleton {
	private static StatusDisplayer displayer = new StatusDisplayer() {
		@Override
		public void displayStatus(Object status) {
			Logger.getGlobal().info(status.toString());
		}
	};
	
	public static void setStatusDisplayer(StatusDisplayer displayer) {
		StatusDisplayerSingleton.displayer = displayer;
	}
	
	public static StatusDisplayer getStatusDisplayer() {
		return displayer;
	}
}
