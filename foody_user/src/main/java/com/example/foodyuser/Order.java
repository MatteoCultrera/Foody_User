package com.example.foodyuser;

import android.animation.LayoutTransition;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class Order extends AppCompatActivity {

    private ArrayList<OrderItem> orders, copyOrders;
    private RVAdapterOrder adapter;
    private RecyclerView ordersList;
    private TextView total;
    private ImageView backButton;
    private MaterialButton placeOrder;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private ConstraintLayout mainLayout, notesLayout;
    private TextView time, noteTitle, notes, editButton, deleteButton;
    private ImageButton pickTime;
    private EditText input;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor edit;
    private String delivAddress;
    private boolean hasNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        init();
    }

    public void init(){
        sharedPref = this.getSharedPreferences("myPreference", MODE_PRIVATE);
        delivAddress = sharedPref.getString("delivery_address", "");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        backButton = findViewById(R.id.backButton);
        placeOrder = findViewById(R.id.place_order);
        time = findViewById(R.id.delivery_time);
        mainLayout = findViewById(R.id.price_layout);
        mainLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        notesLayout = findViewById(R.id.notesLayout);
        notesLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        noteTitle = findViewById(R.id.noteTitle);
        notes = findViewById(R.id.notes);
        editButton = findViewById(R.id.edit_button);
        deleteButton = findViewById(R.id.delete_button);
        pickTime = findViewById(R.id.chooseTime);
        JsonHandler handler =  new JsonHandler();
        final File directory = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File orderFile = new File(directory, getString(R.string.order_file_name));
        Bundle extras = getIntent().getExtras();
        final String restID = extras.getString("restaurantID","");
        final String restName = extras.getString("restaurantName", "");
        final String restAddress = extras.getString("restaurantAddress", null);
        final String restTime = extras.getString("restaurantTime", "");

        orders = handler.getOrders(orderFile);
        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Inserting the reservation inside the user reservation DB
                final DatabaseReference database = FirebaseDatabase.getInstance().getReference()
                        .child("reservations").child("users").child(firebaseUser.getUid());
                HashMap<String, Object> child = new HashMap<>();
                copyOrders = orders;
                final String identifier = firebaseUser.getUid() + System.currentTimeMillis();
                Calendar calendar = Calendar.getInstance();
                Calendar calendar2 = Calendar.getInstance();
                calendar.add(Calendar.MINUTE, 40);
                calendar2.add(Calendar.MINUTE, 20);
                String deliveryTime = new SimpleDateFormat("HH:mm", Locale.UK).format(calendar.getTime());
                String bikerTime = new SimpleDateFormat("HH:mm", Locale.UK).format(calendar2.getTime());
                String notes;
                if(sharedPref.contains("notes"))
                    notes = sharedPref.getString("notes","");
                else
                    notes = null;
                final ReservationDBUser reservation = new ReservationDBUser(identifier, restID, copyOrders, false, notes,
                        deliveryTime, "Pending", total.getText().toString());
                reservation.setRestaurantName(restName);
                reservation.setRestaurantAddress(restAddress);
                child.put(identifier, reservation);
                database.updateChildren(child).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), R.string.error_order, Toast.LENGTH_SHORT).show();
                    }
                });

                //Inserting the new reservation inside the restaurant reservations DB
                DatabaseReference databaseRest = FirebaseDatabase.getInstance().getReference()
                        .child("reservations").child("restaurant").child(restID);
                HashMap<String, Object> childRest = new HashMap<>();
                ReservationDBRestaurant reservationRest = new ReservationDBRestaurant(identifier, "", copyOrders, false,
                        notes, sharedPref.getString("phoneNumber", null),
                        sharedPref.getString("name", null), deliveryTime, bikerTime, "Pending",
                        delivAddress, total.getText().toString());
                childRest.put(identifier, reservationRest);
                databaseRest.updateChildren(childRest).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), R.string.error_order, Toast.LENGTH_SHORT).show();
                    }
                });

                orders.clear();
                sharedPref.edit().remove("notes").apply();
                closeActivity();
            }
        });

        total = findViewById(R.id.total);
        ordersList = findViewById(R.id.order_list);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        ordersList.setLayoutManager(llm);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        adapter = new RVAdapterOrder(orders, this);
        ordersList.setAdapter(adapter);

        if(sharedPref.contains("notes")){
            hasNote = true;
            addNote(sharedPref.getString("notes",""));
        }else{
            hasNote = false;
            deleteNote();
        }


        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteNote();
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plusPressed(sharedPref.getString("notes",""));
            }
        });

        if(hasNote){
            editButton.setText("Edit Note");
        }

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPref.edit().putString("restTime", restTime).apply();
                BottomSheetFragment fragment = new BottomSheetFragment();
                fragment.show(getSupportFragmentManager(), fragment.getTag());
            }
        });

        updatePrice();
    }

    private void plusPressed(String starting){
        input = new EditText(editButton.getContext());
        input.setText(starting);
        final LinearLayout container = new LinearLayout(editButton.getContext());
        container.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(getResources().getDimensionPixelSize(R.dimen.short16), 0, getResources().getDimensionPixelSize(R.dimen.short16), 0);
        final AlertDialog.Builder builder = new AlertDialog.Builder(editButton.getContext(), R.style.AppCompatAlertDialogStyle);
        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(getResources().getString(R.string.accept), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               addNote(input.getText().toString());
            }
        });
        builder.setTitle(getResources().getString(R.string.insert_note));
        input.setLayoutParams(lp);
        builder.setCancelable(false);
        input.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setSingleLine(false);
        input.setLines(1);
        input.setMaxLines(3);
        container.addView(input, lp);
        builder.setView(container);
        final AlertDialog dialogDism = builder.create();
        Objects.requireNonNull(dialogDism.getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialogDism.show();
        dialogDism.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        dialogDism.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.secondaryText));
        WindowManager.LayoutParams layouts= new WindowManager.LayoutParams();
        layouts.width = WindowManager.LayoutParams.MATCH_PARENT;
        layouts.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layouts.dimAmount = 0.7f;
        dialogDism.getWindow().setAttributes(layouts);
        dialogDism.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogDism.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getColor(R.color.colorAccent));
        if(starting.length() == 0){
            dialogDism.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.colorPrimaryDark));
            dialogDism.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        }else{
            dialogDism.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.colorAccent));
            dialogDism.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
        }

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0) {
                    dialogDism.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    dialogDism.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.colorPrimaryDark));
                    dialogDism.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getColor(R.color.colorAccent));
                } else {
                    dialogDism.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    dialogDism.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.colorAccent));
                    dialogDism.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getColor(R.color.colorAccent));

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    public void addNote(String text){
        noteTitle.setVisibility(View.VISIBLE);
        notes.setVisibility(View.VISIBLE);
        notes.setText(text);
        editButton.setText("Edit");
        deleteButton.setVisibility(View.VISIBLE);
        hasNote = true;
        sharedPref.edit().putString("notes",text).apply();
    }

    public void deleteNote(){
        noteTitle.setVisibility(View.GONE);
        notes.setVisibility(View.GONE);
        editButton.setText("Add Notes");
        deleteButton.setVisibility(View.GONE);
        sharedPref.edit().remove("notes").apply();
        hasNote = false;
    }

    public void removeItem(int index){
        orders.remove(index);
        adapter.notifyItemRemoved(index);
        adapter.notifyItemRangeChanged(index, orders.size());
    }

    public void closeActivity(){
        if(orders.size() == 0){
            File directory = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            File orderFile = new File(directory, getString(R.string.order_file_name));
            if(orderFile.exists())
                orderFile.delete();
        }
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JsonHandler handler = new JsonHandler();
        if(orders!=null && orders.size() > 0){
            String jsonOrders = handler.ordersToJSON(orders);
            File directory = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            File toSave = new File(directory, getString(R.string.order_file_name));
            handler.saveStringToFile(jsonOrders, toSave);
        }
    }

    public void updatePrice(){
        float tp=0;

        for(int i = 0; i < orders.size(); i++)
            tp += orders.get(i).getTotal();

        tp+=RestaurantShow.getRestDeliveryPrice()*0.5;
        total.setText(String.format(Locale.UK, "%.2f €", tp));
    }
}
