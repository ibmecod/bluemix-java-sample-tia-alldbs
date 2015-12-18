/* Copyright IBM Corp. 2014 All Rights Reserved                      */
package com.sampleapp.db;


public class Influencer {
	
	private String twitterHandle;
	private int totalscore;
	private int fcount;
	private int fscore;
	private int rtcount;
	private int rtscore;
	private int mcount;
	
	public String getTwitterHandle() {
		return twitterHandle;
	}
	public void setTwitterHandle(String twitterHandle) {
		this.twitterHandle = twitterHandle;
	}
	public int getTotalscore() {
		return totalscore;
	}
	public void setTotalscore(int totalscore) {
		this.totalscore = totalscore;
	}
	public int getFcount() {
		return fcount;
	}
	public void setFcount(int fcount) {
		this.fcount = fcount;
	}
	public int getFscore() {
		return fscore;
	}
	public void setFscore(int fscore) {
		this.fscore = fscore;
	}
	public int getRtcount() {
		return rtcount;
	}
	public void setRtcount(int rtcount) {
		this.rtcount = rtcount;
	}
	public int getRtscore() {
		return rtscore;
	}
	public void setRtscore(int rtscore) {
		this.rtscore = rtscore;
	}
	public int getMcount() {
		return mcount;
	}
	public void setMcount(int mcount) {
		this.mcount = mcount;
	}
	

}
