package com.bjelac.passwordmanager.views;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;

import com.bjelac.passwordmanager.R;
import com.bjelac.passwordmanager.database.DataProviderService;
import com.bjelac.passwordmanager.presenter.VaultLoginFragmentPresenter;
import com.bjelac.passwordmanager.utils.ActivityStarter;
import com.bjelac.passwordmanager.utils.ApplicationContextProvider;
import com.bjelac.passwordmanager.utils.LoggerUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class VaultLoginFragmentActivity extends Fragment {
    private final String TAG = VaultLoginFragmentActivity.class.getName();
    public static Spinner tagFilterSpinner;
    public static VaultLoginFragmentActivity vaultLoginFragmentActivity;
    private FloatingActionButton fab;
    private List<String> tags;

    public VaultLoginFragmentActivity() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        vaultLoginFragmentActivity = this;
        View view = inflater.inflate(R.layout.fragment_vault_password, container, false);

        VaultLoginFragmentPresenter.getInstance(view);

        fab = view.findViewById(R.id.fab);
        if (DataProviderService.getInstance().filteredLoginModels.size() > 0) {
            moveAddFirstItemButtonToCorner(fab);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityStarter.showAddNewLogInPopUp(getContext());
            }
        });

        tagFilterSpinner = view.findViewById(R.id.spi_tagFilter);
        tagFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    DataProviderService.getInstance().filterLogins(tags.get(position));
                } else {
                    DataProviderService.getInstance().removeFilter();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setTagsForFilter();
        VaultLoginFragmentPresenter.refreshDataSet();
        if (DataProviderService.getInstance().filteredLoginModels.size() > 0) {
            moveAddFirstItemButtonToCorner(fab);
        } else {
            moveAddFirstItemButtonToMiddle(fab);
        }
    }

    private void moveAddFirstItemButtonToCorner(FloatingActionButton fab) {
        ((FrameLayout.LayoutParams) fab.getLayoutParams()).gravity = Gravity.END;
        ((FrameLayout.LayoutParams) fab.getLayoutParams()).gravity = Gravity.BOTTOM;
    }
    private void moveAddFirstItemButtonToMiddle(FloatingActionButton fab) {
        ((FrameLayout.LayoutParams) fab.getLayoutParams()).gravity = Gravity.CENTER;
    }

    private void setTagsForFilter() {
        DataProviderService.getInstance().refreshTags();
        tags = new ArrayList<>(DataProviderService.getInstance().loginTags);
        try {
            Collections.sort(tags, String.CASE_INSENSITIVE_ORDER);
            Collections.reverse(tags);
            tags.add(getResources().getString(R.string.lbl_tagFilterSelectAll));
            Collections.reverse(tags);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(ApplicationContextProvider.getContext(), android.R.layout.simple_spinner_item, tags);
            tagFilterSpinner.setAdapter(adapter);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        } catch (Exception e) {
            LoggerUtils.logD(TAG, e.getMessage());
        }
    }
}
