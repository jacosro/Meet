package dds.project.meet.logic;

import android.content.Context;
import android.location.Address;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import dds.project.meet.R;

/**
 * Created by RaulCoroban on 28/04/2017.
 */

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {

    private List<Address> dataLocations;
    Card card;
    Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView placeName;
        public TextView placeExtra;


        public ViewHolder(View itemView) {
            super(itemView);
            placeName = (TextView) itemView.findViewById(R.id.placeName);
            placeExtra = (TextView) itemView.findViewById(R.id.placeExtra);

        }
    }

    public LocationAdapter(List<Address> myyDataset, Context context){
        dataLocations = myyDataset;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(LocationAdapter.ViewHolder holder, final int position) {
        holder.placeName.setText(dataLocations.get(position).getAddressLine(0));
        holder.placeExtra.setText(dataLocations.get(position).getAddressLine(1) + ", " + dataLocations.get(position).getCountryName() + ", " + dataLocations.get(position).getCountryCode());

    }

    @Override
    public int getItemCount() {
        return dataLocations.size();
    }

}
