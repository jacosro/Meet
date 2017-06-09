package dds.project.meet.presentation;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;

import dds.project.meet.logic.CardFactory;
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
import dds.project.meet.persistence.Persistence;
import dds.project.meet.persistence.QueryCallback;

public class MainActivity extends BaseActivity {

    // UI elements
    private TextView numberCards;
    private FloatingActionButton fab;
    private RecyclerView recyclerCards;
    public static RecyclerView.Adapter adapterCards;
    private RecyclerView.LayoutManager layoutManagerCards;
    private ImageView noEvents;

    private DrawerLayout drawerLayout;
    private ConstraintLayout background;

    // Class fields
    static ArrayList<Card> dataCards;
    private Originator originator;
    private CareTaker careTaker;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerCards = (RecyclerView) findViewById(R.id.recycler_cards);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        numberCards = (TextView) findViewById(R.id.numberCards);
        noEvents = (ImageView) findViewById(R.id.noEvents);
        background = (ConstraintLayout) findViewById(R.id.back);
        dataCards = new ArrayList<Card>();

        findViewById(R.id.mainMeetTitle).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                doSignOut();
                return true;
            }
        });

        //Memento tools declaration
        originator = new Originator();
        careTaker = new CareTaker();



        //Initalize RecyclerView
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

        //Gestures RecyclerView
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(createHelperCallback());
        itemTouchHelper.attachToRecyclerView(recyclerCards);

        loadCards();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCard(v);
            }
        });

        if(dataCards.size() > 0) {
            noEvents.setVisibility(View.GONE);
            background.setBackgroundResource(R.drawable.back);
        } else {
            noEvents.setVisibility(View.VISIBLE);
            background.setBackgroundResource(R.drawable.full);
        }

    }

    private void doSignOut() {
        mPersistence.doSignOut();
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
                        Snackbar undoDelete = Snackbar.make(findViewById(R.id.drawer_layout), "\"" + name + "\"" + " deleted" , Snackbar.LENGTH_LONG);
                        undoDelete.setAction("RESTORE", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                addCard(originator.getState());
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
        numberCards.setText(dataCards.size() + " upcoming event(s)");

        if(dataCards.size() > 0) {
            noEvents.setVisibility(View.GONE);
            background.setBackgroundResource(R.drawable.back);
        }
    }

    private void deleteCard(int adapterPosition) {
        Command deleteCard = new RemoveCardCommand(recyclerCards.getAdapter(), dataCards, adapterPosition);
        deleteCard.execute();

        if(dataCards.size() > 0) {
            numberCards.setText(dataCards.size() + " upcoming event(s)");
        } else {
            numberCards.setText("No events. No one loves you");
            noEvents.setVisibility(View.VISIBLE);
            background.setBackgroundResource(R.drawable.full);
        }

    }

    private void moveCard(int adapterPositionOld, int adapterPositionNew) {
        Command moveCard = new MoveCardCommand(recyclerCards.getAdapter(), dataCards, adapterPositionOld, adapterPositionNew);
        moveCard.execute();
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

        Card one = new Card("10:30", 12 , 3 , 2016 , "Cena Montaditos", "Av.Blasco Ibañez", 5, 5);
        Card two = new Card("14:55", 17 , 5 , 2017 , "Comida La Vella", "ETSINF UPV", 7, 2);
        Command addCard = new AddCardCommand(recyclerCards.getAdapter(), dataCards, one);
        addCard.execute();

        Command addCard2 = new AddCardCommand(recyclerCards.getAdapter(), dataCards, two);
        addCard2.execute();

        /*mPersistence.getAllCards(new QueryCallback<Collection<Card>>() {
            @Override
            public void result(Collection<Card> data) {
                for (Card card : data) {
                    new AddCardCommand(adapterCards, dataCards, card).execute();
                }
            }
        });*/
        numberCards.setText(dataCards.size() + " upcoming event(s)");
    }

    public void createCard(View v) {
        Intent intent = new Intent(this, CreateNewEventActivity.class);
        startActivityForResult(intent, 0);
    }

    public void openEvent(Card card) {
        Intent intent = new Intent(this, EventActivity.class);

        intent.putExtra("EXTRA_NAME", card.getName());
        intent.putExtra("EXTRA_LOCATION", card.getLocation());
        intent.putExtra("EXTRA_TIME", card.getTime());
        intent.putExtra("EXTRA_LOCATION", card.getLocation());
        intent.putExtra("EXTRA_DATE_DAY", card.getDateDay());
        intent.putExtra("EXTRA_DATE_MONTH", card.getDateMonth());
        intent.putExtra("EXTRA_DATE_YEAR", card.getDateYear());

        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            if (intent != null) {
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    String name = extras.getString("EXTRA_NAME");
                    int day = extras.getInt("EXTRA_DAY");
                    int month = extras.getInt("EXTRA_MONTH");
                    int year = extras.getInt("EXTRA_DISTANCE");
                    String address = extras.getString("EXTRA_ADDRESS");
                    String whatTimeLabel = extras.getString("EXTRA_TIME");
                    int participantsNum = extras.getInt("EXTRA_PART_NUM");
                    int distance = extras.getInt("EXTRA_DISTANCE");

                    Card newCard = CardFactory.getCard(whatTimeLabel, day, month, year, name, address, participantsNum, distance);
                    addCard(newCard);
                    adapterCards.notifyDataSetChanged();
                    recyclerCards.smoothScrollToPosition(dataCards.size());

                }
            }
        }
    }

}
