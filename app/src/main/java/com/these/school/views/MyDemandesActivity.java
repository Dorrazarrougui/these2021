package com.these.school.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.these.school.R;
import com.these.school.adapters.RecyclerAdapter;
import com.these.school.adapters.RecyclerDemandesAdapter;
import com.these.school.models.Classe;
import com.these.school.models.Demande;
import com.these.school.models.Publication;
import com.these.school.utils.OnItemSelected;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyDemandesActivity extends AppCompatActivity implements OnItemSelected {

    public static final String TAG = "Demande_Activity";
    private SwipeRefreshLayout srl;
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    private List<Demande> myDemandes;
    private FirebaseFirestore dbRef;
    private FirebaseUser currentUser;
    private String uid;
    private RecyclerDemandesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_demandes);

        srl = findViewById(R.id.srl);
        recyclerView = findViewById(R.id.recyclerview);

        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fillDemandes();
                srl.setRefreshing(false);
            }
        });

        myDemandes = new ArrayList<>();

        progressDialog = new ProgressDialog(this);

        dbRef = FirebaseFirestore.getInstance();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        fillDemandes();
    }

    private void fillDemandes() {
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

        Query classes = dbRef.collection("demandes")
                .whereEqualTo("ens.id", uid)
                .whereEqualTo("state", 0);
        classes.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    myDemandes.clear();
                    if(task.getResult()!=null){
                        if(task.getResult().size()>0){
                            for(QueryDocumentSnapshot doc : task.getResult()){
                                Demande offer = doc.toObject(Demande.class);
                                myDemandes.add(offer);
                            }
                        }else{}
                        setAdapter();
                    }else{}
                }else{}
                progressDialog.dismiss();
            }
        });
    }

    private void setAdapter() {
        adapter = new RecyclerDemandesAdapter(MyDemandesActivity.this, myDemandes, this);
        recyclerView.setAdapter(adapter);

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
    public void onClick(int position, Object object, String tag) {
        if(tag.equals(TAG)){
            addAbonnement(object);
        }
    }

    private void addAbonnement(Object object) {
        Demande pub = (Demande)object;
        DocumentReference doc = dbRef.collection("users").document(pub.getParent().getId());
        doc.update("classes", FieldValue.arrayUnion(pub.getClasse().getId())).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    updateState(pub.getId());
                }else{

                }
            }
        });
    }

    private void updateState(String id) {
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("state", 1);
        DocumentReference doc = dbRef.collection("demandes").document(id);
        doc.update(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Demande acceptée avec succès", Toast.LENGTH_LONG).show();
                    fillDemandes();
                }else{

                }
            }
        });
    }
}