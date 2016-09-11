package com.example.a747.smartlearningmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Profile extends AppCompatActivity {

    List<String> monday = new ArrayList<String>();
    List<String> tuesday = new ArrayList<String>();
    List<String> wednesday = new ArrayList<String>();
    List<String> thursday = new ArrayList<String>();
    List<String> friday = new ArrayList<String>();
    List<String> saturday = new ArrayList<String>();
    List<String> sunday = new ArrayList<String>();

    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, List<String>> expandableListDetail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        String std_id = pref.getString("std_id", null);
        if(std_id != null){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.profile);
        }else{
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
        }

        getProfile(std_id);
        getSchedule();

        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        expandableListAdapter = new CustomExpandableListAdapter(this, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        expandableListTitle.get(groupPosition) + " List Expanded.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        expandableListTitle.get(groupPosition) + " List Collapsed.",
                        Toast.LENGTH_SHORT).show();

            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Toast.makeText(
                        getApplicationContext(),
                        expandableListTitle.get(groupPosition)
                                + " -> "
                                + expandableListDetail.get(
                                expandableListTitle.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT
                ).show();
                return false;
            }
        });
    }

    public void getProfile(String std_id){
        class GetDataJSON extends AsyncTask<String,Void,String> {
            HttpURLConnection urlConnection = null;
            public String strJSON;
            protected String doInBackground(String... params) {
                try {
                    URL url = new URL("http://54.169.58.93//Profile.php?std_id="+params[0]);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    int code = urlConnection.getResponseCode();
                    if(code==200){
                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                        if (in != null) {
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                            String line = "";
                            while ((line = bufferedReader.readLine()) != null)
                                strJSON = line;
                        }
                        in.close();
                    }
                    return strJSON;
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    urlConnection.disconnect();
                }
                return strJSON;
            }
            protected void onPostExecute(String strJSON) {
                try{
                    JSONArray data = new JSONArray(strJSON);
                    JSONObject c = data.getJSONObject(0);
                    TextView tv_pf;
                    ImageView tv_pfi;
                    tv_pf = (TextView) findViewById(R.id.tv_pf_std_id);
                    tv_pf.setText(c.getString("std_id"));
                    tv_pf = (TextView) findViewById(R.id.tv_pf_fristname);
                    tv_pf.setText(c.getString("firstname"));
                    tv_pf = (TextView) findViewById(R.id.tv_pf_lastname);
                    tv_pf.setText(c.getString("lastname"));
                    tv_pf = (TextView) findViewById(R.id.tv_pf_grade);
                    tv_pf.setText(c.getString("grade"));
                    tv_pf = (TextView) findViewById(R.id.tv_pf_email);
                    tv_pf.setText(c.getString("email"));
                    tv_pf = (TextView) findViewById(R.id.tv_pf_teleno);
                    tv_pf.setText(c.getString("phonenum"));
                    tv_pfi = (ImageView) findViewById(R.id.tv_pf_image);
                    //tv_pfi.setImageResource(c.getString("image"));
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
        new GetDataJSON().execute(std_id);
    }

    public void getSchedule(){
        SQLiteDatabase mydatabase = openOrCreateDatabase("Schedule",MODE_PRIVATE,null);
        Cursor resultSet = mydatabase.rawQuery("SELECT * FROM Schedule;",null);
        resultSet.moveToFirst();
        while(!resultSet.isAfterLast()){
            switch (resultSet.getString(resultSet.getColumnIndex("subject_date"))){
                case "1" :
                    monday.add(resultSet.getString(resultSet.getColumnIndex("subject_code")));
                    monday.add(resultSet.getString(resultSet.getColumnIndex("subject_name")));
                    monday.add(resultSet.getString(resultSet.getColumnIndex("subject_date")));
                    monday.add(resultSet.getString(resultSet.getColumnIndex("subject_time_start")));
                    monday.add(resultSet.getString(resultSet.getColumnIndex("subject_time_ended")));
                    break;
                case "2" :
                    tuesday.add(resultSet.getString(resultSet.getColumnIndex("subject_code")));
                    tuesday.add(resultSet.getString(resultSet.getColumnIndex("subject_name")));
                    tuesday.add(resultSet.getString(resultSet.getColumnIndex("subject_date")));
                    tuesday.add(resultSet.getString(resultSet.getColumnIndex("subject_time_start")));
                    tuesday.add(resultSet.getString(resultSet.getColumnIndex("subject_time_ended")));
                    break;
                case "3" :
                    wednesday.add(resultSet.getString(resultSet.getColumnIndex("subject_code")));
                    wednesday.add(resultSet.getString(resultSet.getColumnIndex("subject_name")));
                    wednesday.add(resultSet.getString(resultSet.getColumnIndex("subject_date")));
                    wednesday.add(resultSet.getString(resultSet.getColumnIndex("subject_time_start")));
                    wednesday.add(resultSet.getString(resultSet.getColumnIndex("subject_time_ended")));
                    break;
                case "4" :
                    thursday.add(resultSet.getString(resultSet.getColumnIndex("subject_code")));
                    thursday.add(resultSet.getString(resultSet.getColumnIndex("subject_name")));
                    thursday.add(resultSet.getString(resultSet.getColumnIndex("subject_date")));
                    thursday.add(resultSet.getString(resultSet.getColumnIndex("subject_time_start")));
                    thursday.add(resultSet.getString(resultSet.getColumnIndex("subject_time_ended")));
                    break;
                case "5" :
                    friday.add(resultSet.getString(resultSet.getColumnIndex("subject_code")));
                    friday.add(resultSet.getString(resultSet.getColumnIndex("subject_name")));
                    friday.add(resultSet.getString(resultSet.getColumnIndex("subject_date")));
                    friday.add(resultSet.getString(resultSet.getColumnIndex("subject_time_start")));
                    friday.add(resultSet.getString(resultSet.getColumnIndex("subject_time_ended")));
                    break;
                case "6" :
                    saturday.add(resultSet.getString(resultSet.getColumnIndex("subject_code")));
                    saturday.add(resultSet.getString(resultSet.getColumnIndex("subject_name")));
                    saturday.add(resultSet.getString(resultSet.getColumnIndex("subject_date")));
                    saturday.add(resultSet.getString(resultSet.getColumnIndex("subject_time_start")));
                    saturday.add(resultSet.getString(resultSet.getColumnIndex("subject_time_ended")));
                    break;
                case "7" :
                    sunday.add(resultSet.getString(resultSet.getColumnIndex("subject_code")));
                    sunday.add(resultSet.getString(resultSet.getColumnIndex("subject_name")));
                    sunday.add(resultSet.getString(resultSet.getColumnIndex("subject_date")));
                    sunday.add(resultSet.getString(resultSet.getColumnIndex("subject_time_start")));
                    sunday.add(resultSet.getString(resultSet.getColumnIndex("subject_time_ended")));
                    break;
            }
            resultSet.moveToNext();
        }
        mydatabase.close();
        expandableListDetail  = new HashMap<String, List<String>>();
        expandableListDetail.put("Sunday", sunday);
        expandableListDetail.put("Saturday", saturday);
        expandableListDetail.put("Friday", friday);
        expandableListDetail.put("Thursday", thursday);
        expandableListDetail.put("Wednesday", wednesday);
        expandableListDetail.put("Tuesday", tuesday);
        expandableListDetail.put("Monday", monday);
    }

    public void onClickBack(View v) {
        Intent intent = new Intent(this,more_setting.class);
        startActivity(intent);
    }
}
