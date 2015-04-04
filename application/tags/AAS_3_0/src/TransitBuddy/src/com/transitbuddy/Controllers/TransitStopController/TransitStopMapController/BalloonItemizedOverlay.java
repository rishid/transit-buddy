
package com.transitbuddy.Controllers.TransitStopController.TransitStopMapController;



import java.util.List;

import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.transitbuddy.*;


public abstract class BalloonItemizedOverlay<Item extends OverlayItem> extends ItemizedOverlay<Item>
{
	private MapView mMapView;
	private BalloonOverlayView<Item> mBalloonView;
	private View mClickRegion;
	private int mViewOffset;
	final MapController mMapController;
	private Item mCurrentFocussedItem;
	private int mCurrentFocussedIndex;
	private int mMarkerHeight;
	
	/**
	 * Create a new BalloonItemizedOverlay
	 * 
	 * @param defaultMarker - A bounded Drawable to be drawn on the map for each item in the overlay.
	 * @param mapView - The view upon which the overlay items are to be drawn.
	 */
	public BalloonItemizedOverlay(Drawable defaultMarker, MapView mapView) 
	{
		super(defaultMarker);
		
		mMarkerHeight = defaultMarker.getIntrinsicHeight();
		
		this.mMapView = mapView;
		
		mViewOffset = 0;
		mMapController = mapView.getController();
	}
	
	/**
	 * Set the horizontal distance between the marker and the bottom of the information
	 * balloon. The default is 0 which works well for center bounded markers. If your
	 * marker is center-bottom bounded, call this before adding overlay items to ensure
	 * the balloon hovers exactly above the marker. 
	 * 
	 * @param pixels - The padding between the center point and the bottom of the
	 * information balloon.
	 */
	public void setBalloonBottomOffset(int pixels) 
	{
		mViewOffset = pixels;
	}
	
	public int getBalloonBottomOffset() 
	{
		return (mViewOffset + mMarkerHeight);
	}
	
	/**
	 * Override this method to handle a "tap" on a balloon. By default, does nothing 
	 * and returns false.
	 * 
	 * @param index - The index of the item whose balloon is tapped.
	 * @param item - The item whose balloon is tapped.
	 * @return true if you handled the tap, otherwise false.
	 */
	protected boolean onBalloonTap(int index, Item item) 
	{
		return false;
	}

	/* (non-Javadoc)
	 * @see com.google.android.maps.ItemizedOverlay#onTap(int)
	 */
	@Override
	protected final boolean onTap(int index) 
	{	
		mCurrentFocussedIndex = index;
		mCurrentFocussedItem = createItem(index);
		
		boolean isRecycled;
		
		if ( mBalloonView == null)
		{
			mBalloonView = createBalloonOverlayView();
			mClickRegion = (View) mBalloonView.findViewById(R.id.balloon_inner_layout);
			mClickRegion.setOnTouchListener(createBalloonTouchListener());
			isRecycled = false;
		} 
		else 
		{
			isRecycled = true;
		}
	
		mBalloonView.setVisibility(View.GONE);
		
		List<Overlay> mapOverlays = mMapView.getOverlays();
		
		if (mapOverlays.size() > 1)
		{
			hideOtherBalloons(mapOverlays);
		}
		
		mBalloonView.setData(mCurrentFocussedItem);
		
		GeoPoint point = mCurrentFocussedItem.getPoint();
		MapView.LayoutParams params = new MapView.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, point,
				MapView.LayoutParams.BOTTOM_CENTER);
		params.mode = MapView.LayoutParams.MODE_MAP;
		
		mBalloonView.setVisibility(View.VISIBLE);

		if (isRecycled)
		{
			mBalloonView.setLayoutParams(params);
		} 
		else 
		{
			mMapView.addView(mBalloonView, params);
		}
		
		mMapController.animateTo(point);
		
		return true;
	}

	/**
	 * Creates the balloon view. Override to create a sub-classed view that
	 * can populate additional sub-views.
	 */
	protected BalloonOverlayView<Item> createBalloonOverlayView() 
	{
		return new BalloonOverlayView<Item>(getMapView().getContext(), getBalloonBottomOffset());
	}
	
	/**
	 * Expose map view to subclasses.
	 * Helps with creation of balloon views. 
	 */
	protected MapView getMapView()
	{
		return mMapView;
	}
	
	/**
	 * Sets the visibility of this overlay's balloon view to GONE. 
	 */
	protected void hideBalloon() 
	{
		if (mBalloonView != null)
		{
			mBalloonView.setVisibility(View.GONE);
		}
	}
	
	/**
	 * Hides the balloon view for any other BalloonItemizedOverlay instances
	 * that might be present on the MapView.
	 * 
	 * @param overlays - list of overlays (including this) on the MapView.
	 */
	private void hideOtherBalloons(List<Overlay> overlays) 
	{
		
		for (Overlay overlay : overlays) 
		{
			if (overlay instanceof BalloonItemizedOverlay<?> && overlay != this)
			{
				((BalloonItemizedOverlay<?>) overlay).hideBalloon();
			}
		}	
	}
	
	/**
	 * Sets the onTouchListener for the balloon being displayed, calling the
	 * overridden {@link #onBalloonTap} method.
	 */
	private OnTouchListener createBalloonTouchListener()
	{
		return new OnTouchListener() 
		{
			public boolean onTouch(View v, MotionEvent event)
			{
				
				View l =  ((View) v.getParent()).findViewById(R.id.balloon_main_layout);
				Drawable d = l.getBackground();
				
				if (event.getAction() == MotionEvent.ACTION_DOWN) 
				{
					int[] states = {android.R.attr.state_pressed};
					
					if (d.setState(states))
					{
						d.invalidateSelf();
					}
					
					return true;
				} 
				else if (event.getAction() == MotionEvent.ACTION_UP)
				{
					int newStates[] = {};
					
					if (d.setState(newStates)) 
					{
						d.invalidateSelf();
					}
					
					// call overridden method
					onBalloonTap(mCurrentFocussedIndex, mCurrentFocussedItem);
					
					return true;	
				} 
				else 
				{
					return false;
				}		
			}
		};
	}
}
