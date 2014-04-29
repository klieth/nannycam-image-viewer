package info.piosar.nannycam.nannycam;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;


public class LoginActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        final Button button = (Button) findViewById(R.id.connect);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = ((EditText)findViewById(R.id.ip)).getText().toString();
                String u = ((EditText)findViewById(R.id.username)).getText().toString();
                String p = ((EditText)findViewById(R.id.password)).getText().toString();
                new LoginTask().execute(ip, u, p);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class LoginTask extends AsyncTask<String, String, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            if (params.length != 3) {
                Log.d("LoginTask", "Must pass info from all three blanks");
                publishProgress("Must pass info from all three blanks");
                return null;
            }
            try {
                Log.d("LoginTask","Connecting to: " + params[0]);
                //Socket sock = new Socket(params[0], 8080);
                Socket sock = AppConstants.initSocket(params[0], 8080);
                OutputStream os = sock.getOutputStream();
                InputStream is = sock.getInputStream();
                // Do the USER portion
                os.write("USER bob".getBytes());
                int f = 0, s;
                while ((s = is.read()) > -1) {
                    if ((char)s == 'o' || (char)s == 'n') {
                        f = s;
                    }
                    if ((char)f == 'o' && (char)s == 'k') {
                        break;
                    } else if ((char)f == 'n' && (char)s == 'o') {
                        return false;
                    }
                }
                Log.d("LoginTask","Passed USER stage");
                // Do the PASS portion
                os.write("PASS pass".getBytes());
                f = 0; s;
                while ((s = is.read()) > -1) {
                    if ((char)s == 'o') {
                        f = s;
                    }
                    if ((char)f == 'o' && (char)s == 'k') {
                        Log.d("LoginTask","Passed PASS stage");
                        return true;
                    } else if ((char)f == 'n' && (char)s == 'o') {
                        return false;
                    }
                }
            } catch (UnknownHostException e) {
                Log.d("LoginTask","Unknown host");
                publishProgress("Unknown host");
                return null;
            } catch (IOException e) {
                Log.d("LoginTask", "Socket connection failed due to an IOException");
                return null;
            } catch (Exception e) {
                Log.d("LoginTask", "Some other unknown exception: " + e.getMessage());
                return null;
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... msg) {
            Toast.makeText(getApplicationContext(), msg[0], Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Log.d("LoginTask","finished");
            if (result != null && result) {
                Intent toMain = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(toMain);
            } else {
                Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
