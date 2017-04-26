package com.vayetek.financeapp.utils;

import android.content.Context;
import android.graphics.Typeface;

public class FontQuicksand {
    private Context context;

    public FontQuicksand(Context context) {
        this.context = context;
    }

    public Typeface getLight() {
        return TypeFacesUtils.obtainTypeface(context, "Quicksand-Light");
    }

    public Typeface getDash() {
        return TypeFacesUtils.obtainTypeface(context, "Quicksand_Dash");
    }

    public Typeface getRegular() {
        return TypeFacesUtils.obtainTypeface(context, "Quicksand-Regular");
    }


    public Typeface getBold() {
        return TypeFacesUtils.obtainTypeface(context, "Quicksand-Bold");
    }

}
