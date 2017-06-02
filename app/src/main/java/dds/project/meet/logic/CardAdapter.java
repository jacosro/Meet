package dds.project.meet.logic;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import dds.project.meet.R;


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
        public TextView date;
        //public ImageView image;

        public ViewHolder(View v) {
            super(v);
            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return false;
                }
            });
            name = (TextView) v.findViewById(R.id.name);
            location = (TextView) v.findViewById(R.id.location);
            time = (TextView) v.findViewById(R.id.time);
            km = (TextView) v.findViewById(R.id.distance);
            pers = (TextView) v.findViewById(R.id.personsnumber);
            date = (TextView) v.findViewById(R.id.date);
            //image = (ImageView) v.findViewById(R.id.eventImageView);
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
        String[] months = {"Jan.", "Feb.", "March", "April", "May", "June", "July", "Aug.", "Sept.", "Oct.", "Nov.", "Dec."};
        holder.itemView.setTag(position);
        holder.name.setText(mDataset.get(position).getName());
        holder.location.setText(mDataset.get(position).getLocation());
        holder.time.setText(mDataset.get(position).getTime());
        holder.date.setText(mDataset.get(position).getDateDay() + " " + months[mDataset.get(position).getDateMonth()]);
        holder.pers.setText(mDataset.get(position).getPersons() + " pers.");
        holder.km.setText(mDataset.get(position).getKm() + " km");
        //Uri img = Uri.parse(mDataset.get(position).getImage());
        //holder.image.setImageURI(img);

    }

    @Override
    public int getItemCount() {
        return mDataset.size();

    }

}
