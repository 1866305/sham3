package com.example.myapplicationsowh6a;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashBoardActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_profile);
         actionBar = getSupportActionBar();
        actionBar.setTitle("Profile");


        firebaseAuth = FirebaseAuth.getInstance();
        BottomNavigationView navigationView= findViewById(R.id.Nvigate);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);

        actionBar.setTitle("Home");
        HomeFragment fragment1 =  new HomeFragment();
        FragmentTransaction frg1 =getSupportFragmentManager().beginTransaction();
        frg1.replace(R.id.content, fragment1, "");
        frg1.commit();
    }

    private  BottomNavigationView.OnNavigationItemSelectedListener selectedListener = new BottomNavigationView.OnNavigationItemSelectedListener()
    {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId())
            {
                case R.id.nav_home:
                    actionBar.setTitle("Home");
                    HomeFragment fragment1 =  new HomeFragment();
                    FragmentTransaction frg1 =getSupportFragmentManager().beginTransaction();
                    frg1.replace(R.id.content, fragment1, "");
                    frg1.commit();
                    return true;
                case R.id.nav_Profile:

                    actionBar.setTitle("Profile");
                    ProfileFragment fragment2 =  new ProfileFragment();
                    FragmentTransaction frg2 =getSupportFragmentManager().beginTransaction();
                    frg2.replace(R.id.content, fragment2, "");
                    frg2.commit();

                    return true;

                case R.id.nav_Users:

                    actionBar.setTitle("Users");
                    UsersFragment fragment3 =  new UsersFragment();
                    FragmentTransaction frg3 =getSupportFragmentManager().beginTransaction();
                    frg3.replace(R.id.content, fragment3, "");
                    frg3.commit();

                    return true;
            }
            return false;
        }
    };
     private  void Checkstatus()
     {
         FirebaseUser user =  firebaseAuth.getCurrentUser();
         if(user != null)
         {
            //LoginInUser.setText(user.getEmail());
         }
         else
             {
                 startActivity(new Intent(DashBoardActivity.this, MainActivity.class));
                 finish();
             }
     }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStart() {
        Checkstatus();
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_logout)
        {
            firebaseAuth.signOut();
            Checkstatus();
        }
        return super.onOptionsItemSelected(item);
    }
}