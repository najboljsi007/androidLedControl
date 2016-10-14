package com.example.tadejbukovec.ledcontroller;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.net.URL;

//wrapper class foe operations
class Operation{
}

class LedOperation extends Operation{

    int ledIndex;
    String ledColor;
    int state;

    public LedOperation(int index, int state, String color) {
        ledIndex = index;
        this.state = state;
        ledColor = color;
    }

}
class ServoOperation extends Operation{}

class NetworkThread implements Runnable{

    BlockingQueue<Operation> ledInstruction = new LinkedBlockingQueue();

    public void addToQueue(String color){
        for(int i=0;i<15;i++){
            LedOperation newOperation = new LedOperation(i, 1, color);
            ledInstruction.add(newOperation);
        }
    }

    @Override
    public void run() {
        while(true){

            try {
                Operation o = ledInstruction.take();
                URL url;

                url = new URL(String.format("http://192.168.1.108/?led=%d&color=%s&turnOn=%d", 0, (LedOperation) o. ));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.authors);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "PENIS feat. Migga, mr. Erdzo, mr. Cherry", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        Button whiteLight = (Button) findViewById(R.id.ledWhiteBtn);
        Button colorLight = (Button) findViewById(R.id.ledColorBtn);
        Button servo = (Button) findViewById(R.id.servoBtn);
        Button kill = (Button) findViewById(R.id.killBtn);

        //white light button logic
        whiteLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String color = "00FFFFFF";
                LedOperation activateLED = new LedOperation(1, color);
            }
        });

        //color light button logic
        colorLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newRandomColor = generateRandomColor();
                LedOperation activateLED = new LedOperation(1, newRandomColor);
            }
        });

        //servo button logic
        servo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //exit the app button logic
        kill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
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
