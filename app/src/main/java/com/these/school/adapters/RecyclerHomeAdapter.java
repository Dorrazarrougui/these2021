package com.these.school.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.squareup.picasso.Picasso;
import com.these.school.R;
import com.these.school.models.Classe;
import com.these.school.models.Publication;
import com.these.school.utils.OnItemSelected;
import com.these.school.views.HomeActivityPar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class RecyclerHomeAdapter extends RecyclerView.Adapter {

    List<Publication> list;
    Context context;
    OnItemSelected callback;
    private String uid;
    private int pos;

    public RecyclerHomeAdapter(Context context, List<Publication> list, String uid, OnItemSelected callback) {
        this.list = list;
        this.context = context;
        this.callback = callback;
        this.uid = uid;
        pos = -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.publication_ens_card, parent, false);
        ViewHolderClass viewHolder = new ViewHolderClass(view);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ViewHolderClass viewHolderClass = (ViewHolderClass)holder;
        Publication pub = list.get(position);
        viewHolderClass.setDate(pub.getTime());
        viewHolderClass.setClasse(pub.getClasse());
        viewHolderClass.setContent(pub.getDescription());
        if(pub.getLikes()!=null && pub.getLikes().size()>0)
            viewHolderClass.setLikes(String.valueOf(pub.getLikes().size()));
        else
            viewHolderClass.setLikes("0");
        viewHolderClass.setImage(pub.getImage());
        final String id = pub.getId();
        if(pub.getLikes() != null && pub.getLikes().contains(uid))
            Glide.with(context)
                    .load(R.drawable.ic_like_full)
                    .timeout(60000)
                    .placeholder(R.drawable.ic_like)
                    .into(viewHolderClass.like);
        else
            viewHolderClass.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onClick(position, pub, HomeActivityPar.TAG);
                }
            });
        if(position==pos){
            Glide.with(context)
                    .load(R.drawable.ic_like_full)
                    .timeout(60000)
                    .placeholder(R.drawable.ic_like)
                    .into(viewHolderClass.like);
            if(pub.getLikes()!=null && pub.getLikes().size()>0)
                viewHolderClass.setLikes(String.valueOf(pub.getLikes().size()+1));
            else
                viewHolderClass.setLikes("1");
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public class ViewHolderClass extends RecyclerView.ViewHolder {
        //public RelativeLayout root;
        public TextView cardTime;
        public TextView cardContent;
        public TextView cardClasse;
        public TextView cardLikes;
        public AppCompatImageView cardImage;
        public ImageView like;

        public ViewHolderClass(View itemView) {
            super(itemView);
            cardClasse = itemView.findViewById(R.id.cardClasse);
            cardContent = itemView.findViewById(R.id.cardContent);
            cardTime = itemView.findViewById(R.id.cardTime);
            cardLikes = itemView.findViewById(R.id.cardLikes);
            cardImage = itemView.findViewById(R.id.cardImage);
            like = itemView.findViewById(R.id.like);
            //root = itemView.findViewById(R.id.cardRoot);
        }

        public void setClasse(Classe classe) {
            cardClasse.setText(classe.getClasseName());
        }

        public void setContent(String string) {
            cardContent.setText(string);
        }

        public void setDate(Timestamp date) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            cardTime.setText(sdf.format(date.toDate()));
        }

        public void setLikes(String string) {
            cardLikes.setText(string);
        }

        public void setImage(String downloadUrl) {
            if(downloadUrl!=null)
                Glide.with(context)
                        .load(downloadUrl)
                        .timeout(60000)
                        .placeholder(R.drawable.placeholder)
                        .into(cardImage);
            else
                cardImage.setVisibility(View.GONE);

            //Picasso.get().load(downloadUrl).placeholder(R.drawable.placeholder).into(cardImage);
        }
    }

    private String timestampToDate(long timestamp){
        Calendar cal = Calendar.getInstance(Locale.FRENCH);
        cal.setTimeInMillis(timestamp*1000);
        String date = android.text.format.DateFormat.format("dd-MM-yyyy hh:mm", cal).toString();
        return date;
    }
}
