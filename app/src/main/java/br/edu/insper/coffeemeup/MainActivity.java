package br.edu.insper.coffeemeup;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    // From https://examples.javacodegeeks.com/android/core/socket-core/android-socket-example/
    private   ServerSocket serverSocket;
    protected Handler      updateConversationHandler;
    protected Thread       serverThread = null;


    private static final int    PORT = 5000;
    private static final String HOST = "192.168.43.189";

    private Socket socket;
    private Button button;

    @Override
    protected void onStop()
    {
        super.onStop();
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        openSocket();
        updateConversationHandler = new Handler();
        this.serverThread = new Thread(new ServerThread());
        this.serverThread.start();


        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    DataOutputStream dataOutputStream = new DataOutputStream(
                            socket.getOutputStream());
                    dataOutputStream.writeUTF("Hello from Android!");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    openSocket();
                }
            }
        });
    }
}
