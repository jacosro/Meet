package dds.project.meet.presentation;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.sql.Timestamp;
import java.util.ArrayList;

import dds.project.meet.R;
import dds.project.meet.logic.Card;
import dds.project.meet.logic.CardD;
import dds.project.meet.logic.CardTrashAdapter;
import dds.project.meet.logic.RecyclerItemClickListener;
import dds.project.meet.logic.memento.CareTaker;
import dds.project.meet.logic.memento.Memento;
import dds.project.meet.logic.memento.Originator;

public class TrashActivity extends BaseActivity {

    // UI elements
    private FloatingActionButton fab;
    private RecyclerView recyclerCardsTrash;
    public  RecyclerView.Adapter adapterCardsTrash;
    private RecyclerView.LayoutManager layoutManagerCards;
    private ImageView noEvents;
    private View tabBar;
    private View coloredBackgroundView;
    private View toolbarContainer;
    private View toolbar;

    private DrawerLayout drawerLayout;
    private ConstraintLayout background;

    // Class fields
    private ArrayList<CardD> dataCardsTrash;
    private Originator originator;
    private CareTaker careTaker;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trash_activity);

        recyclerCardsTrash = (RecyclerView) findViewById(R.id.recycler_cards);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        noEvents = (ImageView) findViewById(R.id.noEvents);
        background = (ConstraintLayout) findViewById(R.id.back);
        tabBar = findViewById(R.id.fake_tab);
        coloredBackgroundView = findViewById(R.id.colored_background_view);
        toolbarContainer = findViewById(R.id.toolbar_vbox);
        toolbar = findViewById(R.id.toolbar);
        dataCardsTrash = new ArrayList<CardD>();



        //Memento tools declaration
        originator = new Originator();
        careTaker = new CareTaker();



        //Initalize RecyclerView
        recyclerCardsTrash.setHasFixedSize(true);
        layoutManagerCards = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerCardsTrash.setLayoutManager(layoutManagerCards);
        adapterCardsTrash = new CardTrashAdapter(dataCardsTrash, this);
        recyclerCardsTrash.setAdapter(adapterCardsTrash);

        recyclerCardsTrash.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        CardD c = dataCardsTrash.get(position);
                        new AlertDialog.Builder(TrashActivity.this)
                                .setTitle("Closing application")
                                .setMessage("Are you sure you want to restore?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Result
                                    }
                                }).setNegativeButton("No", null).show();

                    }
                })
        );

        //Gestures RecyclerView


        loadCards();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCard(v);
            }
        });


        setupToolbar();
        setupRecyclerView();

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

        recyclerCardsTrash.setOnScrollListener(new RecyclerView.OnScrollListener() {
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


    private void addCard(CardD card) {


    }

    private void deleteCard(int adapterPosition) {

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


    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
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
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void loadCards() {
        int index = 0;

        Card one = new Card("10:30", 12 , 3 , 2016 , "Cena Montaditos", "Av.Blasco Ibañez", 5, 5, );
        Card two = new Card("10:30", 12 , 3 , 2016 , "Cena Montaditos", "Av.Blasco Ibañez", 5, 5, );
        CardD oneD = new CardD(one, new Timestamp(System.currentTimeMillis()));
        CardD twoD = new CardD(two, new Timestamp(System.currentTimeMillis()));
        dataCardsTrash.add(oneD);
        dataCardsTrash.add(twoD);

        while(careTaker.getMemoListSize() > 0) {
            Memento x = careTaker.get(index);
            CardD adder = new CardD(x.getState(), x.getTimeStamp());
            dataCardsTrash.add(adder);
            index++;
        }
        adapterCardsTrash.notifyDataSetChanged();
    }

    public void createCard(View v) {

    }

}
