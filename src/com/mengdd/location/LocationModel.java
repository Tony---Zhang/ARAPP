package com.mengdd.location;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.View;

import com.mengdd.components.ViewModel;
/**
 * Location ViewModel is for finding current location information.
 * This class doesn't have any UI elements and use both gps and network as its
 * location providers
 * 
 * @author Dandan Meng <mengdandanno1@163.com>
 * @version 1.0
 * @since 2013-07-01
 * 
 */
public abstract class LocationModel extends ViewModel
{
	protected Location mCurrentLocation = null;

	public Location getCurrentLocaitonLocation()
	{
		return mCurrentLocation;
	}
	protected LocationModel(Activity activity)
	{
		super(activity);
	}

	public abstract void registerLocationUpdates();

	public abstract void unregisterLocationUpdates();

	@Override
	public final View getView()
	{
		return null;
	}



}
