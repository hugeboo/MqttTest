package ru.dotkit.mqtt.mqtttest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ru.dotkit.mqtt.broker.Server;
import ru.dotkit.mqtt.client.Client;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Server server = Server.getServer();
        server.start();

        Runnable r = new Runnable() {
            @Override
            public void run() {
                Client client = new Client();
                try {
                    client.openConnection();
                    //Thread.sleep(100*1000);
                    client.sendData("Hello! It's me!".getBytes());
                    client.closeConnection();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
        while (true){}
    }
}
