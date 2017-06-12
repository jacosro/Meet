package dds.project.meet.presentation;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import dds.project.meet.R;
import dds.project.meet.logic.Card;
import dds.project.meet.logic.CardAdapter;
import dds.project.meet.logic.CardFactory;
import dds.project.meet.logic.RecyclerItemClickListener;
import dds.project.meet.logic.command.AddCardCommand;
import dds.project.meet.logic.command.Command;
import dds.project.meet.logic.command.NewCardCommand;
import dds.project.meet.logic.command.RemoveCardCommand;
import dds.project.meet.logic.memento.CareTaker;
import dds.project.meet.logic.memento.Originator;
import dds.project.meet.persistence.QueryCallback;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    // UI elements
    private TextView numberCards;
    private FloatingActionButton fab;
    private RecyclerView recyclerCards;
    private CardAdapter adapterCards;
    private RecyclerView.LayoutManager layoutManagerCards;
    private ImageView noEvents;
    private View tabBar;
    private View coloredBackgroundView;
    private View toolbarContainer;
    private Toolbar toolbar;


    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ConstraintLayout background;

    // Class fields
    private Originator originator;
    private CareTaker careTaker;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        recyclerCards = (RecyclerView) findViewById(R.id.recycler_cards);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        numberCards = (TextView) findViewById(R.id.numberCards);
        noEvents = (ImageView) findViewById(R.id.noEvents);
        background = (ConstraintLayout) findViewById(R.id.back);
        tabBar = findViewById(R.id.fake_tab);
        coloredBackgroundView = findViewById(R.id.colored_background_view);
        toolbarContainer = findViewById(R.id.toolbar_vbox);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        drawerLayout.addDrawerListener(toggle);

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();

        //Memento tools declaration
        originator = new Originator();
        careTaker = new CareTaker();



        //Initalize RecyclerView
        recyclerCards.setHasFixedSize(true);
        layoutManagerCards = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerCards.setLayoutManager(layoutManagerCards);
        adapterCards = new CardAdapter();
        recyclerCards.setAdapter(adapterCards);

        recyclerCards.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Card c = adapterCards.get(position);
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

        setupToolbar();
        setupRecyclerView();
        refreshUI();
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        return false;
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }
    private void setupRecyclerView() {

        recyclerCards.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                scrollColoredViewParallax(dy);
                if (dy > 0) {
                    hideToolbarBy(dy);
                } else {
                    showToolbarBy(dy);
                }
            }
            private void scrollColoredViewParallax(int dy) {
                coloredBackgroundView.setTranslationY(coloredBackgroundView.getTranslationY() - dy / 3);
            }
            private void hideToolbarBy(int dy) {
                if (cannotHideMore(dy)) {
                    toolbarContainer.setTranslationY(-tabBar.getBottom());
                } else {
                    toolbarContainer.setTranslationY(toolbarContainer.getTranslationY() - dy);
                }
            }
            private boolean cannotHideMore(int dy) {
                return Math.abs(toolbarContainer.getTranslationY() - dy) > tabBar.getBottom();
            }
            private void showToolbarBy(int dy) {
                if (cannotShowMore(dy)) {
                    toolbarContainer.setTranslationY(0);
                } else {
                    toolbarContainer.setTranslationY(toolbarContainer.getTranslationY() - dy);
                }
            }
            private boolean cannotShowMore(int dy) {
                return toolbarContainer.getTranslationY() - dy > 0;
            }
        });
    }

    private ItemTouchHelper.Callback createHelperCallback(){
        ItemTouchHelper.SimpleCallback simpleCallback =
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        return true;
                    }

                    @Override
                    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                        Card aux = adapterCards.get(viewHolder.getAdapterPosition());
                        String name = aux.getName();

                        originator.setState(aux);
                        careTaker.add(originator.saveStateToMemento());

                        deleteCard(viewHolder.getAdapterPosition());
                        Snackbar undoDelete = Snackbar.make(findViewById(R.id.drawer_layout), "\"" + name + "\"" + " deleted" , Snackbar.LENGTH_LONG);
                        undoDelete.setAction("RESTORE", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new NewCardCommand(adapterCards, originator.getState()).execute();
                                careTaker.undo();
                            }
                        });
                        undoDelete.show();
                    }
            };
            return simpleCallback;
    }

    private void addCard(Card card) {
        Command addCard = new AddCardCommand(adapterCards, card);
        addCard.execute();
        refreshUI();
    }

    private void deleteCard(int adapterPosition) {
        Command deleteCard = new RemoveCardCommand(adapterCards, adapterPosition);
        deleteCard.execute();
        refreshUI();
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

    public void loadCards() {
        /*
        Card one = new Card("10:30", 12 , 3 , 2016 , "Cena Montaditos", "Av.Blasco Ibañez", 5, 5);
        Card two = new Card("14:55", 17 , 5 , 2017 , "Comida La Vella", "ETSINF UPV", 7, 2);
        Command addCard = new AddCardCommand(recyclerCards.getAdapter(), dataCards, one);
        addCard.execute();

        Command addCard2 = new AddCardCommand(recyclerCards.getAdapter(), dataCards, two);
        addCard2.execute();
        */
        showProgressDialog();
        mPersistence.cardDAO.getAllCards(new QueryCallback<Card>() {
            @Override
            public void result(Card data) {
                if (data != null) {
                    addCard(data);
                } else {
                    hideProgressDialog();
                }
            }
        });
    }

    public void createCard(View v) {
        Intent intent = new Intent(this, CreateNewEventActivity.class);
        startActivity(intent);
    }

    public void openEvent(Card card) {
        Intent intent = new Intent(this, EventActivity.class);

        /*
        intent.putExtra("EXTRA_NAME", card.getName());
        intent.putExtra("EXTRA_LOCATION", card.getLocation());
        intent.putExtra("EXTRA_TIME", card.getTime());
        intent.putExtra("EXTRA_LOCATION", card.getLocation());
        intent.putExtra("EXTRA_DATE_DAY", card.getDateDay());
        intent.putExtra("EXTRA_DATE_MONTH", card.getDateMonth());
        intent.putExtra("EXTRA_DATE_YEAR", card.getDateYear());
        */

        intent.putExtra("key", card.getDbKey());

        startActivity(intent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.nav_home:
                Toast.makeText(this, "Home", Toast.LENGTH_LONG).show();
                break;
            case R.id.nav_spaces:
                Toast.makeText(this, "Spaces", Toast.LENGTH_LONG).show();
                break;
            case R.id.nav_contacts:
                Toast.makeText(this, "Contacts", Toast.LENGTH_LONG).show();
                break;
            case R.id.nav_focus:
                Toast.makeText(this, "Focus", Toast.LENGTH_LONG).show();
                break;
            case R.id.nav_logout:
                mPersistence.userDAO.doSignOut();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    private void refreshUI() {
        numberCards.setText(adapterCards.getItemCount() + " upcoming event(s)");

        if(adapterCards.getItemCount() > 0) {
            noEvents.setVisibility(View.GONE);
            background.setBackgroundResource(R.drawable.back);
        } else {
            numberCards.setText("No events. No one loves you");
            noEvents.setVisibility(View.VISIBLE);
            background.setBackgroundResource(R.drawable.full);
        }
    }
}
