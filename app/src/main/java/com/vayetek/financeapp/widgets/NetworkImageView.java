package com.vayetek.financeapp.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class NetworkImageView extends ImageView {

    protected String imageURL;
    private int imageRes;

    public NetworkImageView(Context context) {
        super(context);
    }

    public NetworkImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NetworkImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setImageUrl(String imageURL, final int defaultImgRes) {
        this.imageURL = imageURL;
        setImageResource(defaultImgRes);
        Transformation transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(8)
                .oval(false)
                .build();
        Picasso.with(getContext()).load(imageURL).transform(transformation).resize(400, 400).centerCrop().into(this, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                setImageResource(defaultImgRes);
            }
        });
    }

    public void setImageUrl(String imageURL) {
        this.imageURL = imageURL;
        Transformation transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(9)
                .oval(false)
                .build();
        Picasso.with(getContext()).load(imageURL).transform(transformation).resize(400, 400).centerCrop().into(this);
    }

    @Override
    public void setImageResource(int imageRes) {
        this.imageRes = imageRes;
        Transformation transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(9)
                .oval(false)
                .build();
        Picasso.with(getContext()).load(imageRes).transform(transformation).resize(400, 400).centerCrop().into(this);
    }


}
