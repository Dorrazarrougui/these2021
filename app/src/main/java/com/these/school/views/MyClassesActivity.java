package com.these.school.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.these.school.R;
import com.these.school.adapters.RecyclerAdapter;
import com.these.school.models.Classe;

import java.util.ArrayList;
import java.util.List;

public class MyClassesActivity extends AppCompatActivity implements AddClasseDialog.OnAddClasseListener {

    private SwipeRefreshLayout srl;
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    private FloatingActionButton addClasse;
    private List<Classe> myClasses;
    private FirebaseFirestore dbRef;
    private FirebaseUser currentUser;
    private String uid;
    private RecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_classes);

        srl = findViewById(R.id.srl);
        recyclerView = findViewById(R.id.recyclerview);
        addClasse = findViewById(R.id.addClasse);

        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fillClasses();
                srl.setRefreshing(false);
            }
        });

        myClasses = new ArrayList<>();

        progressDialog = new ProgressDialog(this);

        addClasse.setOnClickListener(v -> {
            FragmentManager fragmentManager = getSupportFragmentManager();
            AddClasseDialog dialog = AddClasseDialog.newInstance("Ajouter une classe");
            dialog.show(fragmentManager, "fragment_add_classe");
        });

        dbRef = FirebaseFirestore.getInstance();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        fillClasses();
    }

    private void fillClasses() {
        if(!internetAvailable(getApplicationContext())){
            Toast.makeText(this, "Verifiez la connexion svp", Toast.LENGTH_LONG).show();
            return;
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.send_publication);
        progressDialog.setMessage(getResources().getString(R.string.patientez));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentUser.getUid();

        Query classes = dbRef.collection("classes")
                .whereEqualTo("teacher", uid);
        classes.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    myClasses.clear();
                    if(task.getResult()!=null){
                        if(task.getResult().size()>0){
                            for(QueryDocumentSnapshot doc : task.getResult()){
                                Classe offer = doc.toObject(Classe.class);
                                myClasses.add(offer);
                            }
                            adapter = new RecyclerAdapter(MyClassesActivity.this, myClasses);
                            recyclerView.setAdapter(adapter);
                        }else{}
                    }else{}
                }else{}
                progressDialog.dismiss();
            }
        });
    }

    public static boolean internetAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onSuccess() {
        fillClasses();
    }
}