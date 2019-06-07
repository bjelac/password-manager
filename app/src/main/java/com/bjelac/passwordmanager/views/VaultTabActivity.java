package com.bjelac.passwordmanager.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.bjelac.passwordmanager.R;
import com.bjelac.passwordmanager.adapters.SectionsPageAdapter;
import com.bjelac.passwordmanager.database.DataProviderService;

public class VaultTabActivity extends AppCompatActivity {

    private SectionsPageAdapter sectionsPageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vault_tab);
        sectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        ViewPager mViewPager = findViewById(R.id.container);
        setupViewPager(mViewPager);
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        sectionsPageAdapter.addFragment(new VaultLoginFragmentActivity(), getResources().getString(R.string.lbl_tabLogin));
        sectionsPageAdapter.addFragment(new VaultTwoFAuthFragmentActivity(), getResources().getString(R.string.lbl_tab2fa));
        sectionsPageAdapter.addFragment(new SettingsFragmentActivity(), getResources().getString(R.string.lbl_tabSettings));
        viewPager.setAdapter(sectionsPageAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (DataProviderService.activityLocked) {
            //reauthenticate user
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        DataProviderService.activityLocked = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataProviderService.destroyInstance();
    }
}