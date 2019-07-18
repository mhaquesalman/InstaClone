package com.salman.instaclone.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.salman.instaclone.R;

import java.util.ArrayList;
import java.util.List;

import com.salman.instaclone.adapter.PostAdapter;
import com.salman.instaclone.adapter.StoryAdapter;
import com.salman.instaclone.model.Post;
import com.salman.instaclone.model.Story;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    RecyclerView recyclerView;
    PostAdapter postAdapter;
    List<Post> postList;

    RecyclerView recyclerView_story;
    StoryAdapter storyAdapter;
    List<Story> storyList;

    List<String> followingList;

    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         View view = inflater.inflate(R.layout.fragment_home, container, false);

         recyclerView = view.findViewById(R.id.recycler_view);
         recyclerView.setHasFixedSize(true);
         LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
         linearLayoutManager.setReverseLayout(true);
         linearLayoutManager.setStackFromEnd(true);
         recyclerView.setLayoutManager(linearLayoutManager);
         postList = new ArrayList<>();
         postAdapter = new PostAdapter(getContext(), postList);
         recyclerView.setAdapter(postAdapter);

         recyclerView_story = view.findViewById(R.id.recycler_view_story);
         recyclerView_story.setHasFixedSize(true);
         LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getContext(),
                 LinearLayoutManager.HORIZONTAL, false);
         recyclerView_story.setLayoutManager(linearLayoutManager1);
         storyList = new ArrayList<>();
         storyAdapter = new StoryAdapter(getContext(), storyList);
         recyclerView_story.setAdapter(storyAdapter);

         progressBar = view.findViewById(R.id.progess_circular);

         checkFollowing();
         return view;
    }

    public void checkFollowing(){
        followingList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingList.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    followingList.add(snapshot.getKey());
                }
                readPost();
                readStory();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled:checkFollowing "+databaseError.getMessage());
            }
        });
    }

    public void readPost(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                    for (String id: followingList){
                        if (post.getPublisher().equals(id)){
                            postList.add(post);
                        }
                    }
                }
                postAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled:readPost "+databaseError.getMessage());
            }
        });
    }

    public void readStory(){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long timecurrent = System.currentTimeMillis();
                storyList.clear();
                storyList.add(new Story("",0,0,"", firebaseUser.getUid()));
                for (String id : followingList){
                   int countStory = 0;
                    Story story = null;
                   for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        story = snapshot.getValue(Story.class);
                       if (timecurrent > story.getTimeStart() && timecurrent <story.getTimeend()){
                           countStory++;
                       }
                   }
                   if (countStory > 0){
                       storyList.add(story);
                   }
                }
                storyAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
