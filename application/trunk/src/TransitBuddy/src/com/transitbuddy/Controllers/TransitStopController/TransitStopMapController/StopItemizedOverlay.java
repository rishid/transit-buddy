package com.transitbuddy.Controllers.TransitStopController.TransitStopMapController;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;

import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class StopItemizedOverlay extends BalloonItemizedOverlay<OverlayItem>   
{
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	
	public StopItemizedOverlay(Drawable defaultMarker, MapView mapView) 
	{
		super(boundCenter(defaultMarker), mapView);
	}
	
	@Override
	protected OverlayItem createItem(int i) 
	{
		return mOverlays.get(i);
	}

	@Override
	public int size() 
	{
		return mOverlays.size();
	}
	
	public void addOverlay(OverlayItem overlay) 
	{
	    mOverlays.add(overlay);
	    populate();
	}
	
	public void addAllOverlay(ArrayList<OverlayItem> overlayItems)
	{
		mOverlays.addAll(overlayItems);
		populate();	
	}

	// Allows you to handle events if the balloon is tapped
	@Override
	protected boolean onBalloonTap(int index, OverlayItem item)
	{
		return true;
	}
}
