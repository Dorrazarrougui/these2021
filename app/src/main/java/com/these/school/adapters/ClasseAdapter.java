package com.these.school.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.these.school.R;
import com.these.school.models.Classe;
import com.these.school.utils.OnItemSelected;
import com.these.school.views.ClassesListDialog;

import java.util.List;

public class ClasseAdapter extends RecyclerView.Adapter {

    private List<Classe> listClasse;
    private Context context;
    private OnItemSelected callback;
    private List<String> joined;

    public ClasseAdapter(Context context, List<Classe> listClasse, List<String> joined, OnItemSelected callback) {
        this.listClasse = listClasse;
        this.context = context;
        this.callback = callback;
        this.joined = joined;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.classe_dialog_card, parent, false);
        ViewHolderClass viewHolder = new ViewHolderClass(view);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ViewHolderClass viewHolderClass = (ViewHolderClass)holder;
        Classe classe = listClasse.get(position);
        viewHolderClass.setTitle(classe.getClasseName());
        viewHolderClass.setLevel(classe.getClasseLevel());
        final String id = classe.getId();
        if(joined!=null)
            viewHolderClass.setBtn(classe.getId());
        viewHolderClass.joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onClick(position, classe, ClassesListDialog.TAG);
            }
        });
    }


    @Override
    public int getItemCount() {
        return listClasse.size();
    }

    public class ViewHolderClass extends RecyclerView.ViewHolder {
        public Button joinBtn;
        public TextView cardTitle;
        public TextView cardLevel;

        public ViewHolderClass(View itemView) {
            super(itemView);
            cardTitle = itemView.findViewById(R.id.cardTitle);
            cardLevel = itemView.findViewById(R.id.cardLevel);
            joinBtn = itemView.findViewById(R.id.joinBtn);
        }

        public void setTitle(String string) {
            cardTitle.setText(string);
        }

        public void setLevel(String string) {
            cardLevel.setText(string);
        }

        public void setBtn(String id) {
            if(joined.contains(id)){
                joinBtn.setEnabled(false);
                joinBtn.setText("Abonné");
            }
        }
    }
}
