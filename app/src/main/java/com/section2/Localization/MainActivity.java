package com.section2.Localization;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.PendingIntent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private static final String Loclisation_file = "Localisation.csv";

    Button btnLoadCSV;
    RadioGroup radioGroupLang;
    List<String[]> m_localisation_Data = null;

    List<String> m_supportedLanguages;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLoadCSV = (Button)findViewById(R.id.button_load_from_server);
        radioGroupLang = (RadioGroup)findViewById(R.id.RadioGroup);
        m_localisation_Data = readCSVFromAsset(Loclisation_file);

        btnLoadCSV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_localisation_Data = readCSVFromAsset(Loclisation_file);
            }
        });

        setupLocalisationToView(m_localisation_Data);

    }

    public void onCheckedChangedParent(int checkedId) {

        Log.i("HERE", "button clicked: "+checkedId);
        /*String selection = getString(R.string.radio_group_selection);
        String none = getString(R.string.radio_group_none);
        mChoice.setText(selection +
                (checkedId == View.NO_ID ? none : checkedId));*/
    }

    void setupLocalisationToView(List<String[]> data){
        String csvRow;
        // intialize radio button as per languages Available
        String row[] = data.get(0);
        for(int i = 1; i< row.length; ++i){
            String buttonTxt = row[i].toString();
            RadioButton rbtn = new RadioButton(this);
            rbtn.setId(i);
            rbtn.setText(buttonTxt);
            radioGroupLang.addView(rbtn);

            rbtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Log.i("HERE", "button clicked: "+isChecked);
                    if(isChecked){
                        int id = buttonView.getId();
                        onCheckedChangedParent(id);
                        setupLocalizationTextData(id);
                    }
                }
            });
        }
        // Read Localisation file create some required view and setup data
    }


    void setupLocalizationTextData(int LocalisationIndex){

        if(m_localisation_Data.size() <= 0)
            return;  // No Localization avail

        for(int i = 1; i< m_localisation_Data.size(); ++i){

            String rowData[] = m_localisation_Data.get(i);
            String TextViewId = rowData[0].toString();
            int resID = getResources().getIdentifier(TextViewId, "id", getPackageName());

            TextView tv = (TextView)findViewById(resID);
            if(tv != null){
                if(rowData.length > LocalisationIndex) {
                    tv.setText(rowData[LocalisationIndex]);
                }
                else {
                    String StringName = rowData[0].toString()+"_TEXT";
                    int resName = getResources().getIdentifier(StringName, "string", getPackageName());
                    tv.setText(getString(resName));
                }
            }
            else{

                Log.i("HERE", "Localisation is available however no TextView map with it  ");
            }
        }
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
