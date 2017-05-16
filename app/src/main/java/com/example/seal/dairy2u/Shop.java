package com.example.seal.dairy2u;

import android.graphics.Typeface;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Shop extends AppCompatActivity {
    private ViewPager mViewPager;

    private Typeface mainfont, textfont;

    private DatabaseReference mDatabase;
    private ValueEventListener mPostListener;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int[] tabIcons = {
            R.drawable.ic_milk_white, //Includes milk and drinks
            R.drawable.ic_butter_white, //Includes butter
            R.drawable.ic_cheese_white,
            R.drawable.ic_yogurt_white
    };
    static List<Item> productList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        productList.clear();

        // --- Custom typefaces ---
        mainfont = Typeface.createFromAsset(getAssets(), "fonts/Neon.ttf");
        textfont = Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Regular.ttf");

        //mDatabase = FirebaseDatabase.getInstance().getReference().child("farmers");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.logo);

        viewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
    }

    /*@Override
    public void onStart() {
        super.onStart();
        //--- Add single value event listener to the post ---
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
    }*/

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new Fragment_Drinks());
        adapter.addFrag(new Fragment_Drinks());
        adapter.addFrag(new Fragment_Drinks());
        adapter.addFrag(new Fragment_Drinks());
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment) {
            mFragmentList.add(fragment);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }


    /*private void collectAllItems(Map<String,Object> users) {

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
    }*/
}
