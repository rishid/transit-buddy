package com.transitbuddy.Controllers.TransitStopController.TransitStopMapController;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.maps.OverlayItem;
import com.transitbuddy.*;

public class BalloonOverlayView<Item extends OverlayItem> extends FrameLayout
{

	private LinearLayout mLayout;
	private TextView mTitle;
	private TextView mSnippet;

	/**
	 * Create a new BalloonOverlayView.
	 * 
	 * @param context - The activity context.
	 * @param balloonBottomOffset - The bottom padding (in pixels) to be applied
	 * when rendering this view.
	 */
	public BalloonOverlayView(Context context, int balloonBottomOffset)
	{
		super(context);

		setPadding(10, 0, 10, balloonBottomOffset);
		mLayout = new LinearLayout(context);
		mLayout.setVisibility(VISIBLE);

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.balloon_overlay, mLayout);
		mTitle = (TextView) v.findViewById(R.id.balloon_item_title);
		mSnippet = (TextView) v.findViewById(R.id.balloon_item_snippet);

		ImageView close = (ImageView) v.findViewById(R.id.close_img_button);
		close.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) 
			{
				mLayout.setVisibility(GONE);
			}
		});

		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.NO_GRAVITY;

		addView(mLayout, params);

	}
	
	/**
	 * Sets the view data from a given overlay item.
	 * 
	 * @param item - The overlay item containing the relevant view data 
	 * (title and snippet). 
	 */
	public void setData(Item item) 
	{	
		mLayout.setVisibility(VISIBLE);
		
		if (item.getTitle() != null) 
		{
			mTitle.setVisibility(VISIBLE);
			mTitle.setText(item.getTitle());
		} 
		else 
		{
			mTitle.setVisibility(GONE);
		}
		if (item.getSnippet() != null)
		{
			mSnippet.setVisibility(VISIBLE);
			mSnippet.setText(item.getSnippet());
		} 
		else 
		{
			mSnippet.setVisibility(GONE);
		}		
	}
}
