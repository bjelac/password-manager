package com.bjelac.passwordmanager.presenter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bjelac.passwordmanager.R;
import com.bjelac.passwordmanager.adapters.LogInRecyclerAdapter;


public class VaultLoginFragmentPresenter {
    private static VaultLoginFragmentPresenter singleInstance;
    private static RecyclerView.Adapter recyclerViewAdapter;

    private static void setRecycleViewAdapter(View view) {
        recyclerViewAdapter = new LogInRecyclerAdapter(view.getContext());
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    public static VaultLoginFragmentPresenter getInstance(View view) {
        if (singleInstance == null) {
            singleInstance = new VaultLoginFragmentPresenter();
        }
        setRecycleViewAdapter(view);
        return singleInstance;
    }

    public static void refreshDataSet() {
        if (recyclerViewAdapter != null) {
            recyclerViewAdapter.notifyDataSetChanged();
        }
    }
}