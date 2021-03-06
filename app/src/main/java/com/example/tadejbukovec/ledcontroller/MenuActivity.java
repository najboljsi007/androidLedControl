package com.example.tadejbukovec.ledcontroller;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.net.URL;

//wrapper class foe operations
interface Operation{
    String makeQuery();
}

class Constants{

    static final String url = "http://192.168.1.102/";

}


class LedOperation implements Operation{

    int ledIndex;
    String ledColor;
    int state;

    public LedOperation(int index, int state, String color) {
        ledIndex = index;
        this.state = state;
        ledColor = color;
    }

    @Override
    public String makeQuery() {
        return String.format(Constants.url + "led?led=%d&color=%s&turnOn=1", ledIndex, ledColor);
    }
}

class ContiniousServoOperation implements Operation{

    int enable;
    //1 for clockwise, 0 for counter-clockwise
    int rotation;

    public ContiniousServoOperation(int enable, int rotation) {
        this.enable = enable;
        this.rotation = rotation;
    }

    @Override
    public String makeQuery() {
        return String.format(Constants.url + "motor_continious?enable=%d&dir=%d",enable, rotation);
    }
}

class RandomServoOperation implements Operation{

    boolean enable;


    public RandomServoOperation(boolean enable) {
        this.enable = enable;
    }

    @Override
    public String makeQuery() {
        return String.format(Constants.url + "motor_random?enable=%b",enable);
    }
}

class NetworkThread implements Runnable{

    BlockingQueue<Operation> ledInstruction = new LinkedBlockingQueue();

    public void addToQueue(Operation operation){

                ledInstruction.add(operation);

    }

    @Override
    public void run() {
        while(true){

            try {
                Operation o = ledInstruction.take();
                try {
                    URL url = new URL(o.makeQuery());
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    InputStream ss = connection.getInputStream();
                    OutputStream cc = connection.getOutputStream();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    continue;
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}

public class MenuActivity extends AppCompatActivity {

    private NetworkThread runnable;
    private Thread networkThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        runnable = new NetworkThread();
        networkThread = new Thread(runnable);
        networkThread.start();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.authors);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "PENIS feat. Migga, mr. Erdzo, mr. Cherry", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        final Button whiteLight = (Button) findViewById(R.id.ledWhiteBtn);
        final Button colorLight = (Button) findViewById(R.id.ledColorBtn);
        final Switch spin = (Switch) findViewById(R.id.rotation);
        final Switch spinModify = (Switch) findViewById(R.id.rotationmode);
        final SeekBar speed = (SeekBar) findViewById(R.id.speed);
        final TextView textis = (TextView) findViewById(R.id.textView);

        //white light button logic
        whiteLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!whiteLight.getText().equals("Turn off da lite")){
                    String color = "00FFFFFF";
                    LedOperation activateLED = new LedOperation(1, 1, color);
                    runnable.addToQueue(activateLED);
                    whiteLight.setText("Turn off da lite");
                }
                else{
                    String color = "00000000";
                    LedOperation activateLED = new LedOperation(1, 1, color);
                    runnable.addToQueue(activateLED);
                    whiteLight.setText("White lite");
                }
            }
        });

        //color light button logic
        colorLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!colorLight.getText().equals("Turn off da lite")){
                    String newRandomColor = generateRandomColor();
                    LedOperation activateLED = new LedOperation(1, 1, newRandomColor);
                    runnable.addToQueue(activateLED);
                    colorLight.setText("Turn off da lite");
                }
                else{
                    String color = "00000000";
                    LedOperation activateLED = new LedOperation(1, 1, color);
                    runnable.addToQueue(activateLED);
                    colorLight.setText("Kolor lite");
                }
            }
        });


        //spin switch logic
        spin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked==true){
                    ContiniousServoOperation spinDaLight = new ContiniousServoOperation(1, 1);
                    runnable.addToQueue(spinDaLight);
                }
                else{
                    ContiniousServoOperation spinDaLight = new ContiniousServoOperation(0, 0);
                    runnable.addToQueue(spinDaLight);
                }
            }
        });

        //spin switch logic
        spinModify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked==true){
                    RandomServoOperation dizzleDaBitches = new RandomServoOperation(true);
                    runnable.addToQueue(dizzleDaBitches);
                }
                else{
                    ContiniousServoOperation spinDaLight = new ContiniousServoOperation(0, 0);
                    runnable.addToQueue(spinDaLight);
                }
            }
        });

        //speed switch logic
        speed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


                if (progress<30){
                    textis.setText("SLOW");
                }
                if (progress>30 || progress<70 && progress>30){
                    textis.setText("MEDIUM");
                }
                if (progress> 70){
                    textis.setText("\"LIGHT\" SPEED");
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });





    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    //method for creating LED API request
    public String sendLEDRequest(boolean isWhite, int ledNumber){

        String request;

        if (isWhite==true){
            String color = "FFFFFF";
            request = String.format("192.168.1.108/?led=%d&color=%s&turnOn=1", ledNumber, color);
        }
        else{
            String color = generateRandomColor();
            request = String.format("192.168.1.108/?led=%d&color=%s&turnOn=1", ledNumber, color);
        }

        return request;
    }

    //random LED color generator
    public String generateRandomColor(){

        Random rand = new Random();
        String red = Integer.toHexString(rand.nextInt(256));
        String green = Integer.toHexString(rand.nextInt(256));
        String blue = Integer.toHexString(rand.nextInt(256));
        String result = "00"+red+green+blue;
        return result;
    }


}
