package com.these.school.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.these.school.R;
import com.these.school.models.User;
import com.these.school.utils.OnItemSelected;
import com.these.school.views.EnsListDialog;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter {

    List<User> list;
    Context context;
    OnItemSelected callback;

    public UserAdapter(Context context, List<User> list, OnItemSelected callback) {
        this.list = list;
        this.context = context;
        this.callback = callback;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_card, parent, false);
        ViewHolderClass viewHolder = new ViewHolderClass(view);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ViewHolderClass viewHolderClass = (ViewHolderClass)holder;
        User user = list.get(position);
        viewHolderClass.setName(user.getName());
        viewHolderClass.setMail(user.getEmail());
        final String id = user.getId();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onClick(position, user, EnsListDialog.TAG);
            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolderClass extends RecyclerView.ViewHolder {
        public TextView cardName;
        public TextView cardMail;

        public ViewHolderClass(View itemView) {
            super(itemView);
            cardName = itemView.findViewById(R.id.cardName);
            cardMail = itemView.findViewById(R.id.cardMail);
        }

        public void setName(String string) {
            cardName.setText(string);
        }

        public void setMail(String string) {
            cardMail.setText(string);
        }

    }
}
