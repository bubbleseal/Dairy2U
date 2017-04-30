package com.example.seal.dairy2u;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemList extends AppCompatActivity {
    private TextView mainTitle;
    private ImageButton b_addEvent, b_logout;
    private LinearLayout ll;
    private int buttonID;
    String LOG = "Spot Tag: ";

    private Typeface mainfont, textfont;

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

        // --- Custom typefaces ---
        mainfont = Typeface.createFromAsset(getAssets(), "fonts/Neon.ttf");
        textfont = Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Regular.ttf");

        // --- Set title font ---
        mainTitle = (TextView) findViewById(R.id.title);
        mainTitle.setTypeface(mainfont);

        // --- Image button ---
        b_logout = (ImageButton) findViewById(R.id.b_logout);
        b_addEvent = (ImageButton) findViewById(R.id.b_addEvent);
        b_addEvent.setOnClickListener(new ItemList.ButtonHandler());
        b_logout.setOnClickListener(new ItemList.ButtonHandler());

        // --- Initialize Database ---
        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("farmers").child(user.getUid());

        ll = (LinearLayout)findViewById(R.id.ItemList);

        mAuth = FirebaseAuth.getInstance();
    }


    @Override
    public void onStart() {
        super.onStart();
        // --- Add single value event listener to the post ---
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // --- Get all items and add to Map ---
                if (dataSnapshot.hasChild("items")) {
                    getAllItems((Map<String, Object>) dataSnapshot.child("items").getValue());
                    //mDatabase.removeEventListener(this);
                } else {
                    Toast.makeText(ItemList.this, "No items", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ItemList.this, "Failed to load items.", Toast.LENGTH_SHORT).show();
            }
        };
        mDatabase.addListenerForSingleValueEvent(postListener);

        // --- Keep copy of post listener so we can remove it when app stops ---
        mPostListener = postListener;

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

            // --- Create event object, add to list ---
            items.add(new Item(name,price));
        }

        // --- For each event in list, create a row --
        for(Item item: items){
            createItem(item.name, item.price);
        }
    }


    //--- LIST ITEMS ---
    public void createItem(String name, String price){
        LinearLayout container = new LinearLayout(this);
        TextView newItem = new TextView(this);
        TextView iPrice = new TextView(this);

        // --- Set linear layout attributes ---
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        container.setOrientation(LinearLayout.HORIZONTAL);
        lp.setMargins(20, 10, 20, 10);
        container.setLayoutParams(lp);
        container.setPadding(60,0,60,0);
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(0xFFffffff);
        container.setBackground(gd);

        // --- Set item name attributes ---
        newItem.setText(name);
        newItem.setTextColor(Color.BLACK);
        newItem.setTypeface(textfont);
        newItem.setLayoutParams(new ActionBar.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        newItem.setWidth(500);
        newItem.setHeight(180);
        newItem.setTextSize(18);
        newItem.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        newItem.setBackgroundColor(Color.TRANSPARENT);

        // --- Format price to 2 decimal points for display
        DecimalFormat df = new DecimalFormat("#.00");
        double fPrice = Double.parseDouble(price);
        price = df.format(fPrice);

        // --- Set item price attributes ---
        iPrice.setText("RM" + price);
        iPrice.setTextColor(Color.BLACK);
        iPrice.setTypeface(textfont);
        iPrice.setLayoutParams(new ActionBar.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        iPrice.setWidth(500);
        iPrice.setTextSize(18);
        iPrice.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
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
                // --- Create a dialog box for adding a new item ---
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
                        // --- Add new item to database and preview list ---
                        String newName = iName.getText().toString();
                        String newPrice = iPrice.getText().toString();
                        toDatabase(newName, newPrice);
                        dialog.dismiss();
                        createItem(newName, newPrice);
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


    // --- WRITE NEW ITEM TO DATABASE ---
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
