package ca.maibach.rebooter;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity{
    private EditText editTextIp;
    private EditText editTextPort;
    private EditText editTextUser;
    private EditText editTextPass;
    private Button buttonReboot;
    private Button buttonActivate;
    private Button buttonDeactivate;
    private Button buttonRebootRaspberry;
    private TextView textViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextIp = (EditText) findViewById(R.id.editTextIp);
        editTextPort = (EditText) findViewById(R.id.editTextPort);
        editTextUser = (EditText) findViewById(R.id.editTextUser);
        editTextPass = (EditText) findViewById(R.id.editTextPass);
        buttonReboot = (Button) findViewById(R.id.buttonReboot);
        buttonActivate = (Button) findViewById(R.id.buttonActivate);
        buttonDeactivate = (Button) findViewById(R.id.buttonDeactivate);
        buttonRebootRaspberry = (Button) findViewById(R.id.buttonRebootRaspberry);
        textViewResult = (TextView) findViewById(R.id.textViewResult);

        

        buttonReboot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textViewResult.setText("Rebooting miner...");
                new SSHTask().execute(getParams("~/Desktop/restart.py"));
            }
        });

        buttonRebootRaspberry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textViewResult.setText("Rebooting raspberry...");
                new SSHTask().execute(getParams("sudo reboot"));
            }
        });

        buttonActivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textViewResult.setText("Activating watchdog...");
                new SSHTask().execute(getParams("~/Desktop/watchdog.py 0 &"));
            }
        });

        buttonDeactivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textViewResult.setText("Deactivating watchdog...");
                new SSHTask().execute(getParams("killall -9 watchdog.py"));
            }
        });
    }

    private String[] getParams(String command){
        String[] params = new String[5];
        params[0] = editTextUser.getText().toString();
        params[1] = editTextPass.getText().toString();
        params[2] = editTextIp.getText().toString();
        params[3] = editTextPort.getText().toString();
        params[4] = command;

        return params;
    }


    public class SSHTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String user = params[0];
            String pass = params[1];
            String ip = params[2];
            int port = Integer.parseInt(params[3]);

            SSHManager manager = new SSHManager(user, pass, ip, "", port);
            String errorMessage = manager.connect();
            String response = manager.sendCommand(params[4]);
            manager.close();
            return "Connection error: " + errorMessage +
                    "\nCommand executed: " + params[4] +
                    "\nResponse: " + response;
        }

        @Override
        protected void onPostExecute(String result) {
            textViewResult.setText(result);
        }
    }
}
