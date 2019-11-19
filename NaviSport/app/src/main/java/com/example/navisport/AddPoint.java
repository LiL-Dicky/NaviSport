package com.example.navisport;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AddPoint extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_point);

        Button ended = findViewById(R.id.Ended);
        ended.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPoint.this.finish();
            }
        });
    }
}
