package com.project.jsonwebservice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayAdapter<String> categoryAdapter;
    ArrayList<String> categoryList = new ArrayList<String>();
    ArrayList<String> categoryCodeList = new ArrayList<String>();

    ArrayAdapter<String> acitivityAdapter;
    ArrayList<String> acitivityList = new ArrayList<String>();
    ArrayList<JSONArray> acitivityInfoList = new ArrayList<JSONArray>();

    ListView list;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("JSONWebService");

        list = (ListView)findViewById(R.id.list);
        spinner = (Spinner)findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(spinnerListenter);
        categoryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, categoryList);
        spinner.setAdapter(categoryAdapter);

        acitivityAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, acitivityList);
        list.setAdapter(acitivityAdapter);
        list.setOnItemClickListener(listViewOnItemClickListener);

        showCategoryTask task = new showCategoryTask();
        task.execute();
    }

    Spinner.OnItemSelectedListener spinnerListenter = new Spinner.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            showActTask task = new showActTask();
            task.execute(categoryCodeList.get(position));
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //將使用者選取活動的JSONObject以字串方式傳送到ActInfo類別中
            Intent intent = new Intent(MainActivity.this, JSONWebService2Activity.class);
            intent.putExtra("act", acitivityInfoList.get(position).toString());
            startActivity(intent);
        }
    };


    ListView.OnItemClickListener listViewOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent = new Intent(MainActivity.this, JSONWebService2Activity.class);
            intent.putExtra("act", acitivityInfoList.get(i).toString());
            startActivity(intent);
        }
    };

    //新增類別showCategoryTask
    class showCategoryTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            String result = "";

            try {
                URL url = new URL("https://cloud.culture.tw/frontsite/trans/SearchShowAction.do?method=doFindAllTypeJ");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                //判斷連線是否建立成功
                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    //讀取回應的結果
                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "utf-8"));
                    String str;
                    while ((str = reader.readLine()) != null) {
                        result = result + str;
                    }

                    //將回應的結果轉換成JSONArray
                    JSONArray jsonCategory = new JSONArray(result);
                    for (int i = 0; i < jsonCategory.length(); i++) {
                        //從JSONArray中取得各個元素，轉換為JSONObject
                        JSONObject json = jsonCategory.getJSONObject(i);

                        //取出活動種類的名稱加入到ArrayList中
                        categoryList.add(json.getString("categoryName"));
                        categoryCodeList.add(json.getString("categoryCode"));
                    }
                    reader.close();
                }
                urlConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //更新Spinner中活動種類資料
            categoryAdapter.notifyDataSetChanged();
        }
    }

    //新增類別showActTask
    class showActTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {

            String result = "";

            try {
                URL url = new URL("https://cloud.culture.tw/frontsite/trans/SearchShowAction.do?method=doFindTypeJ&category=" + params[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "utf-8"));
                    String str;
                    while ((str = reader.readLine()) != null) {
                        result = result + str;
                    }
                    acitivityList.clear();
                    acitivityInfoList.clear();

                    JSONArray jsonActs = new JSONArray(result);
                    for (int i = 0; i < jsonActs.length(); i++) {
                        acitivityList.add(jsonActs.getJSONObject(i).getString("title"));
                        acitivityInfoList.add(jsonActs.getJSONObject(i).getJSONArray("showInfo"));
                    }
                    reader.close();
                }
                urlConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            acitivityAdapter.notifyDataSetChanged();

        }
    }
}
