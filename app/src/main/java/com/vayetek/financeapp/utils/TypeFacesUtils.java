package com.vayetek.financeapp.utils;

import android.content.Context;
import android.graphics.Typeface;

import java.util.Hashtable;

public class TypeFacesUtils {

	private static final Hashtable<String, Typeface> cache = new Hashtable<String, Typeface>();

	public static Typeface obtainTypeface(Context context,
			String typefaceName) throws IllegalArgumentException {
		synchronized (cache) {
			Typeface typeface = cache.get(typefaceName);
			if (typeface == null) {
				typeface = createTypeface(context, typefaceName);
				cache.put(typefaceName, typeface);
			}
			return typeface;
		}
	}

	private static Typeface createTypeface(Context context,
			String typefaceName) throws IllegalArgumentException {
		String typefacePath;
		
		if(typefaceName != null)
		typefacePath = "fonts/" + typefaceName + ".otf";
		else
			throw new IllegalArgumentException(
					"Unknown `typeface` attribute value " + typefaceName);

		return Typeface.createFromAsset(context.getAssets(),
				typefacePath);
	}


}

