package com.example.navisport;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class AddPoint extends AppCompatActivity {

    private final static String FILE_NAME = "pos.txt";
    private String text;
    int flag = 0;

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

    public void saveText(){

        FileOutputStream fos = null;
        try {
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos.write(text.getBytes());
        }
        catch(IOException ex) {System.out.println(ex);}
        finally{
            try{
                if(fos!=null)
                    fos.close();
            }
            catch(IOException ex){
                System.out.println(ex);
            }
        }
    }

    public void openText() throws IOException {

        FileInputStream fin = openFileInput(FILE_NAME);
        InputStreamReader reader = new InputStreamReader(fin);
        BufferedReader buffer = new BufferedReader(reader);
        StringBuilder str = new StringBuilder();
        while((text = buffer.readLine()) != null){
            str.append(text).append("\n");
        }
    }
}
