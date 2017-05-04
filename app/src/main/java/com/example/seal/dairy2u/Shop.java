package com.example.seal.dairy2u;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Shop extends AppCompatActivity {
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private Typeface mainfont, textfont;

    private DatabaseReference mDatabase;
    private ValueEventListener mPostListener;
    static List<Item> productList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        productList.clear();

        // --- Custom typefaces ---
        mainfont = Typeface.createFromAsset(getAssets(), "fonts/Neon.ttf");
        textfont = Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Regular.ttf");

        mDatabase = FirebaseDatabase.getInstance().getReference().child("farmers");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // --- Add single value event listener to the post ---
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // --- Get all users and add to Map ---
                collectAllItems((Map<String,Object>) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Shop.this, "Failed to load items.", Toast.LENGTH_SHORT).show();
            }
        };
        mDatabase.addListenerForSingleValueEvent(postListener);

        // --- Keep copy of post listener so we can remove it when app stops ---
        mPostListener = postListener;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_shop, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            switch (position) {
                case 0:
                    return PlaceholderFragment.newInstance(0);
                case 1:
                    return PlaceholderFragment.newInstance(1);
                case 2:
                    return PlaceholderFragment.newInstance(2);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 6 total pages.
            return 6;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position){
                case 0:
                    return "Milk";
                case 1:
                    return "Drinks";
                case 2:
                    return "Cheese";
                case 3:
                    return "Butter";
                case 4:
                    return "Cream";
                case 5:
                    return "Yogurt";
            }
            return null;
        }
    }

    private void collectAllItems(Map<String,Object> users) {

        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()){

            // --- Get user map
            Map singleUser = (Map) entry.getValue();
            // --- Get item field and append to list
            Map itemList = (Map) singleUser.get("items");

            getAllItems(itemList);
        }
    }

    // --- RETRIEVE ALL INDIVIDUAL ITEM VALUES FROM MAP ---
    private void getAllItems(Map<String,Object> itemList) {
        // --- iterate through each user, ignoring their UID ---
        for (Map.Entry<String, Object> entry : itemList.entrySet()){

            // --- Get item map ---
            Map singleItem = (Map) entry.getValue();
            // --- Get individual value ---
            String name = (String)singleItem.get("name");
            String price = (String)singleItem.get("price");
            String type = (String)singleItem.get("type");

            // --- Create event object, add to list ---
            productList.add(new Item(name,price,type));
        }
    }
}
