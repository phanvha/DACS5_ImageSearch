package com.utt.zipapp.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.utt.zipapp.HomeActivity;
import com.utt.zipapp.LoginActivity;
import com.utt.zipapp.R;

public class CheckGoogleAccountStatus {
    public static boolean  getcheckDataAccount(Context context){
        GoogleSignInAccount gg = GoogleSignIn.getLastSignedInAccount(context);
        if (gg!=null){
            return true;
        }else{
            return false;
        }
    }
}
