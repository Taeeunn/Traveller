package edu.skku.map.mapproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class InfoPage extends AppCompatActivity implements OnMapReadyCallback {

    EditText editText;
    Button button;
    TextView textView, temp, weather;
    String result;
    private ImageView image;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_page);


        editText=(EditText) findViewById(R.id.editText);
        button=(Button) findViewById(R.id.button);
        temp=(TextView) findViewById(R.id.temp);
        weather=(TextView) findViewById(R.id.weather);
        textView=(TextView) findViewById(R.id.textView);
        image=(ImageView) findViewById(R.id.image);

        button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String city=editText.getText().toString();

                SupportMapFragment mapFragment=(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                mapFragment.getMapAsync(InfoPage.this);


                OkHttpClient client=new OkHttpClient();

                HttpUrl.Builder urlBuilder=HttpUrl.parse("https://api.openweathermap.org/data/2.5/weather").newBuilder();
                urlBuilder.addQueryParameter("q", city);
                urlBuilder.addQueryParameter("appid", "5da2b04669534d2b1e667986fa586e06");

                String url=urlBuilder.build().toString();


                Request req=new Request.Builder().url(url).build();

                client.newCall(req).enqueue(new Callback() {

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        final String myResponse = response.body().string();
                        System.out.println(myResponse);
                        Gson gson=new GsonBuilder().create();
                        JsonObject job=gson.fromJson(myResponse, JsonObject.class);
                        JsonElement entry1=job.getAsJsonArray("weather");
                        String s=entry1.toString().substring(1, entry1.toString().length()-1);
                        if (s.contains("},")){
                            String data[] = s.split("},");
                            s=data[0]+"}";
                        }
                        JsonElement entry2=job.getAsJsonObject("main");
                        JsonElement entry3=job.getAsJsonObject("sys");
                        final JsonElement entry4=job.get("timezone");

                        final DataModel parse1=gson.fromJson(s, DataModel.class);
                        final DataModel parse2=gson.fromJson(entry2.toString(), DataModel.class);
                        final DataModel parse3=gson.fromJson(entry3.toString(), DataModel.class);

                        InfoPage.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                result="";
                                String iconUrl = "https://openweathermap.org/img/w/" + parse1.getIcon() + ".png";

                                Glide.with(InfoPage.this).load(iconUrl).into(image);

                                String temptext=Double.toString(Math.round((parse2.getTemp()-273.00)*100)/100.0)+"℃";
                                String feelliketext=Double.toString(Math.round((parse2.getFeels_like()-273.00)*100)/100.0)+"℃";
                                String mintext=Double.toString(Math.round((parse2.getTemp_min()-273.00)*100)/100.0)+"℃";
                                String maxtext=Double.toString(Math.round((parse2.getTemp_max()-273.00)*100)/100.0)+"℃";
                                String humidity=Double.toString(parse2.getHumidity())+"%";

                                temp.setText(temptext);
                                weather.setText(parse1.getMain());

                                result+=parse1.getDescription()+"\n";
                                result+="체감기온: "+feelliketext+"\n";
                                result+="최저기온: "+mintext+"\n";
                                result+="최고기온: "+maxtext+"\n";
                                result+="습도: "+humidity+"\n";


                                int timezone=Integer.parseInt(entry4.toString());

                                TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
                                java.util.Date sunrise=new java.util.Date((parse3.getSunrise()+timezone)*1000);
                                java.util.Date sunset=new java.util.Date((parse3.getSunset()+timezone)*1000);
                                System.out.println(myResponse);

                                if (timezone<0) {
                                    result += "일출시각: " + sunrise.toString().substring(0, sunrise.toString().length() - 5) + Integer.toString((int) (timezone / 3600)) + "\n";
                                    result += "일몰시각: " + sunset.toString().substring(0, sunset.toString().length() - 5) + Integer.toString((int) (timezone / 3600)) + "\n";
                                }else {
                                    result += "일출시각: " + sunrise.toString().substring(0, sunrise.toString().length() - 5) + "+" + Integer.toString((int) (timezone / 3600)) + "\n";
                                    result += "일몰시각: " + sunset.toString().substring(0, sunset.toString().length() - 5) + "+" + Integer.toString((int) (timezone / 3600)) + "\n";
                                }


                                textView.setText(result);
                            }
                        });
                    }

                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        final String city=editText.getText().toString();

        OkHttpClient client=new OkHttpClient();

        HttpUrl.Builder urlBuilder=HttpUrl.parse("https://api.openweathermap.org/data/2.5/weather").newBuilder();
        urlBuilder.addQueryParameter("q", city);
        urlBuilder.addQueryParameter("appid", "5da2b04669534d2b1e667986fa586e06");

        String url=urlBuilder.build().toString();
        Request req=new Request.Builder().url(url).build();

        final GoogleMap map=googleMap;
        client.newCall(req).enqueue(new Callback() {

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String myResponse = response.body().string();


                Gson gson=new GsonBuilder().create();
                JsonObject job=gson.fromJson(myResponse, JsonObject.class);
                JsonElement entry=job.getAsJsonObject("coord");

                final DataModel parse=gson.fromJson(entry.toString(), DataModel.class);


                InfoPage.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Double lon=parse.getLon();
                        Double lat=parse.getLat();
                        mMap=map;
                        LatLng SEOUL=new LatLng(lat, lon);

                        MarkerOptions markerOptions=new MarkerOptions();
                        markerOptions.position(SEOUL);
                        markerOptions.position(SEOUL);
                        markerOptions.title(city);
                        mMap.addMarker(markerOptions);

                        mMap.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(5));

                    }
                });
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }
        });
    }
}
