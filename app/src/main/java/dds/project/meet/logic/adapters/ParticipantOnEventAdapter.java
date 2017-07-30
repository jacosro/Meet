package dds.project.meet.logic.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;
import java.util.Random;

import dds.project.meet.R;
import dds.project.meet.logic.entities.Event;
import dds.project.meet.logic.entities.User;
import dds.project.meet.persistence.Persistence;
import dds.project.meet.persistence.util.QueryCallback;

/**
 * Created by RaulCoroban on 28/04/2017.
 */

public class ParticipantOnEventAdapter extends RecyclerView.Adapter<ParticipantOnEventAdapter.ViewHolder> {

    private List<User> dataMembers;
    Event event;
    Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView nameParticipant;
        public TextView acroParticipant;
        public CardView c;
        public Button remove;

        public ViewHolder(View itemView) {
            super(itemView);
            nameParticipant = (TextView) itemView.findViewById(R.id.nameParticipant);
            acroParticipant = (TextView) itemView.findViewById(R.id.acroParticipant);
            c = (CardView) itemView.findViewById(R.id.cardView2);
            remove = (Button) itemView.findViewById(R.id.removeButton);
        }
    }

    public ParticipantOnEventAdapter(List<User> myyDataset, Context context, Event event){
        dataMembers = myyDataset;
        this.context = context;
        this.event = event;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_participant, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ParticipantOnEventAdapter.ViewHolder holder, final int position) {
        final String name = dataMembers.get(position).getUsername();
        holder.nameParticipant.setText(name);

        String myUid = "";
        for (User user : event.getParticipants()) {
            if (user.getUsername().equals(name)) {
                myUid = user.getUid();
            }
        }
        if (myUid.equals(event.getOwner())) {
            holder.remove.setText("OWNER");
            holder.remove.setBackgroundColor(0xFF3498db);
        } else {
            holder.remove.setVisibility(View.VISIBLE);
            holder.remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(context)
                            .setTitle("Closing application")
                            .setMessage("Are you sure you want to remove \" " + name + " \" from participants?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final User user = dataMembers.get(position);
                                    Persistence.getInstance().userDAO.removeUserFromEvent(event, user, new QueryCallback<Boolean>() {
                                        @Override
                                        public void result(Boolean data) {
                                            Log.d("USER KICKED ON ASS", "Hasta luego Maricarmen");
                                            event.getParticipants().remove(user);
                                            dataMembers.remove(user);
                                            notifyDataSetChanged();
                                        }
                                    });
                                }
                            }).setNegativeButton("No", null).show();
                }
            });
        }
        holder.c.setCardBackgroundColor(setRandomColor());

        String[] split = name.split("\\s");
        String initials;
        if (split.length == 2) {
            initials = (split[0].substring(0, 1)+ split[1].substring(0, 1)).toUpperCase();
        } else {
            initials = split[0].substring(0, 2).toUpperCase();
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
