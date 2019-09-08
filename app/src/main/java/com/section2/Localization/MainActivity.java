package com.section2.Localization;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String Loclisation_file = "https://frontieronlinecontent.s3.amazonaws.com/Loc/Localisation.csv";
    private static final String TAG = MainActivity.class.getSimpleName();

    Button          m_btnLoadCSV;
    RadioGroup      m_radioGroupLang;
    TextView        m_hintText;
    List<String[]>  m_localisation_Data = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialisation
        m_btnLoadCSV = (Button)findViewById(R.id.button_load_from_server);
        m_radioGroupLang = (RadioGroup)findViewById(R.id.RadioGroup);
        m_hintText = (TextView) findViewById(R.id.hinttext);

        //call read CSV function on intitialisation of application and again on button click
        readCSVFileFromServer();
        m_btnLoadCSV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readCSVFileFromServer();
            }
        });
    }

    /*Read CSV Function will first check internet connection and proceed with task*/
    private void readCSVFileFromServer(){
        m_hintText.setText(R.string.hintText_initialisation);
        ConnectivityManager conMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conMgr.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected()){
            CSVFileDownloaderTask downloadFilesTask = new CSVFileDownloaderTask();
            downloadFilesTask.execute();
            m_btnLoadCSV.setEnabled(false);
        }
        else
        {
            m_hintText.setText(R.string.hintText_No_network);
            m_btnLoadCSV.setEnabled(true);
            Log.i(TAG, "Network error No-connection available: ");
        }
    }

    private class CSVFileDownloaderTask extends AsyncTask<URL, Void, List<String[]>>
    {
        protected List<String[]> doInBackground(URL... urls) {
            return downloadReadFileContent();
        }

        @Override
        protected void onPostExecute(List<String[]> result) {
            if(result != null){
                if(result.size() > 0){
                    m_hintText.setText(R.string.hintText_Load);
                    m_btnLoadCSV.setEnabled(true);
                    m_localisation_Data = result;
                    setupLocalisationLanguages(m_localisation_Data);
                }
                else{
                    m_hintText.setText(R.string.hintText_Error);
                }
            }
        }

        @Override
        protected void onPreExecute() {
            m_btnLoadCSV.setEnabled(false);
            Log.i(TAG, "inside onPreExecute: ");
        }
    }

    private List<String[]> downloadReadFileContent() {
        URL mUrl = null;
        List<String[]> CSVData = new ArrayList<>();
        String[] CSVLinedata = null;
        try {
            mUrl = new URL(Loclisation_file);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            assert mUrl != null;
            URLConnection urlConnection = mUrl.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new
                    InputStreamReader(urlConnection.getInputStream()));
            String line = "";
            while((line = bufferedReader.readLine()) != null){
                CSVLinedata = line.split(",");
                CSVData.add(CSVLinedata);
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return CSVData;
    }

    void setupLocalisationLanguages(List<String[]> data){
        if(data.size() == 0)
            return;

        m_radioGroupLang.removeAllViews();
        String csvRow;
        // intialize radio button as per languages Available
        String row[] = data.get(0);
        for(int i = 1; i< row.length; ++i){
            String buttonTxt = row[i].toString();
            RadioButton rbtn = new RadioButton(this);
            rbtn.setId(i);
            rbtn.setText(buttonTxt);
            m_radioGroupLang.addView(rbtn);

            rbtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        int id = buttonView.getId();
                        setupLocalizationTextData(id);
                    }
                }
            });
        }
        // set automatic to English
        RadioButton r = (RadioButton)m_radioGroupLang.getChildAt(0);
        if(r != null){
            r.setChecked(true);
            setupLocalizationTextData(r.getId());
        }
    }

        /*  data of selected localisaton index is filled into text view
            textview is selected with Key of localisation tex in CSV
            if new Key in CSV and new textview added into avtivy with key id.
            data will be automatically filled in new text view
        */
    void setupLocalizationTextData(int LocalisationIndex){
        if(m_localisation_Data.size() <= 0)
            return;  // No Localization avail

        for(int i = 1; i< m_localisation_Data.size(); ++i){
            String rowData[] = m_localisation_Data.get(i);
            String TextViewId = rowData[0].toString();
            int resID = getResources().getIdentifier(TextViewId, "id", getPackageName());

            TextView tv = (TextView)findViewById(resID);
            if(tv != null){
                if(rowData.length > LocalisationIndex || rowData.toString() == "") {
                    tv.setText(rowData[LocalisationIndex]);
                }
                else {
                    // This is to set fallback text if localsation is not available
                    String StringName = rowData[0].toString()+"_TEXT";
                    int resName = getResources().getIdentifier(StringName, "string", getPackageName());
                    tv.setText(getString(resName));
                }
            }
            else{
                Log.i(TAG, "Localisation is available however no TextView map with it  ");
            }
        }
    }
}
