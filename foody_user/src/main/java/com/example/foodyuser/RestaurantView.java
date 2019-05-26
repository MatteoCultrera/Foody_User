package com.example.foodyuser;

import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideOption;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
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
import java.util.List;

public class RestaurantView extends AppCompatActivity {

    TabLayout tabs;
    ViewPager viewPager;
    File storage;
    ImageView background;
    private Restaurant thisRestaurant;
    private Toolbar toolbar;
    private final String DIRECTORY_IMAGES = "showImages";
    private final String PROFILE_IMAGE = "profilePic.jpg";
    private String reName, reUsername, reAddress;
    private ArrayList<Card> cards;
    private ShowMenuFragment showMenu;
    private int imageFetched;
    private int imageToFetch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_view);
        tabs = findViewById(R.id.htab_tabs);
        viewPager = findViewById(R.id.htab_viewpager);
        toolbar = findViewById(R.id.htab_toolbar);
        background = findViewById(R.id.htab_header);
        showMenu = new ShowMenuFragment();

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

        init();
    }


    private void init(){
        if(storage == null)
            setupImagesDirectory();

        Bundle extras = getIntent().getExtras();
        reName = extras.getString("restaurant_id","");
        reUsername = extras.getString("restaurant_name", null);
        reAddress = extras.getString("restaurant_address", null);

        //Fetch Restaurant Image and save in internal storage
        fetchRestaurant();
        fetchMenu();


    }

    private void setupImagesDirectory(){

        File root = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        storage = new File(root.getPath()+File.separator+DIRECTORY_IMAGES);

        storage.mkdirs();

    }

    private void setupViewPager(ViewPager viewPager) {


        ViewPagerAdapter adapter = new ViewPagerAdapter(
                getSupportFragmentManager());
        adapter.addFrag(showMenu, getResources().getString(R.string.menu));
        adapter.addFrag(new UserFragment(), getResources().getString(R.string.reviews));
        adapter.addFrag(new UserFragment(), getResources().getString(R.string.info));
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

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        Query query = database.child("restaurantsInfo").child(reName);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    thisRestaurant = ds.getValue(Restaurant.class);
                    //cuisines.setText(thisRestaurant.getKitchensString());
                    //deliveryPrice.setText(thisRestaurant.getDeliveryPriceString());
                    //distance.setText(thisRestaurant.getDistanceString());
                    StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
                    final File image = new File(storage, PROFILE_IMAGE);
                    mStorageRef.child(thisRestaurant.getImagePath()).getFile(image).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            thisRestaurant.setImagePath(image.getPath());
                            RequestOptions options = new RequestOptions();
                            options.signature(new ObjectKey(image.getName()+image.lastModified()));
                            Glide
                                    .with(getApplicationContext())
                                    .setDefaultRequestOptions(options)
                                    .load(thisRestaurant.getImagePath())
                                    .into(background);
                        }
                    });

                }
                toolbar.setTitle(thisRestaurant.getUsername());
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

                if(imageToFetch == 0)
                    showMenu.init(cards);
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
                                            Log.d("MAD","called init");
                                            showMenu.init(cards);
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        imageFetched++;
                                        d.setImage(null);
                                        if(imageFetched == imageToFetch){
                                            Log.d("MAD","called init");
                                            showMenu.init(cards);
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



}
