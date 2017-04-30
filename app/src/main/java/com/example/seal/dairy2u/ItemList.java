package com.example.seal.dairy2u;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
            String type = (String)singleItem.get("type");

            // --- Create event object, add to list ---
            items.add(new Item(name,price,type));
        }

        // --- For each event in list, create a row --
        for(Item item: items){
            createItem(item.name, item.price, item.type);
        }
    }


    //--- LIST ITEMS ---
    public void createItem(String name, String price, String type){
        LinearLayout container = new LinearLayout(this);
        TextView iName_field = new TextView(this);
        TextView iPrice_field = new TextView(this);

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


        // --- Set item type icon ---
        switch (type){
            case "Butter":
                container.addView(createIcons(R.drawable.ic_butter));
                break;
            case "Cheese":
                container.addView(createIcons(R.drawable.ic_cheese));
                break;
            case "Cream":
                container.addView(createIcons(R.drawable.ic_cream));
                break;
            case "Drinks":
                container.addView(createIcons(R.drawable.ic_drink));
                break;
            case "Milk":
                container.addView(createIcons(R.drawable.ic_milk));
                break;
            case "Yogurt":
                container.addView(createIcons(R.drawable.ic_yogurt));
                break;
        }

        // --- Set item name attributes ---
        iName_field.setText(name);
        iName_field.setTextColor(Color.BLACK);
        iName_field.setTypeface(textfont);
        iName_field.setLayoutParams(new ActionBar.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        iName_field.setWidth(500);
        iName_field.setHeight(180);
        iName_field.setTextSize(18);
        iName_field.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        iName_field.setBackgroundColor(Color.TRANSPARENT);


        // --- Format price to 2 decimal points for display
        DecimalFormat df = new DecimalFormat("#.00");
        double fPrice = Double.parseDouble(price);
        price = df.format(fPrice);

        // --- Set item price attributes ---
        iPrice_field.setText("RM" + price);
        iPrice_field.setTextColor(Color.BLACK);
        iPrice_field.setTypeface(textfont);
        iPrice_field.setLayoutParams(new ActionBar.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        iPrice_field.setWidth(500);
        iPrice_field.setTextSize(18);
        iPrice_field.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        iPrice_field.setBackgroundColor(Color.TRANSPARENT);

        container.addView(iName_field);
        container.addView(iPrice_field);
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
                final Spinner iType = (Spinner) mView.findViewById(R.id.item_type);
                ArrayAdapter adapter = ArrayAdapter.createFromResource(ItemList.this, R.array.item_arrays, R.layout.spinner_item);
                iType.setAdapter(adapter);

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
                        String newType = String.valueOf(iType.getSelectedItem());

                        toDatabase(newName, newPrice, newType);
                        dialog.dismiss();
                        createItem(newName, newPrice, newType);
                    }
                });


            } else if  (i == R.id.b_logout){
                mAuth.signOut();
                startActivity(new Intent(ItemList.this, LogIn.class));
            }
        }
    }


    //--- Create guest feedback icons
    public ImageView createIcons(int ic_url){
        ImageView icon = new ImageView(this);
        icon.setImageResource(ic_url);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        params.height = 180;
        params.width = 100;
        icon.setPadding(0,0,20,0);
        icon.setLayoutParams(params);
        return icon;
    }



    // --- WRITE NEW ITEM TO DATABASE ---
    private void toDatabase(String name, String price, String type) {
        Item item = new Item(name, price, type);
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
