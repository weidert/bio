package com.heliomug.utils;

public class DataSet {
	private static final int DEFAULT_BINS = 10;
	
	private double[] x;
	
	public DataSet(double[] x) {
		this.x = new double[x.length];
		for (int i = 0 ; i < x.length ; i++) {
			this.x[i] = x[i];
		}
	}

	public int[] hist() {
		return hist(DEFAULT_BINS);
	}
	
	public int[] hist(int bins) {
		return hist(min(), max(), bins);
	}
	
	public int[] hist(double min, double max, int bins) {
		double perBin = (max - min) / bins;
		int[] toRet = new int[bins];
		for (int i = 0 ; i < x.length ; i++) {
			int bin = (int)((x[i] - min) / perBin);
			if (bin >= 0 && bin < bins) {
				toRet[bin]++;
			}
		}
		return toRet;
	}
	
	public double size() {
		return x.length;
	}
	
	public double min() {
		double min = Double.MAX_VALUE;
		for (int i = 0 ; i < x.length ; i++) {
			if (x[i] < min) min = x[i];
		}
		return min;
	}
	
	public double max() {
		double max = Double.MIN_VALUE;
		for (int i = 0 ; i < x.length ; i++) {
			if (x[i] > max) max = x[i];
		}
		return max;
	}

	public double sum() {
		double sum = 0;
		for (int i = 0 ; i < x.length ; i++) {
			sum += x[i];
		}
		return sum;
	}
	
	public double mu() {
		return this.sum() / this.size();
	}
	
	public double s() {
		double mu = mu();
		double tot = 0;
		for (int i = 0 ; i < x.length ; i++) {
			tot += (x[i] - mu) * (x[i] - mu);
		}
		return Math.sqrt(tot / (size() - 1));
	}

	public double sigma() {
		double mu = mu();
		double tot = 0;
		for (int i = 0 ; i < x.length ; i++) {
			tot += (x[i] - mu) * (x[i] - mu);
		}
		return Math.sqrt(tot / size());
	}
	
	public String statsSummary() {
		StringBuilder sb = new StringBuilder();
		sb.append("n: \t" + size() + "\n");
		sb.append("avg: \t" + mu() + "\n");
		sb.append("sum: \t" + sum() + "\n");
		sb.append("min: \t" + min() + "\n");
		sb.append("max: \t" + max() + "\n");
		sb.append("samp sd: \t" + s() + "\n");
		sb.append("pop sd: \t" + sigma() + "\n");
		return sb.toString();
	}
}
