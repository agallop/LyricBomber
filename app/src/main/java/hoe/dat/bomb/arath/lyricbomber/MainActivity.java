package hoe.dat.bomb.arath.lyricbomber;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class MainActivity extends AppCompatActivity {

    String string_to_send;
    Button bombButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bombButton = (Button) findViewById(R.id.bomb);
        bombButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bomb();
            }
        });
    }

    public void bomb(){
        final SmsManager smsManager = SmsManager.getDefault();
        final CountDownLatch latch = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    string_to_send = httpGet("https://www.cs.utexas.edu/~agallop/Text%20Bomber/Lyric1");
                } catch (Exception ex){

                }
                latch.countDown();
            }
        }).start();
        while(true) {
            try {
                latch.await();
                break;
            } catch (Exception ex) {

            }
        }

        final Scanner sc = new Scanner(string_to_send);

        smsManager.sendTextMessage("Put Number Here", null, "This Text Bomber has been brought to you by Anthony", null, null);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (sc.hasNextLine()){
                    while (true)
                    try{
                    Thread.sleep(1000);
                    smsManager.sendTextMessage("Put Number Here", null, sc.nextLine(), null, null);
                        break;
                    } catch (Exception ex){

                    }
                }
            }
        }).start();

    }

    public static String httpGet(String urlStr) throws IOException {
        Log.d("MAIN", "httpstart");
        URL url = new URL(urlStr);
        URLConnection con = url.openConnection();
        HttpURLConnection conn = (HttpURLConnection) con;
        //conn.setConnectTimeout(10000);
        //conn.setReadTimeout(10000);
        // conn.setRequestMethod("GET");

        if(conn.getResponseCode() == -1){
            return "failed to connect";
        }


        if (conn.getResponseCode() != 200) {
            Log.d("MAIN", "response != 200");
            return conn.getResponseMessage();
        }

        // Buffer the result into a string
        BufferedReader rd = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            Log.d("MAIN", "line append");
            sb.append(line + "\n");
        }
        rd.close();
        Log.d("MAIN", "finish append");
        conn.disconnect();
        return sb.toString();
    }
}
