package dds.project.meet.presentation;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import dds.project.meet.logic.command.AddCardCommand;
import dds.project.meet.logic.command.Command;
import dds.project.meet.logic.command.MoveCardCommand;
import dds.project.meet.logic.command.RemoveCardCommand;
import dds.project.meet.logic.memento.Originator;
import dds.project.meet.logic.memento.CareTaker;
import dds.project.meet.R;
import dds.project.meet.logic.Card;
import dds.project.meet.logic.CardAdapter;
import dds.project.meet.logic.RecyclerItemClickListener;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private RecyclerView recyclerCards;
    public static RecyclerView.Adapter adapterCards;
    private RecyclerView.LayoutManager layoutManagerCards;

    static ArrayList<Card> dataCards;
    public Originator originator;
    public CareTaker careTaker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerCards = (RecyclerView) findViewById(R.id.recycler_cards);

        dataCards = new ArrayList<Card>();



        recyclerCards.setHasFixedSize(true);
        layoutManagerCards = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerCards.setLayoutManager(layoutManagerCards);

        adapterCards = new CardAdapter(dataCards, this);
        recyclerCards.setAdapter(adapterCards);

        recyclerCards.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Card c = dataCards.get(position);
                        openEvent(c);
                    }
                })
        );
        //Gestures
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(createHelperCallback());
        itemTouchHelper.attachToRecyclerView(recyclerCards);

        //Memento tools declaration
        originator = new Originator();
        careTaker = new CareTaker();

        TextView numberCards = (TextView) findViewById(R.id.numberCards);
        numberCards.setText(adapterCards.getItemCount() + " events waiting for you");
        loadCards();

        findViewById(R.id.mainMeetTitle).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                doSignOut();
                return true;
            }
        });
    }

    private void doSignOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private ItemTouchHelper.Callback createHelperCallback(){
        ItemTouchHelper.SimpleCallback simpleCallback =
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        moveCard(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                        return true;
                    }

                    @Override
                    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                        Card aux = dataCards.get(viewHolder.getAdapterPosition());
                        String name = aux.getName();

                        originator.setState(aux);
                        careTaker.add(originator.saveStateToMemento());

                        deleteCard(viewHolder.getAdapterPosition());
                        Snackbar undoDelete = Snackbar.make(findViewById(R.id.drawer_layout), name , Snackbar.LENGTH_LONG);
                        undoDelete.setAction("RESTORE", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                undoDeletion();
                            }
                        });
                        undoDelete.show();
                    }
            };
            return simpleCallback;
    }

    private void addCard(Card card) {
        Command addCard = new AddCardCommand(recyclerCards.getAdapter(), dataCards, card);
        addCard.execute();
    }

    private void deleteCard(int adapterPosition) {
        Command deleteCard = new RemoveCardCommand(recyclerCards.getAdapter(), dataCards, adapterPosition);
        deleteCard.execute();
    }

    private void moveCard(int adapterPositionOld, int adapterPositionNew) {
        Command moveCard = new MoveCardCommand(recyclerCards.getAdapter(), dataCards, adapterPositionOld, adapterPositionNew);
        moveCard.execute();
    }

    private void undoDeletion() {
        addCard(originator.getState());
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sidebar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){
            case R.id.nav_home:

                break;
            case R.id.nav_spaces:

                break;
            case R.id.nav_contacts:

                break;
            case R.id.nav_focus:

                break;
        }

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void loadCards() {
        Card one = new Card("10:30", "21 Mar." , "Cena Montaditos", "Av.Blasco Ibañez", 5, 5);
        Command addCard = new AddCardCommand(recyclerCards.getAdapter(), dataCards, one);
        addCard.execute();
    }

    public void createCard(View v) {
        Intent intent = new Intent(this, CreateNewEventActivity.class);
        startActivity(intent);
        /*Card c = new CardFactory().getCard();
        dataCards.add(c);
        adapterCards.notifyDataSetChanged();*/
    }

    public void openEvent(Card card) {
        Intent intent = new Intent(this, EventActivity.class);

        intent.putExtra("EXTRA_NAME", card.getName());
        intent.putExtra("EXTRA_LOCATION", card.getLocation());
        intent.putExtra("EXTRA_TIME", card.getTime());
        intent.putExtra("EXTRA_LOCATION", card.getLocation());
        intent.putExtra("EXTRA_DATE", card.getDate());

        startActivity(intent);
    }

}
