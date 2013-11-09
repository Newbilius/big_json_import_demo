package com.example.big_json_import_demo;

import android.app.Activity;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.gson.GsonBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.*;

public class Main extends Activity {
    public static final String baseUrl=""; //вписываем сюда базовый адрес

    public static final String UrlSmall=baseUrl+"small.json";//7 килобайт
    public static final String UrlBig=baseUrl+"big.json";    //5.79 мб.
    public static final String UrlVeryBig=baseUrl+"very_big.json";    //13.3 мб.

    public static GsonBuilder gsonBuilder = new GsonBuilder();
    public static com.google.gson.Gson Gson = gsonBuilder.create();

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

    public class LoadBigDataTmpFile extends AsyncTask<Void, Void, Void> {

        String _url="";
        File cache_dir;

        public LoadBigDataTmpFile(String url){
            _url=url;
            cache_dir = getExternalCacheDir();
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

                Log.d("начали парсинг...");
                FileInputStream fileInputStream=new FileInputStream(file);
                InputStreamReader reader = new InputStreamReader(fileInputStream);
                HumorItems list= Gson.fromJson(reader,HumorItems.class);
                Log.d("закончили парсинг.");

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
                InputStreamReader reader = new InputStreamReader(stream);
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

    public class LoadBigDataPublish extends AsyncTask<Void, Void, Void> {

        String _url="";

        public LoadBigDataPublish(String url){
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
                InputStream stream = new InputStreamDecorator<InputStream>(AndroidHttpClient.getUngzippedContent(httpEntity)) {
                    @Override
                    protected void publishByteReaded(long amount) {
                        super.publishByteReaded(amount);
                        // Обработчик прогресса чтения файла
                        Log.d("скачано "+Main.PrepareSize(bytesReadedCount));
                    }
                };

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
                InputStreamReader reader = new InputStreamReader(stream);
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

        //успешно скачивает маленький
        //new LoadData(UrlSmall).execute();

        //фатально падает, слишком большой
        //new LoadData(UrlBig).execute();

        //нормально качает, большой
        //new LoadBigData(UrlBig).execute();
        //new LoadBigDataTmpFile(UrlBig).execute();

        //нормально качает, очень большой
        //new LoadBigData(UrlBig).execute();    //без временного файла, парсинг в реальном времени, без сообщения процесса
        //new LoadBigDataTmpFile(UrlVeryBig).execute(); //с сохранением во временный файл и сообщением процесса скачивания
        new LoadBigDataPublish(UrlBig).execute(); //без временного файла, парсинг в реальном времени и с сообщением процесса
    }

    public static String PrepareSize(long size){
        if (size<1024){
            return size+" б.";
        }else
        {
            return size/1024+" кб.";
        }
    }
}
