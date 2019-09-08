package com.section2.Localization;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private static final String Loclisation_file = "Localisation.csv";

    Button btnLoadCSV;
    List<String[]> m_localisation_Data = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLoadCSV = (Button)findViewById(R.id.button_load_from_server);

        m_localisation_Data = readCSVFromAsset(Loclisation_file);

        btnLoadCSV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_localisation_Data = readCSVFromAsset(Loclisation_file);
            }
        });

        setupLocalisationToView(m_localisation_Data);
    }


    void setupLocalisationToView(List<String[]> data){

        // Read Localisation file create some required view and setup data

    }


    /*ToDo CSV need to read from server*/
    private List<String[]> readCSVFromAsset(String fileName){
        List<String[]> returnData = new ArrayList<>();
        try {
            InputStream inputStream = getAssets().open(fileName);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String[] lineConent;
            String line = null;
            while ((line = bufferedReader.readLine()) != null){
                    lineConent = line.split(",");
                    returnData.add(lineConent);
            }
            bufferedReader.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return returnData;
    }
}
