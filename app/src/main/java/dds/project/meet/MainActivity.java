package dds.project.meet;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private RecyclerView recyclerCards;
    public static RecyclerView.Adapter adapterCards;
    private RecyclerView.LayoutManager layoutManagerCards;

    static TextView numberCards;
    static ArrayList<Card> dataCards;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerCards = (RecyclerView) findViewById(R.id.recycler_cards);

        dataCards = new ArrayList<Card>();

        loadCards();

        recyclerCards.setHasFixedSize(true);
        layoutManagerCards = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerCards.setLayoutManager(layoutManagerCards);

        adapterCards = new CardAdapter(dataCards, this);
        recyclerCards.setAdapter(adapterCards);

        recyclerCards.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        System.out.println(position);
                        Card c = dataCards.get(position);
                        openEvent(c);
                    }
                })
        );

        TextView numberCards = (TextView) findViewById(R.id.numberCards);
        numberCards.setText(dataCards.size() + " events waiting for you");
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

    public void nextScreen(View view) {
        Intent intent = new Intent(this, CreateNewEvent.class);
        startActivity(intent);
    }

    public void loadCards() {
        dataCards.add(new Card("10:30", "21 Mar." , "Cena Montaditos", "Av.Blasco Ibañez", 5, 5));
    }

    public void createCard(View v) {
        Intent intent = new Intent(this, CreateNewEvent.class);
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
