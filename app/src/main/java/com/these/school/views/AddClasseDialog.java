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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.these.school.R;
import com.these.school.models.Classe;
import com.these.school.models.User;

public class AddClasseDialog extends DialogFragment {

    private TextView title;
    private EditText name;
    private Spinner level;
    private Button add;
    private ProgressDialog progressDialog;
    private FirebaseFirestore dbRef;
    private FirebaseFirestoreSettings settings;

    public AddClasseDialog() {}

    public static AddClasseDialog newInstance(String title){
        AddClasseDialog dialog = new AddClasseDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        dialog.setArguments(args);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_add_classe, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        title = view.findViewById(R.id.title);
        name = view.findViewById(R.id.name);
        level = view.findViewById(R.id.spinner);
        add = view.findViewById(R.id.button);

        title.setText(getArguments().getString("title", "Ajouter une classe"));

        progressDialog = new ProgressDialog(getActivity());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.levels, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        level.setAdapter(adapter);

        name.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        add.setOnClickListener(v -> {
            addClasse();
        });

        dbRef = FirebaseFirestore.getInstance();

    }

    private void addClasse() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle(R.string.send_publication);
        progressDialog.setMessage(getResources().getString(R.string.patientez));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        String type = level.getSelectedItem().toString();
        final Classe c = new Classe();
        c.setClasseName(name.getText().toString().trim());
        c.setClasseLevel(type);
        c.setTeacher(FirebaseAuth.getInstance().getCurrentUser().getUid());
        DocumentReference doc = dbRef.collection("classes").document();
        final String docId = doc.getId();
        if(docId !=null && !docId.isEmpty()){
            c.setId(docId);
            doc.set(c).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(getActivity(), "Super, classe Ajouter.", Toast.LENGTH_LONG).show();
                        OnAddClasseListener listener = (OnAddClasseListener) getActivity();
                        listener.onSuccess();
                        dismiss();
                    }
                    progressDialog.dismiss();
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    public interface OnAddClasseListener{
        void onSuccess();
    }
}
