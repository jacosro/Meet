package dds.project.meet.logic;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

import dds.project.meet.R;

/**
 * Created by RaulCoroban on 28/04/2017.
 */

public class ParticipantOnEventAdapter extends RecyclerView.Adapter<ParticipantOnEventAdapter.ViewHolder> {

    private ArrayList<User> dataMembers;
    Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView nameParticipant;
        public TextView acroParticipant;
        public TextView distanceParticipant;
        public CardView c;

        public ViewHolder(View itemView) {
            super(itemView);
            nameParticipant = (TextView) itemView.findViewById(R.id.nameParticipant);
            distanceParticipant = (TextView) itemView.findViewById(R.id.distanceParticipant);
            acroParticipant = (TextView) itemView.findViewById(R.id.acroParticipant);
            c = (CardView) itemView.findViewById(R.id.cardView2);
        }
    }

    public ParticipantOnEventAdapter(ArrayList<User> myyDataset, Context context){
        dataMembers = myyDataset;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.participant_on_event, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ParticipantOnEventAdapter.ViewHolder holder, int position) {
        String name = dataMembers.get(position).getName();
        holder.nameParticipant.setText(name);
        //holder.distanceParticipant.setText(dataMembers.get(position).getEmail());

        holder.c.setCardBackgroundColor(setRandomColor());

        String[] split = name.split("\\s");
        String initials;
        if (split.length == 2) {
            initials = (split[0].substring(0, 1)+ split[1].substring(0, 1)).toUpperCase();
        } else {
            initials = split[0].substring(0, 2);
        }
        holder.acroParticipant.setText(initials);
    }

    @Override
    public int getItemCount() {
        return dataMembers.size();
    }

    private int setRandomColor() {
        int [] colors = {0xFFF3A356,0xFF5BB397,0xFF528fdb,0xFF9A69E4,0xFFEDB64D};
        Random r = new Random();
        int selectColor = r.nextInt(colors.length);
        return colors[selectColor];
    }
}
