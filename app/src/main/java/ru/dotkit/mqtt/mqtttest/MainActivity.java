package ru.dotkit.mqtt.mqtttest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ru.dotkit.mqtt.broker.Example_Server;
import ru.dotkit.mqtt.broker.Server;
import ru.dotkit.mqtt.broker.ServerOptions;
import ru.dotkit.mqtt.client.Example_Client;

public class MainActivity extends AppCompatActivity {

    private Server _server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ServerOptions options = new ServerOptions();
        _server = new Server(options);
        _server.start();

//        Example_Server exampleServer = Example_Server.getServer();
//        exampleServer.start();
//
//        Runnable r = new Runnable() {
//            @Override
//            public void run() {
//                Example_Client client = new Example_Client();
//                try {
//                    client.openConnection();
//                    //Thread.sleep(100*1000);
//                    client.sendData("Hello! It's me!".getBytes());
//                    client.closeConnection();
//                } catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//        };
//
//        Thread t = new Thread(r);
//        t.start();
//        while (true){}
    }
}
