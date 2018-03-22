package com.developers.droidteam.merisafety;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class HighAlertFragment extends Fragment {


    private DatabaseReference mDatabase;
    private DatabaseReference guarEnd ;

    private final String sp_db = "account_db";
    private final String l_key = "login_key";
    private final String d_key = "users";
    private final String n_key = "name";
    private final String m_key = "mobile";
    private final String e_key = "email";
    private final String cur_key="curloc";
    private final String cur_db = "currentloc";
    private final String gn_key="gname";
    private final String gm_key="gmobile";
    private final String ge_key="gemail";
    View v;
    Context con;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        con=context;
    }

    public HighAlertFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_high_alert, container, false);
        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


       /* Button b = (Button) v.findViewById(R.id.save_me_map_security);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(con,MapsActivity.class));
            }
        });*/

        ConnectivityManager cm = (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo activeNetwork =  cm.getActiveNetworkInfo();
        final boolean isConnected = activeNetwork!=null&&activeNetwork.isConnectedOrConnecting();


        SharedPreferences sp = con.getSharedPreferences(sp_db, Context.MODE_PRIVATE);
        final String user = sp.getString(l_key, null);
        final String gname = sp.getString(gn_key,null);
        final String gmobile = sp.getString(gm_key,null);


        if(isConnected){

            sendAlert(gname,gmobile);

        }else{

            mDatabase = FirebaseDatabase.getInstance().getReference();

            guarEnd = mDatabase.child(d_key).child(user).child(user);

            guarEnd.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    sendAlert(dataSnapshot.child(n_key).getValue().toString(),dataSnapshot.child(m_key).getValue().toString());

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }



    }

    public void sendAlert(String name, final String mobile){


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(con);
        boolean msg = sharedPref.getBoolean(SettingsActivity.KEY_SEND_MSG,true);
        boolean call = sharedPref.getBoolean(SettingsActivity.KEY_DIAL_CALL,true);
        boolean em = sharedPref.getBoolean(SettingsActivity.KEY_SEND_EMAIL,true);


        SharedPreferences sp = con.getSharedPreferences("account_db", Context.MODE_PRIVATE);
        final PendingIntent pi = PendingIntent.getBroadcast(getActivity(), 0, new Intent("in.wptrafficanalyzer.sent"), 0);
        final PendingIntent pin = PendingIntent.getBroadcast(getActivity(), 0, new Intent("in.wptrafficanalyzer.delivered"), 0);
        final SmsManager smss = SmsManager.getDefault();
        sp = con.getSharedPreferences(cur_db, Context.MODE_PRIVATE);
        String loc = sp.getString(cur_key,null);
        final String sms = name+" needs you!, i'm in emergency. My Location is "+loc+". You received this alert because you are the guardian";


        if(call){

            new Thread(){

                @Override
                public void run() {
                    super.run();

                    try {
                        Thread.sleep(100);

                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" +mobile));
                        startActivity(intent);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
       if(msg){

           smss.sendTextMessage(mobile, null, sms, pi, pin);

       }

    }
}
