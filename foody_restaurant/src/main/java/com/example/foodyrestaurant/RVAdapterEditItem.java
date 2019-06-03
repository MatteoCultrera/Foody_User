package com.example.foodyrestaurant;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import de.hdodenhof.circleimageview.CircleImageView;

public class RVAdapterEditItem extends RecyclerView.Adapter<RVAdapterEditItem.DishEdit>{

    private final ArrayList<Dish> dishes;
    private final MenuEditItem editItem;
    private boolean unchanged = true;

    RVAdapterEditItem(ArrayList<Dish> dishes, MenuEditItem editItem){
        this.dishes = dishes;
        this.editItem = editItem;
    }

    @Override
    public int getItemCount() {
        return dishes.size();
    }

    @NonNull
    @Override
    public RVAdapterEditItem.DishEdit onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.menu_item_edit, viewGroup, false);
        final DishEdit pvh = new DishEdit(v, new DishNameEditTextListener(), new DishDescriptionEditTextListener(),
                new DecimalDigitsInputFilter(5,2), new DishPriceListener(), new DeleteListener());


        pvh.dishName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    pvh.dishName.setSelection(pvh.dishName.getText().length());
                    if(pvh.dishName.length() > 0)
                        pvh.dishName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.delete_fill_black, 0);
                }else{
                    if(pvh.dishName.length()>0 && pvh.dishName.getError()==null)
                        pvh.dishName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
            }
        });

        pvh.dishDesc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    pvh.dishDesc.setSelection(pvh.dishDesc.getText().length());
                    if(pvh.dishDesc.length() > 0)
                        pvh.dishDesc.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.delete_fill_black, 0);
                }else{
                    pvh.dishDesc.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
            }
        });

        return pvh;
    }

    private boolean alreadyExists(String name){
        boolean correct = true;
        int occurence = 0;

        for(int i=0; i < dishes.size(); i++){
            if(dishes.get(i).getDishName().equals(name))
                occurence++;
        }

        if(occurence > 1)
            correct = false;

        return !correct;
    }

    private boolean checkError(){
        boolean correct = true;

        for(int i=0; i < dishes.size(); i++){
            if(dishes.get(i).getDishName().isEmpty())
                correct = false;
        }

        return correct;
    }

    @Override
    public void onBindViewHolder(@NonNull final RVAdapterEditItem.DishEdit dishViewHolder,final int i) {
        dishViewHolder.editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editItem.showPickImageDialog(i);
            }
        });
        final RequestOptions options = new RequestOptions().fitCenter();
        if(dishes.get(i).getPathDB() == null){
            Glide
                    .with(dishViewHolder.dishPicture.getContext())
                    .load(R.drawable.placeholder_plate)
                    .apply(options)
                    .into(dishViewHolder.dishPicture);
        }else{
            StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
            mStorageRef.child(dishes.get(i).getPathDB()).getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide
                                    .with(dishViewHolder.dishPicture.getContext())
                                    .load(uri)
                                    .apply(options)
                                    .into(dishViewHolder.dishPicture);
                        }
                    });

        }
        dishViewHolder.nameListener.updatePosition(dishViewHolder.getAdapterPosition());
        dishViewHolder.descriptionListener.updatePosition(dishViewHolder.getAdapterPosition());
        dishViewHolder.dishPriceListener.updatePosition(dishViewHolder.getAdapterPosition());
        dishViewHolder.deleteListener.updatePosition(dishViewHolder.getAdapterPosition());

        dishViewHolder.dishName.setText(dishes.get(i).getDishName());
        dishViewHolder.dishDesc.setText(dishes.get(i).getDishDescription());
        Float value = dishes.get(i).getPrice();
        if (value != 0.0f) {
            dishViewHolder.price.setText(String.format(Locale.UK, "%.2f", value));
        } else {
            dishViewHolder.price.setText("");
        }

        dishViewHolder.dishName.setImeOptions(EditorInfo.IME_ACTION_DONE);
        dishViewHolder.dishName.setRawInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        dishViewHolder.dishDesc.setImeOptions(EditorInfo.IME_ACTION_DONE);
        dishViewHolder.dishDesc.setRawInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);


        dishViewHolder.price.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    dishViewHolder.price.setSelection(dishViewHolder.price.getText().length());
                    if(dishViewHolder.price.length() > 0)
                        dishViewHolder.price.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.delete_fill_black, 0);
                }else{
                    dishViewHolder.price.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    //dishViewHolder.price.setText(String.format(Locale.UK, "%.2f",dishes.get(i).getPrice()));
                }
            }
        });

        if(!dishViewHolder.dishName.getText().toString().isEmpty() && dishViewHolder.valid){
            dishViewHolder.editImage.setEnabled(true);
            dishViewHolder.dishName.setError(null);
        }

        if(dishViewHolder.dishName.getError() == null && !dishViewHolder.dishName.hasFocus())
            dishViewHolder.dishName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    static class DishEdit extends RecyclerView.ViewHolder {
        final CardView cardView;
        final EditText dishName;
        final EditText dishDesc;
        final EditText price;
        final CircleImageView dishPicture;
        final DishNameEditTextListener nameListener;
        final DishDescriptionEditTextListener descriptionListener;
        final DecimalDigitsInputFilter decimalDigitsInputFilter;
        final DishPriceListener dishPriceListener;
        final DeleteListener deleteListener;
        final MaterialButton deleteButton;
        final MaterialButton editImage;
        boolean valid;

        DishEdit(View itemView, DishNameEditTextListener nameListener,
                 DishDescriptionEditTextListener descriptionListener,
                 DecimalDigitsInputFilter decimalDigitsInputFilter,
                 DishPriceListener dishPriceListener,
                 DeleteListener deleteListener) {
            super(itemView);
            valid = true;
            cardView = itemView.findViewById(R.id.dish_card);
            dishPicture = itemView.findViewById(R.id.dish_image);
            dishName = itemView.findViewById(R.id.dish_name);
            dishDesc = itemView.findViewById(R.id.dish_description);
            price = itemView.findViewById(R.id.dish_price);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            editImage = itemView.findViewById(R.id.edit_image);
            this.nameListener = nameListener;
            this.descriptionListener = descriptionListener;
            this.decimalDigitsInputFilter = decimalDigitsInputFilter;
            this.dishPriceListener = dishPriceListener;
            this.deleteListener = deleteListener;

            dishName.addTextChangedListener(nameListener);
            nameListener.setEditText(dishName, this, this.editImage);

            dishDesc.addTextChangedListener(descriptionListener);
            descriptionListener.setEditText(dishDesc);

            deleteButton.setOnClickListener(deleteListener);

            dishName.setOnTouchListener(new View.OnTouchListener() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (dishName.getCompoundDrawables()[2] != null) {
                            if (event.getX() >= (dishName.getRight() - dishName.getLeft() - dishName.getCompoundDrawables()[2].getBounds().width())) {
                                dishName.setText("");
                            }
                        }
                    }
                    return false;
                }
            });


            dishDesc.setOnTouchListener(new View.OnTouchListener() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_UP) {
                        if(dishDesc.getCompoundDrawables()[2]!=null){
                            if(event.getX() >= (dishDesc.getRight()- dishDesc.getLeft() - dishDesc.getCompoundDrawables()[2].getBounds().width())) {
                                dishDesc.setText("");
                            }
                        }
                    }
                    return false;
                }
            });

            price.setOnTouchListener(new View.OnTouchListener() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_UP) {
                        if(price.getCompoundDrawables()[2]!=null){
                            if(event.getX() >= (price.getRight()- price.getLeft() - price.getCompoundDrawables()[2].getBounds().width())) {
                                price.setText("");
                            }
                        }
                    }
                    return false;
                }
            });

            price.setFilters(new InputFilter[] {decimalDigitsInputFilter});
            price.addTextChangedListener(dishPriceListener);
            dishPriceListener.setEditText(price);

        }
    }

    private class DeleteListener implements View.OnClickListener {
        int position;

        void updatePosition(int position){
            this.position = position;
        }

        public void onClick(View arg0) {

            unchanged = false;
            editItem.removeItem(position);
        }
    }

    private class DishNameEditTextListener implements TextWatcher {
        private int position;
        private EditText editText;
        private DishEdit dishEdit;
        private MaterialButton editImage;
        private int count = 0;

        void setEditText(EditText text, DishEdit dishEdit, MaterialButton editImage){
            editText = text;
            this.dishEdit = dishEdit;
            this.editImage = editImage;
        }

        void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (count != 0){
                unchanged = false;
            }
            count ++;
            dishes.get(position).setDishName(charSequence.toString());
            if(!editImage.isEnabled()){
                editImage.setEnabled(true);
            }

            editItem.saveEnabled(true);
            if(charSequence.toString().isEmpty()){
                editImage.setEnabled(false);
                editText.setError(editText.getContext().getString(R.string.error_dish_name_missing));
                dishEdit.valid=false;
                editItem.saveEnabled(false);
            } else if(alreadyExists(charSequence.toString())){
                editImage.setEnabled(false);
                editText.setError(editText.getContext().getString(R.string.error_dish_name_duplicate));
                dishEdit.valid = false;
                editItem.saveEnabled(false);
            } else if(checkError()){
                dishEdit.valid = true;
            }
            else
                dishEdit.valid = true;

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if(dishes.get(position).getDishName().length() != 0 && editText.hasFocus())
                editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.delete_fill_black, 0);
        }
    }

    private class DishDescriptionEditTextListener implements TextWatcher {
        private int position;
        private EditText editText;
        private int count = 0;

        void setEditText(EditText text){
            editText = text;
        }

        void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (count != 0){
                unchanged = false;
            }
            count ++;
            dishes.get(position).setDishDescription(charSequence.toString());
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if(dishes.get(position).getDishName().length() == 0 || !editText.hasFocus())
                editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            else
                editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.delete_fill_black, 0);
        }
    }

    private class DishPriceListener implements TextWatcher {
        private int position;
        private EditText editText;
        private int count = 0;

        void setEditText(EditText text){
            editText = text;
        }

        void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (count != 0){
                unchanged = false;
            }
            count ++;
            String price = editText.getText().toString();
            if(price.isEmpty())
                price = "0";
            dishes.get(position).setPrice(Float.parseFloat(price));
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if(dishes.get(position).getDishName().length() == 0 || !editText.hasFocus())
                editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            else
                editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.delete_fill_black, 0);
        }
    }

    class DecimalDigitsInputFilter implements InputFilter {

        final Pattern mPattern;

        DecimalDigitsInputFilter(int digitsBeforeZero, int digitsAfterZero) {
            mPattern=Pattern.compile("[0-9]{0," + (digitsBeforeZero-1) + "}+((\\.[0-9]{0," + (digitsAfterZero-1) + "})?)||(\\.)?");
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            Matcher matcher=mPattern.matcher(dest);
            if(!matcher.matches())
                return "";
            return null;
        }

    }

    boolean getUnchanged(){
        return unchanged;
    }

    void setUnchanged(boolean b){
        this.unchanged = b;
    }
}
