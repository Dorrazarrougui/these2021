package com.these.school.views;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.these.school.R;
import com.these.school.adapters.UserAdapter;
import com.these.school.models.Classe;
import com.these.school.models.User;
import com.these.school.utils.OnItemSelected;

import java.util.ArrayList;
import java.util.List;

public class EnsListDialog extends DialogFragment{

    public static final String TAG = "ENS_LIST_DIALOG";
    private TextView title;
    private RecyclerView list;
    private ProgressDialog progressDialog;
    private FirebaseFirestore dbRef;
    private List<User> myList;
    private UserAdapter adapter;
    OnItemSelected listener;

    public EnsListDialog() {}

    public static EnsListDialog newInstance(String title){
        EnsListDialog dialog = new EnsListDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        dialog.setArguments(args);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_ens_list, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        title = view.findViewById(R.id.title);
        list = view.findViewById(R.id.list);

        title.setText(getArguments().getString("title", "Abonnements ..."));

        progressDialog = new ProgressDialog(getActivity());

        dbRef = FirebaseFirestore.getInstance();
        myList = new ArrayList<>();

        list.setLayoutManager(new LinearLayoutManager(getActivity()));
        list.setHasFixedSize(true);

        fillList();

    }

    private void fillList() {
        if(!HomeActivityPar.internetAvailable(getActivity())){
            Toast.makeText(getActivity(), "Verifiez la connexion svp", Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle(R.string.send_publication);
        progressDialog.setMessage(getResources().getString(R.string.patientez));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        Query parent = dbRef.collection("users")
                .whereEqualTo("type", "Enseignant");
        parent.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult()!=null){
                        if(task.getResult().size()>0){
                            for(QueryDocumentSnapshot doc : task.getResult()){
                                User p = doc.toObject(User.class);
                                myList.add(p);
                            }
                            if(myList.size()>0){
                                adapter = new UserAdapter(getActivity(), myList, listener);
                                list.setAdapter(adapter);
                            }

                        }else{}
                    }else{}
                }else{}
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    public void setListener(OnItemSelected listener) {
        this.listener = listener;
    }
}
