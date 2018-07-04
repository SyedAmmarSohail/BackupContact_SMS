package com.example.ammar.backupcontacts_sms;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by ammar on 4/24/16.
 */
public class ViewContacts extends AppCompatActivity implements View.OnClickListener{

    Firebase myFirebaseRef;
    TextView textfire;
    static ListView viewList;
    HashMap<String, String> hm = new HashMap<String, String>();
    static List<Contacts_NameEmailNumber> check;
    Context context;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    Button restore, delete;
    FirebaseUser user;

   // static List<Contacts_NameEmailNumber> check = new ArrayList<Contacts_NameEmailNumber>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_contacts);
        ////////////change////////
        firebaseAuth = FirebaseAuth.getInstance();


        //if the user is not logged in
        //that means current user will return null
        if (firebaseAuth.getCurrentUser() == null) {
            //closing this activity
            finish();
            //starting login activity
            startActivity(new Intent(this, LoginActivity.class));
        }
        //  Firebase.setAndroidContext(this);
        //getting the database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();
        user = firebaseAuth.getCurrentUser();
        ////////////change///////

        Firebase.setAndroidContext(this);
        textfire = (TextView) findViewById(R.id.ViewListText);
        viewList = (ListView) findViewById(R.id.viewList);
        restore = (Button) findViewById(R.id.restore);
        delete = (Button) findViewById(R.id.delete);
        restore.setOnClickListener(this);
        delete.setOnClickListener(this);

        /////////////////////////////

        String url = null;
        try {


    //        url = "https://backup-contacts-sms.firebaseio.com/aacomasfg";
            url = databaseReference.child(user.getUid()).toString();

            Log.e("URLLanguage", url.toString() + "");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Toast.makeText(this, "Error in URL", Toast.LENGTH_SHORT)
                    .show();
        }

        if (isOnline() == true) {

//            convert.setVisibility(View.INVISIBLE);
//            progressText.setVisibility(View.VISIBLE);

            BackgroundWork.TaskListener listener = new BackgroundWork.TaskListener() {

                @Override
                public void onFinished(ArrayList<?> result) {

//                    convert.setVisibility(View.VISIBLE);
//                    progressText.setVisibility(View.INVISIBLE);

                    ArrayList<Contacts_NameEmailNumber> translated = (ArrayList<Contacts_NameEmailNumber>) result;

							/* String translated = translate(text); */
                    Log.e("agia 3", "yes");

                    Log.d("CheckInShowList", "ss");

                    Log.d("Check", result.size() + "");

                //    Log.d("Check", translated.get(1).getName());

                    showInList(translated);

                 //   viewList.setAdapter(new custom_phoneList(context, translated));

                    Log.d("CheckInShowList", "aa");
/*                    if (translated != null) {
                        Log.e("agia 4", "yes");

                    }*/
                }
            };
            BackgroundWork back = new BackgroundWork(listener);
            Log.d("back start", "started");
            back.execute(url);
        } else {
            Toast.makeText(this, "Please Connect to the Inetrnet",
                    Toast.LENGTH_SHORT).show();
        }

        //////////////////////////////

      //  ReadDataFromFirbase();

    }


    /*public void ReadDataFromFirbase(){
        myFirebaseRef = new Firebase("https://backup-contacts-sms.firebaseio.com/aacomasfg");

         myFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot snapshot) {
                 hm = (HashMap<String, String>) snapshot.getValue();
                 Log.d("HM", hm + ": ");



                *//*for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    Contacts_NameEmailNumber post = postSnapshot.getValue(Contacts_NameEmailNumber.class);
                    System.out.println(post.getName() + " - " + post.getNumber());
                }*//*

                 Set set = hm.entrySet();
                 // Get an iterator
                 Iterator i = set.iterator();
                 // Display elements
                 while (i.hasNext()) {
                     Map.Entry me = (Map.Entry) i.next();
                     *//*Log.d("KEY",  me.getKey() + ": ");
                     Log.d("Value", me.getValue() + "");*//*
                 }

                 Map<String, String> map = new TreeMap<String, String>(hm);
                 System.out.println("After Sorting:");
                 Set set2 = map.entrySet();
                 Iterator iterator2 = set2.iterator();
                 while (iterator2.hasNext()) {
                     Map.Entry me2 = (Map.Entry) iterator2.next();
                     *//*System.out.print(me2.getKey() + ": ");
                     System.out.println(me2.getValue());*//*

                     check.add(new Contacts_NameEmailNumber(me2.getKey() + "", "", me2.getValue() + ""));
                     Log.d("KEY", me2.getKey() + ": ");
                     Log.d("Value", me2.getValue() + "");

                 }


                 Log.d("Check", check.get(1).getName());
                 Log.d("Check", check.get(1).getNumber());

             }


             @Override
             public void onCancelled(FirebaseError error) {
             }
         });


        // showInList();

    }*/


    public void showInList(ArrayList<Contacts_NameEmailNumber> check){

        Log.d("CheckInShowList", "ss");
//        Log.d("Check", check.get(1).getName());

        viewList.setAdapter(new custom_phoneList(ViewContacts.this, check));

        Log.d("CheckInShowList", "aa");

    }



    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.restore:

                break;
            case R.id.delete:
                databaseReference.child(user.getUid()).removeValue();
                break;
        }
    }
}
