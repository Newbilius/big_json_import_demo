package com.example.big_json_import_demo;

import android.app.Activity;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class Main extends Activity {
    String url="http://siteszone.ru//test_data/small.json";
    //String url="http://siteszone.ru//test_data/big.json";

    public static GsonBuilder gsonBuilder = new GsonBuilder();
    public static com.google.gson.Gson Gson = gsonBuilder.create();

    public class LoadData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            Log.d("333");
            //скачивание данных
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            String responseString=null;

            HttpResponse response = null;
            try {
                //скачивание данных
                response = httpclient.execute(httppost);

                HttpEntity httpEntity=response.getEntity();
                InputStream stream = AndroidHttpClient.getUngzippedContent(httpEntity);

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder responseBuilder= new StringBuilder();
                char[] buff = new char[1024*32];
                int read;
                while((read = bufferedReader.read(buff)) != -1) {
                    responseBuilder.append(buff, 0, read) ;
                    Log.d("скачано " + PrepareSize(responseBuilder.length()));
                }

                //парсинг данных
                ArrayList<HumorItem> items;
                Type listOfTestObjectsType = new TypeToken<ArrayList<HumorItem>>(){}.getType();
                items= Gson.fromJson(responseBuilder.toString(),listOfTestObjectsType);

                //тестовый вывод
                for (HumorItem item:items){
                    Log.d("Текст: "+item.text);
                    Log.d("Ссылка: "+item.url);
                    Log.d("-------------------");
                }

            } catch (IOException e) {
                e.printStackTrace();
                Log.e("ошибка "+e.getMessage());
            }

            return null;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Log.d("111");
        new LoadData().execute();
        Log.d("2222");
    }

    public String PrepareSize(long size){
        if (size<1024){
            return size+" б.";
        }else
        {
            return size/1024+" кб.";
        }
    }
}
