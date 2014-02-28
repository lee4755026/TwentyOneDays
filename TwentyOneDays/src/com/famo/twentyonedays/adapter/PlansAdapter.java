package com.famo.twentyonedays.adapter;

import java.util.List;

import com.famo.twentyonedays.R;
import com.famo.twentyonedays.model.PlanEntry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class PlansAdapter extends BaseAdapter {
	private static final String TAG="PlansAdapter";
	private Context mContext;
	private LayoutInflater inflater;
	private List<PlanEntry> dataList;

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
		if(convertView==null){
			convertView=inflater.inflate(R.layout.list_item, null);
		}
		TextView text=(TextView) convertView.findViewById(android.R.id.text1);
		text.setText(dataList.get(position).title);
		convertView.setOnTouchListener(new OnTouchListener() {
			
			private float lastPositionX=0;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				float x=event.getX();
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					lastPositionX=event.getX();
					break;
				case MotionEvent.ACTION_MOVE:{
					int detaX=(int) (lastPositionX-x);
					v.findViewById(R.id.front_layout).scrollBy(detaX, 0);
					lastPositionX=x;
				}

				}
				return true;   
			}
		});
		/*
		convertView.setOnTouchListener(new View.OnTouchListener() {
		float downX=0;
		float upX=0;
		@Override
		public boolean onTouch(View v, MotionEvent event) {
                    switch(event.getAction())//根据动作来执行代码     
                    {    
                    case MotionEvent.ACTION_MOVE://滑动     
//                        Toast.makeText(context, "move...", Toast.LENGTH_SHORT).show();  
                        break;    
                    case MotionEvent.ACTION_DOWN://按下     
//                        Toast.makeText(context, "down...", Toast.LENGTH_SHORT).show();  
                        downX = event.getX();  
                        break;    
                    case MotionEvent.ACTION_UP://松开     
                        upX = event.getX();  
//                        Toast.makeText(context, "up..." + Math.abs(UpX-DownX), Toast.LENGTH_SHORT).show();  
                        if(-(upX-downX) > 20){  
//                            ViewHolder holder = (ViewHolder) v.getTag();  
//                            holder.cBox.setVisibility(View.VISIBLE);  
                        	TranslateAnimation animation=new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,-0.2f, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
            				animation.setDuration(2000);
            				animation.setFillAfter(true);
            				v.findViewById(R.id.front_layout).startAnimation(animation);
                        } else  if((upX-downX) > 20){  
                        	TranslateAnimation animation=new TranslateAnimation(Animation.RELATIVE_TO_SELF, -0.2f, Animation.RELATIVE_TO_SELF,0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
            				animation.setDuration(2000);
            				animation.setFillAfter(true);
            				v.findViewById(R.id.front_layout).startAnimation(animation);
                        }else{
                        	Toast.makeText(mContext, "暂定为点击。。", Toast.LENGTH_SHORT).show();  
                        }
                        break;
					default:    
                    }    
                    return true;   
                }  
	});
		*/
		return convertView;
	}

}
