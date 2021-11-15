package com.project.jsonwebservice;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JSONWebService2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jsonweb_service2);

        setTitle("JSONWebService");

        ArrayList<String> actInfo = new ArrayList<String>();
        ListView listView = (ListView)findViewById(R.id.listView);

        try {
            //從Intent中取得字串，轉換為JSONArray
            JSONArray info = new JSONArray(getIntent().getExtras().getString("act"));
            for (int i = 0; i < info.length(); i++) {
                //取得JSONArray中的JSONObject，將時間地點等內容存至ArrayList
                JSONObject actJson = info.getJSONObject(i);
                actInfo.add("開始時間：" + actJson.getString("time") + "\n" +
                        "結束時間：" + actJson.getString("endTime") + "\n\n" +
                        "場地：" + actJson.getString("locationName") + "\n" +
                        "地址：" + actJson.getString("location")  + "\n\n" +
                        "售票資訊：\n" + actJson.getString("price")
                );
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, actInfo));
    }
}
