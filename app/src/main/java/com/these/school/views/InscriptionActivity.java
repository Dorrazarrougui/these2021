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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.these.school.ApplicationData;
import com.these.school.R;
import com.these.school.models.User;
import com.these.school.utils.InputValidations;

public class InscriptionActivity extends AppCompatActivity {

    private TextInputLayout tilnom, tilemail, tilpassword, tiltel1, tiltel2;
    private Spinner spinnerType;
    private Button btnSignIn, btnLogin;
    private ProgressBar progress;
    ImageView logoInscription;
    private LinearLayout llMain;

    private long lastClickTime = 0;
    private FirebaseAuth mAuth;
    FirebaseFirestore dbRef;
    private String TAG = "INSCRIPTION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setSharedElementEnterTransition(new ChangeBounds().setDuration(500));
            //getWindow().setSharedElementReturnTransition(new ChangeBounds().setDuration(3000));
        }
        setContentView(R.layout.activity_inscription);

        mAuth = FirebaseAuth.getInstance();

        llMain = findViewById(R.id.llMain);
        tilnom = findViewById(R.id.tilnom);
        tilemail = findViewById(R.id.tilemail);
        tilpassword = findViewById(R.id.tilpassword);
        tiltel1 = findViewById(R.id.tiltel1);
        tiltel2 = findViewById(R.id.tiltel2);
        spinnerType = findViewById(R.id.spinnerType);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnLogin = findViewById(R.id.btnLogin);
        logoInscription = findViewById(R.id.logoInscription);
        progress = findViewById(R.id.progress);

        dbRef = FirebaseFirestore.getInstance();

        InputValidations.setEditMaxLength(tilnom.getEditText(), 50);
        InputValidations.setEditMaxLength(tilemail.getEditText(), 70);
        InputValidations.setEditMaxLength(tilpassword.getEditText(), 30);
        InputValidations.setEditMaxLength(tiltel1.getEditText(), 15);
        InputValidations.setEditMaxLength(tiltel2.getEditText(), 15);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignIn();
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.types, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLogin();
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
                    register();
                }else{
                    btnSignIn.setEnabled(true);
                    Toast.makeText(this, R.string.verifyData, Toast.LENGTH_LONG).show();
                }
            }else{
                btnSignIn.setEnabled(true);
                Toast.makeText(this, R.string.noNet, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void register() {
        llMain.setAlpha(0.3f);
        progress.setVisibility(View.VISIBLE);
        String type = spinnerType.getSelectedItem().toString();
        final User c = new User();
        c.setName(tilnom.getEditText().getText().toString().trim());
        c.setType(type);
        c.setPhone1(tiltel1.getEditText().getText().toString().trim());
        c.setPhone2(tiltel2.getEditText().getText().toString().trim());
        c.setEmail(tilemail.getEditText().getText().toString().trim());
        c.setImage("default_image");
        String password = tilpassword.getEditText().getText().toString();

        mAuth.createUserWithEmailAndPassword(c.getEmail(), password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            final String uid = user.getUid();
                            c.setId(uid);

                            DocumentReference doc = dbRef.collection("users").document(uid);
                            doc.set(c).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        ApplicationData.uid = uid;
                                        if(type.equals("Enseignant")){
                                            Intent intent = new Intent(InscriptionActivity.this, HomeActivityEns.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();
                                        }else{
                                            Intent i = new Intent(InscriptionActivity.this, HomeActivityPar.class);
                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(i);
                                            finish();
                                        }

                                    }else{
                                        Log.d(TAG, "DATABASE:failure", task.getException());
                                        Toast.makeText(InscriptionActivity.this, "Création utilisateur échouée", Toast.LENGTH_LONG).show();
                                        progress.setVisibility(View.VISIBLE);
                                        llMain.setAlpha(1);
                                        btnSignIn.setEnabled(true);
                                        enableAll();
                                    }
                                }
                            });

                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            progress.setVisibility(View.VISIBLE);
                            if(task.getException() instanceof FirebaseAuthUserCollisionException)
                                Toast.makeText(InscriptionActivity.this, "Email déja utilisé", Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(InscriptionActivity.this, "Inscription échouée", Toast.LENGTH_LONG).show();
                            llMain.setAlpha(1);
                            btnSignIn.setEnabled(true);
                            enableAll();
                        }

                    }
                });
    }

    private void goToLogin() {
        Intent intent = new Intent(InscriptionActivity.this, LoginActivity.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Pair[] pairs = new Pair[5];
            pairs[0] = new Pair<View, String>(logoInscription, "logo_trans");
            pairs[1] = new Pair<View, String>(tilemail, "til1_trans");
            pairs[2] = new Pair<View, String>(tilpassword, "til2_trans");
            pairs[3] = new Pair<View, String>(btnSignIn, "btn1_trans");
            pairs[4] = new Pair<View, String>(btnLogin, "btn2_trans");
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(InscriptionActivity.this, pairs);
            startActivity(intent, options.toBundle());
        }else{
            startActivity(intent);
        }
    }

    protected boolean checkNetwork() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected())return true;
        else return false;
    }

    private boolean checkInputs() {
        boolean name1 = InputValidations.hasText(tilnom.getEditText());
        boolean phone1 = InputValidations.hasText(tiltel1.getEditText());
        boolean email1 = InputValidations.hasText(tilemail.getEditText());
        boolean password1 = InputValidations.hasText(tilpassword.getEditText());
        boolean password2 = InputValidations.trueLength(tilpassword.getEditText(), 6);
        boolean name2 = InputValidations.isName(tilnom.getEditText(), true);
        boolean phone2 = InputValidations.isPhoneNumber(tiltel1.getEditText(), true);
        boolean phone3 = InputValidations.isPhoneNumber(tiltel2.getEditText(), false);
        boolean email2 = InputValidations.isEmail(tilemail.getEditText(), false);
        boolean type = (spinnerType.getCount()>0)?((spinnerType.getSelectedItemPosition()>=0)?true:false):false;

        return name1&&phone1&&name2&&phone2&&phone3&&email1&&email2&&password1&&password2&&type;
    }

    private void disableAll(){
        tilnom.setEnabled(false);
        tiltel1.setEnabled(false);
        tiltel2.setEnabled(false);
        tilemail.setEnabled(false);
        tilpassword.setEnabled(false);
        spinnerType.setEnabled(false);
        btnLogin.setEnabled(false);
        btnSignIn.setEnabled(false);
    }
    private void enableAll(){
        tilnom.setEnabled(true);
        tiltel1.setEnabled(true);
        tiltel2.setEnabled(true);
        tilemail.setEnabled(true);
        tilpassword.setEnabled(true);
        spinnerType.setEnabled(true);
        btnLogin.setEnabled(true);
        btnSignIn.setEnabled(true);
    }
}