package dds.project.meet.logic;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dds.project.meet.R;

/**
 * Created by RaulCoroban on 28/04/2017.
 */

public class ParticipantAdapter extends RecyclerView.Adapter<ParticipantAdapter.ViewHolder> {

    private ArrayList<Participant> dataMembers;
    Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView nameParticipant;
        public TextView acroParticipant;
        public TextView emailParticipant;

        public ViewHolder(View itemView) {
            super(itemView);
            nameParticipant = (TextView) itemView.findViewById(R.id.nameParticipant);
            emailParticipant = (TextView) itemView.findViewById(R.id.emailParticipant);
            acroParticipant = (TextView) itemView.findViewById(R.id.acroParticipant);
        }
    }

    public ParticipantAdapter(ArrayList<Participant> myyDataset, Context context){
        dataMembers = myyDataset;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.participant, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ParticipantAdapter.ViewHolder holder, int position) {
        String name = dataMembers.get(position).getName();
        holder.nameParticipant.setText(name);
        holder.emailParticipant.setText(dataMembers.get(position).getEmail());

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
}
