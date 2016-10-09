package fr.cleger.noxonremote;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private SharedPreferences preferences;
    private OutputStream out = null;
    private Socket socket = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    public static final byte[] intToByteArray(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
    }

    private String sendNoxonCommand(Integer command) throws IOException {
        String host = preferences.getString(SettingsActivity.KEY_PREF_HOST, "");
        if (socket == null) {
            socket = new Socket(host, NoxonComConstants.NOXON_WAA_PORT);
            out = socket.getOutputStream();
        }
        try {
            out.write(intToByteArray(command));
        } catch (IOException e) {
            out.close();
            out = null;
            socket.close();
            socket = null;
        }
        return "Send command "+ command +" ok";

    }

    private class sendNoxonCommandTask extends AsyncTask<Integer, Void, String> {
        @Override
        protected String doInBackground(Integer... cmd) {
            try {
                return sendNoxonCommand(cmd[0]);
            } catch (IOException e) {
                e.printStackTrace();
                return "Failed to send command";
            }
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            TextView text = (TextView) findViewById(R.id.radio_connected);
            text.setText(result);
        }
    }

    public boolean checkNetwork() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Failed to connect to server")
                    .setTitle("Network error");
            builder.create().show();
            return false;
        }

        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (!checkNetwork())
            return true;

        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)){
            new sendNoxonCommandTask().execute(NoxonComConstants.KEY_VOL_DOWN);
            return true;
        } else if ((keyCode == KeyEvent.KEYCODE_VOLUME_UP)){
            new sendNoxonCommandTask().execute(NoxonComConstants.KEY_VOL_UP);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    /** Called when the user touches the button */
    public void OnClickNoxonHandler(View view) {
        Integer cmd;
        if (!checkNetwork())
            return;

        switch (view.getId()) {
            case R.id.b_home:
                cmd = NoxonComConstants.KEY_HOME;
                break;
            case R.id.b_fav:
                cmd = NoxonComConstants.KEY_FAVORITES;
                break;
            case R.id.b_settings:
                cmd = NoxonComConstants.KEY_SETTINGS;
                break;
            case R.id.b_vol_down:
                cmd = NoxonComConstants.KEY_VOL_DOWN;
                break;
            case R.id.b_vol_up:
                cmd = NoxonComConstants.KEY_VOL_UP;
                break;
            case R.id.b_shuffle:
                cmd = NoxonComConstants.KEY_SHUFFLE;
                break;
            case R.id.b_repeat:
                cmd = NoxonComConstants.KEY_REPEAT;
                break;
            case R.id.b_previous:
                cmd = NoxonComConstants.KEY_PREVIOUS;
                break;
            case R.id.b_next:
                cmd = NoxonComConstants.KEY_NEXT;
                break;
            case R.id.b_up:
                cmd = NoxonComConstants.KEY_UP;
                break;
            case R.id.b_down:
                cmd = NoxonComConstants.KEY_DOWN;
                break;
            case R.id.b_right:
                cmd = NoxonComConstants.KEY_RIGHT;
                break;
            case R.id.b_left:
                cmd = NoxonComConstants.KEY_LEFT;
                break;
            case R.id.b_stop:
                cmd = NoxonComConstants.KEY_STOP;
                break;
            case R.id.b_play:
                cmd = NoxonComConstants.KEY_PLAY;
                break;
            case R.id.b_info:
                cmd = NoxonComConstants.KEY_INFO;
                break;
            default:
                return;
        }

        new sendNoxonCommandTask().execute(cmd);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            Intent myIntent = new Intent(this, SettingsActivity.class);
            startActivity(myIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
