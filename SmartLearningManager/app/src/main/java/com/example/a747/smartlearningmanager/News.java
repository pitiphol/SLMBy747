package com.example.a747.smartlearningmanager;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.net.URL;

public class News extends AppCompatActivity {

    String from;
    Boolean updateStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            from = extras.getString("from");
            String title = extras.getString("title");
            System.out.println("Title: "+title);
            RSS_UpdateCount(title);
            String desc = extras.getString("desc");
            String cutAttachment = desc;
            String temp;
            TextView tv_title = (TextView) findViewById(R.id.tv_news_title);
            TextView tv_desc = (TextView) findViewById(R.id.tv_news_desc);
            TextView url = ((TextView)findViewById(R.id.tv_news_url));
            tv_title.setText(title);
            if(desc.contains("Attachment")) {
                while (cutAttachment.contains("Attachment")) {
                    cutAttachment = desc.substring(0, desc.indexOf("Attachment"));
                    temp = desc.substring(desc.indexOf("KB") + 2, desc.length());
                    while (temp.contains("KB")) {
                        temp = temp.substring(temp.indexOf("KB") + 2, temp.length());
                    }
                    tv_desc.setText(cutAttachment);
                    url.setText(Html.fromHtml(temp));
                    url.setClickable(true);
                    url.setMovementMethod(LinkMovementMethod.getInstance());
                }

            }else{
                tv_desc.setText(desc);
            }
        }
    }

    private void RSS_UpdateCount(String title){
        class GetDataJSON extends AsyncTask<String,Void,String> {
            HttpURLConnection urlConnection = null;
            public String strJSON;
            protected String doInBackground(String... params) {
                try {
                    URL url = new URL("http://54.169.58.93/RSS_UpdateCount.php?title="+params[0]);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    int code = urlConnection.getResponseCode();
                    if(code==200){
                        updateStatus = true;
                    }else{
                        updateStatus = false;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    urlConnection.disconnect();
                }
                return strJSON;
            }
            protected void onPostExecute(String strJSON) {
                try{
                    if(updateStatus == true){
                        Toast.makeText(getApplicationContext(),"News updated", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(),"News not updated", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        new GetDataJSON().execute(title.replaceAll(" ","%20"));
    }

    public void gotoHome(View v){
        Intent intent;
        if(from.equalsIgnoreCase("Main")) {
            intent = new Intent(this, Main.class);
        }else{
            intent = new Intent(this, Page_news.class);
        }
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            Log.d("CDA", "onKeyDown Called");
            Intent intent;
            if(from.equalsIgnoreCase("Main")) {
                intent = new Intent(this, Main.class);
            }else{
                intent = new Intent(this, Page_news.class);
            }
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
