package com.sidmobileapps.gatequizenggmaths;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
/*------------------------------Variables Declaration---------------------------------------------*/
    public static String solutionPdfName;
    public static String sectionName;
    //Constant key Values
    public static final String EXTRA_SECTION_NAME = "extraSectionName";
    public static final String EXTRA_SUB_SECTION = "extraSubSection";
    //UI components declaration
    private String subSectionName;
    private Spinner spinnerSubsections;
    private Spinner spinnerSections;
    private Button btnStartQuiz;
    private Button btnStartCalc;
    //AdView declaration
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Initialising Variables
        spinnerSections = findViewById(R.id.spinner_section);
        spinnerSubsections = findViewById(R.id.spinner_sub_section);
        mAdView = findViewById(R.id.adViewBanner);
        btnStartQuiz = findViewById(R.id.btn_start_quiz);
        btnStartCalc = findViewById(R.id.btn_calc_link);

        showBannerAd();

        loadSections();
        //sectionName=spinnerSections.getSelectedItem().toString();
        spinnerSections.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sectionName = parent.getItemAtPosition(position).toString();
                loadSubSections();
                spinnerSubsections.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        subSectionName = parent.getItemAtPosition(position).toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        subSectionName = parent.getItemAtPosition(0).toString();
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                sectionName = parent.getItemAtPosition(0).toString();
            }
        });

        btnStartQuiz.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                btnStartQuiz.setScaleX((float) 0.9);
                btnStartQuiz.setScaleY((float) 0.9);
                startQuiz();
            }
        });

        btnStartCalc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, WebCalculator.class));
            }
        });
    }
/*---------------------------------Method for Start Quiz------------------------------------------*/
    private void startQuiz() {
        //To Generate saved PDF name
        solutionPdfName = sectionName+" "+subSectionName+".pdf";

        Intent intent = new Intent(MainActivity.this,QuizActivity.class);
        //Passing variables to Next Activity
        intent.putExtra(EXTRA_SECTION_NAME, sectionName);
        intent.putExtra(EXTRA_SUB_SECTION, subSectionName);
        startActivity(intent);
        //To finish this Activity after Starting Next Activity
        finish();
    }
/*-------------------------------Method for Category Selector-------------------------------------*/
    private void loadSections() {
        //Getting Sections from Database
        DatabaseAccess dbHelper = DatabaseAccess.getInstance(this);
        dbHelper.open();
        List<String> sections = dbHelper.getAllSections();
        dbHelper.close();
        //Loading categories into Spinner using Array Adapter(Category class)
        ArrayAdapter<String> adapterSections = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, sections);
        adapterSections.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSections.setAdapter(adapterSections);
    }
/*--------------------------Method for Sets(Difficulty) Selector----------------------------------*/
    private void loadSubSections() {
        //Getting SubSections from Question class
        DatabaseAccess dbHelper = DatabaseAccess.getInstance(this);
        dbHelper.open();
        List<String> subSections = dbHelper.getAllSubSections();
        dbHelper.close();
        //Loading Sets(Difficulty) into Spinner using Array Adapter(String)
        ArrayAdapter<String> adapterSubSections = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, subSections);
        adapterSubSections.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSubsections.setAdapter(adapterSubSections);
    }
/*-----------------------------Method for Closing App Alert---------------------------------------*/
    public void showAppCloseAlert(Context context) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        //Alert Message
        alertBuilder.setMessage("Do you want to Close this Application ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        System.exit(0);
                        moveTaskToBack(true);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                    }
                });
        //Creating dialog box
        AlertDialog alert = alertBuilder.create();
        //Setting the title manually
        alert.setTitle("Alert!");
        alert.show();
    }
/*---------------------------------Method for Showing Banner Ad-----------------------------------*/
    private void showBannerAd() {
        mAdView.loadAd(new AdRequest.Builder().build());
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
            }
            @Override
            public void onAdClosed() {
                //Toast.makeText(getApplicationContext(), "Ad is closed!", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onAdFailedToLoad(int errorCode) {
                //Toast.makeText(getApplicationContext(), "Ad failed to load! error code: " + errorCode, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onAdLeftApplication() {
                //Toast.makeText(getApplicationContext(), "Ad left application!", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }
        });
    }
/*------------------------------Method for Back Press Button--------------------------------------*/
    @Override
    public void onBackPressed() {
        showAppCloseAlert(this);
    }
/*-------------------------Methods for Activity Life Cycle Management-----------------------------*/
    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }
}
/*--------------------------------------End of Main Activity Code---------------------------------*/
