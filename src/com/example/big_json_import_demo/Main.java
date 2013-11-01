package com.example.big_json_import_demo;

import android.app.Activity;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class Main extends Activity {
    String UrlSmall="http://siteszone.ru/test_data/small.json";//7 килобайт
    String UrlBig="http://siteszone.ru/test_data/big.json";    //5.79 мб.
    String UrlVeryBig="http://siteszone.ru/test_data/very_big.json";    //13.3 мб.

    public static GsonBuilder gsonBuilder = new GsonBuilder();
    public static com.google.gson.Gson Gson = gsonBuilder.create();
    public File cache_dir;

    public class LoadData extends AsyncTask<Void, Void, Void> {

        String _url="";

        public LoadData(String url){
            _url=url;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //скачивание данных
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(_url);

            HttpResponse response = null;
            try {
                //скачивание данных
                response = httpclient.execute(httppost);

                HttpEntity httpEntity=response.getEntity();
                InputStream stream = AndroidHttpClient.getUngzippedContent(httpEntity);

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder responseBuilder= new StringBuilder();
                char[] buff = new char[1024*512];
                int read;
                while((read = bufferedReader.read(buff)) != -1) {
                    responseBuilder.append(buff, 0, read) ;
                    Log.d("скачано " + PrepareSize(responseBuilder.length()));
                }

                //парсинг данных
                HumorItems list= Gson.fromJson(responseBuilder.toString(),HumorItems.class);

                //тестовый вывод
                for (HumorItem message:list.Items){
                    Log.d("Текст: "+message.text);
                    Log.d("Ссылка: "+message.url);
                    Log.d("-------------------");
                }

                Log.d("ВСЕГО СКАЧАНО "+list.Items.size());

            } catch (IOException e) {
                e.printStackTrace();
                Log.e("ошибка "+e.getMessage());
            }

            return null;
        }
    }

    public class LoadBigData extends AsyncTask<Void, Void, Void> {

        String _url="";

        public LoadBigData(String url){
            _url=url;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //скачивание данных
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(_url);

            HttpResponse response = null;
            try {
                //скачивание данных
                response = httpclient.execute(httppost);

                HttpEntity httpEntity=response.getEntity();
                InputStream stream = AndroidHttpClient.getUngzippedContent(httpEntity);

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
                File file = new File(cache_dir, "temp_json_new.json");

                if (file.exists()){
                    file.delete();
                }

                file.createNewFile();

                FileOutputStream fileOutputStream=new FileOutputStream(file,true);
                BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(fileOutputStream));


                char[] buff = new char[1024*1024];
                int read;
                long FullSize=0;
                while((read = bufferedReader.read(buff)) != -1) {
                    bufferedWriter.write(buff,0,read);
                    FullSize+=read;
                    Log.d("скачано " + PrepareSize(FullSize));
                }
                bufferedWriter.flush();
                fileOutputStream.close();


                FileInputStream fileInputStream=new FileInputStream(file);
                InputStreamReader reader = new InputStreamReader(fileInputStream);
                HumorItems list= Gson.fromJson(reader,HumorItems.class);

                    //тестовый вывод
                    for (HumorItem message:list.Items){
                        Log.d("Текст: "+message.text);
                        Log.d("Ссылка: "+message.url);
                        Log.d("-------------------");
                    }
                Log.d("ВСЕГО СКАЧАНО "+list.Items.size());

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

        cache_dir = this.getExternalCacheDir();

        //успешно скачивает
        //new LoadData(UrlSmall).execute();

        //падает, слишком большой
        //new LoadData(UrlBig).execute();

        //нормально качает большой
        //new LoadBigData(UrlBig).execute();

        //нормально качает очень большой
        new LoadBigData(UrlVeryBig).execute();
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
