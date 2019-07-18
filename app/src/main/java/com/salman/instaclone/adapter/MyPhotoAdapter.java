package com.salman.instaclone.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.salman.instaclone.R;

import java.util.List;

import com.salman.instaclone.fragment.PostDetailFragment;
import com.salman.instaclone.model.Post;

public class MyPhotoAdapter extends RecyclerView.Adapter<MyPhotoAdapter.myViewHolder>{
    Context mContex;
    List<Post> mPosts;

    public MyPhotoAdapter(Context mContex, List<Post> mPosts) {
        this.mContex = mContex;
        this.mPosts = mPosts;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContex).inflate(R.layout.photos_item, viewGroup, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder myViewHolder, int i) {
       final Post post = mPosts.get(i);
        Glide.with(mContex).load(post.getPostimage()).into(myViewHolder.post_image);

        myViewHolder.post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContex.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("postid", post.getPostid());
                editor.apply();

                ((FragmentActivity)mContex).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new PostDetailFragment()).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        ImageView post_image;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            post_image = itemView.findViewById(R.id.post_image);
        }
    }


}
