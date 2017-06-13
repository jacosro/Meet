package dds.project.meet.logic.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Random;

import dds.project.meet.R;
import dds.project.meet.logic.entities.User;

/**
 * Created by RaulCoroban on 09/06/2017.
 */

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder>{
    private List<User> dataMembers;
    Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView nameContact;
        public TextView phoneContact;
        public TextView acroContact;

        public ViewHolder(View itemView) {
            super(itemView);
            nameContact = (TextView) itemView.findViewById(R.id.nameContact);
            phoneContact = (TextView) itemView.findViewById(R.id.phoneContact);
            acroContact = (TextView) itemView.findViewById(R.id.acroParticipant);
        }
    }

    public ContactAdapter(List<User> myyDataset, Context context){
        dataMembers = myyDataset;
        this.context = context;
    }

    @Override
    public ContactAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact, parent, false);
        ContactAdapter.ViewHolder vh = new ContactAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ContactAdapter.ViewHolder holder, int position) {
        final String name = dataMembers.get(position).getName();
        holder.nameContact.setText(name);
        holder.phoneContact.setText(dataMembers.get(position).getTelephone());



        String[] split = name.split("\\s");
        String initials;
        if (split.length >= 2) {
            initials = (split[0].substring(0, 1)+ split[1].substring(0, 1)).toUpperCase();
        } else {
            if (split[0].length() == 1) {
                initials = split[0].substring(0, 1);
            } else {
                initials = split[0].substring(0, 2);

            }
        }
        holder.acroContact.setText(initials);
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

    public User get(int position) {
        return dataMembers.get(position);
    }
}
