package dds.project.meet.presentation;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;

import dds.project.meet.R;
import dds.project.meet.logic.adapters.EventAdapter;
import dds.project.meet.logic.commands.AddCardCommand;
import dds.project.meet.logic.commands.Command;
import dds.project.meet.logic.commands.NewCardCommand;
import dds.project.meet.logic.commands.RemoveCardCommand;
import dds.project.meet.logic.entities.Event;
import dds.project.meet.logic.entities.User;
import dds.project.meet.logic.memento.CareTaker;
import dds.project.meet.logic.memento.Originator;
import dds.project.meet.logic.util.GLocation;
import dds.project.meet.logic.util.MyLocation;
import dds.project.meet.logic.util.ProfileImage;
import dds.project.meet.logic.util.RecyclerItemClickListener;
import dds.project.meet.logic.util.TimeDistance;
import dds.project.meet.persistence.util.QueryCallback;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private static final int PICK_IMAGE_REQUEST = 1;
    // UI elements
    private TextView numberEvents;
    private FloatingActionButton fab;
    private RecyclerView recyclerEvents;
    private EventAdapter adapterEvents;
    private RecyclerView.LayoutManager layoutManagerEvents;
    private ImageView noEvents;
    private View tabBar;
    private View coloredBackgroundView;
    private View toolbarContainer;
    private Toolbar toolbar;
    private TextView nameDrawer;
    private TextView emailDrawer;
    private ImageButton refreshEvents;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ConstraintLayout background;

    private View header;
    private View avatarHolder;
    private ImageView avatar;

    private ProfileImage mProfileImage;


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

        header = navigationView.getHeaderView(0);
        avatarHolder = header.findViewById(R.id.smallImageContainer);
        nameDrawer = (TextView) header.findViewById(R.id.nameTextViewDrawer);
        emailDrawer = (TextView) header.findViewById(R.id.emailTextViewDrawer);
        avatar = (CircleImageView) header.findViewById(R.id.avatar);

        // Set NavigationDrawer data
        // Set Profile image
        mProfileImage = ProfileImage.getInstance(this);
        mProfileImage.get(new QueryCallback<Uri>() {
            @Override
            public void result(Uri data) {
                loadAvatar(data);
            }
        });
        // Set Name and email
        User me = mPersistence.userDAO.getCurrentUser();
        emailDrawer.setText(me.getEmail());
        nameDrawer.setText(me.getName());


        recyclerEvents = (RecyclerView) findViewById(R.id.recycler_cards);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        numberEvents = (TextView) findViewById(R.id.numberCards);
        noEvents = (ImageView) findViewById(R.id.noEvents);
        background = (ConstraintLayout) findViewById(R.id.back);
        tabBar = findViewById(R.id.fake_tab);
        coloredBackgroundView = findViewById(R.id.colored_background_view);
        toolbarContainer = findViewById(R.id.toolbar_vbox);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        refreshEvents = (ImageButton) findViewById(R.id.refreshCardsButton);

        //Memento tools declaration
        originator = new Originator();
        careTaker = new CareTaker();


        setListeners();
        initializeRecyclerView();
        setupToolbar();
        setupRecyclerView();
        refreshUI();
    }

    private void setListeners() {

        //Navigation View
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();

        //Parallax Effect
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        drawerLayout.addDrawerListener(toggle);

        avatarHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCard(v);
            }
        });

        refreshEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float deg = refreshEvents.getRotation() + 720F;
                refreshEvents.animate().rotation(deg).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(800);
                loadEvents();
                refreshUI();

            }
        });

        mPersistence.eventDAO.setListenerForUserRemoved(new QueryCallback<String>() {
            @Override
            public void result(String data) {
                if (data != null) {
                    int pos = adapterEvents.removeItemWithId(data);
                    if (pos > -1) {
                        adapterEvents.notifyItemRemoved(pos);
                    }
                }
            }
        });
    }

    private void initializeRecyclerView() {
        recyclerEvents.setHasFixedSize(true);
        layoutManagerEvents = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerEvents.setLayoutManager(layoutManagerEvents);
        adapterEvents = new EventAdapter();
        recyclerEvents.setAdapter(adapterEvents);

        recyclerEvents.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Event c = adapterEvents.get(position);
                        openEvent(c);
                    }
                })
        );

        //Gestures RecyclerView
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(createHelperCallback());
        itemTouchHelper.attachToRecyclerView(recyclerEvents);
    }

    //Auxiliar methods
    public void createCard(View v) {
        Intent intent = new Intent(this, CreateNewEventActivity.class);
        startActivity(intent);
    }

    public void openEvent(Event event) {
        Intent intent = new Intent(this, EventActivity.class);
        intent.putExtra("key", event.getDbKey());
        startActivity(intent);
    }

    //
    @Override
    public void onStart() {
        super.onStart();
        loadEvents();
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        return false;
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
    }

    private void setupRecyclerView() {

        recyclerEvents.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

    //Handle RecyclerView Gestures
    private ItemTouchHelper.Callback createHelperCallback(){
        ItemTouchHelper.SimpleCallback simpleCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        return true;
                    }

                    @Override
                    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                        Event aux = adapterEvents.get(viewHolder.getAdapterPosition());
                        String name = aux.getName();

                        originator.setState(aux);
                        careTaker.add(originator.saveStateToMemento());

                        deleteCard(viewHolder.getAdapterPosition());
                        Snackbar undoDelete = Snackbar.make(findViewById(R.id.drawer_layout), "\"" + name + "\"" + " deleted" , Snackbar.LENGTH_LONG);
                        undoDelete.setAction("RESTORE", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new NewCardCommand(adapterEvents, originator.getState()).execute();
                                careTaker.undo();
                                refreshUI();
                            }
                        });
                        undoDelete.show();
                    }
            };
            return simpleCallback;
    }

    private void addCardToUI(Event event) {
        Command addCard = new AddCardCommand(adapterEvents, event);
        addCard.execute();
        refreshUI();
    }

    private void deleteCard(int adapterPosition) {
        Command deleteCard = new RemoveCardCommand(adapterEvents, adapterPosition);
        deleteCard.execute();
        refreshUI();
    }

    private void refreshUI() {
        numberEvents.setText(adapterEvents.getItemCount() + " upcoming event(s)");

        if(adapterEvents.getItemCount() > 0) {
            noEvents.setVisibility(View.GONE);
            background.setBackgroundResource(R.drawable.default_background);
        } else {
            numberEvents.setText("No events. No one loves you");
            noEvents.setVisibility(View.VISIBLE);
            background.setBackgroundResource(R.drawable.default_background_full);
        }
    }

    private void refreshKm() {
        for (int i = 0; i < adapterEvents.getItemCount(); i++) {
            refreshKm(adapterEvents.get(i));
        }
    }

    private void refreshKm(final Event event) {
        final int pos = adapterEvents.indexOf(event);

        try {
            final GLocation location = new GLocation(this, event.getLocation());

            MyLocation.get(this, new QueryCallback<LatLng>() {
                @Override
                public void result(LatLng data) {
                    int km = (int) TimeDistance.calculateDistanceBetween(data, location.getLocation());
                    event.setKm(km);
                    adapterEvents.notifyItemChanged(pos, km + " km");
                }
            });
        } catch (IOException e) {
            Log.e(TAG, "Error getting location: " + e);
        }
    }

    //Sidebar Handler
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sidebar, menu);
        return true;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.nav_home:
                Toast.makeText(this, "You are on homescreen", Toast.LENGTH_LONG).show();
                break;
            case R.id.nav_spaces:
                Toast.makeText(this, "\"Spaces\" is coming soon", Toast.LENGTH_LONG).show();
                break;
            case R.id.nav_contacts:
                Toast.makeText(this, "\"Contacts\" is coming soon", Toast.LENGTH_LONG).show();
                break;
            case R.id.nav_focus:
                Toast.makeText(this, "\"Focus\" is coming soon", Toast.LENGTH_LONG).show();
                break;
            case R.id.nav_logout:
                mPersistence.userDAO.doSignOut();
                mProfileImage.clearCache();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.app_version:
                Toast.makeText(this, getString(R.string.app_name), Toast.LENGTH_SHORT).show();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    //Firebase Handler
    public void loadEvents() {
        mPersistence.eventDAO.getAllEvents(new QueryCallback<Event>() {
            @Override
            public void result(Event data) {
                if (data != null) {
                    addCardToUI(data);
                    recyclerEvents.smoothScrollToPosition(0);
                    refreshKm(data);
                }
            }
        });
    }

    public void loadAvatar(Uri uri) {
        Glide.with(this)
                .load(uri)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .centerCrop()
                .into(avatar);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                Uri imageUri = data.getData();
                if (!imageUri.getLastPathSegment().endsWith(".jpg")) {
                    Toast.makeText(this, "Invalid image type! Use jpg", Toast.LENGTH_SHORT).show();
                } else {
                    mProfileImage.set(imageUri);
                    loadAvatar(imageUri);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MyLocation.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    refreshKm();
                }
        }
    }
}
