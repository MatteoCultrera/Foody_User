package com.example.foodyrestaurant;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.foody_library.Review;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class ShowReviewsActivity extends AppCompatActivity {

    private ArrayList<Review> reviews;
    private RecyclerView recyclerView;
    private RVAdapterReview adapterReview;
    private FirebaseUser firebaseUser;
    private SharedPreferences prefs;
    private ArrayList<String> imagesPath;
    private Dialog dialog;
    private boolean allImages;
    private int imagesToFetch, imagesFetched;
    private File storage;
    private String USERS_IMAGE_REVIEWS = "userProfileImages";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_reviews);
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
        storage = new File(root.getPath()+File.separator+USERS_IMAGE_REVIEWS);

        loadingAppear();

        if(storage.exists()){
            fetchReviews(true);
        }else{
            imagesToFetch = 0;
            imagesFetched = 0;
            fetchReviews(false);
        }
    }

    private void loadingAppear(){
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.loading_dialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.setCancelable(false);
        dialog.show();
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        recyclerView.setVisibility(View.GONE);
    }

    private void loadingDisappear(){
        dialog.dismiss();
        adapterReview = new RVAdapterReview(reviews);
        recyclerView.setAdapter(adapterReview);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void fetchReviews(final boolean hasImages){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("reviews");
        Query query = database.child(firebaseUser.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot singleReview : dataSnapshot.getChildren()){
                    Review review = singleReview.getValue(Review.class);

                    if(review.getImagePath() != null && !hasImages){
                        if(!imagesPath.contains(review.getImagePath())){
                            imagesToFetch++;
                        }
                    }
                    review.setImagePath(storage.getPath()+File.separator);
                    reviews.add(review);
                }

                if(hasImages){
                    for(Review r : reviews){
                        if(r.getImagePath() != null)
                            if(r.getImagePath().length() > 0)
                                r.setImagePath(storage.getPath()+File.separator+r.getUserID()+".jpg");
                    }
                    allImages = prefs.getBoolean("reviewsImages",false);
                    if(allImages)
                        loadingDisappear();
                }else{
                    for(String s : imagesPath)
                        fetchImage(s);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.db_error_message),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchImage(final String imagePath){
        if(imagePath == null)
            return;
        if(imagePath.length() <= 0)
            return;

        String userId = imagePath.split("/")[2].split(".")[0].substring(0,28);
        final File userProfile = new File(storage.getPath()+File.separator+userId+".jpg");
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        mStorageRef.child(imagePath).getFile(userProfile)
                .addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                        imagesFetched++;
                        for(Review r : reviews){
                            if(r.getImagePath().equals(imagePath)){
                                if(task.isSuccessful())
                                    r.setImagePath(userProfile.getPath());
                                else
                                    r.setImagePath(null);
                            }
                        }
                        if(imagesFetched==imagesToFetch){
                            prefs.edit().putBoolean("reviewsImages",true).apply();
                            loadingDisappear();
                        }
                    }
                });
    }
}
