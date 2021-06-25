package com.these.school.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.these.school.R;
import com.these.school.models.Classe;

import java.util.List;

public class SpinnerClasseAdapter extends BaseAdapter {

    private List<Classe> classes;
    private Activity context;

    public SpinnerClasseAdapter(Activity context, List<Classe> classes) {
        this.classes = classes;
        this.context = context;
    }

    @Override
    public int getCount() {
        if(classes==null)
            return 0;
        return classes.size();
    }

    @Override
    public Object getItem(int position) {
        return classes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Classe classe = (Classe)getItem(position);
        View row = context.getLayoutInflater().inflate(R.layout.spinner_one_class_item, null, true);
        ((TextView)row.findViewById(R.id.class_name)).setText(classe.getClasseName());
        return row;
    }
}
