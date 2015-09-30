package com.mikhaildev.profiru.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.mikhaildev.profiru.R;
import com.mikhaildev.profiru.ui.fragment.FriendListFragment;
import com.mikhaildev.profiru.util.StringUtils;


public class FriendListActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showBackButton(false);
        showMainFragment();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.a_friend_list;
    }

    private void showMainFragment() {
        FriendListFragment friendListFragment = (FriendListFragment) getSupportFragmentManager().findFragmentByTag(StringUtils.FRIEND_LIST_FRAGMENT);
        if (friendListFragment == null) {
            friendListFragment = new FriendListFragment();
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, friendListFragment, StringUtils.FRIEND_LIST_FRAGMENT);
        transaction.commit();
    }
}
