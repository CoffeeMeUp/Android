package br.edu.insper.coffeemeup;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;


public class MainActivity extends AppCompatActivity {

    // From https://examples.javacodegeeks.com/android/core/socket-core/android-socket-example/
    private   ServerSocket serverSocket;
    protected Handler      updateConversationHandler;
    protected Thread       serverThread = null;

    protected static int    PORT = 6666;
//    protected static String host = "192.168.43.189";  // Pedro's phone
    protected static String host = "192.168.0.13";   // Pedro's house

    protected static final String HOUR_HEADER    = "hora:";
    protected static final String PAYLOAD_SUFFIX = "\r\n";

    private Socket  socket;
    private boolean connected = false;

    private Button   button;    // msg sender
    private Button   defineIpBtn;
    private TextView text;      // msgs recieved from the server
    private EditText hourText;  // hour to be woken up
    private EditText hostIpText;

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // msg receiver
        updateConversationHandler = new Handler();
        this.serverThread = new Thread(new ServerThread());
        this.serverThread.start();
        text = (TextView) findViewById(R.id.txt_msg);

        // msg sender
        hourText = (EditText) findViewById(R.id.hour_text);
        button   = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ClientThread().execute();
            }
        });

        hostIpText  = (EditText) findViewById(R.id.host_ip);
        defineIpBtn = (Button) findViewById(R.id.define_ip);
        defineIpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                host = hostIpText.getText().toString();
                Log.d("IP DEFINE", host);
                Toast.makeText(getApplicationContext(), "IP definido para " + host,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private class ClientThread extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params)
        {
            try {
                socket = new Socket(host, PORT);
                OutputStream os = socket.getOutputStream();
                String msg = hourText.getText().toString();
                String payload = HOUR_HEADER + msg + PAYLOAD_SUFFIX;
                os.write(payload.getBytes(Charset.forName("UTF-8")));
            } catch (IOException e) {
                Handler handler = new Handler(getApplicationContext().getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), R.string.invalid_ip,
                                Toast.LENGTH_LONG).show();
                    }
                });
                e.printStackTrace();
            } catch (RuntimeException e) {
                Log.d("SOCKET OPEN", "Socket doesn't exists");
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (RuntimeException e) {
                    Log.d("SOCKET CLOSE", "Could not close socket");
                }
            }
            return null;
        }
    }

    private class ServerThread implements Runnable {

        @Override
        public void run() {
            Socket socket = null;
            try {
                serverSocket = new ServerSocket(PORT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    socket = serverSocket.accept();
                    CommunicationThread communicationThread = new CommunicationThread(socket);
                    new Thread(communicationThread).start();
                } catch (IOException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class CommunicationThread implements Runnable {

        private Socket clientSocket;
        private BufferedReader input;

        CommunicationThread(Socket clientSocket)
        {
            this.clientSocket = clientSocket;
            try {
                this.input = new BufferedReader(
                        new InputStreamReader(this.clientSocket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    String msg = input.readLine();
                    updateConversationHandler.post(new UpdateUIThread(msg));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class UpdateUIThread implements Runnable {

        private String msg;

        UpdateUIThread(String msg)
        {
            this.msg = msg;
        }

        @Override
        public void run() {
            text.setText(msg);
        }
    }
}
