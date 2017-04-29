package com.example.seal.dairy2u;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemList extends AppCompatActivity {
    private TextView mainTitle;
    private ImageButton b_addEvent, b_logout;
    private LinearLayout ll;
    private int buttonID;

    private Typeface mainfont;

    private FirebaseAuth mAuth;
    FirebaseUser user;
    private DatabaseReference mDatabase;
    private ValueEventListener mPostListener;
    List<Item> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        items.clear();

        //Custom typefaces
        mainfont = Typeface.createFromAsset(getAssets(), "fonts/Neon.ttf");

        //Set title font
        mainTitle = (TextView) findViewById(R.id.title);
        mainTitle.setTypeface(mainfont);

        //Image button
        b_logout = (ImageButton) findViewById(R.id.b_logout);
        b_addEvent = (ImageButton) findViewById(R.id.b_addEvent);
        b_addEvent.setOnClickListener(new ItemList.ButtonHandler());
        b_logout.setOnClickListener(new ItemList.ButtonHandler());

        // Initialize Database
        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("farmers").child(user.getUid());

        ll = (LinearLayout)findViewById(R.id.ItemList);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Add value event listener to the post
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get all events and have it sorted
                if (dataSnapshot.hasChild("items")) {
                    getAllItems((Map<String, Object>) dataSnapshot.child("items").getValue());
                } else {
                    Toast.makeText(ItemList.this, "No items", Toast.LENGTH_SHORT).show();
                    /*noItems.setText("You have no items in your list.");

                    noItems.setLayoutParams(new ActionBar.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    noItems.setWidth(500);
                    noItems.setTextSize(20);
                    noItems.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                    noItems.setBackgroundColor(Color.TRANSPARENT);

                    ll.addView(noItems);*/
                    //startActivity(new Intent(ItemList.this, Event_Add.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ItemList.this, "Failed to load items.", Toast.LENGTH_SHORT).show();
            }
        };
        mDatabase.addValueEventListener(postListener);

        // Keep copy of post listener so we can remove it when app stops
        mPostListener = postListener;

    }

    private void getAllItems(Map<String,Object> itemList) {
        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : itemList.entrySet()){

            //Get item map
            Map singleItem = (Map) entry.getValue();
            //Get individual value
            String name = (String)singleItem.get("name");
            String price = (String)singleItem.get("price");

            //Create event object, add to list
            items.add(new Item(name,price));
        }

        //For each event in list, create a row
        for(Item item: items){
            createItem(item.name, item.price);
        }
    }

    //--- LIST ITEMS ---
    public void createItem(String name, String price){

        LinearLayout container = new LinearLayout(this);
        TextView newItem = new TextView(this);
        TextView iPrice = new TextView(this);

        container.setLayoutParams(new ActionBar.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        container.setOrientation(LinearLayout.HORIZONTAL);

        newItem.setText(name);
        newItem.setLayoutParams(new ActionBar.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        newItem.setWidth(500);
        newItem.setTextSize(20);
        newItem.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        newItem.setBackgroundColor(Color.TRANSPARENT);

        iPrice.setText(price);
        iPrice.setLayoutParams(new ActionBar.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        iPrice.setWidth(300);
        iPrice.setTextSize(12);
        iPrice.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        iPrice.setBackgroundColor(Color.TRANSPARENT);

        container.addView(newItem);
        container.addView(iPrice);
        ll.addView(container);
    }


    class ButtonHandler implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            int i = v.getId();
            if (i == R.id.b_addEvent) {

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(ItemList.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_item_creation, null);
                final TextView iName = (TextView) mView.findViewById(R.id.item_name);
                final TextView iPrice = (TextView) mView.findViewById(R.id.item_price);
                Button addButton = (Button) mView.findViewById(R.id.b_create);

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                addButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        toDatabase(iName.getText().toString(), iPrice.getText().toString());
                        dialog.dismiss();
                    }
                });


            } else if  (i == R.id.b_logout){
                mAuth.signOut();
                startActivity(new Intent(ItemList.this, LogIn.class));
            }
        }
    }

    //--- Button listener for each Event ---
    /*View.OnClickListener buttonClicked(final Button button)  {
        return new View.OnClickListener() {
            public void onClick(View v) {
                String criteria = button.getText().toString().trim();
                Item passEvent = new Item();
                for(Item event: events){
                    if(criteria == event.title){
                        passEvent = event;
                    }
                }

                //--- Pass event object to next activity
                Intent intent = new Intent(ItemList.this, Event_Page.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("eventObj",passEvent);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        };
    }*/

    //--- ADD ITEM DETAILS TO DATABASE
    private void toDatabase(String name, String price) {
        Item item = new Item(name, price);
        mDatabase.child("items").child(name).setValue(item);
    }

    @Override
    public void onBackPressed(){
        /*Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);*/
    }
}
