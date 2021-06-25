package com.these.school.views;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.these.school.R;
import com.these.school.adapters.RecyclerAdapter;
import com.these.school.adapters.SpinnerClasseAdapter;
import com.these.school.models.Classe;
import com.these.school.models.Publication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddPublicationDialog extends DialogFragment implements AdapterView.OnItemSelectedListener {

    private static final int CHOOSER_REQUEST=13;
    private TextView title;
    private EditText text;
    private Spinner classesList;
    private Button addImage;
    private Button add;
    private ProgressDialog progressDialog;
    private AppCompatImageView imageView;
    private FirebaseFirestore dbRef;
    private StorageReference storageRef;
    private FirebaseFirestoreSettings settings;
    private FirebaseUser currentUser;
    private String uid;
    private List<Classe> myClasses;
    private SpinnerClasseAdapter spinnerClasseAdapter;
    private Uri uri;
    private Classe classe;

    public AddPublicationDialog() {}

    public static AddPublicationDialog newInstance(String title){
        AddPublicationDialog dialog = new AddPublicationDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        dialog.setArguments(args);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_add_publication, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        title = view.findViewById(R.id.title);
        text = view.findViewById(R.id.text);
        classesList = view.findViewById(R.id.spinner);
        imageView = view.findViewById(R.id.imageview);
        addImage = view.findViewById(R.id.buttonImage);
        add = view.findViewById(R.id.buttonAdd);

        dbRef = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference().child("publications");

        title.setText(getArguments().getString("title", "Ajouter une publication"));

        progressDialog = new ProgressDialog(getActivity());

        myClasses = new ArrayList<>();
        classe = new Classe();
        fillClasses();
        addImagesAction();
        sendPublicationAction();
        classesList.setOnItemSelectedListener(this);

        text.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        add.setOnClickListener(v -> {
            sendPublicationAction();
        });

        dbRef = FirebaseFirestore.getInstance();

    }

    private void fillClasses() {
        if(!internetAvailable(getActivity())){
            Toast.makeText(getActivity(), "Verifiez la connexion svp", Toast.LENGTH_LONG).show();
            return;
        }
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
                            spinnerClasseAdapter = new SpinnerClasseAdapter(getActivity(), myClasses);
                            classesList.setAdapter(spinnerClasseAdapter);
                        }else{}
                    }else{}
                }else{}
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

    private void sendPublicationAction() {
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkEntries()){
                    if(internetAvailable(getActivity()))
                        sendPub();
                    else Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void sendPub() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle(R.string.send_publication);
        progressDialog.setMessage(getResources().getString(R.string.patientez));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        DocumentReference doc = dbRef.collection("publications").document();
        final String docId = doc.getId();
        if(docId !=null && !docId.isEmpty()){
            final Publication pub = new Publication();
            pub.setDescription(text.getText().toString());
            pub.setId(docId);
            pub.setClasse(classe);
            pub.setDeleted(0);
            pub.setEns(currentUser.getUid());
            pub.setTime(Timestamp.now());

            DocumentReference doc2 = dbRef.collection("publications").document(docId);
            doc2.set(pub).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        if(uri!= null){
                            Long tsLong = System.currentTimeMillis();
                            String imageName = tsLong.toString().concat("_pub");
                            storageRef.child(docId).child(imageName+".jpg").putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if (!task.isSuccessful()) {
                                        throw task.getException();
                                    }
                                    return task.getResult().getStorage().getDownloadUrl();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    String url = String.valueOf(task.getResult());
                                    storeImageUrl(docId, url);
                                }
                            });
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Publication échouée", Toast.LENGTH_LONG).show();
                }
            });

        }else{
            Toast.makeText(getActivity(), "No ID", Toast.LENGTH_LONG).show();
        }
    }

    private void storeImageUrl(final String newKey, String url) {
        dbRef.collection("publications").document(newKey)
                .update("image", url)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Publication publié avec succès", Toast.LENGTH_LONG).show();
                        dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        classe.setId(((Classe)spinnerClasseAdapter.getItem(position)).getId());
        classe.setClasseName(((Classe)spinnerClasseAdapter.getItem(position)).getClasseName());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void addImagesAction() {
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Choisissez des images"), CHOOSER_REQUEST);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CHOOSER_REQUEST && resultCode== Activity.RESULT_OK && data!=null){

            if(data.getData()!=null){
                Uri uri = data.getData();
                this.uri = uri;

                Glide.with(getActivity())
                        .load(uri)
                        .into(imageView);
            }
        }
    }

    private boolean checkEntries() {
        /*if(tilTitle.getEditText().getText().length()==0) {
            Toast.makeText(this, "Titre obligatoire", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(list.size()==0) {
            Toast.makeText(this, "Ajouter au moin une image", Toast.LENGTH_SHORT).show();
            return false;
        }*/
        return true;
    }

    public interface OnAddClasseListener{
        void onSuccess();
    }
}
