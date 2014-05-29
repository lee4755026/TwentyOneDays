package com.famo.twentyonedays.model;

import java.util.Date;
import com.famo.twentyonedays.ui.widget.SlideViewWidget;

/**
 * 计划
 * @author LiChaofei 
 * <br/>2014-2-26 上午11:13:21
 */
public class PlanEntry{
	
	
	public PlanEntry() {
	}
	public PlanEntry(int id, String title) {
		this.id = id;
		this.title = title;
	}
	public int id;
	/**
	 * 标题
	 */
	public String title;
	/**
	 * 内容
	 */
	public String content;
	/**
	 * 开始时间
	 */
	public String startDate;
	/**
	 * 结束时间
	 */
	public String endDate;  
	/**
	 * 提醒时间
	 */
	public String reminderTime;
	/**
	 * 创建时间
	 */
	public String createTime;
	
	
	public int color;	
	public Date syncTime;
	public int flag;
	public String reserved;
	
	public SlideViewWidget slideView;

	@Override
	public String toString() {
		return "PlanEntry [id=" + id + ", title=" + title + ", content="
				+ content + ", startDate=" + startDate + ", endDate=" + endDate
				+ ", reminderTime=" + reminderTime + ", createTime="
				+ createTime + ", color=" + color + ", syncTime=" + syncTime
				+ ", flag=" + flag + ", reserved=" + reserved + ", slideView="
				+ slideView + "]";
	}
	
	
}
