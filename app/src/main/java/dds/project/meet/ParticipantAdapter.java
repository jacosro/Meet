package dds.project.meet;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.reflect.Member;
import java.util.ArrayList;

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
        holder.nameParticipant.setText(dataMembers.get(position).getName());
        holder.emailParticipant.setText(dataMembers.get(position).getEmail());
        holder.acroParticipant.setText("AB");
    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
