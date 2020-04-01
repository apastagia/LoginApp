package com.example.myapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class RegistrationActivity extends AppCompatActivity {

    private EditText userName, userEmail, userPassword, userAge;
    private Button regButton;
    private TextView userLogin;
    private ImageView userProfilePic;
    private static int PICK_IMAGE = 123;
    Uri imagePath;

    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    String age, name, email, password;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK && data.getData()!= null) {
            imagePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                userProfilePic.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setUIViews();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        storageReference = firebaseStorage.getReference();
        //StorageReference myRef1 = storageReference.child(firebaseAuth.getUid());

        userProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select image"), PICK_IMAGE);
            }
        });

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    String user_email = userEmail.getText().toString().trim();
                    String user_password = userPassword.getText().toString().trim();

                    firebaseAuth.createUserWithEmailAndPassword(user_email, user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                sendUserData();
                                firebaseAuth.signOut();
                                Toast.makeText(RegistrationActivity.this, "upload completed", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                                /*Toast.makeText(RegistrationActivity.this, "Successfully Register", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegistrationActivity.this, MainActivity.class));*/
                                //sendEmailVerification();
                            }else {
                                Toast.makeText(RegistrationActivity.this, "Failed", Toast.LENGTH_SHORT).show();

                            }}
                    });
                }
            }
        });

        userLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                Toast.makeText(RegistrationActivity.this, "success", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUIViews() {
        userName = (EditText) findViewById(R.id.etuserName);
        userEmail = (EditText) findViewById(R.id.etuserEmail);
        userPassword = (EditText) findViewById(R.id.etuserPassword);
        regButton = (Button) findViewById(R.id.btnRegister);
        userLogin = (TextView) findViewById(R.id.tvregister);
        userAge = (EditText)findViewById(R.id.etAge);
        userProfilePic = (ImageView)findViewById(R.id.imgprofile);
    }

    private Boolean validate() {
        Boolean result = false;

        age = userAge.getText().toString();
        name = userName.getText().toString();
        email = userEmail.getText().toString();
        password = userPassword.getText().toString();

        if(name.isEmpty() || email.isEmpty() || password.isEmpty() || age.isEmpty() || imagePath == null)
        {
            Toast.makeText(this, "fill required field", Toast.LENGTH_SHORT).show();
        }
        else
        {
            result = true;
        }
        return result;
    }

    private void sendEmailVerification(){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser!=null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        sendUserData();
                        Toast.makeText(RegistrationActivity.this, "verified successfully", Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                    }
                    else
                    {
                        Toast.makeText(RegistrationActivity.this, "not sent", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendUserData()
    {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference(firebaseAuth.getUid());
        StorageReference imageReference = storageReference.child(firebaseAuth.getUid()).child("Images").child("Profile Pic");
        UploadTask uploadTask = imageReference.putFile(imagePath);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegistrationActivity.this, "upload failed", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(RegistrationActivity.this, "upload successful", Toast.LENGTH_SHORT).show();
            }
        });

        UserProfile userProfile = new UserProfile(age, name, email);
        myRef.setValue(userProfile);
    }
}
