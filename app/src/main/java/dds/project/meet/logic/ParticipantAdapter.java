package dds.project.meet.logic;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Random;

import dds.project.meet.R;
import dds.project.meet.persistence.Persistence;

/**
 * Created by RaulCoroban on 28/04/2017.
 */

public class ParticipantAdapter extends RecyclerView.Adapter<ParticipantAdapter.ViewHolder> {

    private List<User> dataMembers;
    Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView nameParticipant;
        public TextView acroParticipant;
        public CardView quitParticipant;
        public CardView cardHolder;

        public ViewHolder(View itemView) {
            super(itemView);
            nameParticipant = (TextView) itemView.findViewById(R.id.nameParticipant);
            acroParticipant = (TextView) itemView.findViewById(R.id.acroParticipant);
            quitParticipant = (CardView) itemView.findViewById(R.id.contact_quit);
            cardHolder = (CardView) itemView.findViewById(R.id.cardViewHolder);
        }
    }

    public ParticipantAdapter(List<User> myyDataset, Context context){
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
        holder.cardHolder.setCardBackgroundColor(setRandomColor());

        // Don't show the x if you are the contact
        if (Persistence.getInstance().userDAO.getCurrentUser().equals(dataMembers.get(position))) {
            holder.quitParticipant.setVisibility(View.INVISIBLE);
        }


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
