package com.example.foodyuser;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideOption;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.example.foody_library.Review;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class RestaurantView extends AppCompatActivity {

    TabLayout tabs;
    ViewPager viewPager;
    File storage;
    ImageView background;
    private Restaurant thisRestaurant;
    private Toolbar toolbar;
    private final String DIRECTORY_IMAGES = "showImages";
    private final String CARDS = "cards.json";
    private final String REVIEWS = "reviews.json";
    private SharedPreferences shared;
    private String reName, reUsername, reAddress;
    private ArrayList<Card> cards;
    private ArrayList<Review> reviews;
    private ShowMenuFragment showMenu;
    private ShowInfoFragment showInfo;
    private ShowReviewFragment showReview;
    private int imageFetched;
    private int imageToFetch;
    private int reviewsImageToFetch;
    private int reviewsImageFetched;
    private TextView totalText, price;
    private ConstraintLayout totalLayout, addReview;
    private final String RESTAURANT_IMAGES = "RestaurantImages";
    int session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shared = getSharedPreferences("myPreference", MODE_PRIVATE);
        setContentView(R.layout.activity_restaurant_view);
        tabs = findViewById(R.id.htab_tabs);
        viewPager = findViewById(R.id.htab_viewpager);
        toolbar = findViewById(R.id.htab_toolbar);
        background = findViewById(R.id.htab_header);
        totalText = findViewById(R.id.price_show_frag);
        totalLayout = findViewById(R.id.price_show_layout_frag);
        price = findViewById(R.id.restaurant_del_price_frag);
        addReview = findViewById(R.id.add_review);
        showMenu = new ShowMenuFragment();
        showInfo = new ShowInfoFragment();
        showReview = new ShowReviewFragment();
        totalLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        session = 0;
    }


    private void init(){

        Log.d("PROVA","Into init");


        showMenu.setFather(this);
        showReview.setFather(this);


        setupViewPager(viewPager);

        viewPager.setOffscreenPageLimit(3);

        tabs.post(new Runnable() {
            @Override
            public void run() {
                tabs.setupWithViewPager(viewPager);
            }
        });


        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                switch (tab.getPosition()){
                    case 0:
                        totalLayout.setVisibility(View.VISIBLE);
                        addReview.setVisibility(View.GONE);
                        break;
                    case 1:
                        totalLayout.setVisibility(View.GONE);
                        addReview.setVisibility(View.VISIBLE);
                        break;
                    default:
                        totalLayout.setVisibility(View.GONE);
                        addReview.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        Bundle extras = getIntent().getExtras();

        reName = extras.getString("restaurant_id","");
        reUsername = extras.getString("restaurant_name", null);
        reAddress = extras.getString("restaurant_address", null);

        if(!shared.contains("selectedTime")){
            Calendar now = Calendar.getInstance();
            int hours = now.get(Calendar.HOUR_OF_DAY);
            int minutes = now.get(Calendar.MINUTE);
            minutes += 30;
            if(minutes/60 != 0){
                hours++;
                minutes = minutes%60;
            }

            String minTime = String.format("%02d:%02d",hours,minutes);
            shared.edit().putString("minTime",minTime).apply();
            shared.edit().putString("selectedTime",minTime).apply();

        }

        File root = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        storage = new File(root.getPath()+File.separator+DIRECTORY_IMAGES);

        if(!storage.exists()){
            Log.d("PROVA","No storage found");
            setupImagesDirectory();
            //Fetch Restaurant Image and save in internal storage
            fetchRestaurant();
            //Fetches the menu and automatically adds it to fragment
            fetchMenu();
            //Fetches the reviews and automatically adds them to fragment
            fetchReviews();

            updateTotal();

        }else{
            //Fetch Cards From Storage
            Log.d("MAD3","Storage found");
            if (thisRestaurant == null)
                fetchRestaurant();
            else{
                price.setText(thisRestaurant.getDeliveryPriceString());
                showInfo.init(thisRestaurant);
            }
            cardsFromFile();
            reviewsFromFile();
            updateTotal();

        }


    }

    public void cardsFromFile(){
        Log.d("MAD3","ON cards From File");
        final int set = session;
        File cardFile = new File(storage, CARDS);
        JsonHandler handler = new JsonHandler();

        if(set == session){
            cards = handler.getCards(cardFile);
            addOrders();
            Log.d("MAD3","called init set "+set+" session "+session);
            showMenu.init(cards);
        }

    }


    public void reviewsFromFile(){
        final int set = session;
        File reviewsFile = new File(storage, REVIEWS);
        JsonHandler handler = new JsonHandler();

        if(set == session){
            reviews = handler.getReviews(reviewsFile);
            showReview.init(reviews);
        }
    }

    public void updateTotal(){

        float total = 0;

        if(cards == null){
            totalDisappear();
            return;
        }
        if(cards.size() == 0){
            totalDisappear();
            return;
        }

        for(Card c : cards){
            for(Dish d : c.getDishes()){
                if(d.getOrderItem() != null)
                    total += d.getOrderItem().getPrice() * d.getOrderItem().getPieces();
            }
        }


        if (total > 0){
            if(totalText.getText().length() > 0)
                setTotalText(total);
            else
                totalAppear(total);
        }else{
            totalDisappear();
        }


    }

    private void setTotalText(float total){
        totalText.setText(String.format("%.2f €", total));
    }

    private void totalAppear(float total){
        totalText.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.short10));
        totalText.setText(String.format("%.2f €", total));
        totalLayout.setBackgroundResource(R.drawable.price_background);
        totalLayout.setClickable(true);
        totalLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JsonHandler handler = new JsonHandler();
                //Save Cards To Storage
                if(cards!=null){
                    File file = new File(storage, CARDS);
                    String json = handler.toJSON(cards);
                    handler.saveStringToFile(json, file);
                }
                //Save Orders to Storage
                ArrayList<OrderItem> orders = new ArrayList<>();
                for (Card c: cards){
                    for(Dish d : c.getDishes()){
                        if(d.getOrderItem() != null)
                            orders.add(d.getOrderItem());
                    }
                }

                if(orders.size() > 0){
                    File root = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
                    File file = new File(root, getString(R.string.order_file_name));
                    String json = handler.ordersToJSON(orders);
                    handler.saveStringToFile(json, file);
                }

                Calendar cal = Calendar.getInstance();
                int day = cal.get(Calendar.DAY_OF_WEEK);
                if(day == 1) {
                    day = 6;
                } else
                    day = day-2;
                String time = thisRestaurant.getDaysTime().get(day);

                Intent intent = new Intent(totalLayout.getContext(), Order.class);
                intent.putExtra("restaurantID", reName);
                intent.putExtra("restaurantName", reUsername);
                intent.putExtra("restaurantAddress", reAddress);
                intent.putExtra("restaurantTime", time);

                startActivity(intent);

            }
        });

    }

    private void totalDisappear(){
        totalText.setText("");
        totalText.setCompoundDrawablePadding(0);
        totalLayout.setBackgroundResource(R.drawable.price_background_dis);
        totalLayout.setClickable(false);
        totalLayout.setOnClickListener(null);
    }

    private void setupImagesDirectory(){

        File root = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        storage = new File(root.getPath()+File.separator+DIRECTORY_IMAGES);

        storage.mkdirs();

    }

    private void addOrders(){

        File root = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File orders = new File(root, getResources().getString(R.string.order_file_name));

        if(!orders.exists())
            return;

        JsonHandler handler = new JsonHandler();
        ArrayList<OrderItem> ordersArray = handler.getOrders(orders);

        for(OrderItem o : ordersArray){
            for(Card c: cards){
                for(Dish d: c.getDishes()){
                    if(d.getDishName().equals(o.getOrderName())){
                        d.setOrderItem(o);
                    }
                }
            }

        }

    }

    private void setupViewPager(ViewPager viewPager) {


        ViewPagerAdapter adapter = new ViewPagerAdapter(
                getSupportFragmentManager());
        adapter.addFrag(showMenu, getResources().getString(R.string.menu));
        adapter.addFrag(showReview, getResources().getString(R.string.reviews));
        adapter.addFrag(showInfo, getResources().getString(R.string.info));
        viewPager.setAdapter(adapter);
    }

    private static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

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

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void fetchRestaurant(){

        if(thisRestaurant != null)
            return;

        final int set = session;

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        Query query = database.child("restaurantsInfo").child(reName);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (set!=session)
                    return;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    thisRestaurant = ds.getValue(Restaurant.class);
                    //cuisines.setText(thisRestaurant.getKitchensString());
                    //deliveryPrice.setText(thisRestaurant.getDeliveryPriceString());
                    //distance.setText(thisRestaurant.getDistanceString());
                    thisRestaurant.setUid(dataSnapshot.getKey());
                    File root = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    File dir = new File(root.getPath()+File.separator+RESTAURANT_IMAGES);
                    thisRestaurant.setImagePath(dir.getPath()+File.separator+thisRestaurant.getUid()+".jpg");
                    File imageFile = new File(thisRestaurant.getImagePath());
                    RequestOptions options = new RequestOptions();
                    options.signature(new ObjectKey(thisRestaurant.getImagePath()+" "+imageFile.lastModified()));

                    if(thisRestaurant.getImagePath()!=null){
                        Glide
                                .with(toolbar.getContext())
                                .setDefaultRequestOptions(options)
                                .load(thisRestaurant.getImagePath())
                                .into(background);
                    }


                }
                toolbar.setTitle(thisRestaurant.getUsername());
                Log.d("VANGOGH",""+thisRestaurant.getUid());
                showInfo.init(thisRestaurant);
                price.setText(thisRestaurant.getDeliveryPriceString());
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for(Card c :cards){
                            Log.d("MAD2",c.getTitle());
                            for(Dish d: c.getDishes()){
                                Log.d("MAD2",String.format("\t %s ",d.getDishName())+(d.getOrderItem()==null?"none":d.getOrderItem().getPiecesString()));
                            }
                        }
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("SWSW", databaseError.getMessage());
            }
        });
    }

    private void fetchMenu(){
        imageFetched = 0;
        imageToFetch = 0;
        final int set = session;
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child("restaurantsMenu").child(reName);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                cards = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    for (DataSnapshot ds1 : ds.getChildren()) {
                        Card card = ds1.getValue(Card.class);
                        for(final Dish d : card.getDishes()){
                            if(d.getPathDB()!=null)
                                imageToFetch++;
                        }
                        cards.add(card);
                    }
                }

                if(imageToFetch == 0){
                    Log.d("MAD","called init set "+set+" session "+session);
                    if(set == session){
                        addOrders();
                        showMenu.init(cards);
                    }
                }
                else{
                    int pos = 0;
                    for(Card c : cards){
                        for(final Dish d: c.getDishes()){
                            if(d.getPathDB()!=null){
                                Log.d("MAD","Saving image");
                                StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
                                final File currentimage = new File(storage, "dish"+pos+".jpg");
                                mStorageRef.child(d.getPathDB()).getFile(currentimage).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                        Log.d("MAD", "Saved Image "+imageToFetch+" "+imageFetched);
                                        d.setImage(Uri.fromFile(currentimage));
                                        imageFetched++;
                                        if(imageFetched == imageToFetch){
                                            Log.d("MAD","called init set "+set+" session "+session);
                                            if(set == session){
                                                addOrders();
                                                showMenu.init(cards);
                                            }
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        imageFetched++;
                                        d.setImage(null);
                                        if(imageFetched == imageToFetch){
                                            Log.d("MAD","called init set "+set+" session "+session);
                                            if(set == session){
                                                addOrders();
                                                showMenu.init(cards);
                                            }
                                        }
                                    }
                                });
                            }
                            pos++;
                        }
                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("SWSW", databaseError.getMessage());
            }
        });
    }

    private void fetchReviews(){
        final int set = session;
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child("reviews").child(reName);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(set == session){
                    reviews = new ArrayList<>();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String reviewID = ds.getKey();
                        String userId = ds.child("userID").getValue(String.class);
                        String notes = ds.child("notes").getValue(String.class);
                        Float rating = ds.child("rating").getValue(Float.class);

                        if(notes != null)
                            if(notes.length() == 0)
                                notes = null;

                        Review review = new Review(reviewID, userId, notes,rating);

                        reviews.add(review);

                    }

                    if(reviews.size() > 0)
                        fetchUsers(set);
                    else
                        showReview.init(reviews);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("SWSW", databaseError.getMessage());
            }
        });
    }

    private void fetchUsers(final int num){
        final int set = num;
        final HashMap<String, String> usersWithImages = new HashMap<>();
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child("endUsers");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(set == session) {
                    for (Review r : reviews) {
                        String username = dataSnapshot.child(r.getUserID()).child("info").child("username").getValue(String.class);
                        String imagePath = dataSnapshot.child(r.getUserID()).child("info").child("imagePath").getValue(String.class);
                        if (imagePath != null) {
                            if (imagePath.length() > 0) {
                                usersWithImages.put(r.getUserID(), imagePath);
                                r.setImagePath(storage.getPath() + File.separator + r.getUserID() + ".jpg");
                            }
                        }

                        r.setUserName(username);
                    }

                    if (!usersWithImages.isEmpty()) {
                        setUserImages(usersWithImages, set);
                    } else {
                        if (set == session)
                            showReview.init(reviews);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("SWSW", databaseError.getMessage());
            }
        });

    }

    private void setUserImages(final HashMap<String, String> images, int num){
        Log.d("TESTFETCH","Into Set User Image");
        final int set = num;
        if(set == session){
            reviewsImageToFetch = images.size();
            reviewsImageFetched = 0;
        }

        for(final String s : images.keySet()){
            Log.d("TESTFETCH",s+ " "+images.get(s));
        }

        for(final String s : images.keySet()){
            StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
            final File currentimage = new File(storage.getPath() + File.separator + s + ".jpg");
            mStorageRef.child(images.get(s)).getFile(currentimage).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Log.d("TESTFETCH", "Saved Image "+reviewsImageToFetch+" "+reviewsImageFetched+" "+s+" "+images.get(s));
                    reviewsImageFetched++;
                    if(reviewsImageFetched == reviewsImageToFetch){
                        if(set == session)
                            showReview.init(reviews);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("TESTFETCH", "Not saved Image "+reviewsImageToFetch+" "+reviewsImageFetched+" "+s+" "+images.get(s));
                    reviewsImageFetched++;
                    if(reviewsImageFetched == reviewsImageToFetch){
                        Log.d("MAD","called init set "+set+" session "+session);
                        if(set == session)
                            showReview.init(reviews);
                    }
                }
            });
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        session++;
        if(session > 2000)
            session = 0;

        if(showMenu.notReady() || showReview.notReady()){
            Log.d("PROVA","deleting storage because not completed");
            if(storage.exists())
                removeStorage();
            showMenu.removeCards();
            showReview.removeReviews();
        }else{
            JsonHandler handler = new JsonHandler();
            String cJson = handler.toJSON(cards);
            for(Card c: cards)
                Log.d("MAD3",c.getTitle());
            File m1 = new File(storage, CARDS);
            handler.saveStringToFile(cJson, m1);
            String reviewsToJson = handler.reviewsToJSON(reviews);
            File m2 = new File(storage, REVIEWS);
            handler.saveStringToFile(reviewsToJson, m2);
            showMenu.removeCards();
            showReview.removeReviews();
        }

        /*
        File menu = new File(storage, CARDS);
        if(imageFetched != imageToFetch){
            Log.d("PROVA","deleting storage because not completed");
            Log.d("PROVA",menu.exists()?"Menu Exists":"Menu not exists");
            if(storage.exists())
                removeStorage();
            showMenu.removeCards();
        }else if(!menu.exists() && cards.size() > 0){
            Log.d("PROVA",menu.exists()?"Menu Exists":"Menu not exists");
            Log.d("PROVA","cards size "+cards.size());
            JsonHandler handler = new JsonHandler();
            String cardsToJson = handler.toJSON(cards);
            File m1 = new File(storage, CARDS);
            handler.saveStringToFile(cardsToJson, m1);
        }else if(cards.size() == 0){
            Log.d("PROVA","cards size "+cards.size());
            if(storage.exists())
                removeStorage();
            showMenu.removeCards();
        }*/

    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void removeStorage(){
        for(File f : storage.listFiles()){
            f.delete();
        }
        storage.delete();
        storage = null;
    }
}
