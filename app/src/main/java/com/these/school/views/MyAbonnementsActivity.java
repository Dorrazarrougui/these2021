package com.these.school.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.UploadTask;
import com.these.school.R;
import com.these.school.adapters.RecyclerAdapter;
import com.these.school.models.Classe;
import com.these.school.models.Demande;
import com.these.school.models.User;
import com.these.school.utils.OnItemSelected;

import java.util.ArrayList;
import java.util.List;

public class MyAbonnementsActivity extends AppCompatActivity implements AddClasseDialog.OnAddClasseListener, OnItemSelected {

    private SwipeRefreshLayout srl;
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    private AppCompatButton addClasse;
    private List<Classe> myClasses;
    private FirebaseFirestore dbRef;
    private FirebaseUser currentUser;
    private String uid;
    private User mParent;
    private RecyclerAdapter adapter;
    private EnsListDialog dialogEns;
    private ClassesListDialog dialogClasses;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_abonnements);

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
            fragmentManager = getSupportFragmentManager();
            dialogEns = EnsListDialog.newInstance("Ajouter une classe");
            dialogEns.setListener(this);
            dialogEns.show(fragmentManager, "fragment_add_abonnement");
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

        Query parent = dbRef.collection("users")
                .whereEqualTo("id", uid)
                .limit(1);
        parent.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult()!=null){
                        if(task.getResult().size()>0){
                            for(QueryDocumentSnapshot doc : task.getResult()){
                                User p = doc.toObject(User.class);
                                mParent = p;
                            }
                            if(mParent!=null && mParent.getClasses()!=null && mParent.getClasses().size()>0)
                                getClassesByUser(mParent.getClasses());
                            else
                                progressDialog.dismiss();

                        }else{}
                    }else{}
                }else{progressDialog.dismiss();}
            }
        });
    }

    private void getClassesByUser(List<String> classesList) {
        Query classes = dbRef.collection("classes")
                .whereIn("id", classesList);
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
                            adapter = new RecyclerAdapter(MyAbonnementsActivity.this, myClasses);
                            recyclerView.setAdapter(adapter);
                        }else{}
                    }else{}
                }else{}
                progressDialog.dismiss();
            }
        });
    }

    private void openClassDialog(User user) {
        fragmentManager = getSupportFragmentManager();
        dialogClasses = ClassesListDialog.newInstance(user.getName(), user.getId());
        dialogClasses.setListener(this);
        dialogClasses.setJoined(mParent.getClasses());
        dialogClasses.show(fragmentManager, "fragment_add_abonnement");
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

    @Override
    public void onClick(int position, Object object, String tag) {
        if(tag.equals(EnsListDialog.TAG)){
            User user = (User)object;
            if(dialogEns != null)
                dialogEns.dismiss();
            openClassDialog(user);
        }

        if(tag.equals(ClassesListDialog.TAG)){
            Classe classe = (Classe)object;
            if(dialogClasses != null)
                dialogClasses.dismiss();
            joinClasse(classe);
        }
    }

    private void joinClasse(Classe classe) {
        //send request to enseignant
        DocumentReference doc = dbRef.collection("demandes").document();
        final String docId = doc.getId();

        Demande demande = new Demande();
        demande.setId(docId);
        demande.setClasse(classe);
        User ens = new User();
        ens.setId(classe.getTeacher());
        demande.setEns(ens);
        demande.setParent(mParent);
        demande.setState(0);
        demande.setTime(Timestamp.now());

        DocumentReference doc2 = dbRef.collection("demandes").document(docId);
        doc2.set(demande).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(MyAbonnementsActivity.this, " Votre demande a été envoyée", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

}