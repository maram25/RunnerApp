package com.example.runner.History;

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

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder>{

    Context context;
    FragmentManager fragmentManager;

    @NonNull
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history,parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }
    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.ViewHolder holder, int position) {
        holder.Item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Blackout( new DetailsMapFaragment());
            }
        });

    }



    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView Time,Date,StartTime,Step,Distance;
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
