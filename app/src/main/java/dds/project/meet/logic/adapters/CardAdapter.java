package dds.project.meet.logic.adapters;

import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import dds.project.meet.R;
import dds.project.meet.logic.entities.Card;


/**
 * Created by RaulCoroban on 15/04/2017.
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder>{
    private SortedList<Card> mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView location;
        public TextView time;
        public TextView km;
        public TextView pers;
        public TextView date;
        public ImageView image;

        public ViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.name);
            location = (TextView) v.findViewById(R.id.location);
            time = (TextView) v.findViewById(R.id.time);
            km = (TextView) v.findViewById(R.id.distance);
            pers = (TextView) v.findViewById(R.id.personsnumber);
            date = (TextView) v.findViewById(R.id.date);
            image = (ImageView) v.findViewById(R.id.eventImageView);
        }

    }

    public CardAdapter() {
        mDataset = new SortedList<Card>(Card.class, new SortedList.Callback<Card>() {
            @Override
            public int compare(Card o1, Card o2) {
                return o1.compareTo(o2);
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }

            @Override
            public boolean areContentsTheSame(Card oldItem, Card newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areItemsTheSame(Card item1, Card item2) {
                String key1 = item1.getDbKey();
                String key2 = item2.getDbKey();

                if (key1 != null && key2 != null) {
                    return key1.equals(key2);
                }
                return item1 == item2;
            }

            @Override
            public void onInserted(int position, int count) {
                notifyItemRangeInserted(position, count);
            }

            @Override
            public void onRemoved(int position, int count) {
                notifyItemRangeRemoved(position, count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                notifyItemMoved(fromPosition, toPosition);
            }
        });
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
        String locationName = mDataset.get(position).getLocation();

        if(locationName.length() > 28) {
            locationName = locationName.substring(0, Math.min(locationName.length(),28)) + "...";
        }

        holder.itemView.setTag(position);
        holder.name.setText(mDataset.get(position).getName());
        holder.location.setText(locationName);
        holder.time.setText(mDataset.get(position).getTime());
        holder.date.setText(mDataset.get(position).getDateDay() + "" + correctSuperScript(mDataset.get(position).getDateDay()) + " " + months[mDataset.get(position).getDateMonth()]);
        holder.pers.setText(mDataset.get(position).getPersons() + " pers.");
        holder.km.setText(mDataset.get(position).getKm() + " km");

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
        if (!payloads.isEmpty()) {
            Object object = payloads.get(0);
            if (object instanceof String) {
                holder.km.setText(object.toString());
            }
        } else {
            super.onBindViewHolder(holder, position, payloads);
        }
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


    // region CardList Helpers
    public Card get(int position) {
        return mDataset.get(position);
    }

    public int add(Card item) {
        return mDataset.add(item);
    }

    public int indexOf(Card item) {
        return mDataset.indexOf(item);
    }

    public void updateItemAt(int index, Card item) {
        mDataset.updateItemAt(index, item);
    }

    public void addAll(Collection<Card> items) {
        mDataset.beginBatchedUpdates();
        for (Card item : items) {
            mDataset.add(item);
        }
        mDataset.endBatchedUpdates();
    }

    public void addAll(Card... items) {
        addAll(Arrays.asList(items));
    }

    public boolean remove(Card item) {
        return mDataset.remove(item);
    }

    public int removeItemWithId(String id) {
        for (int i = 0; i < mDataset.size(); i++) {
            if (id.equals(mDataset.get(i).getDbKey())) {
                mDataset.removeItemAt(i);
                return i;
            }
        }
        return -1;
    }

    public Card removeItemAt(int index) {
        return mDataset.removeItemAt(index);
    }

    public void clear() {
        mDataset.beginBatchedUpdates();
        //remove items at end, to avoid unnecessary array shifting
        while (mDataset.size() > 0) {
            mDataset.removeItemAt(mDataset.size() - 1);
        }
        mDataset.endBatchedUpdates();
    }
}
