package com.example.runner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.runner.History.HistoryFragment;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    ConstraintLayout StartActivity;

    Button Start;
     ImageView Stop,Resume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StartActivity = findViewById(R.id.start_activity);
        Definations();
        Actions();
    }

    private void Actions() {
        Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartActivity.setVisibility(View.VISIBLE);
            }
        });
        Stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //   goToFragment(new HistoryFragment());

            }
        });
    }

    private void Definations() {
      //  StartActivity = findViewById(R.id.start_activity);
        Start = findViewById(R.id.Start);
        Resume = findViewById(R.id.Resume);
        Stop = findViewById(R.id.Stop);

    }
   /* public void goToFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.replace(R.id.nav_host_fragment, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }*/
}