package com.example.ammar.backupcontacts_sms;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.apache.commons.lang.WordUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Contacts extends AppCompatActivity {
  //  public TextView outputText;
    ListView phoneList;
    Button upload;
    static List<Contacts_NameEmailNumber> list;
    String[] duplicateNumber = new String[10];
    int  i = 0, dLength;
    static Context mContext;
    Firebase myFirebaseRef;
    private FirebaseAuth firebaseAuth;
    private StorageReference mStorageRef;
    private DatabaseReference databaseReference;
    FirebaseActivity fb;
    SharedPreferences sharedPreferences;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    public Uri downloadUrl;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts);
        //initializing firebase authentication object
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
       /* if (!FirebaseApp.getApps(this).isEmpty()) {

        }*/
        mStorageRef = FirebaseStorage.getInstance().getReference();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        list = new ArrayList<Contacts_NameEmailNumber>();
        upload = (Button) findViewById(R.id.Upload);
        setSupportActionBar(toolbar);
        mContext = Contacts.this;
    //    outputText = (TextView) findViewById(R.id.textView1);
        phoneList = (ListView) findViewById(R.id.list);
        fetchContacts();

        getVCF();


        phoneList.setAdapter(new custom_phoneList(this, list));

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sharedPreferences = getApplication().getSharedPreferences("Options",
                        MODE_PRIVATE);
                String email = sharedPreferences.getString("email", "No Name Define");
                String password = sharedPreferences.getString("password", "No Name Define");
               /* UserName.setText(name);*/

                email = email.replaceAll("[.@_]", "");
                email = email + password;

                FirebaseUser user = firebaseAuth.getCurrentUser();

                Uri fileContacts = Uri.fromFile(new File("storage/sdcard0/Backup_Contacts/Contacts.vcf"));

                StorageReference vcfFileUserID = mStorageRef.child(user.getUid());
                StorageReference vcfFileRfrnc = vcfFileUserID.child("Contacts.vcf");

                UploadTask uploadTask = vcfFileRfrnc.putFile(fileContacts);

                // Register observers to listen for when the download is done or if it fails
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Toast.makeText(getApplicationContext(), "Failed to upload", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                         downloadUrl = taskSnapshot.getDownloadUrl();
                    }
                });

                /////////////newFirebase////////////
                /*mFirebaseInstance = FirebaseDatabase.getInstance();

                // get reference to 'users' node
                mFirebaseDatabase = mFirebaseInstance.getReference("Contacts");
                //  Storing in a loop
                for (Contacts_NameEmailNumber up_NameEmailNumber : list) {
                    mFirebaseDatabase.child("First").child(up_NameEmailNumber.getName()).setValue(up_NameEmailNumber.getNumber());
                }*/

                /////////////////change///////////////
            //    FirebaseUser user = firebaseAuth.getCurrentUser();


                databaseReference.child(user.getUid()).setValue(list);
                /////////////////change///////////////
                //////////////newFirebase////////

                /* myFirebaseRef = new Firebase("https://backup-contacts-sms.firebaseio.com/"+email );
                for (Contacts_NameEmailNumber up_NameEmailNumber : list) {
                    myFirebaseRef.child("Contacts").child(up_NameEmailNumber.getName()).setValue(up_NameEmailNumber.getNumber());
                }
                myFirebaseRef.child("vcfFile").child("File").setValue("File Name");*/
            }
        });




    }

    /////////////////////VCF/////////////////////

    public static void getVCF() {
        final String vfile = "Contacts.vcf";
        String Folder_name = "Backup_Contacts";

        Cursor phones =  mContext.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                null, null, null);
        phones.moveToFirst();
    /*    for (int i = 0; i < phones.getCount(); i++) {*/
            String lookupKey = phones.getString(phones
                    .getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
            Uri uri = Uri.withAppendedPath(
                    ContactsContract.Contacts.CONTENT_VCARD_URI,
                    lookupKey);
            AssetFileDescriptor fd;
            try {
                fd = mContext.getContentResolver().openAssetFileDescriptor(uri, "r");
                FileInputStream fis = fd.createInputStream();
                byte[] buf = new byte[(int) fd.getDeclaredLength()];
                fis.read(buf);
                String VCard = new String(buf);
                File f = new File(Environment.getExternalStorageDirectory(), Folder_name);
                if (!f.exists()) {
                    f.mkdirs();
                }
              /*  String path = Environment.getExternalStorageDirectory()
                        .toString() + File.separator + Folder_name + File.separator + vfile;*/
                String path = f + File.separator + vfile;
                FileOutputStream mFileOutputStream = new FileOutputStream(path,
                        true);
                mFileOutputStream.write(VCard.toString().getBytes());
                phones.moveToNext();
                Log.d("Vcard", VCard);
                Toast.makeText(mContext , "VCard Created.", Toast.LENGTH_SHORT).show();

            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
       /* }*/
    }
    /////////////////////VCF/////////////////////

    public void fetchContacts() {

        String phoneNumber = null;
        String email = null;
        String name = null;

        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

        Uri EmailCONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
        String DATA = ContactsContract.CommonDataKinds.Email.DATA;


        StringBuffer output = new StringBuffer();

        ContentResolver contentResolver = getContentResolver();

        Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null, null);

        // Loop for every contact in the phone
        if (cursor.getCount() > 0) {

            while (cursor.moveToNext()) {

                String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
                name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));

                if(name == null){
                    name = "Unknown";
                }

               name = WordUtils.capitalize(name);


                name = name.replaceAll("[./]", "");

                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));

                if (hasPhoneNumber > 0) {

                    output.append("\n" + name);

                    // Query and loop for every phone number of the contact
                    Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[]{contact_id}, null);

            //        while (phoneCursor.moveToNext()) {


                    if (phoneCursor.moveToNext()){
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                   //     output.append("\n " + phoneNumber);

                        phoneNumber = phoneNumber.replaceAll("[- ]","");

          //              i = i;

                  //      list.add(i, new Contacts_NameEmailNumber(name, "", phoneNumber));

//                        duplicateNumber[i++] = phoneNumber;

                        dLength = duplicateNumber.length;

           //             Arrays.sort(duplicateNumber);

//                        i++;

 //                       output.append("\n Phone number:" + phoneNumber);
                        /*myFirebaseRef = new Firebase("https://backup-contacts-sms.firebaseio.com/ammar");
                        myFirebaseRef.child(name).setValue(phoneNumber);*/

                    }

                    i++;
                    /*output.append("\n Phone number:" + duplicateNumber[0]);

                    for (int j = 1 ; j < duplicateNumber.length ; j++){

                        if (!(duplicateNumber[0].matches(duplicateNumber[j])) && duplicateNumber[j] != null){
                            output.append("\n Phone number:" + duplicateNumber[j]);
                        }

                    }*/


                    phoneCursor.close();

                    // Query and loop for every email of the contact
                    Cursor emailCursor = contentResolver.query(EmailCONTENT_URI, null, EmailCONTACT_ID + " = ?", new String[]{contact_id}, null);

                    while (emailCursor.moveToNext()) {

                        email = emailCursor.getString(emailCursor.getColumnIndex(DATA));

                        output.append("\nEmail:" + email);


                    }

                    emailCursor.close();
                }

                output.append("\n");

                list.add(new Contacts_NameEmailNumber(name, email, phoneNumber));
            }

        //    outputText.setText(output);
          //  list.add(new Contacts_NameEmailNumber(name, email, phoneNumber));


             //   Collections.sort(list);

                /*Collections.sort(list, new Comparator<Contacts_NameEmailNumber>() {
                    @Override
                    public int compare(Contacts_NameEmailNumber lhs, Contacts_NameEmailNumber rhs) {

                        return lhs.getName().compareTo(rhs.getName());
                    }
                });*/
            int k =0;
            for (Contacts_NameEmailNumber check : list){
            //    outputText.setText(check.getName() + "\n" + check.getNumber());
             //   outputText.setText("\n");

                k++;
            }


            Log.d("InList", list.get(0).getName() + " " + list.get(0).getNumber());


        }
    }

}
