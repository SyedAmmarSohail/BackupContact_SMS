package com.example.ammar.backupcontacts_sms.auth;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ammar.backupcontacts_sms.Contacts;
import com.example.ammar.backupcontacts_sms.R;
import com.example.ammar.backupcontacts_sms.ViewContacts;
import com.example.ammar.backupcontacts_sms.auth.GetStartActivity;
import com.example.ammar.backupcontacts_sms.auth.SignedInActivity;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.firebase.ui.auth.ui.ExtraConstants.EXTRA_IDP_RESPONSE;

/**
 * Created by ammar on 3/9/16.
 */
public class Selection extends AppCompatActivity implements View.OnClickListener{

    LinearLayout sync, restore, view, delete ;
    @BindView(android.R.id.content)
    View mRootView;

    @BindView(R.id.user_email)
    TextView mUserEmail;

    @BindView(R.id.user_display_name)
    TextView mUserDisplayName;

    @BindView(R.id.user_enabled_providers)
    TextView mEnabledProviders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            startActivity(GetStartActivity.createIntent(this));
            finish();
            return;
        }

        setContentView(R.layout.selection);

        initialize();
        ButterKnife.bind(this);
        populateProfile();

        sync.setOnClickListener(this);
        view.setOnClickListener(this);
    }

    public void initialize(){
        sync = (LinearLayout) findViewById(R.id.syncContacts);
        restore = (LinearLayout) findViewById(R.id.restore);
        view = (LinearLayout) findViewById(R.id.view);
        delete = (LinearLayout) findViewById(R.id.delete);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.syncContacts:
                Toast.makeText(Selection.this, "Yes", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Selection.this, Contacts.class));

                break;

            case R.id.view:
                Toast.makeText(Selection.this, "Yes", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Selection.this, ViewContacts.class));

                break;
        }
    }

    @MainThread
    private void populateProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        mUserEmail.setText(
                TextUtils.isEmpty(user.getEmail()) ? "No email" : user.getEmail());
        mUserDisplayName.setText(
                TextUtils.isEmpty(user.getDisplayName()) ? "No display name" : user.getDisplayName());

        StringBuilder providerList = new StringBuilder();

        providerList.append("Providers used: ");

        if (user.getProviders() == null || user.getProviders().isEmpty()) {
            providerList.append("none");
        } else {
            Iterator<String> providerIter = user.getProviders().iterator();
            while (providerIter.hasNext()) {
                String provider = providerIter.next();
                if (GoogleAuthProvider.PROVIDER_ID.equals(provider)) {
                    providerList.append("Google");
                } else if (FacebookAuthProvider.PROVIDER_ID.equals(provider)) {
                    providerList.append("Facebook");
                } else if (EmailAuthProvider.PROVIDER_ID.equals(provider)) {
                    providerList.append("Password");
                } else {
                    providerList.append(provider);
                }

                if (providerIter.hasNext()) {
                    providerList.append(", ");
                }
            }
        }

        mEnabledProviders.setText(providerList);
    }
    private void deleteAccount() {
        AuthUI.getInstance()
                .delete(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            startActivity(GetStartActivity.createIntent(Selection.this));
                            finish();
                        } else {
                            showSnackbar(R.string.delete_account_failed);
                        }
                    }
                });
    }

    @MainThread
    private void showSnackbar(@StringRes int errorMessageRes) {
        Snackbar.make(mRootView, errorMessageRes, Snackbar.LENGTH_LONG)
                .show();
    }

    public static Intent createIntent(Context context, IdpResponse idpResponse) {
        Intent in = new Intent();
        in.setClass(context, Selection.class);
        in.putExtra(EXTRA_IDP_RESPONSE, idpResponse);
        return in;
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
        if (id == R.id.action_logout) {
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                startActivity(GetStartActivity.createIntent(Selection.this));
                                finish();
                            } else {
                                showSnackbar(R.string.sign_out_failed);
                            }
                        }
                    });
            return true;
        }
        if (id == R.id.action_remove) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to delete this account?")
                    .setPositiveButton("Yes, nuke it!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deleteAccount();
                        }
                    })
                    .setNegativeButton("No", null)
                    .create();

            dialog.show();
            return true;

        }

        return super.onOptionsItemSelected(item);
    }


}
