package dds.project.meet;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by RaulCoroban on 15/04/2017.
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder>{
    private ArrayList<Card> mDataset;
    Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView location;
        public TextView time;
        public TextView km;
        public TextView pers;
        public ImageView image;

        public ViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.name);
            location = (TextView) v.findViewById(R.id.location);
            time = (TextView) v.findViewById(R.id.time);
            km = (TextView) v.findViewById(R.id.distance);
            pers = (TextView) v.findViewById(R.id.personsnumber);
            image = (ImageView) v.findViewById(R.id.eventImageView);
        }

    }

    public ArrayList<Card> getData() {
        return mDataset;
    }

    public CardAdapter(ArrayList<Card> myyDataset, Context context){

        mDataset = myyDataset;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View tv = LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false);
        ViewHolder vh = new ViewHolder(tv);
        return vh;
    }

    //Mostar info 1 a 1
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemView.setTag(position);
        holder.name.setText(mDataset.get(position).getName());
        holder.location.setText(mDataset.get(position).getLocation());
        holder.time.setText(mDataset.get(position).getTime());
        //holder.pers.setText(mDataset.get(position).getPersons());
        //holder.km.setText(mDataset.get(position).getKm());
        Uri img = Uri.parse(mDataset.get(position).getImage());
        holder.image.setImageURI(img);

    }

    @Override
    public int getItemCount() {
        return mDataset.size();

    }

}