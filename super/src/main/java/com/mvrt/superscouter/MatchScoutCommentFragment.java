package com.mvrt.superscouter;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mvrt.superscouter.adapters.CommentListAdapter;
import com.mvrt.superscouter.view.TabFragment;

import java.util.HashMap;

public class MatchScoutCommentFragment extends TabFragment {

    RecyclerView recycler;
    CommentListAdapter commentsAdapter;

    int[] teams;

    public static MatchScoutCommentFragment createInstance(int[] teams){
        MatchScoutCommentFragment frag = new MatchScoutCommentFragment();
        frag.teams = teams;
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_match_comment, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState){
        commentsAdapter = new CommentListAdapter(teams);
        recycler = (RecyclerView)v.findViewById(R.id.matchscout_comment_recycler);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.setAdapter(commentsAdapter);
    }

    @Override
    public String getTitle() {
        return "Comments";
    }

    public HashMap<Integer, String> getComments(){
        return commentsAdapter.getComments();
    }
}
