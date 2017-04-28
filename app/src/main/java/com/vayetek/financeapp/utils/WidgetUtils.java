package com.vayetek.financeapp.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class WidgetUtils {

    public static void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

/*
    public static Bitmap getMarkerBitmapFromView(LayoutInflater inflater, Business business) {

        View customMarkerView = inflater.inflate(R.layout.view_custom_marker, null);
        NetworkImageView markerBusinessImg = (NetworkImageView) customMarkerView.findViewById(R.id.business_img);
        TextView markerBusinessName = (TextView) customMarkerView.findViewById(R.id.business_name);
        markerBusinessName.setText(business.getName());
        switch (business.random) {
            case 0:
                markerBusinessImg.setImageResource(R.drawable.restaurant_pardef);
                break;
            case 1:
                markerBusinessImg.setImageResource(R.drawable.bar_pardef);
                break;
            case 2:
                markerBusinessImg.setImageResource(R.drawable.cafe_pardef);
                break;
        }
        markerBusinessImg.setImageUrl(Urls.imageUrl + business.getImgURI());

        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }
*/
}
