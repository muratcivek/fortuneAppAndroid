package dev.tas.fallabeni;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import dev.tas.fallabeni.databinding.ActivitySignUpBinding;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    FirebaseAuth mAuth;
    EditText username;
    EditText userMail;
    EditText userPassword;
    EditText userPasswordConfirm;
    Button signUpButton;
    TextView loginText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        username = binding.signupName;
        userMail = binding.signupEmail;
        userPassword = binding.signupPassword;
        userPasswordConfirm = binding.signupConfirmPassword;
        signUpButton = binding.signupButton;
        loginText = binding.loginRedirectText;


        mAuth = FirebaseAuth.getInstance();

        listeners();
    }

    public void signUp() {

        String userMail, userPassword, userPasswordConfirm, username;
        userMail = this.userMail.getText().toString();
        userPassword = this.userPassword.getText().toString();
        userPasswordConfirm = this.userPasswordConfirm.getText().toString();
        username = this.username.getText().toString();

        if (Validate.validate(SignUpActivity.this, userMail, userPassword, userPasswordConfirm)) {

            mAuth.createUserWithEmailAndPassword(userMail, userPassword)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "Kullanıcı kaydı başarıyla tamamlandı", Toast.LENGTH_SHORT).show();

                            FirebaseUser user = mAuth.getCurrentUser();

                            User newUser = new User(username, userMail, userPassword);
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Registered Users");
                            assert user != null;
                            databaseReference.child(user.getUid()).setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        user.sendEmailVerification();
                                        mAuth.signOut();

                                        Toast.makeText(SignUpActivity.this, "Kullanıcı kaydı başarıyla gerçekleştirildi. Lütfen e-postanızı doğrulayın",
                                                Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(SignUpActivity.this, "Kullanıcı kaydı gerçekleştirilemedi. Lütfen tekrar deneyin",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignUpActivity.this, task.getException().getLocalizedMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void listeners() {

        loginText.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
        });
        signUpButton.setOnClickListener(v -> signUp());
    }


}