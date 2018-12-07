package com.influx.marcus.theatres.homepage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.influx.marcus.theatres.R;
import com.influx.marcus.theatres.api.ApiModels.HomeMoviePosters.Banner;
import com.influx.marcus.theatres.api.ApiModels.showtime.ShowtimeRequest;
import com.influx.marcus.theatres.showtime.ShowtimeActivity;
import com.influx.marcus.theatres.utils.AppConstants;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;


public class BannerPagerAdapter extends PagerAdapter {
    private Activity mActivity;
    private int mPageCount;
    private List<Banner> bannerlist;
    ConstraintLayout constraintLayout;
    private Context mContext;


    public BannerPagerAdapter(@Nullable FragmentActivity activity, @NotNull List<Banner> bannersItem) {
        mActivity = activity;
        mContext = activity;
        this.bannerlist = bannersItem;
    }

    @Override
    public int getCount() {
        return this.bannerlist.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {

        return (view == (View) obj);
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        ViewGroup viewGroup = (ViewGroup) mActivity.getLayoutInflater().inflate(
                R.layout.viewpagerlayout, null);

        TextView movieName = viewGroup.findViewById(R.id.tvName);
        TextView movieGenre = viewGroup.findViewById(R.id.tvGenre);
        TextView movieRating = viewGroup.findViewById(R.id.txtMovieRating);
        ImageView ivPoster = viewGroup.findViewById(R.id.ivPoster);
        final Banner bannersItem = bannerlist.get(position);

        final String latitude = AppConstants.Values.getString(AppConstants.Values.getKEY_LATITUDE(),
                mContext);
        final String statecode = AppConstants.Values.getString(AppConstants.Values.getKEY_STATE_CODE(),
                mContext);
        final String longitude = AppConstants.Values.getString(AppConstants.Values.getKEY_LONGITUDE(),
                mContext);
        final List<String> preferedCinemasList = AppConstants.Values.getStringList(AppConstants.Values.getKEY_PREFEREDCINEMA(),
                mContext);


        /**
         * case ismovie = "1"
         case isoffer = "2"
         */

        ivPoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showintent = new Intent(mActivity, ShowtimeActivity.class);
                if (bannersItem.getTMDBId() != null && bannersItem.getBanner_type().equals("1")) {
                    AppConstants.Values.putString(AppConstants.Values.getKEY_SHOWTIME(), "home_showtime", mActivity);
                    AppConstants.Values.putString(AppConstants.Values.getKEY_TMDBID(), bannersItem.getTMDBId(), mActivity);
                    AppConstants.Values.putString(AppConstants.Values.getKEY_MOVIECODE(), bannersItem.getMovie_id(), mActivity);
                    AppConstants.Values.putString(AppConstants.Values.getKEY_MOVIETYPE(), "banner", mActivity);
                    AppConstants.Values.putObject(AppConstants.Values.getKEY_MOVIEDETAILSOBJECT(), bannersItem, mActivity);
                    mActivity.startActivity(showintent);
                    mActivity.overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left);



                   /* ShowtimeRequest request = new ShowtimeRequest(
                            statecode,
                            latitude,
                            longitude,
                            AppConstants.Values.getString(AppConstants.Values.getKEY_MOVIECODE(), mContext),
                            AppConstants.Values.getString(AppConstants.Values.getKEY_TMDBID(), mContext),
                            preferedCinemasList
                    );*/

                } else {
                    //donothing if no movie id
                }
            }
        });

        if (bannersItem.getTMDBId() != null && bannersItem.getMovie_id() != null) {
            if (bannersItem.getTMDBId().equalsIgnoreCase("") &&
                    bannersItem.getMovie_id().equalsIgnoreCase("")) {
                movieName.setText(bannersItem.getTitle());
                if (bannersItem.getRating().equalsIgnoreCase("")) {
                    movieRating.setVisibility(View.GONE);
                } else {
                    movieRating.setText(bannersItem.getRating());
                }

                if (bannersItem.getImg_url().equalsIgnoreCase("")) {
                    // donothing
                } else {

                    Glide.with(mActivity)
                            .load(bannersItem.getImg_url())
                            .apply(RequestOptions.placeholderOf(R.drawable.marcus_placeholder).error(R.drawable.marcus_placeholder))
                            .into(ivPoster);

                }
                if (bannersItem.getGenre().size() > 1) {
                    String genrename = bannersItem.getGenre().get(0);
                    for (int i = 1; i < bannersItem.getGenre().size(); i++) {
                        genrename = genrename + "," + bannersItem.getGenre().get(i);
                        movieGenre.setText(genrename);
                    }
                } else if (bannersItem.getGenre().size() > 0) {
                    movieGenre.setText(bannersItem.getGenre().get(0));
                } else {
                    movieGenre.setVisibility(View.GONE);
                }

            } else {
                movieRating.setVisibility(View.GONE);
                // donothing show only image poster
                if (bannersItem.getImg_url().equalsIgnoreCase("")) {
                    // empty image url
                } else {
                    Glide.with(mActivity)
                            .load(bannersItem.getImg_url())
                            .apply(RequestOptions.placeholderOf(R.drawable.marcus_placeholder).error(R.drawable.marcus_placeholder))
                            .into(ivPoster);
//                    Picasso.with(mActivity)
//                            .load(bannersItem.getImg_url())
//                            .placeholder(R.drawable.marcus_placeholder)
//                            .into(ivPoster);
                }

            }
        }

        container.addView(viewGroup);
        return viewGroup;
    }

    Random rnd = new Random();

    private int randomColor() {
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        //must be overridden else throws exception as not overridden.
        Log.d("Tag", collection.getChildCount() + "");
        collection.removeView((View) view);
    }



}