package com.example.ammar.backupcontacts_sms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

class BackgroundWork extends AsyncTask<String, Void, ArrayList<?> >{

	Firebase myFirebaseRef;
	HashMap<String, String> hm = new HashMap<String, String>();
	static ArrayList<Contacts_NameEmailNumber> check = new ArrayList<Contacts_NameEmailNumber>();
	private FirebaseAuth firebaseAuth;
	private StorageReference mStorageRef;
	private DatabaseReference databaseReference;
	Contacts contacts;


	public interface TaskListener {

		 public void onFinished(ArrayList<?> result);
	}


private TaskListener taskListener;

public BackgroundWork(TaskListener listener){
	this.taskListener = listener;
}


	protected void onPostExecute(ArrayList<Contacts_NameEmailNumber> result) {
	//	super.onPostExecute(result);
		Log.d("onpost", "Connecting to ");
		if (result.size() > 1) {
			Log.d("OnPostCheck", result.size() + "");
		}
		else{
			Log.d("OnPostCheck", "no Value");
		}
		this.taskListener.onFinished(result);


	}

	/*@Override
	protected void onPostExecute(ArrayList<?> result) {
		super.onPostExecute(result);
		Log.d("onpost", "Connecting to ");
		Log.d("", "Connecting to ");
		this.taskListener.onFinished(result);

	}*/



	@Override
	protected ArrayList<Contacts_NameEmailNumber> doInBackground(String... params) {
		// TODO Auto-generated method stub
		
		Log.e("background", "Connecting to ");
		
		String translated = null;
	    try {

			Log.e("try", "Connecting to ");

		//	myFirebaseRef = new Firebase("https://backup-contacts-sms.firebaseio.com/aacomasfg");

			//////////////change////////
			firebaseAuth = FirebaseAuth.getInstance();


			//if the user is not logged in
			//that means current user will return null
			if (firebaseAuth.getCurrentUser() == null) {
				//closing this activity
				//finish();
				//starting login activity
				// startActivity(new Intent(this, LoginActivity.class));
			}
			//  Firebase.setAndroidContext(this);
			//getting the database reference
			databaseReference = FirebaseDatabase.getInstance().getReference();
			final FirebaseUser user = firebaseAuth.getCurrentUser();
			mStorageRef = FirebaseStorage.getInstance().getReference();
			///////////////change///////

			databaseReference.child(user.getUid()).addValueEventListener(new com.google.firebase.database.ValueEventListener() {
				@Override
				public void onDataChange(com.google.firebase.database.DataSnapshot snapshot) {

					/////////change///////////
					for (com.google.firebase.database.DataSnapshot noteDataSnapshot : snapshot.getChildren()) {
						Contacts_NameEmailNumber note = noteDataSnapshot.getValue(Contacts_NameEmailNumber.class);
						check.add(note);
					}

					/////////change//////////

					/////comment krdia////
					/*hm = (HashMap<String, String>) snapshot.getValue();
					Log.d("HM", hm + ": ");*/
					/////comment krdia////


                /*for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    Contacts_NameEmailNumber post = postSnapshot.getValue(Contacts_NameEmailNumber.class);
                    System.out.println(post.getName() + " - " + post.getNumber());
                }*/

					/////comment krdia////
					/*Set set = hm.entrySet();
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
						Log.d("KEY 2", me2.getKey() + ": ");
						Log.d("Value 2", me2.getValue() + "");


					}*/

					/////comment krdia////


			/*		Log.d("Check", check.get(1).getName());
					Log.d("Check", check.get(1).getNumber());*/

				}


				@Override
				public void onCancelled(DatabaseError error) {

					Log.d("Error", "Error Firebase");
				}
			});

/*			mStorageRef.child(user.getUid()).getFile(contacts.downloadUrl)
					.addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
						@Override
						public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
							// Successfully downloaded data to local file
							// ...
							Log.e("File Rcv", "File Rcv");
						}
					}).addOnFailureListener(new OnFailureListener() {
				@Override
				public void onFailure(@NonNull Exception exception) {
					// Handle failed download
					// ...
				}
			});*/

	//		onPostExecute(check);
		} catch (Exception e) {
			Log.e("Errorrrrr", e.toString());
		}
		Log.e("return", "return");



	//	onPostExecute(check);

	  //  return (ArrayList<Contacts_NameEmailNumber>) check;
		return  check;

		
	}
	
	}
