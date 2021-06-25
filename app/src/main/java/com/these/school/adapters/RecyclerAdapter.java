package com.these.school.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.these.school.R;
import com.these.school.models.Classe;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter {

    List<Classe> listClasse;
    Context context;

    public RecyclerAdapter(Context context, List<Classe> listClasse) {
        this.listClasse = listClasse;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.classe_card, parent, false);
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
        /*viewHolderClass.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent oneOfferIntent = new Intent(context, OneOfferActivity.class);
                oneOfferIntent.putExtra("num", id);
                context.startActivity(oneOfferIntent);
            }
        });*/
    }


    @Override
    public int getItemCount() {
        return listClasse.size();
    }

    public class ViewHolderClass extends RecyclerView.ViewHolder {
        public RelativeLayout root;
        public TextView cardTitle;
        public TextView cardLevel;

        public ViewHolderClass(View itemView) {
            super(itemView);
            cardTitle = itemView.findViewById(R.id.cardTitle);
            cardLevel = itemView.findViewById(R.id.cardLevel);
            //root = itemView.findViewById(R.id.cardRoot);
        }

        public void setTitle(String string) {
            cardTitle.setText(string);
        }

        public void setLevel(String string) {
            cardLevel.setText(string);
        }
    }
}
