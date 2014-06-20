package com.famo.twentyonedays.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.famo.twentyonedays.R;
import com.famo.twentyonedays.model.PlanEntry;
import com.famo.twentyonedays.ui.DetailActivity;
import com.famo.twentyonedays.ui.MainActivity;
import com.famo.twentyonedays.ui.widget.ListViewCustom;
import com.famo.twentyonedays.ui.widget.SlideViewWidget;

public class PlansAdapter extends BaseAdapter {
	private static final String TAG="PlansAdapter";
	private Context mContext;
	private LayoutInflater inflater;
	private List<PlanEntry> dataList;
	private ViewHolder holder;
    private SlideViewWidget mLastSlideViewWithStatusOn;

	public PlansAdapter(Context mContext, List<PlanEntry> dataList) {
		this.mContext = mContext;
		this.dataList = dataList;
		this.inflater=(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return dataList.get(position).id;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {	
		SlideViewWidget slideView=null;
		if(convertView==null){
			View itemView=inflater.inflate(R.layout.list_item, null);
			
			slideView=new SlideViewWidget(mContext);
			slideView.setContentView(itemView);
			
			holder=new ViewHolder(slideView);
			slideView.setOnSlideListener(new SlideViewWidget.OnSlideListener() {
				
				@Override
				public void onSlide(View view, int status) {
					if (mLastSlideViewWithStatusOn != null && mLastSlideViewWithStatusOn != view) {
			            mLastSlideViewWithStatusOn.shrink();
			        }

			        if (status == SLIDE_STATUS_ON) {
			            mLastSlideViewWithStatusOn = (SlideViewWidget) view;
			        }					
				}

				@Override
				public void onClick(int position) {
					Log.d(TAG, "SlideView onClick");
					int id=(int) getItemId(position);
					Intent detail=new Intent(mContext, DetailActivity.class);
					detail.putExtra(MainActivity.PLAN_ID, id);
					detail.putExtra(MainActivity.PLAN_TITLE, ((PlanEntry)getItem(position)).title);
					mContext.startActivity(detail);
					
				}
			});
			slideView.setTag(holder);
		}else{
			slideView=(SlideViewWidget) convertView;
			holder=(ViewHolder) slideView.getTag();
		}
		
		PlanEntry planEntry = dataList.get(position);
		planEntry.slideView=slideView;
		holder.text.setText(planEntry.title);
		try {
		  Date  date=new SimpleDateFormat("yyyy/MM/dd").parse(planEntry.endDate);
		  if(!date.after(new Date())) {//计划结束
		      Log.d(TAG, "计划结束");
		      slideView.getContentView().setBackgroundResource(R.drawable.list_item_done_background);
		  }
            
        } catch (ParseException e) {
            e.printStackTrace();
        }
		final int index=position;
		final SlideViewWidget tempView=slideView;
		final ListViewCustom list=(ListViewCustom) parent;
		holder.deleteHolder.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (v.getId() == R.id.holder) {
		            Log.e(TAG, "onClick v=" + v);	
		            
					tempView.shrink();//避免删除之后第一条item出现的异常滑动情况。
					list.getOnItemDeleteClickListener().onDelete(index);
//		            new AlertDialog.Builder(mContext)
//		            .setTitle("提示")
//		            .setMessage("确定要删除吗?")
//		            .setPositiveButton("是",new DialogInterface.OnClickListener() {
//						
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//
//							dataList.remove(index);
//				            notifyDataSetChanged();							
//						}
//					})
//					.setNegativeButton("否", null)
//					.create().show();
		            
		        }				
			}
		});

		
		return slideView;
	}
	
	private static class ViewHolder{
		TextView text;
		ViewGroup deleteHolder;
		
		ViewHolder(View view) {
			text = (TextView) view.findViewById(android.R.id.text1);
			deleteHolder = (ViewGroup) view.findViewById(R.id.holder);
		}
	}

}
