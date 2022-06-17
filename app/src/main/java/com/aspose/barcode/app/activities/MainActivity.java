package com.aspose.barcode.app.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.aspose.barcode.License;
import com.aspose.barcode.app.R;
import com.aspose.barcode.app.databinding.ActivityMainBinding;
import com.aspose.barcode.app.fragments.barcoderecognition.RecognitionViewModel;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
{

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private RecognitionViewModel recognitionViewModel;
    private static final String TAG = "###MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        Locale.setDefault(new Locale("en", "US"));
        License license = setLicense(getApplicationContext(), "Aspose.BarCode.Android.Java.lic");
        Log.d(TAG,"is licensed : " + license.isLicensed());




        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {

            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS,
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_recognition, R.id.nav_generation)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        recognitionViewModel =
                new ViewModelProvider(this).get(RecognitionViewModel.class);
        Log.d(TAG, "recognitionViewModel = " + recognitionViewModel);
    }

   private License setLicense(Context context, String licenseFileName)
   {
       License  license = new License();
       try
       {
           InputStream inputStream = context.getAssets().open(licenseFileName);
//           BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//           StringBuilder sb = new StringBuilder();
//           String mLine = reader.readLine();
//           while (mLine != null) {
//               sb.append(mLine); // process line
//               mLine = reader.readLine();
//               Log.d(TAG,mLine);
//           }
//           reader.close();
           license.setLicense(inputStream);
       }
       catch (Exception e)
       {
           Log.d(TAG,"exception : " + e.getLocalizedMessage());
           Log.d(TAG, "exception stack : ");
           e.printStackTrace();
       }
       return license;
   }


    @Override
    protected void onStart()
    {
        Log.d(TAG, "onStart");
        super.onStart();

//        FragmentManager fm = getSupportFragmentManager();
//        List<Fragment> fragments = fm.getFragments();
//        for(Fragment f:fragments)
//        {
//            Log.d(TAG,f.getId() + "");
//        }
//        Fragment lastFragment = fragments.get(fragments.size() - 1);
//        List<Fragment> fragments1 = lastFragment.getChildFragmentManager().getFragments();
        Fragment foregroundFragment = getForegroundFragment();
        Log.d(TAG, "foregroundFragment = " + foregroundFragment);
//        for(Fragment f:fragments1)
//        {
//            Log.d(TAG,f.getId() + "");
//        }


//        RecognitionFragment recognitionFragment = ((RecognitionFragment) getSupportFragmentManager().findFragmentById(R.id.RecognitionFragment));
//        Log.d(TAG, "recognitionFragment " + recognitionFragment);
//        recognitionFragment = ((RecognitionFragment) getSupportFragmentManager().findFragmentByTag("RecognitionFragment"));
//        Log.d(TAG, "recognitionFragment " + recognitionFragment);
    }

    public Fragment getForegroundFragment()
    {
        Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        return navHostFragment == null ? null : navHostFragment.getChildFragmentManager().getFragments().get(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


    }
}