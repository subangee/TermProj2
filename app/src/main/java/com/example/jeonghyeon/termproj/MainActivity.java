package com.example.jeonghyeon.termproj;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001; //intent 에 사용될 request code
    private SignInButton sign_in_button;
    private UserDBLoader userLoader;
    private LinearLayout login;
    ProgressDialog pd;
    private Handler loginHandler;
    private Handler loginLoading;
    private Person currentPerson;
    public static String userNickname;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        /*변수 세팅*/
        sign_in_button  = (SignInButton)findViewById(R.id.sign_in_button);
        login = (LinearLayout)findViewById(R.id.login);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .build();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        loginHandler = new Handler();
        loginLoading = new Handler();
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        sign_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGoogleApiClient.connect();
            }
        });//구글 로그인


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//메뉴
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {//로그아웃
            if (mGoogleApiClient.isConnected()) {
                Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient);
                mGoogleApiClient.disconnect();
                login.setVisibility(View.VISIBLE);
                recreate();//로그아웃과 함께 어플 초기화, 같은 휴대폰으로 다른 유저가 접속할 시 서로 다른 DB를 사용하게 함.
                            //서버에 유저별 DB를 등록해서 저장하게 하는 것이 좋을 것으로 생각됨.

            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {//로그인 시도

        currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient); //로그인 유저 정보 획득

        if(currentPerson != null) {//유저가 존재
            userLoader = new UserDBLoader();
            userLoader.load(currentPerson.getId());//사용자 존재 하는가 확인
            new Thread(new Runnable() {
                @Override
                public void run() {
                    loginLoading.post(new Runnable() {
                        @Override
                        public void run() {
                            pd = ProgressDialog.show(MainActivity.this, "", "로그인 시도 중...", true);//사용자에게 서버에 접속중임을 알림
                        }
                    });
                    while(!userLoader.Flag()){//결과 나올 때 까지
                        try {
                            Thread.sleep(100);//대기
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            loginHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    pd.dismiss();//다이얼로그 닫기
                                    if(userLoader.getNick() != null){//사용자 DB에 존재
                                        userNickname = userLoader.getNick();
                                        Toast toast = Toast.makeText(MainActivity.this, "환영합니다! " + userLoader.getNick() +"님.", Toast.LENGTH_SHORT);
                                        toast.show();
                                        login.setVisibility(View.GONE);
                                    }
                                    else{//DB에 없으면
                                        login.setVisibility(View.GONE);
                                        NickEditDialog nickEditDialog = new NickEditDialog(MainActivity.this,currentPerson.getId());//닉네임 설정 다이얼로그
                                        nickEditDialog.setCancelable(false);
                                        nickEditDialog.show();
                                    }
                                }
                            });
                        }
                    }).start();

                }
            }).start();
        }
        else{
            Log.w("main","error");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            mGoogleApiClient.connect();
        }
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, RC_SIGN_IN);

            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {

        }
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
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            /*TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));*/
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private FragmentManager fm;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fm = fm;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = fm.findFragmentByTag("android:switcher:" + mViewPager.getId() + ":" + getItemId(position));
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if(fragment != null){
                return fragment;
            }
            switch (position){
                case 0:
                    return RhymeFrag.newInstance();
                case 1:
                    return LyricFrag.newInstance();
                case 2:
                    return SongFrag.newInstance();
                default:
                    return null;
            }
        }


        @Override
        public int getCount() {//탭의 수
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "라임 찾기";
                case 1:
                    return "가사 쓰기";
                case 2:
                    return "연습하기";
                case 3:
                    return "듣기";
                default:
                    return null;
            }
        }
    }


}
