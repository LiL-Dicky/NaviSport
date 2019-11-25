package com.example.navisport;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class AddPoint extends AppCompatActivity {

    private final static String FILE_NAME = "pos.txt";
    private final static String FREE_SPACE = "@@@   @@@";
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
                CheckBox chosen = findViewById(R.id.Chosen_loc);
                if(chosen.isChecked()){
                    flag = 1;
                }else{
                    flag = 0;
                }
                EditText name = findViewById(R.id.Name);
                EditText lattitude = findViewById(R.id.Lattitude);
                EditText longtitude = findViewById(R.id.Longtitude);
                if(lattitude.length() == 0 || longtitude.length() == 0 || name.length() == 0){
                    if(flag == 1 && name.length() > 0){
                        String name1 = name.getText().toString();
                        double lattitude1 = 0;
                        double longtitude1 = 0;
                        try {
                            openText();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if(text != null){
                            text = text + FREE_SPACE + flag + FREE_SPACE + name1 + FREE_SPACE + lattitude1 + FREE_SPACE + longtitude1;
                        }else{
                            text = flag + FREE_SPACE + name1 + FREE_SPACE + lattitude1 + FREE_SPACE + longtitude1;
                        }
                        saveText();
                        Toast.makeText(AddPoint.this, "Successfully added a new point", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(AddPoint.this, "Error adding new point", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    String name1 = name.getText().toString();
                    double lattitude1 = Double.parseDouble(lattitude.getText().toString());
                    double longtitude1 = Double.parseDouble(longtitude.getText().toString());
                    try {
                        openText();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(text != null){
                        text = text + FREE_SPACE + flag + FREE_SPACE + name1 + FREE_SPACE + lattitude1 + FREE_SPACE + longtitude1;
                    }else{
                        text = flag + FREE_SPACE + name1 + FREE_SPACE + lattitude1 + FREE_SPACE + longtitude1;
                    }
                    saveText();
                    Toast.makeText(AddPoint.this, "Successfully added a new point", Toast.LENGTH_SHORT).show();
                }

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
        BufferedReader buffer = new BufferedReader(new InputStreamReader(openFileInput(FILE_NAME)));
        StringBuilder str = new StringBuilder();
        while((text = buffer.readLine()) != null){
            str.append(text).append("\n");
        }
    }
}
