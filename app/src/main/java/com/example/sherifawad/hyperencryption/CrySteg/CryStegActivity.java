package com.example.sherifawad.hyperencryption.CrySteg;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import com.google.android.material.tabs.TabLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sherifawad.hyperencryption.CustomUI.ProgressDialog;
import com.example.sherifawad.hyperencryption.R;
import com.example.sherifawad.hyperencryption.Threads.AsyncHelper;

import connection.ClientServer;
import parameters.Client;

public class CryStegActivity extends AppCompatActivity {
    public static boolean connectionCheck = false;
    public static boolean allValuesCheck = false;
    private  final String TAG = "CrySteg";
    private  Context contextCrySteg;
    private Send send;
    private Recive recive;
    private Handler mainHandler = new Handler();
    private Handler handler;
    private Runnable handlerRunnable;
    private Client client;


    /**
     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;


//    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contextCrySteg = this;

        ConnectionInitiation();

    }

    @SuppressLint("StaticFieldLeak")
    private void ConnectionInitiation() {
        final ProgressDialog progressDialog = new ProgressDialog(contextCrySteg);
        new AsyncHelper(null, contextCrySteg){
            @Override
            protected Void doInBackground(Void... voids) {
                ClientServer bankClient = null;
                do {

                    if (!connectionCheck) {
//                        bankClient = new ClientServer("192.168.162.2", 7894);
                        bankClient = new ClientServer("192.168.1.4", 7894);
//                        bankClient = new ClientServer("localhost", 7894);
//                        bankClient = new ClientServer("10.0.2.2", 7894);
                        bankClient.start();
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        bankClient.interrupt();
                    }
                    if (connectionCheck && allValuesCheck) {
                        System.out.println("allValuesCheck is " + allValuesCheck);
                        break;
                    }

                } while (!allValuesCheck);
                System.out.println("Outside the loop");
                return super.doInBackground(voids);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog.ShowProgressDialog();
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                ShowActivity();
                progressDialog.DismissProgressDialog();
            }

        }.execute();
    }

    private void ShowActivity() {
//        handler.removeCallbacks(handlerRunnable);
//        parEXist((RelativeLayout) send.getView().findViewById(R.id.snd_relativeLayout), true);
//        send.getView().findViewById(R.id.snd_image_btn).setEnabled(false);
//        parEXist((RelativeLayout) recive.getView().findViewById(R.id.rcv_relativeLayout), true);
        setContentView(R.layout.activity_cry_steg);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cry_steg, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_cry_steg, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
//            return PlaceholderFragment.newInstance(position + 1);

            switch (position) {
                case 0:
                    send = new Send();
                    return send;
                case 1:
                    recive = new Recive();
                    return recive;
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }
    }

    private void parEXist(RelativeLayout layout, boolean enable) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            child.setEnabled(enable);
            if (child instanceof ViewGroup) {
                ViewGroup group = (ViewGroup) child;
                for (int j = 0; j < group.getChildCount(); j++) {
                    group.getChildAt(j).setEnabled(enable);
                }
            }

        }
    }

//    public static Context getCryStegContext() {
//        return CryStegActivity.contextCrySteg;
//    }
}
