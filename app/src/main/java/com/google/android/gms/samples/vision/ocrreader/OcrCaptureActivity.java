package com.google.android.gms.samples.vision.ocrreader;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

/**
 * Activity for the whole Ocr Detecting app.
 */
public final class OcrCaptureActivity extends AppCompatActivity {
    private static final String TAG = "OcrCaptureActivity";

    // New objects
    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;

    /**
     * Initializes the UI.
     */
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new CameraFragment(), "Camera");
        adapter.addFragment(new GalleryFragment(), "Gallery");
        adapter.addFragment(new InfoFragment(), "About");
        viewPager.setAdapter(adapter);
    }
}
