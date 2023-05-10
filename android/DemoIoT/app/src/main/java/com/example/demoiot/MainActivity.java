package com.example.demoiot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;

import android.widget.TextView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.listener.OnDrawListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.slider.Slider;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.github.mikephil.charting.data.Entry;

public class MainActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
//    public  static ProgressBar progressBar;
    public static int isInProgress = -1;
    public static long referenceTimeStamp = -1;
    MQTTHelper mqttHelper;
    TextView txtTemp, txtHumi;

    LabeledSwitch btnLed, btnPump;
    Slider sldLed;
    public LineChart lineChart;

    String btn1= "nguyenvudev20/feeds/btn1";
    String btn2= "nguyenvudev20/feeds/btn2";
    String cb1= "nguyenvudev20/feeds/cb1";  // Temperature
    String cb2= "nguyenvudev20/feeds/cb2";  // Light
    String khutrang= "nguyenvudev20/feeds/khautrang";
    String hinhanh= "nguyenvudev20/feeds/cb3";
    String dimmer= "nguyenvudev20/feeds/dimmer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        progressBar = (ProgressBar) findViewById(R.id.simpleProgressBar);
//        progressBar.setVisibility(View.VISIBLE);

        txtTemp = findViewById(R.id.txtTemperature);
        txtHumi = findViewById(R.id.txtHumidity);
        sldLed = findViewById(R.id.sldLed);
        btnLed = findViewById(R.id.btnLed);
        btnPump  = findViewById(R.id.btnPump);

        lineChart = findViewById(R.id.activity_main_linechart);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        sldLed.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {

            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                int val = (int)slider.getValue();
                sendDataMQTT(dimmer, String.valueOf(val));
            }
        });

        btnLed.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if(isOn == true){
                    sendDataMQTT(btn1, "1");
                }else{
                    sendDataMQTT(btn1, "0");
                }
            }
        });


        btnPump.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if(isOn == true){
                    sendDataMQTT(btn2, "1");
                }else{
                    sendDataMQTT(btn2, "0");
                }
            }
        });
//
//        String[] arrayInitValueToTopics =
//            {
//                "quang_nguyen_van/feeds/button1",
//                "quang_nguyen_van/feeds/button2",
//                "quang_nguyen_van/feeds/sensor1",  // Temperature
////                "quang_nguyen_van/feeds/sensor2",  // Light
//                "quang_nguyen_van/feeds/sensor3",  // Humidity
////                "quang_nguyen_van/feeds/ai",
////                "quang_nguyen_van/feeds/image",
//                "quang_nguyen_van/feeds/dimmer"
//            };
        String[] arrayInitValueToTopics =  {
                    "nguyenvudev20/feeds/btn1",
                    "nguyenvudev20/feeds/btn2",
                    "nguyenvudev20/feeds/cb1",  // Temperature
                    "nguyenvudev20/feeds/cb2",  // Light
                    "nguyenvudev20/feeds/dimmer"
                };
        // https://io.adafruit.com/api/v2/quang_nguyen_van/feeds/sensor1/data/chart?hours=72
        // https://io.adafruit.com/api/v2/quang_nguyen_van/feeds/sensor1/data?limit=1
        // String url = "https://io.adafruit.com/api/v2/quang_nguyen_van/feeds/sensor1/data?limit=1";

        isInProgress = arrayInitValueToTopics.length;
        for(int i = 0; i < arrayInitValueToTopics.length; i++) {
            String url = "https://io.adafruit.com/api/v2/" + arrayInitValueToTopics[i] +"/data?limit=1";
            requestWebData(url, i, "data");
            Log.d("TEST:",arrayInitValueToTopics[i]);
        }

        // https://io.adafruit.com/api/v2/quang_nguyen_van/feeds/sensor1/data/chart?hours=72
        String urlChart = "https://io.adafruit.com/api/v2/"+cb1+"/data/chart?hours=5";
        requestWebData(urlChart, 0, "chart");

        startMQTT();
    }

    public void requestWebData(String url, int index, String queryType){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.d("TEST error:",e.getMessage());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if(response.isSuccessful()){
                    String jsonResponse = response.body().string();
                    Log.d("TEST: ",jsonResponse);
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String dataResponse = "";
                            if(queryType.equals("data")) {
                                dataResponse = jsonResponse.replace("[", "");
                                dataResponse = dataResponse.replace("]", "");
//                                parseJson(jsonResponse, queryType);
                            }
                            else{ dataResponse = jsonResponse; }

                            parseJson(dataResponse, queryType);

                            try {
                                Thread.sleep(1000);
                                if(index == (isInProgress - 1)){
                                    Log.d("TEST", String.valueOf(index));
//                                progressBar.setVisibility(View.GONE);
                                    isInProgress = -1;
//                                    progressDialog.cancel();
                                    progressDialog.dismiss();
                                }
                                // Do some stuff
                            } catch (Exception e) {
                                e.getLocalizedMessage();
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     *
     * @param JSONString
     * @param data / chart
     */
    public void parseJson(String response, String queryType){
        if(queryType.equals("data")) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                String topic = jsonObject.getString("feed_key");
                String message = jsonObject.getString("value");
                Log.d("Topic Data", topic + ": " + message.toString());
                setFeedValue(topic, message.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if(queryType.equals("chart")){
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray data = jsonObject.getJSONArray("data");

                ArrayList<Entry> entries = new ArrayList<>();

                Log.d("Chart Data", data.toString());
                ArrayList<String> xAxisLabels = new ArrayList<String>();
                for(int i = 0; i < data.length(); i++) {
                    Log.d("Chart Data", data.getJSONArray(i).toString());

                    String str_date = data.getJSONArray(i).getString(0).toString();
                    Date localTime = null;
                    try {
                        localTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).parse(str_date);

                        long timeStamp = localTime.getTime();
                        if(i == 0){ referenceTimeStamp = timeStamp; }

                        long Xnew = timeStamp - referenceTimeStamp;
                        xAxisLabels.add(getHour(timeStamp));

                        System.out.println("TimeStamp is " + Xnew);
                        entries.add(new Entry(Float.valueOf(i), Float.parseFloat(data.getJSONArray(i).getString(1).toString())));
                    } catch (java.text.ParseException e) {
                        e.printStackTrace();
                    }
                }

                LineDataSet dataset = new LineDataSet(entries, "Temperature");
                dataset.setColors(ColorTemplate.rgb("FF062BEF"));
                dataset.setCircleColor(ColorTemplate.rgb("#fc0339"));
//                dataset.addColor(ColorTemplate.rgb("#fc3503"));
                //dataset.s

                ArrayList<String> labels = new ArrayList<String>();
                labels.add("Sensor1");

                LineData data1 = new LineData(dataset);

                lineChart.setData(data1);
                lineChart.animateY(100);
                lineChart.animateX(100);

                ValueFormatter xAxisFormatter = new HourAxisValueFormatter(referenceTimeStamp);
                XAxis xAxis = lineChart.getXAxis();
//                xAxis.setLabelRotationAngle(45);

                xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabels));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String getHour(long timestamp){
        Date mDate = new Date();
        DateFormat mDataFormat = new SimpleDateFormat("HH:mm:ss");
        try{
            mDate.setTime(timestamp);
            return mDataFormat.format(mDate);
        }
        catch(Exception ex){
            return "xx";
        }
    }

    public void sendDataMQTT(String topic, String value){
        MqttMessage msg = new MqttMessage();
        msg.setId(1234);
        msg.setQos(0);
        msg.setRetained(false);

        byte[] b = value.getBytes(Charset.forName("UTF-8"));
        msg.setPayload(b);

        try {
            mqttHelper.mqttAndroidClient.publish(topic, msg);
        }catch (MqttException e){
        }
    }

    public void setFeedValue(String topic, String value){
        if(topic.contains("cb1")){  // Temperature
            txtTemp.setText(value + "°C");
        }else if(topic.contains("cb2")){  // Humidity
            txtHumi.setText(value + "%");
        }else if(topic.contains("btn1")){
            if(value.equals("1")) {
                btnLed.setOn(true);
            }else{
                btnLed.setOn(false);
            }
        }else if(topic.contains("btn2")){
            if(value.equals("1")) {
                btnPump.setOn(true);
//                Integer color = btnPump.getColorBorder();
//                Log.d("Border Color:", color.toString());
//                btnPump.setColorOn(-49023);
//                btnPump.setColorOff(Integer.parseInt("#03A9F4"));
//                btnPump.setColorBorder(Integer.parseInt("#03A9F4"));
//                btnPump.setColorBorder(Color.green(200));
//                btnPump.setBackgroundColor(Color.green(2000));
            }else{
                btnPump.setOn(false);
//                btnPump.setColorBorder(-29023);
//                btnPump.setColorOff(-19023);
            }
        }else if(topic.contains("dimmer")){
            sldLed.setValue(Integer.parseInt(value));
            Log.d("Dimmer",  topic + "***" + value);
//                    txtHumi.setText(Integer.parseInt(message.toString()) + "%");
        }
    }

    public void startMQTT(){
        mqttHelper = new MQTTHelper(this);
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("TEST",  topic + "***" + message.toString());
                setFeedValue(topic, message.toString());

//                if(topic.contains("sensor1")){  // Temperature
//                    txtTemp.setText(message.toString() + "°C");
//                }else if(topic.contains("sensor3")){  // Humidity
//                    txtHumi.setText(message.toString() + "%");
//                }else if(topic.contains("button1")){
//                    if(message.toString().equals("1")) {
//                        btnLed.setOn(true);
//                    }else{
//                        btnLed.setOn(false);
//                    }
//                }else if(topic.contains("button2")){
//                    if(message.toString().equals("1")) {
//                        btnPump.setOn(true);
//                        //Integer color = btnPump.getColorBorder();
//                        //Log.d("Border Color:", color.toString());
//                        //btnPump.setColorOn(-49023);
//                        //btnPump.setColorBorder(Color.green(200));
//                        //btnPump.setBackgroundColor(Color.green(200));
//                    }else{
//                        btnPump.setOn(false);
//                        //btnPump.setColorBorder(-3000);
//                        //btnPump.setColorOff(-3000);
//                    }
//                }else if(topic.contains("dimmer")){
//                    sldLed.setValue(Integer.parseInt(message.toString()));
//                    Log.d("Dimmer",  topic + "***" + message.toString());
////                    txtHumi.setText(Integer.parseInt(message.toString()) + "%");
//                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    public static class chartData {
        public String time;
        public float val;

        public void setTime(String time) {
            this.time = time;
        }

        public String getTime() {
            return time;
        }

        public void setVal(float val) {
            this.val = val;
        }

        public float getVal() {
            return val;
        }
    }
}