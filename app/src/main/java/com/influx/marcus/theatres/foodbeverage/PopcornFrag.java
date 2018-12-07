package com.influx.marcus.theatres.foodbeverage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.influx.marcus.theatres.R;
import com.influx.marcus.theatres.common.AppStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

@SuppressLint("ValidFragment")
public class PopcornFrag extends Fragment {
    RecyclerView loc_recycler_view;
    LinearLayoutManager layoutManager;
    ArrayList<FoodModel> arrCinemaFilter;
    public static ArrayList<String> strCin = new ArrayList<>();

    public PopcornFrag(ArrayList<FoodModel> cinemafilter) {
        this.arrCinemaFilter = cinemafilter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recycler, container, false);

        loc_recycler_view = (RecyclerView) rootView.findViewById(R.id.beverage_recycler_view);
        loc_recycler_view.setHasFixedSize(true);


        layoutManager = new LinearLayoutManager(getActivity());
        loc_recycler_view.setLayoutManager(layoutManager);
        loc_recycler_view.setItemAnimator(new DefaultItemAnimator());


        LocFilterAdapter adapter = new LocFilterAdapter(getActivity(), arrCinemaFilter);
        loc_recycler_view.setAdapter(adapter);


        return rootView;
    }

    public class LocFilterAdapter extends RecyclerView.Adapter<LocFilterAdapter.ViewHolder> {
        ArrayList<FoodModel> arrLocFilter;
        Context mcontext;

        public LocFilterAdapter(Context context, ArrayList<FoodModel> filtermodel) {
            this.arrLocFilter = filtermodel;
            this.mcontext = context;
        }

        @Override
        public LocFilterAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_food, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            try {
                final FoodModel foodModel = arrLocFilter.get(position);
                holder.tvLocName.setText(foodModel.getFoodName());
                holder.tvQuan.setText(foodModel.getFoodQuantiity());
                holder.tvCost.setText("$" + foodModel.getFoodCost());
                holder.tvLocName.setText(foodModel.getFoodName());
                //todo make sure image url not null/empty checked before picasso
                Picasso.with(mcontext).load(foodModel.getFoodImage()).into(holder.ivFood);
                if (foodModel.getFoodType().contains("Veg")) {
                    holder.ivType.setImageResource(R.mipmap.ic_veg);
                } else {
                    holder.ivType.setImageResource(R.mipmap.ic_nveg);
                }

                holder.tvPlus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int quantity = Integer.parseInt(holder.tvQuan.getText().toString()) + 1;
                        foodModel.setFoodQuantiity(String.valueOf(quantity));
                        AppStorage.Companion.putBoolean(AppStorage.Companion.getISFOODADD(), true,
                                mcontext);
                        notifyDataSetChanged();

                    }
                });

                holder.tvMinus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!foodModel.getFoodQuantiity().equals("0")) {
                            int quantity = Integer.parseInt(holder.tvQuan.getText().toString()) - 1;
                            foodModel.setFoodQuantiity(String.valueOf(quantity));
                            AppStorage.Companion.putBoolean(AppStorage.Companion.getISFOODADD(), false,
                                    mcontext);
                            notifyDataSetChanged();
                        }

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        @Override
        public int getItemCount() {
            return arrLocFilter.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvLocName, tvCost, tvQuan, tvPlus, tvMinus;
            ImageView ivFood, ivType;


            public ViewHolder(View itemView) {
                super(itemView);
                tvLocName = itemView.findViewById(R.id.tvName);
                tvCost = itemView.findViewById(R.id.tvCost);
                tvQuan = itemView.findViewById(R.id.tvCount);
                tvPlus = itemView.findViewById(R.id.tvPlus);
                tvMinus = itemView.findViewById(R.id.tvMinus);
                ivFood = itemView.findViewById(R.id.ivFood);
                ivType = itemView.findViewById(R.id.tvRating);

            }
        }
    }
}
