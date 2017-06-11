package dds.project.meet.logic;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.Timestamp;
import java.util.ArrayList;

import dds.project.meet.R;


/**
 * Created by RaulCoroban on 15/04/2017.
 */

public class CardTrashAdapter extends RecyclerView.Adapter<CardTrashAdapter.ViewHolder>{
    private ArrayList<CardD> mDataset;
    Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView location;
        public TextView time;
        public TextView km;
        public TextView pers;
        public TextView date;
        public ImageView image;
        public TextView timeStamp;

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
            image = (ImageView) v.findViewById(R.id.eventImageView);
            timeStamp = (TextView) v.findViewById(R.id.dateDeleted);
        }

    }

    public ArrayList<CardD> getData() {
        return mDataset;
    }

    public CardTrashAdapter(ArrayList<CardD> myyDataset, Context context){
        mDataset = myyDataset;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View tv = LayoutInflater.from(parent.getContext()).inflate(R.layout.trash_card, parent, false);
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
        holder.date.setText(mDataset.get(position).getDateDay() + "" + correctSuperScript(mDataset.get(position).getDateDay()) + " " + months[mDataset.get(position).getDateMonth()]);
        holder.pers.setText(mDataset.get(position).getPersons() + " pers.");
        holder.km.setText(mDataset.get(position).getKm() + " km");
        Timestamp x = mDataset.get(position).getStampTime();
        holder.timeStamp.setText("Deleted on " + x.getDate() + correctSuperScript(x.getDate()) + " " + months[x.getMonth()]  + " at " + correctTime(x.getHours(), x.getMinutes()));

    }

    private String correctSuperScript(int day) {
        if(day > 20 && day % 10 == 1) return "st";
        if(day > 20 && day % 10 == 2) return "nd";
        return "th";
    }

    @Override
    public int getItemCount() {
        return mDataset.size();

    }

    private String correctTime(int hourOfDay, int minute) {
        String minuteS = "", hourS = "";

        if(minute < 10) {
            minuteS =  "0" + minute;
        } else minuteS = minute + "";

        if(hourOfDay < 10) {
            hourS = "0" + hourOfDay;
        } else hourS = hourOfDay + "";

        return hourS + ":" + minuteS;
    }

}