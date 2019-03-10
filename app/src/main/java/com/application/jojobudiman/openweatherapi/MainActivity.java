package com.application.jojobudiman.openweatherapi;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    Button getData;
    ImageView cond;
    EditText city;
    TextView howcountry, howcond, howsunrise, howsunset, howtemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cond = (ImageView) findViewById(R.id.geticon);
        city = (EditText) findViewById(R.id.city);
        howcond = (TextView) findViewById(R.id.getcondition);
        howcountry = (TextView) findViewById(R.id.getcountry);
        howsunrise = (TextView) findViewById(R.id.getsunrise);
        howsunset = (TextView) findViewById(R.id.getsunset);
        howtemp = (TextView) findViewById(R.id.gettemp);
        getData = (Button) findViewById(R.id.getweather);

    }


    public void cityGet(View view) {

        String url="http://api.openweathermap.org/data/2.5/weather?q="+ city.getText().toString() +"&APPID=b576207b419a032733550f2ab8ba71d9&mode=json&units=metric";
        new getAPINews().execute(url);
    }


    public class getAPINews extends AsyncTask<String, String, String> {


        @Override
        protected String  doInBackground(String... params) {


            try {
                String NewsData;
                URL url = new URL(params[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(7000);

                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    NewsData = ConvertInputToStringNoChange(in);
                    publishProgress(NewsData);
                } finally {
                    urlConnection.disconnect();
                }

            }catch (Exception ex){}
            return null;
        }




        protected void onProgressUpdate(String... progress) {

            try {
                // Melakukan ekstraksi informasi dari API
                JSONObject json= new JSONObject(progress[0]); // Memanggil JSONObject (Dari JSON object) dari data


                // Error handling jika nama kota tidak ada di Open Weather API, so far belum berhasil
                JSONObject msg = json.getJSONObject("object");
                String errorMsg = msg.getString("message");

                //Mendapatkan informasi cuaca
                JSONObject main = json.getJSONObject("main");
                String temp = main.getString("temp");

                // Mendapatkan deskripsi informasi cuaca dari array JSON
                JSONArray descWeather = json.getJSONArray("weather"); //Memanggil JSONArray (Dari array object) dari data
                JSONObject desc = descWeather.getJSONObject(0);
                String weatherdesc = desc.getString("description");
                String imageIcon = desc.getString("icon");
                String weatherImg = "http://openweathermap.org/img/w/" + imageIcon + ".png";

                // Mendapatkan informasi day/night length dan nama negara
                JSONObject sys = json.getJSONObject("sys");
                String sunrise = sys.getString("sunrise");
                String sunset = sys.getString("sunset");
                String country = sys.getString("country");

                howcountry.setText(country);
                howcond.setText(weatherdesc);
                Picasso.get().load(weatherImg).resize(300,300).centerCrop().into(cond);
                howtemp.setText("Weather: " + temp + "°C");
                howsunrise.setText("Sunrise: " + sunrise);
                howsunset.setText("Sunset: " + sunset);



            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(),"Fail loading API data",Toast.LENGTH_LONG).show(); //Jika error API
            }


        }


    }


    public static String ConvertInputToStringNoChange(InputStream inputStream) {

        BufferedReader bureader=new BufferedReader( new InputStreamReader(inputStream));
        String line ;
        String linereultcal="";

        try{
            while((line=bureader.readLine())!=null) {
                linereultcal+=line;

            }
            inputStream.close();


        }catch (Exception ex){}

        return linereultcal;
    }
}
