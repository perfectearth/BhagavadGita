package com.perfectearth.bhagavadgita;



import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ShareCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;
import com.android.volley.Request;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;
import com.perfectearth.bhagavadgita.Adapter.MainNavigationAdapter;
import com.perfectearth.bhagavadgita.Utilis.CustomProgress;
import com.perfectearth.bhagavadgita.Utilis.NetworkUtils;
import com.perfectearth.bhagavadgita.Utilis.ZoomOutPageTransformer;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;



public class MainActivity extends AppCompatActivity implements NetworkUtils.OnNetworkCheckListener {

    private BottomNavigationView bottomNav;
    private ViewPager2 viewPager;
    private TextView titleTextView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private ActionBarDrawerToggle toggle;
    private View rootView;
    private Toolbar toolbar;

    private String token = "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";
    private static final int RC_APP_UPDATE = 123;
    private AppUpdateManager appUpdateManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        titleTextView = findViewById(R.id.txt_title_main);
        rootView = findViewById(android.R.id.content);

        bottomNav = findViewById(R.id.bottom_navigation);
        viewPager = findViewById(R.id.viewPager);

        viewPager.setAdapter(new MainNavigationAdapter(this));
        viewPager.setPageTransformer(new ZoomOutPageTransformer());
        viewPager.setUserInputEnabled(false);

        appUpdateManager = AppUpdateManagerFactory.create(this);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                        appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    try {
                        appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE,
                                MainActivity.this, RC_APP_UPDATE);
                        finish();
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        toggle = new ActionBarDrawerToggle(this, drawerLayout,toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(android.R.color.white));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        BottomSheetDialog bottomSheetShare = new BottomSheetDialog(this,R.style. AppBottomSheetDialogTheme);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.share_layout, null);
        bottomSheetShare.setContentView(bottomSheetView);
        Button shearApp  = bottomSheetView.findViewById(R.id.share_id);

        shearApp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                openShare();
                bottomSheetShare.dismiss();

            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.test_exam:
                        CustomProgress.showProgressBar(MainActivity.this,false,"Check\nInternet");
                        checkNetwork();
                        // Handle click on menu item 1
                        break;
                    case R.id.support:
                       /* SupportFragment fragment = new SupportFragment();
                        loadFragment(fragment);*/
                        dialogSupport();
                        // Handle click on menu item 2
                        break;
                    case R.id.share:
                        bottomSheetShare.show();
                        // Handle click on menu item 2
                        break;
                    // Add more cases for other menu items as needed
                }

                // Close the drawer menu after handling the click event
                drawerLayout.closeDrawers();

                return true;
            }
        });

        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.menu_home:
                        viewPager.setCurrentItem(0);
                        toggle.setDrawerIndicatorEnabled(true);
                        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                        titleTextView.setText(getString(R.string.gita_name));
                        return true;
                    case R.id.menu_quotes:
                        viewPager.setCurrentItem(1);
                        toggle.setDrawerIndicatorEnabled(false);
                        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                        String name2 = getString(R.string.quotess);
                        titleTextView.setText(name2);
                        return true;
                    case R.id.menu_bookmarks:
                        viewPager.setCurrentItem(2);
                        toggle.setDrawerIndicatorEnabled(false);
                        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                        String name3 = item.getTitle().toString();
                        titleTextView.setText(name3);
                        return true;
                    case R.id.menu_about:
                        viewPager.setCurrentItem(3);
                        toggle.setDrawerIndicatorEnabled(false);
                        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                        String name4 = item.getTitle().toString();
                        titleTextView.setText(name4);
                        return true;
                }
                return false;
            }
        });
    }


    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        viewPager.setVisibility(View.GONE);
        bottomNav.setVisibility(View.GONE);
        toggle.setDrawerIndicatorEnabled(false);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        titleTextView.setText("Support");
        transaction.addToBackStack(null);
        transaction.commit();
    }
    private void openShare() {
        String appPackageName = getPackageName();
        String appLink = "https://play.google.com/store/apps/details?id=" + appPackageName;
        String shareMessage = "Check out my awesome app! " + appLink;

        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(shareMessage)
                .getIntent();

        startActivity(Intent.createChooser(shareIntent, "Share via"));

    }

    private void dialogSupport() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_report);
        dialog.setCancelable(false);
        Spinner spinner = dialog.findViewById(R.id.spinner);
        EditText editText = dialog.findViewById(R.id.edit_text);
        ImageButton closeBtn =dialog.findViewById(R.id.close_dialog_report);
        Button dialogButton = dialog.findViewById(R.id.dialog_button);
        String[] items = {"App Design", "Chapter Problem", "Verse Problem", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(adapter);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = spinner.getSelectedItem().toString();
                String description = editText.getText().toString().trim();
                if (TextUtils.isEmpty(description)) {
                    editText.setError("Input required!");
                } else {
                    CustomProgress.showProgressBar(MainActivity.this,false,"Send Data..");
                    RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                    String url = "https://api.github.com/repos/perfectearth/BhagavadGita/issues";
                    JSONObject jsonBody = new JSONObject();
                    try {
                        jsonBody.put("title", title);
                        jsonBody.put("body", description);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        CustomProgress.hideProgressBar();
                    }
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(MainActivity.this, "Issue send successfully", Toast.LENGTH_SHORT).show();
                            CustomProgress.hideProgressBar();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(MainActivity.this, "Failed to send issue", Toast.LENGTH_SHORT).show();
                            CustomProgress.hideProgressBar();
                        }
                    }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> headers = new HashMap<>();
                            headers.put("Authorization", "token " + token);
                            headers.put("Content-Type", "application/json");
                            return headers;
                        }
                    };
                    requestQueue.add(jsonObjectRequest);
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    // If an in-app update is already in progress, resume the update.
                    try {
                        appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE,
                                MainActivity.this, RC_APP_UPDATE);
                        finish();
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (viewPager.getVisibility()==View.GONE) {
            viewPager.setVisibility(View.VISIBLE);
            bottomNav.setVisibility(View.VISIBLE);
            toggle.setDrawerIndicatorEnabled(true);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            titleTextView.setText(R.string.gita_name);
        }
        int currentItem = viewPager.getCurrentItem();
        if (currentItem == 0) {
            super.onBackPressed();
        } else if (currentItem == 4 || currentItem == 3) {
            viewPager.setCurrentItem(0);
            bottomNav.setSelectedItemId(R.id.menu_home);
        } else {
            viewPager.setCurrentItem(currentItem - 1);
            bottomNav.setSelectedItemId(R.id.menu_home);
        }
    }
    private void checkNetwork() {
        NetworkUtils.isNetworkAvailable(this, this);
    }
    @Override
    public void onNetworkCheck(boolean isNetworkAvailable) {
        if (isNetworkAvailable) {
            CustomProgress.hideProgressBar();
            Intent intent = new Intent(this, UserRegister.class);
            startActivity(intent);

        } else {
            Snackbar.make(rootView, "Network Not Available", Snackbar.LENGTH_LONG).setAction("Action", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Action to be performed on click of action button
                }
            }).show();
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_APP_UPDATE) {
            if (resultCode != RESULT_OK) {
                Log.d("MainActivity", "Update flow failed! Result code: " + resultCode);
            }
        }
    }
}