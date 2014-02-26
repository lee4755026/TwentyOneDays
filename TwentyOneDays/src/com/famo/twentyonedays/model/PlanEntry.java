package com.famo.twentyonedays.model;

import java.util.Date;

/**
 * 计划
 * @author LiChaofei 
 * <br/>2014-2-26 上午11:13:21
 */
public class PlanEntry{
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
	public Date startDate;
	/**
	 * 结束时间
	 */
	public Date endDate;  
	/**
	 * 提醒时间
	 */
	public String reminderTime;
}
