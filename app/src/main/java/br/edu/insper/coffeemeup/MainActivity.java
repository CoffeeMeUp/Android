package br.edu.insper.coffeemeup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private final int    PORT = 5000;
    private final String HOST = "192.168.43.189";

    private Socket socket;
    private Button button;

    private void openSocket()
    {
        try {
            socket = new Socket(HOST, PORT);
            Log.d("SOCKET", String.valueOf(socket.isConnected()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        openSocket();

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
