package com.these.school.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.these.school.R;
import com.these.school.models.Classe;
import com.these.school.models.Demande;
import com.these.school.utils.OnItemSelected;
import com.these.school.views.ClassesListDialog;
import com.these.school.views.MyDemandesActivity;

import java.util.List;

public class RecyclerDemandesAdapter extends RecyclerView.Adapter {

    private List<Demande> list;
    private Context context;
    private OnItemSelected callback;

    public RecyclerDemandesAdapter(Context context, List<Demande> list, OnItemSelected callback) {
        this.list = list;
        this.context = context;
        this.callback = callback;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.demande_dialog_card, parent, false);
        ViewHolderClass viewHolder = new ViewHolderClass(view);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ViewHolderClass viewHolderClass = (ViewHolderClass)holder;
        Demande classe = list.get(position);
        viewHolderClass.setTitle(classe.getParent().getName());
        viewHolderClass.setMail(classe.getParent().getEmail());
        viewHolderClass.setPhone(classe.getParent().getPhone1());
        final String id = classe.getId();
        viewHolderClass.joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onClick(position, classe, MyDemandesActivity.TAG);
            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolderClass extends RecyclerView.ViewHolder {
        public Button joinBtn;
        public TextView cardTitle;
        public TextView cardMail;
        public TextView cardPhone;

        public ViewHolderClass(View itemView) {
            super(itemView);
            cardTitle = itemView.findViewById(R.id.cardTitle);
            cardMail = itemView.findViewById(R.id.cardMail);
            cardPhone = itemView.findViewById(R.id.cardPhone);
            joinBtn = itemView.findViewById(R.id.joinBtn);
        }

        public void setTitle(String string) {
            cardTitle.setText(string);
        }

        public void setMail(String string) {
            cardMail.setText(string);
        }

        public void setPhone(String string) {
            cardPhone.setText(string);
        }
    }
}
