package fr.cleger.noxonremote;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

/**
 * Created by vangelis on 09/10/16.
 */

public class SettingsActivity extends PreferenceActivity {
    public static final String KEY_PREF_HOST = "prefRadioHost";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
    }

}
