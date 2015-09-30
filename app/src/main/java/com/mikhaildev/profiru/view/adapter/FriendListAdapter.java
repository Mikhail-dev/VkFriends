package com.mikhaildev.profiru.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikhaildev.profiru.R;
import com.mikhaildev.profiru.controller.ImagesCacheController;
import com.mikhaildev.profiru.model.Friend;

import java.util.List;

/**
 * Created by E.Mikhail on 29.09.2015.
 */
public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.FriendViewHolder> implements View.OnClickListener {

    public interface FriendListListener {
        void onFriendClicked(View view, long friendId);
    }

    private List<Friend> rules;
    private FriendListListener friendListListener;

    public FriendListAdapter(List<Friend> friends, FriendListListener onClickListener){
        friendListListener = onClickListener;
        setData(friends);
    }

    public void setData(List<Friend> rules) {
        this.rules = rules;
    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);
        FriendViewHolder friendViewHolder = new FriendViewHolder(v);
        return friendViewHolder;
    }

    @Override
    public void onBindViewHolder(final FriendViewHolder friendViewHolder, int position) {
        Friend friend = rules.get(position);
        friendViewHolder.name.setText(friend.getFirstName() + " " + friend.getLastName());
        friendViewHolder.friend.setTag(friend.getUid());
        ImagesCacheController.getInstance().loadBitmapToImageView(friend.getPhotoUrl(), friendViewHolder.photo);
    }

    @Override
    public int getItemCount() {
        return rules.size();
    }

    @Override
    public void onClick(View v) {
        long userId = (Long) v.getTag();
        friendListListener.onFriendClicked(v, userId);
    }


    class FriendViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ImageView photo;
        View friend;

        FriendViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            photo = (ImageView) itemView.findViewById(R.id.photo);
            friend = itemView.findViewById(R.id.friend);
            friend.setOnClickListener(FriendListAdapter.this);
        }
    }

}
