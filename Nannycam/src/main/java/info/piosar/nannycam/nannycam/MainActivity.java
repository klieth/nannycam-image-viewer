package info.piosar.nannycam.nannycam;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img = (ImageView) findViewById(R.id.imageView);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
        } else if (id == R.id.action_connect) {
            //new ConnectTask().execute("54.187.112.23");
            new ConnectTask().execute("192.168.1.118");
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateImage(List<Integer> data) {
        Log.d("Main Thread","Got a frame");
        int[] imgdata = new int[data.size()/3];
        for (int i = 0 ; i < data.size()/3; i += 3) {
            imgdata[i] = (int)((data.get(i) << 16) + (data.get(i+1) << 8) + data.get(i+2));
            if (i%100000 == 0) {
                Log.d("loading data",""+imgdata[i]);
            }
        }
        Log.d("imgdata length",imgdata.length+"");
        Bitmap b = Bitmap.createBitmap(imgdata, 0, 640, 640, 480, Bitmap.Config.RGB_565);
        img.setImageBitmap(b);
        Log.d("Main Thread","Finished with frame");
    }

    private class ConnectTask extends AsyncTask<String, List<Integer>, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                Log.d("socket","Attempting to connect to: " + params[0]);
                Socket s = new Socket(params[0], 8080);
                InputStream is = s.getInputStream();
                List<Integer> buf = new ArrayList<Integer>();
                Log.d("socket","Starting to load frame");
                for (int i = 0; i < 640*480*3; i++) {
                    int r = is.read();
                    if (i%100000==0) Log.d("dl data",""+r);
                    if (r < 0) {
                        buf.add(0);
                    } else {
                        buf.add(r);
                    }
                }
                publishProgress(buf);
                s.close();
                Log.d("socket","Socket closed");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(List<Integer>... progress) {
            Log.d("progress",progress[0].size() + "");
            updateImage(progress[0]);
        }
    }

}
