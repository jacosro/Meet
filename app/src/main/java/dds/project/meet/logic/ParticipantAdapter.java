package dds.project.meet.logic;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;
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
        public CardView cardHolder;
        public Button b;

        public ViewHolder(View itemView) {
            super(itemView);
            nameParticipant = (TextView) itemView.findViewById(R.id.nameParticipant);
            //emailParticipant = (TextView) itemView.findViewById(R.id.emailParticipant);
            acroParticipant = (TextView) itemView.findViewById(R.id.acroParticipant);
            b = (Button) itemView.findViewById(R.id.button2);
            cardHolder = (CardView) itemView.findViewById(R.id.cardViewHolder);
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
    public void onBindViewHolder(final ParticipantAdapter.ViewHolder holder, int position) {
        final String name = dataMembers.get(position).getName();
        String [] first = name.split("\\s");
        holder.nameParticipant.setText(first[0]);
        //holder.emailParticipant.setText(dataMembers.get(position).getEmail());

        holder.cardHolder.setCardBackgroundColor(setRandomColor());


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
