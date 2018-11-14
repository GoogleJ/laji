package com.zhishen.aixuexue.weight;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.adapter.ContactsAdapter;


public class Sidebar extends View {
	private Paint paint;
	private TextView header;
	private float height;
	private ListView mListView;
	private Context context;

	public void setListView(ListView listView){
		mListView = listView;
	}


	public Sidebar(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	private String[] sections = new String[]{"↑","☆","A","B","C","D","E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z","#"};

	private void init(){
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.DKGRAY);
		paint.setTextAlign(Align.CENTER);

		 paint.setTextSize(getResources().getDimensionPixelSize(R.dimen.sider_bar_textsize));
	}


	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		float center = getWidth() / 2;
		height = getHeight() / sections.length;
		for (int i = sections.length - 1; i > -1; i--) {
			canvas.drawText(sections[i], center, height * (i+1), paint);
		}
	}

	private int sectionForPoint(float y) {
		int index = (int) (y / height);
		if(index < 0) {
			index = 0;
		}
		if(index > sections.length - 1){
			index = sections.length - 1;
		}
		return index;
	}

	private void setHeaderTextAndscroll(MotionEvent event){
		 if (mListView == null) {
		        //check the mListView to avoid NPE. but the mListView shouldn't be null
		        //need to check the call stack later
		        return;
		    }
		String headerString = sections[sectionForPoint(event.getY())];
		header.setText(headerString);
		ContactsAdapter adapter = null;
		ListAdapter baseAdapter =mListView.getAdapter();
		if (baseAdapter instanceof HeaderViewListAdapter){
			HeaderViewListAdapter ha = (HeaderViewListAdapter)baseAdapter;
			adapter = (ContactsAdapter) ha.getWrappedAdapter();
		}else if (baseAdapter instanceof ContactsAdapter){
			adapter = (ContactsAdapter)baseAdapter;
		}
		String[] adapterSections = (String[]) adapter.getSections();
		try {
			for (int i = adapterSections.length - 1; i > -1; i--) {
				if(adapterSections[i].equals(headerString)){
					mListView.setSelection(adapter.getPositionForSection(i));
					break;
				}
			}
		} catch (Exception e) {
			Log.e("setHeaderTextAndscroll", e.getMessage());
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:{
			if(header == null){
				header = (TextView) ((View)getParent()).findViewById(R.id.floating_header);
			}
			setHeaderTextAndscroll(event);
			header.setVisibility(View.VISIBLE);
			setBackgroundResource(R.drawable.sidebar_background_pressed);
			return true;
		}
		case MotionEvent.ACTION_MOVE:{
			setHeaderTextAndscroll(event);
			return true;
		}
		case MotionEvent.ACTION_UP:
			header.setVisibility(View.INVISIBLE);
			setBackgroundColor(Color.TRANSPARENT);
			return true;
		case MotionEvent.ACTION_CANCEL:
			header.setVisibility(View.INVISIBLE);
			setBackgroundColor(Color.TRANSPARENT);
			return true;
		}
		return super.onTouchEvent(event);
	}

}
