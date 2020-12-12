package in.programy.noqpartner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.noqpartner.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    GoogleSignInOptions gso;
    int RC_SIGN_IN;
    GoogleSignInClient mGoogleSignInClient;
    public FirebaseAuth mAuth;
    SignInButton signInButton;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = FirebaseFirestore.getInstance();

        RC_SIGN_IN = 0;

        // Configure Google Sign In
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();

        signInButton = findViewById(R.id.singInButton);

        if (mAuth.getCurrentUser() == null){

        }
        else{
            StartWork();
        }

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                 startActivityForResult(signInIntent, RC_SIGN_IN);

            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("info", "Google sign in failed", e);
                if(e.getStatusCode() == 7) {
                    Snackbar.make(findViewById(R.id.activityMainLayout), "No Internet Connection", Snackbar.LENGTH_SHORT).show();
                    // ...
                }
                else{
                    Snackbar.make(findViewById(R.id.activityMainLayout), "Some Error Occurred", Snackbar.LENGTH_SHORT).show();
                }
            }
        }

    }


    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.d("info", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                            if(isNew){
                                FirebaseUser user = mAuth.getCurrentUser();
                                Map<String, Object> data = new HashMap<>();
                                data.put("Name", user.getDisplayName());
                                data.put("Phone Number", user.getPhoneNumber());
                                data.put("Provider Id", user.getProviderId());
                                data.put("Photo Url", user.getPhotoUrl().toString());
                                data.put("Email id", user.getEmail());
                                data.put("Uid", user.getUid());
                                data.put("firebase id",acct.getId());
                                data.put("shop status",false);

                                Log.i("info",data.toString());
                                db.collection("sellers").document(user.getUid()).set(data).
                                        addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Snackbar.make(findViewById(R.id.activityMainLayout), "Successfully Registered", Snackbar.LENGTH_SHORT).show();
                                                Start();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Snackbar.make(findViewById(R.id.activityMainLayout), "Error Occurred.. Try After Some time!!! ", Snackbar.LENGTH_SHORT).show();
                                            }
                                        });

                                Start();
                            }
                            else{

                                StartWork();

                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Snackbar.make(findViewById(R.id.activityMainLayout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }

    public void Start(){
        Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
        startActivity(intent);
    }

    public void StartWork(){
        Intent intent = new Intent(MainActivity.this,homeActivity.class);
        startActivity(intent);
    }

}
