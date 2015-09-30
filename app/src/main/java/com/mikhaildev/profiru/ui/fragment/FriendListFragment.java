package com.mikhaildev.profiru.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mikhaildev.profiru.R;
import com.mikhaildev.profiru.controller.loader.FriendListLoader;
import com.mikhaildev.profiru.model.AsyncResult;
import com.mikhaildev.profiru.model.Friend;
import com.mikhaildev.profiru.ui.activity.FriendImageActivity;
import com.mikhaildev.profiru.util.StringUtils;
import com.mikhaildev.profiru.util.UiUtils;
import com.mikhaildev.profiru.view.DividerItemDecoration;
import com.mikhaildev.profiru.view.adapter.FriendListAdapter;

import java.util.List;


public class FriendListFragment extends Fragment
        implements
            SwipeRefreshLayout.OnRefreshListener,
            LoaderManager.LoaderCallbacks,
            FriendListAdapter.FriendListListener {

    private final int LOADER_ID_UPDATE_FRIENDS  = 1;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View info;
    private List<Friend> mFriends;
    private FriendListAdapter adapter;

    public FriendListFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.f_friend_list, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(this);
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        info = v.findViewById(R.id.info);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public Loader onCreateLoader(int loaderId, Bundle bundle) {
        switch (loaderId) {
            case LOADER_ID_UPDATE_FRIENDS:
                return new FriendListLoader(getActivity());
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        switch (loader.getId()) {
            case LOADER_ID_UPDATE_FRIENDS:
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                handleFriendListResponse(data);
                break;
        }
    }

    private void handleFriendListResponse(Object data) {
        AsyncResult<List<Friend>> result = (AsyncResult<List<Friend>>) data;
        if (result.getData() != null) {
            mFriends = result.getData();
            updateAdapter(mFriends);
            info.setVisibility(View.GONE);
        } else {
            UiUtils.showException(getActivity(), result.getException());
        }
    }

    private void updateAdapter(List<Friend> friends) {
        if (adapter==null) {
            adapter = new FriendListAdapter(friends, this);
            recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
            recyclerView.setAdapter(adapter);
        } else {
            adapter.setData(friends);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onFriendClicked(View view, long friendId) {
        for (int i = 0; i < mFriends.size(); i++) {
            if (mFriends.get(i).getUid()==friendId)  {
                animateIntent(view, mFriends.get(i));
                break;
            }
        }

    }

    public void animateIntent(View view, Friend friend) {
        Intent intent = new Intent(getActivity(), FriendImageActivity.class);
        String transitionName = getString(R.string.transition_string);
        View viewStart = view.findViewById(R.id.photo);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),
                        viewStart,
                        transitionName
                );
        intent.putExtra(StringUtils.EXTRA_DATA, friend);
        ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    @Override
    public void onRefresh() {
        getActivity().getSupportLoaderManager().restartLoader(LOADER_ID_UPDATE_FRIENDS, null, this);
    }
}
