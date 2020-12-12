package in.programy.noqpartner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.noqpartner.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Map;

public class homeActivity extends AppCompatActivity {
    BottomNavigationView bottomNav;
    static String type;
    static String shopName;
    FirebaseFirestore db;
    FirebaseAuth mAuth;


    private long backPressedTime;
    private Toast backToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        String uid = mAuth.getUid();

        db.collection("sellers").document(mAuth.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e != null){
                  //  Toast.makeText(homeActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }

                if(documentSnapshot != null && documentSnapshot.exists()){
                    if(!((Boolean) documentSnapshot.get("shop status"))){
                        Toast.makeText(getApplicationContext(),"First You have to Register Shop",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(homeActivity.this,RegisterActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });




        DocumentReference documentReference = db.collection("shop info").document(uid);

        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                         @Override
                                                         public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                             if (documentSnapshot.exists()) {
                                                                 Map m = documentSnapshot.getData();
                                                                 shopName = ((String) m.get("shop name")).toLowerCase();
                                                                 type = ((String) m.get("type")).toLowerCase();
                                                                 bottomNav = findViewById(R.id.bottomNavigationView);
                                                                 bottomNav.setOnNavigationItemSelectedListener(navListener);
                                                                 getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new FoodMenuFragment()).commit();
                                                                 bottomNav.setVisibility(View.VISIBLE);
                                                             }
                                                         }
                                                     });


    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()){
                case R.id.orders:
                    selectedFragment = new OrdersFragment();
                    break;
                case R.id.foodMenu:
                    selectedFragment = new FoodMenuFragment();
                    break;
                case R.id.history:
                    selectedFragment = new HistoryFragment();
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,selectedFragment).commit();
            return true;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.profile){
            Intent intent = new Intent(getApplicationContext(),ProfileActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }




    @Override
    public void onBackPressed() {
        if(backPressedTime +2000>System.currentTimeMillis()){
            backToast.cancel();

            super.onBackPressed();
            this.finishAffinity();
            return;
        }
        else{
            backToast = Toast.makeText(getBaseContext(),"Press back again to exit",Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();

    }
}
