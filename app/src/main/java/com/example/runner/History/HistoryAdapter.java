package com.example.runner.History;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.runner.Details.DetailsMapFaragment;
import com.example.runner.R;
import com.example.runner.Utils;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    Context mcontext;
    FragmentManager fragmentManager;
    List<HistoryModel> historyModels = new ArrayList<>();


    public HistoryAdapter(Context context, List<HistoryModel> HistoryModels) {
        historyModels = HistoryModels;
        mcontext = context;
    }

    @NonNull
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.Date.setText(historyModels.get(position).getDate()+"");
        holder.Time.setText(historyModels.get(position).getTime()+"");
        holder.Step.setText(historyModels.get(position).getSteps()+"");
        holder.Distance.setText(historyModels.get(position).getDistance()+"");

        Utils.HistoryModels.get(position).setDistance(Utils.Distance + "");
        Utils.HistoryModels.get(position).setSteps(Utils.CountStep);
        Utils.HistoryModels.get(position).setTimeTotal(Utils.TimeTaken);
        Utils.HistoryModels.get(position).setDate(Utils.Date);
        holder.Item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Blackout(new DetailsMapFaragment());
            }
        });
         holder.Delet.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 notifyItemRemoved(position);
                 notifyItemRangeChanged(position, historyModels.size());
             }
         });
        //  Utils.Ids.put(historyModels.get(position).getId(), historyModels.get(position));


    }


    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView Time, Date, StartTime, Step, Distance;
        ImageView Delet;
        ConstraintLayout Item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Time = itemView.findViewById(R.id.time);
            Date = itemView.findViewById(R.id.date);
            StartTime = itemView.findViewById(R.id.Start_time);
            Step = itemView.findViewById(R.id.stepe);
            Delet = itemView.findViewById(R.id.delet);
            Distance = itemView.findViewById(R.id.distance);
            Item = itemView.findViewById(R.id.item);

        }
    }

    private void Blackout(Fragment fragment) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.replace(R.id.main, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }
}
