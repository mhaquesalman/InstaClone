package com.salman.instaclone.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.salman.instaclone.R;

import java.util.List;

import com.salman.instaclone.fragment.PostDetailFragment;
import com.salman.instaclone.fragment.ProfileFragment;
import com.salman.instaclone.model.Notification;
import com.salman.instaclone.model.Post;
import com.salman.instaclone.model.User;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.myViewHolder> {
    private static final String TAG = "NotificationAdapter";
    private Context mContext;
    private List<Notification> mNotification;

    public NotificationAdapter(Context mContext, List<Notification> mNotification) {
        this.mContext = mContext;
        this.mNotification = mNotification;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.notification_item, viewGroup, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder myViewHolder, int i) {
        final Notification notification = mNotification.get(i);

        myViewHolder.text.setText(notification.getText());
        getuserInfo(myViewHolder.post_image, myViewHolder.username, notification.getUserid());
        if (notification.isIspost()){
            myViewHolder.post_image.setVisibility(View.VISIBLE);
            getpostImage(myViewHolder.post_image, notification.getPostid());
        } else {
            myViewHolder.post_image.setVisibility(View.GONE);
        }

        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notification.isIspost()){
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor.putString("postid", notification.getPostid());
                    editor.apply();

                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new PostDetailFragment()).commit();
                } else {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor.putString("profileid", notification.getUserid());
                    editor.apply();

                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new ProfileFragment()).commit();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mNotification.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder{
        ImageView image_profile, post_image;
        TextView username, text;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            image_profile = itemView.findViewById(R.id.image_profile);
            post_image = itemView.findViewById(R.id.post_image);
            username = itemView.findViewById(R.id.username);
            text = itemView.findViewById(R.id.comment);
        }
    }

    public void getuserInfo(final ImageView imageView, final TextView username, String publisherid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(publisherid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(mContext).load(user.getImageurl()).into(imageView);
                username.setText(user.getUsername());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: userInfo" + databaseError.getMessage());
            }
        });
    }

    private void getpostImage(final ImageView imageView, final String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Posts").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                Glide.with(mContext).load(post.getPostimage()).into(imageView);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: userInfo" + databaseError.getMessage());
            }
        });

    }
}
