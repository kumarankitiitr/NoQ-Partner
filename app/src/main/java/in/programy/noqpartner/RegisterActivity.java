package in.programy.noqpartner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.noqpartner.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;

public class RegisterActivity extends AppCompatActivity {

    EditText nameEditText;
    EditText shopNameEditText;
    EditText zipCodeEditText;
    EditText mobileEditText;
    EditText upiEditText;
    EditText typeEditText;
    EditText passwordEditText;
    FirebaseFirestore db;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameEditText = findViewById(R.id.nameEditText);
        shopNameEditText = findViewById(R.id.shopNameEditText);
        zipCodeEditText = findViewById(R.id.zipCodeEditText);
        mobileEditText =findViewById(R.id.mobileEditText);
        typeEditText = findViewById(R.id.typeEditText);
        upiEditText = findViewById(R.id.upiEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        Button outButton = findViewById(R.id.outButton);
        outButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

            }
        });

    }

    public void register(View view){
        if(nameEditText.length() >0 && shopNameEditText.length() >0 && typeEditText.length()>0 && upiEditText.length()>0 && passwordEditText.length()>0){
            if(zipCodeEditText.length() == 6){
                if(mobileEditText.length() == 10){
                    DocumentReference docRef = db.collection("Restaurants").document("cafe");

                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                // Document found in the offline cache
                                DocumentSnapshot document = task.getResult();
                                Map<String, ArrayList> mp = (Map) document.getData();
                                if(mp != null) {
                                    mp.get("name").add(shopNameEditText.getText().toString());
                                    db.collection("Restaurants").document(typeEditText.getText().toString().toLowerCase()).set(mp);
                                }

                                else{
                                    Map<String,ArrayList> mp1 = new HashMap<>();
                                    ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(shopNameEditText.getText().toString()));
                                    mp1.put("name",arrayList);
                                    db.collection("Restaurants").document(typeEditText.getText().toString()).set(mp1);
                                }


                                Map<String, String> data = new HashMap<>();
                                data.put("name", nameEditText.getText().toString().toLowerCase());
                                data.put("shop name", shopNameEditText.getText().toString().toLowerCase());
                                data.put("zip code", zipCodeEditText.getText().toString());
                                data.put("mobile no", mobileEditText.getText().toString());
                                data.put("upi id", upiEditText.getText().toString());
                                data.put("email id",mAuth.getCurrentUser().getEmail());
                                data.put("password", passwordEditText.getText().toString());
                                data.put("type", typeEditText.getText().toString());


                                db.collection("shop info").document(mAuth.getCurrentUser().getUid()).set(data);

                                Map<String, Object> d = new HashMap<>();
                                d.put("upi",upiEditText.getText().toString());
                                d.put("name",nameEditText.getText().toString().toLowerCase());
                                db.collection("Restaurants").document(typeEditText.getText().toString().toLowerCase())
                                        .collection(shopNameEditText.getText().toString().toLowerCase()).document("details").set(d);


                                db.collection("sellers").document(mAuth.getUid()).update("shop status",true);


                                startWork();

                            } else {
                                Snackbar.make(findViewById(R.id.activityRegisterLayout),"Some Error Occurred",Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{
                    Snackbar.make(findViewById(R.id.activityRegisterLayout),"Mobile No. is Wrong",Snackbar.LENGTH_SHORT).show();
                }
            }
            else{
                Snackbar.make(findViewById(R.id.activityRegisterLayout),"Zip code is Wrong",Snackbar.LENGTH_SHORT).show();
            }
        }
        else{
            if(mAuth.getCurrentUser() != null){
                Log.i("info",mAuth.getUid());
            }
            Snackbar.make(findViewById(R.id.activityRegisterLayout),"All Portions are required!!!",Snackbar.LENGTH_SHORT).show();
        }
    }

    public void startWork(){
        Intent intent = new Intent(RegisterActivity.this,homeActivity.class);
        startActivity(intent);
    }


}
