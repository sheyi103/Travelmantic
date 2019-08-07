package com.sasoftsolutions.travelmantics;

import android.app.Activity;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirebaseUtil {
    public static FirebaseDatabase mFirebaseDatabase;
    public static DatabaseReference mDatabaseReference;
    public static FirebaseAuth mFirebaseAuth;
    public static FirebaseStorage mStorage;
    public static StorageReference mStorageRef;
    public static FirebaseAuth.AuthStateListener mAuthListener;
    private static final int RC_SIGN_IN = 123;
    private static FirebaseUtil firebaseUtil;
    public static ArrayList<TravelDeal> mDeals;
    private static Activity caller;

    private FirebaseUtil(){}

    public static void openFbReference(String ref, final Activity callerActivity){
        if(firebaseUtil == null) {
            firebaseUtil = new FirebaseUtil();
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mFirebaseAuth = FirebaseAuth.getInstance();
            caller = callerActivity;
            mAuthListener= new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if(firebaseAuth.getCurrentUser()== null) {
                        FirebaseUtil.signIn();
                    }
                    Toast.makeText(callerActivity.getBaseContext(), "Welcomeback!", Toast.LENGTH_LONG).show();
                }
            };
            connectStorage();

        }

        mDeals = new ArrayList<TravelDeal>();
        mDatabaseReference = mFirebaseDatabase.getReference().child(ref);
    }

    private static void signIn(){

        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

// Create and launch sign-in intent
        caller.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),RC_SIGN_IN);
    }

    public static  void attachListener(){
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }
    public static void detachListener(){
        mFirebaseAuth.removeAuthStateListener(mAuthListener);
    }

    public static void connectStorage(){
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference().child("deals_pictures");
    }
}
