package in.programy.noqpartner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.noqpartner.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ProfileActivity extends AppCompatActivity {

    EditText nameEditText;
    EditText shopNameEditText;
    EditText upiIdEditText;
    EditText emailIdEditText;
    EditText mobileNoEditText;
    FirebaseAuth mAuth;
    ImageView profileImageView;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nameEditText = findViewById(R.id.nameEditText);
        shopNameEditText= findViewById(R.id.shopNameEditText);
        upiIdEditText = findViewById(R.id.upiIdEditText);
        emailIdEditText = findViewById(R.id.emailIdEditText);
        mobileNoEditText = findViewById(R.id.mobileNoEditText);
        profileImageView = findViewById(R.id.profileImageView);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        DocumentReference doc = db.collection("shop info").document(mAuth.getUid());
        doc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e != null){
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
                if(documentSnapshot != null && documentSnapshot.exists()){
                    nameEditText.setText((String)documentSnapshot.get("name"));
                    shopNameEditText.setText((String)documentSnapshot.get("shop name"));
                    upiIdEditText.setText((String)documentSnapshot.get("upi id"));
                    mobileNoEditText.setText((String)documentSnapshot.get("mobile no"));
                    emailIdEditText.setText((String)documentSnapshot.get("email id"));

                    nameEditText.setEnabled(false);
                    shopNameEditText.setEnabled(false);
                    upiIdEditText.setEnabled(false);
                    mobileNoEditText.setEnabled(false);
                    emailIdEditText.setEnabled(false);
                }
                else{
                    Toast.makeText(getApplicationContext(),"no Data to show !!!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        DownloadTask task = new DownloadTask();
        try{
            task.execute(mAuth.getCurrentUser().getPhotoUrl().toString());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(in);

                return bitmap;

            } catch (Exception e) {
                e.printStackTrace();

                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            profileImageView.setImageBitmap(bitmap);
        }
    }

    public void logout(View view){
        mAuth.signOut();
        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
