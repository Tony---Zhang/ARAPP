package com.mengdd.poi.data;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mengdd.arapp.R;
import com.mengdd.poi.ui.IconMarker;
import com.mengdd.poi.ui.Marker;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

/**
 * This class extends DataSource to fetch data from Google Places.
 * 
 * Google Place API:
 * https://developers.google.com/places/documentation/
 * 
 * The source of the codes:
 * 
 * 1."android-augment-reality-framework"
 * project link: http://code.google.com/p/android-augment-reality-framework/
 * 
 * 2.The book: "Pro Android Augmented Reality"
 * http://www.apress.com/9781430239451
 * Official repository for Pro Android Augmented Reality:
 * https://github.com/RaghavSood/ProAndroidAugmentedReality
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 * @author Dandan Meng <mengdandanno1@163.com>
 * @version 1.0
 * @since 2013-07-01
 */
public class GooglePlacesDataSource extends NetworkDataSource
{

	private static final String URL = "https://maps.googleapis.com/maps/api/place/search/json?";
	private static final String TYPES = "airport|amusement_park|aquarium|art_gallery|bus_station|campground|car_rental|city_hall|embassy|establishment|hindu_temple|local_governemnt_office|mosque|museum|night_club|park|place_of_worship|police|post_office|stadium|spa|subway_station|synagogue|taxi_stand|train_station|travel_agency|University|zoo";

	private static String key = null;
	private static Bitmap icon = null;

	public GooglePlacesDataSource(Resources res)
	{
		if (res == null)
			throw new NullPointerException();

		key = res.getString(R.string.google_places_api_key);

		createIcon(res);
	}

	protected void createIcon(Resources res)
	{
		if (res == null)
			throw new NullPointerException();

		icon = BitmapFactory.decodeResource(res, R.drawable.buzz);
	}

	@Override
	public String createRequestURL(double lat, double lon, double alt,
			float radius, String locale)
	{
		try
		{
			return URL + "location=" + lat + "," + lon + "&radius="
					+ (radius * 1000.0f) + "&types=" + TYPES
					+ "&sensor=true&key=" + key;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<Marker> parse(JSONObject root)
	{
		if (root == null)
			throw new NullPointerException();

		JSONObject jo = null;
		JSONArray dataArray = null;
		List<Marker> markers = new ArrayList<Marker>();

		try
		{
			if (root.has("results"))
				dataArray = root.getJSONArray("results");
			if (dataArray == null)
				return markers;
			int top = Math.min(MAX, dataArray.length());
			for (int i = 0; i < top; i++)
			{
				jo = dataArray.getJSONObject(i);
				Marker ma = processJSONObject(jo);
				if (ma != null)
					markers.add(ma);
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return markers;
	}

	private Marker processJSONObject(JSONObject jo)
	{
		if (jo == null)
			throw new NullPointerException();

		if (!jo.has("geometry"))
			throw new NullPointerException();

		Marker ma = null;
		try
		{
			Double lat = null, lon = null;

			if (!jo.isNull("geometry"))
			{
				JSONObject geo = jo.getJSONObject("geometry");
				JSONObject coordinates = geo.getJSONObject("location");
				lat = Double.parseDouble(coordinates.getString("lat"));
				lon = Double.parseDouble(coordinates.getString("lng"));
			}
			if (lat != null)
			{
				String user = jo.getString("name");

				ma = new IconMarker("Google " + user + ": "
						+ jo.getString("name"), lat, lon, 0, Color.RED, icon);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return ma;
	}
}