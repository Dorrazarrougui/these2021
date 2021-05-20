package com.these.school.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.transition.ChangeBounds;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.these.school.R;
import com.these.school.models.User;
import com.these.school.utils.InputValidations;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilemailLogin, tilpasswordLogin;
    private Button btnSignInLogin, btnInscriptionLogin;
    private ProgressBar progressLogin;
    private LinearLayout llMainLogin;
    private ImageView logoLogin;

    private long lastClickTime = 0;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setSharedElementEnterTransition(new ChangeBounds().setDuration(500));
            //getWindow().setSharedElementReturnTransition(new ChangeBounds().setDuration(3000));
        }
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        tilemailLogin = findViewById(R.id.tilemaillogin);
        tilpasswordLogin = findViewById(R.id.tilpasswordLogin);
        btnSignInLogin = findViewById(R.id.btnSignInLogin);
        btnInscriptionLogin = findViewById(R.id.btnInscriptionLogin);
        progressLogin = findViewById(R.id.progressLogin);
        llMainLogin = findViewById(R.id.llMainLogin);
        logoLogin = findViewById(R.id.logoLogin);
        llMainLogin = findViewById(R.id.llMainLogin);
        progressLogin = findViewById(R.id.progressLogin);

        btnSignInLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignIn();
            }
        });

        btnInscriptionLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToInscription();
            }
        });
    }

    private void startSignIn() {
        if (SystemClock.elapsedRealtime() - lastClickTime < 1000){
            lastClickTime = SystemClock.elapsedRealtime();
            return;
        }else {
            lastClickTime = SystemClock.elapsedRealtime();
            if(checkNetwork()) {
                if (checkInputs()) {
                    login();
                }else{
                    btnSignInLogin.setEnabled(true);
                    Toast.makeText(this, R.string.verifyData, Toast.LENGTH_LONG).show();
                }
            }else{
                btnSignInLogin.setEnabled(true);
                Toast.makeText(this, R.string.noNet, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void login(){
        llMainLogin.setAlpha(0.3f);
        progressLogin.setVisibility(View.VISIBLE);
        final User c = new User();
        c.setEmail(tilemailLogin.getEditText().getText().toString().trim());
        c.setPassword(tilpasswordLogin.getEditText().getText().toString());

        mAuth.signInWithEmailAndPassword(c.getEmail(), c.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    String uid = mAuth.getCurrentUser().getUid();

                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }else{
                    Log.d("LOGINN", task.getException().getMessage());
                    progressLogin.setVisibility(View.VISIBLE);
                    Toast.makeText(LoginActivity.this, "Authentification échouée ! réessayez. ", Toast.LENGTH_LONG).show();
                    llMainLogin.setAlpha(1);
                    btnSignInLogin.setEnabled(true);
                    progressLogin.setVisibility(View.GONE);
                }
            }
        });
    }

    protected boolean checkNetwork() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected())return true;
        else return false;
    }

    private boolean checkInputs() {
        boolean email1 = InputValidations.hasText(tilemailLogin.getEditText());
        boolean password1 = InputValidations.hasText(tilpasswordLogin.getEditText());
        boolean password2 = InputValidations.trueLength(tilpasswordLogin.getEditText(), 6);
        boolean email2 = InputValidations.isEmail(tilemailLogin.getEditText(), false);

        return email1&&email2&&password1&&password2;
    }

    private void goToInscription() {
        Intent intent = new Intent(LoginActivity.this, InscriptionActivity.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Pair[] pairs = new Pair[5];
            pairs[0] = new Pair<View, String>(logoLogin, "logo_trans");
            pairs[1] = new Pair<View, String>(tilemailLogin, "til1_trans");
            pairs[2] = new Pair<View, String>(tilpasswordLogin, "til2_trans");
            pairs[3] = new Pair<View, String>(btnSignInLogin, "btn1_trans");
            pairs[4] = new Pair<View, String>(btnInscriptionLogin, "btn2_trans");
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pairs);
            startActivity(intent, options.toBundle());
        }else{
            startActivity(intent);
        }
    }
}