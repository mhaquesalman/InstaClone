package com.salman.instaclone.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.salman.instaclone.R;
import com.salman.instaclone.activity.AddStoryActivity;
import com.salman.instaclone.activity.StoryActivity;

import java.util.List;

import com.salman.instaclone.model.Story;
import com.salman.instaclone.model.User;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.myViewHolder>{
    Context mContext;
    List<Story> mStory;

    public StoryAdapter(Context context, List<Story> mStory) {
        this.mContext = context;
        this.mStory = mStory;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == 0){
            View view = LayoutInflater.from(mContext).inflate(R.layout.add_story_item, viewGroup, false);
            return new myViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.story_item, viewGroup, false);
            return new myViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final myViewHolder myViewHolder, int i) {
       final Story story = mStory.get(i);

       userInfo(myViewHolder, story.getUserid(), i);

       if (myViewHolder.getAdapterPosition() != 0){
           seenStory(myViewHolder, story.getUserid());
       }
       if (myViewHolder.getAdapterPosition() == 0){
           myStory(myViewHolder.addstory_text, myViewHolder.story_plus, false);
       }

       myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (myViewHolder.getAdapterPosition() == 0){
                   myStory(myViewHolder.addstory_text, myViewHolder.story_plus, true);
               } else {
                   //go to story
                   Intent intent = new Intent(mContext, StoryActivity.class);
                   intent.putExtra("userid", story.getUserid());
                   mContext.startActivity(intent);
               }
           }
       });


    }

    @Override
    public int getItemCount() {
        return mStory.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder{
        ImageView story_photo, story_plus, story_photo_seen;
        TextView story_username, addstory_text;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            story_photo = itemView.findViewById(R.id.story_photo);
            story_plus = itemView.findViewById(R.id.story_plus);
            story_photo_seen = itemView.findViewById(R.id.story_photo_seen);
            story_username = itemView.findViewById(R.id.story_username);
            addstory_text = itemView.findViewById(R.id.addstory_text);

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return  0;
        }
        return 1;
    }

    public void userInfo(final myViewHolder viewHolder, String userid, final int pos){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(mContext).load(user.getImageurl()).into(viewHolder.story_photo);
                if (pos !=0 ){
                    Glide.with(mContext).load(user.getImageurl()).into(viewHolder.story_photo_seen);
                    viewHolder.story_username.setText(user.getUsername());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void myStory(final TextView textView, final ImageView imageView, final boolean click){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story")
                .child(firebaseUser.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = 0;
                long timecurrent = System.currentTimeMillis();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Story story = snapshot.getValue(Story.class);
                    if (timecurrent > story.getTimeStart() && timecurrent < story.getTimeend()){
                        count++;
                    }
                }
                if (click){
                    if (count > 0){
                        AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "View Story",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(mContext, StoryActivity.class);
                                        intent.putExtra("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        mContext.startActivity(intent);
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Add Story",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(mContext, AddStoryActivity.class);
                                        mContext.startActivity(intent);
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    } else {
                        Intent intent = new Intent(mContext, AddStoryActivity.class);
                        mContext.startActivity(intent);
                    }

                } else {
                    if (count > 0){
                        textView.setText("My Story");
                        imageView.setVisibility(View.GONE);
                    } else {
                        textView.setText("Add Story");
                        imageView.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void seenStory(final myViewHolder viewHolder, String userid){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if (!snapshot.child("views").child(firebaseUser.getUid()).exists()
                    && System.currentTimeMillis() < snapshot.getValue(Story.class).getTimeend()){
                        i++;
                    }
                }
                if (i > 0){
                   viewHolder.story_photo.setVisibility(View.VISIBLE);
                   viewHolder.story_photo_seen.setVisibility(View.GONE);
                } else {
                    viewHolder.story_photo.setVisibility(View.GONE);
                    viewHolder.story_photo_seen.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
