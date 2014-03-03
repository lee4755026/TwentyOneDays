package com.famo.twentyonedays.adapter;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.famo.twentyonedays.R;
import com.famo.twentyonedays.model.PlanEntry;
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
			});
			slideView.setTag(holder);
		}else{
			slideView=(SlideViewWidget) convertView;
			holder=(ViewHolder) slideView.getTag();
		}
		
		PlanEntry planEntry = dataList.get(position);
		planEntry.slideView=slideView;
		holder.text.setText(planEntry.title);
		final int index=position;
		final SlideViewWidget tempView=slideView;
		holder.deleteHolder.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (v.getId() == R.id.holder) {
		            Log.e(TAG, "onClick v=" + v);		            
		            new AlertDialog.Builder(mContext)
		            .setMessage("确定要删除吗?")
		            .setPositiveButton("是",new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
//							tempView.shrink();
							dataList.remove(index);
				            notifyDataSetChanged();							
						}
					})
					.setNegativeButton("否", null)
					.create().show();
		            
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
