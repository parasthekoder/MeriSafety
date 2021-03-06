package com.developers.droidteam.merisafety;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;

import java.util.Set;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {


    static final String KEY_PREF_LOCATION = "privacy_location";
    static final String KEY_PREF_SHARE_LOCATION = "share_location";
    static final String KEY_HOT = "hot_key";
    static final String KEY_SEND_MSG = "send_msg";
    static final String KEY_DIAL_CALL = "dial_call";
    static final String KEY_SEND_EMAIL = "send_email";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        //Read preference by using below lines
//                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
//        String syncConnPref = sharedPref.getString(SettingsActivity.KEY_PREF_LOCATION, "");


        getFragmentManager().beginTransaction().replace(android.R.id.content,new MyPreferenceFragment()).commit();
        PreferenceManager.setDefaultValues(this, R.xml.preference, false);
    }

    public static class MyPreferenceFragment extends PreferenceFragment
    {

        private static Preference.OnPreferenceChangeListener listener;
        Context con;
        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            con=context;
        }

        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference);

            listener = new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    String stringValue = o.toString();

                    if (preference instanceof ListPreference) {
                        // For list preferences, look up the correct display value in
                        // the preference's 'entries' list.
                        ListPreference listPreference = (ListPreference) preference;
                        int index = listPreference.findIndexOfValue(stringValue);

                        // Set the summary to reflect the new value.
                        preference.setSummary(
                                index >= 0
                                        ? listPreference.getEntries()[index]
                                        : null);

                    } else if (preference instanceof RingtonePreference) {
                        // For ringtone preferences, look up the correct display value
                        // using RingtoneManager.
                        if (TextUtils.isEmpty(stringValue)) {
                            // Empty values correspond to 'silent' (no ringtone).
                            preference.setSummary(R.string.pref_ringtone_silent);

                        } else {
                            Ringtone ringtone = RingtoneManager.getRingtone(
                                    preference.getContext(), Uri.parse(stringValue));

                            if (ringtone == null) {
                                // Clear the summary if there was a lookup error.
                                preference.setSummary(null);
                            } else {
                                // Set the summary to reflect the new ringtone display
                                // name.
                                String name = ringtone.getTitle(preference.getContext());
                                preference.setSummary(name);
                            }
                        }

                    }else if(preference instanceof MultiSelectListPreference){

                        boolean name = false;
                        boolean mob = false;
                        String s = "";
                        if(stringValue.contains("0")){
                           s+="Name";
                           name = true;
                        }
                        if(stringValue.contains("1")){
                           if(name)
                           {
                               s+=", ";
                           }
                            s+="Mobile Number";
                           mob=true;
                        }
                        if(stringValue.contains("2")){
                            if(mob)
                            {
                                s+=", ";
                            }
                            s+="Email";
                        }
                        preference.setSummary(s);
                    }
                    else {
                        // For all other preferences, set the summary to the value's
                        // simple string representation.
                        preference.setSummary(stringValue);
                    }
                    return true;

                }
            };

        }

        @Override
        public void onResume() {
            super.onResume();
            bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
            bindPreferenceSummaryToValue(findPreference("nearby_users_sharing"));
            bindPreferenceSummaryToValue(findPreference("sync_frequency"));
        }

        private static void bindPreferenceSummaryToValue(Preference preference) {
            // Set the listener to watch for value changes.
            preference.setOnPreferenceChangeListener(listener);

            // Trigger the listener immediately with the preference's
            // current value.
            if(preference instanceof MultiSelectListPreference){

                MultiSelectListPreference multiSelectListPreference = (MultiSelectListPreference) preference;
                Set<String> s= multiSelectListPreference.getValues();
                listener.onPreferenceChange(preference,
                        PreferenceManager
                                .getDefaultSharedPreferences(preference.getContext())
                                .getStringSet(preference.getKey(),s ));
            }
            else {

                listener.onPreferenceChange(preference,
                        PreferenceManager
                                .getDefaultSharedPreferences(preference.getContext())
                                .getString(preference.getKey(),""));
            }
        }
    }
    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


}
