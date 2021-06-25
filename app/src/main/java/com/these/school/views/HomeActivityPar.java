package com.these.school.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.these.school.R;
import com.these.school.adapters.RecyclerHomeAdapter;
import com.these.school.models.Publication;
import com.these.school.models.User;
import com.these.school.utils.OnItemSelected;

import java.util.ArrayList;
import java.util.List;

public class HomeActivityPar extends AppCompatActivity implements OnItemSelected {

    public static final String TAG = "HOME_PARENT";
    private DrawerLayout drawerlayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private RecyclerView listPublications;
    private ProgressDialog progressDialog;
    private RecyclerHomeAdapter adapter;
    private SwipeRefreshLayout srl;
    private List<Publication> myList;
    private FirebaseUser currentUser;
    private FirebaseFirestore dbRef;
    private String uid;
    private User mParent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(R.string.home_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_home_par);

        drawerlayout = findViewById(R.id.drawarlayout);
        navigationView = findViewById(R.id.navigationView);
        listPublications = findViewById(R.id.listViewPublications);
        srl = findViewById(R.id.srl);

        toggle = new ActionBarDrawerToggle(this, drawerlayout, R.string.open, R.string.close);
        drawerlayout.addDrawerListener(toggle);
        toggle.syncState();

        progressDialog = new ProgressDialog(this);

        dbRef = FirebaseFirestore.getInstance();

        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUser();
                srl.setRefreshing(false);
            }
        });
        myList = new ArrayList<>();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.accueil: {
                        /*Intent intentSales = new Intent(HomeActivity.this, SalesActivity.class);
                        startActivity(intentSales);*/
                        return true;
                    }

                    case R.id.classes: {
                        Intent intentSales = new Intent(HomeActivityPar.this, MyAbonnementsActivity.class);
                        startActivity(intentSales);
                        return true;
                    }

                    case R.id.settings: {
                        /*Intent intentPeofile = new Intent(HomeActivity.this, ProfileActivity.class);
                        startActivity(intentPeofile);*/
                        return true;
                    }
                    case R.id.logout: {
                        FirebaseAuth.getInstance().signOut();
                        Intent intentLogin = new Intent(HomeActivityPar.this, LoginActivity.class);
                        startActivity(intentLogin);
                        finish();
                        return true;
                    }
                }
                return HomeActivityPar.super.onContextItemSelected(item);
            }
        });

        listPublications.setLayoutManager(new LinearLayoutManager(this));
        listPublications.setHasFixedSize(true);

        getUser();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) return true;
        return super.onOptionsItemSelected(item);
    }

    public static boolean internetAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void getUser(){
        if(!internetAvailable(getApplicationContext())){
            Toast.makeText(this, "Verifiez la connexion svp", Toast.LENGTH_LONG).show();
            return;
        }

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentUser.getUid();

        progressDialog.show();
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
                                fillPublications();

                        }else{}
                    }else{}
                }else{}
                progressDialog.dismiss();
            }
        });
    }

    private void fillPublications() {
        if(!internetAvailable(getApplicationContext())){
            Toast.makeText(HomeActivityPar.this, R.string.no_internet, Toast.LENGTH_LONG).show();
            return;
        }
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentUser.getUid();

        Query pubs = dbRef.collection("publications")
                .whereIn("classe.id", mParent.getClasses())
                .orderBy("time", Query.Direction.DESCENDING);
        pubs.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    myList.clear();
                    if(task.getResult()!=null){
                        if(task.getResult().size()>0){
                            for(QueryDocumentSnapshot doc : task.getResult()){
                                Publication pub = doc.toObject(Publication.class);
                                myList.add(pub);
                            }
                            setAdapter();
                        }else{

                        }
                    }else{

                    }
                }else{

                }
            }
        });
    }

    private void setAdapter() {
        adapter = new RecyclerHomeAdapter(HomeActivityPar.this, myList, uid, this);
        listPublications.setAdapter(adapter);
    }

    @Override
    public void onClick(int position, Object object, String tag) {
        if(tag.equals(TAG)){
            Publication pub = (Publication)object;
            DocumentReference doc = dbRef.collection("publications").document(pub.getId());
            doc.update("likes", FieldValue.arrayUnion(uid)).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(getApplicationContext(), "Liked Successful", Toast.LENGTH_LONG).show();
                        adapter.setPos(position);
                        adapter.notifyItemChanged(position);
                    }else{

                    }
                }
            });
        }
    }
}