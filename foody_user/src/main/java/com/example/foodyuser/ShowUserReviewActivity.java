package com.example.foodyuser;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.foody_library.Review;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class ShowUserReviewActivity extends AppCompatActivity {

    private ArrayList<Review> reviews;
    private RecyclerView recyclerView;
    private RVAdapterReviews adapterReview;
    private FirebaseUser firebaseUser;
    private SharedPreferences prefs;
    private ArrayList<String> imagesPath;
    private boolean allImages;
    private int imagesToFetch, imagesFetched;
    private File storage;
    private String RESTAURANTS_IMAGE_REVIEWS = "restaurantsProfileImages";
    private SpinKitView loading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_reviews);
        loading = findViewById(R.id.loading_reviews);
        recyclerView = findViewById(R.id.list_reviews);
        prefs = getApplicationContext().getSharedPreferences("myPreference", MODE_PRIVATE);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void init(){
        allImages = prefs.getBoolean("reviewsImages",false);
        reviews = new ArrayList<>();
        imagesPath = new ArrayList<>();
        File root = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        storage = new File(root.getPath()+File.separator+RESTAURANTS_IMAGE_REVIEWS);

        loadingAppear();

        if(storage.exists()){
            fetchReviews(true);
        }else{
            storage.mkdirs();
            imagesToFetch = 0;
            imagesFetched = 0;
            fetchReviews(false);
        }
    }

    private void loadingAppear(){
        loading.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    private void loadingDisappear(){
        loading.setVisibility(View.GONE);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        adapterReview = new RVAdapterReviews(reviews);
        recyclerView.setAdapter(adapterReview);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void fetchReviews(final boolean hasImages){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        Query query = database.child("userReviews").child(firebaseUser.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot singleReview : dataSnapshot.getChildren()){
                    Review review = singleReview.getValue(Review.class);
                    if(review.getImagePath() != null && !hasImages){
                        if(!imagesPath.contains(review.getImagePathRest())){
                            imagesPath.add(review.getImagePathRest());
                            imagesToFetch++;
                        }
                    }
                    reviews.add(review);
                }

                if(hasImages){
                    for(Review r : reviews){
                        if(r.getImagePathRest() != null)
                            if(r.getImagePathRest().length() > 0)
                                r.setImagePathRest(storage.getPath()+File.separator+r.getUserID()+".jpg");
                    }
                    allImages = prefs.getBoolean("reviewsImages",false);
                    if(allImages)
                        loadingDisappear();
                }else{
                    for(String s : imagesPath)
                        fetchRestaurantImages(s);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.db_error_message),Toast.LENGTH_SHORT);
            }
        });
    }

    private void fetchRestaurantImages(final String imagePath){
        if(imagePath == null)
            return;
        if(imagePath.length() <= 0)
            return;

        String restaurantId = imagePath.split("/")[2].substring(0,28);
        final File restaurantImage = new File(storage.getPath()+File.separator+restaurantId+".jpg");
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        mStorageRef.child(imagePath).getFile(restaurantImage).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                imagesFetched++;
                for(Review r : reviews){
                    if(r.getImagePathRest().equals(imagePath)){
                        if(task.isSuccessful())
                            r.setImagePathRest(restaurantImage.getPath());
                        else
                            r.setImagePathRest(null);
                    }
                    if(imagesFetched==imagesToFetch){
                        prefs.edit().putBoolean("reviewsImages",true).apply();
                        loadingDisappear();
                    }
                }
            }
        });
    }
}
