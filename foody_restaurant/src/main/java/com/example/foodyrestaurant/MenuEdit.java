package com.example.foodyrestaurant;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
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
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import java.util.Objects;

public class MenuEdit extends AppCompatActivity {

    private RecyclerView recyclerMenu;
    private RVAdapterEdit recyclerAdapter;

    private FloatingActionButton mainFAB;
    private ArrayList<Card> cards;
    private JsonHandler jsonHandler;
    private EditText input;
    private final String JSON_PATH = "menu.json";
    private final String JSON_COPY = "menuCopy.json";
    private File fileTmp;
    private File storageDir;
    private ImageButton save;
    private ImageButton edit;
    private ImageButton exit;
    private ImageView plus, trash;
    private boolean unchanged = true;
    private AlertDialog dialogDism;
    private String dialogCode = "ok";
    private String writingCard = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_edit);

        Log.d("TITLECHECK","onCreate()");
        init();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);

        Log.d("TITLECHECK","onREstoreInstanceState()");
        cards = jsonHandler.getCards(fileTmp);
        String writingCard = savedInstanceState.getString("writing", "");
        String dialogPrec = savedInstanceState.getString("dialog");
        unchanged = savedInstanceState.getBoolean("unchanged");
        if (dialogPrec != null && dialogPrec.compareTo("ok") != 0) {
            if (dialogPrec.compareTo("create") == 0) {
                plusPressed(writingCard);
            } else if (dialogPrec.compareTo("back") == 0){
                onBackPressed();
            } else if (dialogPrec.compareTo("trash") == 0){
                editRotate();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        String json = jsonHandler.toJSON(cards);
        jsonHandler.saveStringToFile(json, fileTmp);
        outState.putBoolean("unchanged", unchanged);
        outState.putString("dialog", dialogCode);
        if (input != null)
            writingCard = input.getText().toString();
        if (!writingCard.equals(""))
            outState.putString("writing", writingCard);
    }

    private void init(){
        Log.d("TITLECHECK","init()");
        fileTmp = new File(storageDir, JSON_COPY);
        jsonHandler = new JsonHandler();
        storageDir =  getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File file = new File(storageDir, JSON_PATH);
        recyclerMenu = findViewById(R.id.menu_edit);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerMenu.setLayoutManager(llm);
        mainFAB = findViewById(R.id.mainFAB);

        if(fileTmp.exists())
            cards = jsonHandler.getCards(fileTmp);
        else
            cards = jsonHandler.getCards(file);


        save = findViewById(R.id.saveButton);
        ImageButton back = findViewById(R.id.backButton);
        edit = findViewById(R.id.editButton);
        exit = findViewById(R.id.endButton);
        plus = findViewById(R.id.plus);
        trash = findViewById(R.id.trash);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String json = jsonHandler.toJSON(cards);
                storageDir =  getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
                File file = new File(storageDir, JSON_PATH);
                jsonHandler.saveStringToFile(json, file);
                finish();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit();
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitEdit();
            }
        });

        /*
        cards = new ArrayList<>();

        ArrayList<Dish> dishes = new ArrayList<>();
        dishes.add(new Dish("Margerita","Pomodoro, Mozzarella, Basilico","3.50 €", null));
        dishes.add(new Dish("Vegetariana","Verdure di Stagione, Pomodoro, Mozzarella","8,00 €", null));
        dishes.add(new Dish("Quattro Stagioni","Pomodoro, Mozzarella, Prosciutto, Carciofi, Funghi, Olive, Grana a Scaglie","6,50 €", null));
        dishes.add(new Dish("Quattro Formaggi","Mozzarella, Gorgonzola, Fontina, Stracchino","7,00 €", null));
        Card c = new Card("Pizza");
        c.setDishes(dishes);
        cards.add(c);

        dishes = new ArrayList<>();
        dishes.add(new Dish("Pasta al Pomodoro","Rigationi, Pomodoro, Parmigiano, Basilico","3.50 €", null));
        dishes.add(new Dish("Carbonara","Spaghetti, Uova, Guanciale, Pecorino, Pepe Nero","8,00 €", null));
        dishes.add(new Dish("Pasta alla Norma","Pomodoro, Pancetta, Melanzane, Grana a Scaglie","6,50 €", null));
        dishes.add(new Dish("Puttanesca","Pomodoro, Peperoncino, Pancetta, Parmigiano","7,00 €", null));
        c = new Card("Primi");
        c.setDishes(dishes);
        cards.add(c);

        dishes = new ArrayList<>();
        dishes.add(new Dish("Braciola Di Maiale","Braciola, Spezie","3.50 €", null));
        dishes.add(new Dish("Stinco Alla Birra","Stinco di Maiale, Birra","8,00 €", null));
        dishes.add(new Dish("Cotoletta e Patatine","Cotoletta di Maiale, Patatine","6,50 €", null));
        dishes.add(new Dish("Filetto al pepe verde","Filetto di Maiale, Salsa alla Senape, Pepe verde in grani","7,00 €", null));
        c = new Card("Secondi");
        c.setDishes(dishes);
        cards.add(c);
        */

        if(dialogCode.equals("trash")){
            for(int i = 0; i < cards.size(); i++)
                cards.get(i).setEditing(true);
        }

        recyclerAdapter = new RVAdapterEdit(cards);
        recyclerMenu.setAdapter(recyclerAdapter);

        mainFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (plus.getVisibility() == View.VISIBLE) {
                    plusPressed("");
                }
                if(trash.getVisibility() == View.VISIBLE) {
                    trashPressed();
                }
            }
        });

    }

    private void plusPressed(String starting){
        input = new EditText(mainFAB.getContext());
        input.setText(starting);
        final LinearLayout container = new LinearLayout(mainFAB.getContext());
        container.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(52, 0, 52, 0);
        final AlertDialog.Builder builder = new AlertDialog.Builder(mainFAB.getContext(), R.style.AppCompatAlertDialogStyle);
        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogCode = "ok";
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(getResources().getString(R.string.accept), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogCode = "ok";
                unchanged = false;
                Card c = new Card(input.getText().toString());
                cards.add(c);
                recyclerAdapter.notifyItemInserted(cards.size() - 1);
            }
        });
        builder.setTitle(getResources().getString(R.string.alert_dialog_new_card_title));
        if (checkDuplicates(input.getText().toString()) || input.getText().toString().equals(""))
            builder.setCancelable(false);
        input.setLayoutParams(lp);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        input.setLines(1);
        input.setMaxLines(1);
        container.addView(input, lp);
        builder.setView(container);
        dialogCode = "create";
        final AlertDialog dialogDism = builder.create();
        Objects.requireNonNull(dialogDism.getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialogDism.show();
        dialogDism.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        dialogDism.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.secondaryText));
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0) {
                    dialogDism.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    dialogDism.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.secondaryText));
                } else {
                    if (checkDuplicates(charSequence)) {
                        dialogDism.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        dialogDism.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.secondaryText));
                        input.setError(getString(R.string.alert_dialog_error_card_name));
                    } else {
                        dialogDism.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                        dialogDism.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.primaryText));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void trashPressed(){
        dialogCode = "ok";
        Iterator<Card> cardIterator;
        int i = 0;
        for(cardIterator = cards.iterator(); cardIterator.hasNext(); i++) {
            if (cardIterator.next().isSelected()) {
                cardIterator.remove();
                recyclerAdapter.notifyItemRemoved(i);
                recyclerAdapter.notifyItemRangeRemoved(i, cards.size()-1);
                unchanged = false;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        File file = new File(storageDir, JSON_COPY);
        JsonHandler jsonPlaceholder =new JsonHandler();
        String json = jsonPlaceholder.toJSON(cards);
        jsonPlaceholder.saveStringToFile(json, file);
        if (dialogDism != null){
            dialogDism.dismiss();
        }
    }

    private void back(){
        if (unchanged){
            super.onBackPressed();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialogCode = "ok";
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton(getResources().getString(R.string.accept), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogCode = "ok";
                    MenuEdit.super.onBackPressed();
                }
            });
            builder.setTitle(getResources().getString(R.string.alert_dialog_back_title));
            builder.setMessage(getResources().getString(R.string.alert_dialog_back_message));
            builder.setCancelable(false);
            dialogCode = "back";
            dialogDism = builder.show();
        }
    }

    @Override
    public void onBackPressed() {
        if (unchanged){
            super.onBackPressed();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialogCode = "ok";
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton(getResources().getString(R.string.accept), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogCode = "ok";
                    MenuEdit.super.onBackPressed();
                }
            });
            builder.setTitle(getResources().getString(R.string.alert_dialog_back_title));
            builder.setMessage(getResources().getString(R.string.alert_dialog_back_message));
            builder.setCancelable(false);
            dialogCode = "back";
            dialogDism = builder.show();
        }
    }
  
    private void edit(){
        dialogCode = "trash";
        animateToEdit(edit, save, exit, mainFAB, plus, trash);
    }

    private void exitEdit(){
        dialogCode = "ok";
        animateToNormal(edit, save, exit, mainFAB, plus, trash);
    }

    private void editRotate(){
        dialogCode = "trash";
        for(int i = 0; i < cards.size();i++)
            cards.get(i).setEditing(true);
    }

    private void animateToEdit(final ImageButton edit,final ImageButton save,final ImageButton end,
                               FloatingActionButton fab, final ImageView plus, final ImageView trash){
        int shortAnimDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);


        edit.animate().alpha(0.0f).setDuration(shortAnimDuration).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                edit.setVisibility(View.GONE);
            }
        }).start();
        edit.animate().scaleX(0.2f).scaleY(0.2f).setDuration(shortAnimDuration).start();

        save.animate().alpha(0.0f).setDuration(shortAnimDuration).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                save.setVisibility(View.GONE);
            }
        }).start();
        save.animate().scaleX(0.2f).scaleY(0.2f).setDuration(shortAnimDuration).start();

        end.setScaleY(0.2f);
        end.setScaleX(0.2f);
        end.setAlpha(0.0f);
        end.setVisibility(View.VISIBLE);
        end.animate().alpha(1.0f).setDuration(shortAnimDuration).start();
        end.animate().scaleX(1f).scaleY(1f).setDuration(shortAnimDuration).setListener(null).start();

        fab.setBackgroundTintList(getColorStateList(R.color.errorColor));


        for(int i = 0; i< cards.size(); i++){
            cards.get(i).setEditing(true);
        }

        for(int i = 0; i< cards.size(); i++){
            if(!recyclerAdapter.normalToEdit(recyclerMenu.findViewHolderForAdapterPosition(i)))
                recyclerAdapter.notifyItemChanged(i);
        }

        plus.animate().alpha(0.0f).setDuration(shortAnimDuration).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                plus.setVisibility(View.GONE);
            }
        }).start();
        plus.animate().scaleX(0.2f).scaleY(0.2f).setDuration(shortAnimDuration).start();


        trash.setScaleY(0.2f);
        trash.setScaleX(0.2f);
        trash.setAlpha(0.0f);
        trash.setVisibility(View.VISIBLE);
        trash.animate().alpha(1.0f).setDuration(shortAnimDuration).setListener(null).start();
        trash.animate().scaleX(1.f).scaleY(1.f).setDuration(shortAnimDuration).setListener(null).start();
    }

    private void animateToNormal(final ImageButton edit,final ImageButton save,final ImageButton end,
                                 FloatingActionButton fab,final ImageView plus,final ImageView trash){
        int shortAnimDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);


        edit.setScaleY(0.2f);
        edit.setScaleX(0.2f);
        edit.setAlpha(0.0f);
        edit.setVisibility(View.VISIBLE);
        edit.animate().alpha(1.0f).setDuration(shortAnimDuration).setListener(null).start();
        edit.animate().scaleX(1.f).scaleY(1.f).setDuration(shortAnimDuration).setListener(null).start();

        save.setScaleY(0.2f);
        save.setScaleX(0.2f);
        save.setAlpha(0.0f);
        save.setVisibility(View.VISIBLE);
        save.animate().alpha(1.0f).setDuration(shortAnimDuration).setListener(null).start();
        save.animate().scaleX(1.f).scaleY(1.f).setDuration(shortAnimDuration).setListener(null).start();

        end.animate().alpha(0.0f).setDuration(shortAnimDuration).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                end.setVisibility(View.GONE);
            }
        }).start();
        end.animate().scaleX(0.2f).scaleY(0.2f).setDuration(shortAnimDuration).start();

        fab.setBackgroundTintList(getColorStateList(R.color.colorAccent));


        for(int i = 0; i< cards.size(); i++){
            cards.get(i).setEditing(false);
            cards.get(i).setSelected(false);
        }

        for(int i = 0; i< cards.size(); i++){
            if(!recyclerAdapter.editToNormal(recyclerMenu.findViewHolderForAdapterPosition(i), i))
                recyclerAdapter.notifyItemChanged(i);
        }

        trash.animate().alpha(0.0f).setDuration(shortAnimDuration).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                trash.setVisibility(View.GONE);
            }
        }).start();
        trash.animate().scaleX(0.2f).scaleY(0.2f).setDuration(shortAnimDuration).start();


        plus.setScaleY(0.2f);
        plus.setScaleX(0.2f);
        plus.setAlpha(0.0f);
        plus.setVisibility(View.VISIBLE);
        plus.animate().alpha(1.0f).setDuration(shortAnimDuration).setListener(null).start();
        plus.animate().scaleX(1.f).scaleY(1.f).setDuration(shortAnimDuration).setListener(null).start();

    }

    private boolean checkDuplicates(CharSequence title){
        boolean duplicate = false;
        for (Card c: cards) {
            if (c.getTitle().equals(title.toString())){
                duplicate = true;
                break;
            }
        }
        return duplicate;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("TITLECHECK","onREsume()");
        init();

    }
}
