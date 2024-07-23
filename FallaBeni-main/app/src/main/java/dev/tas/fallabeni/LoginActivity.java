package dev.tas.fallabeni;

import static android.service.controls.ControlsProviderService.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;



import com.facebook.CallbackManager;

import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;



import dev.tas.fallabeni.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private SharedPreferences sharedPreferences;
    private static final String TAG = "LoginActivity";
    private static final String PREF_NAME = "LoginPrefs";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_REMEMBER_ME = "rememberMe";
    CallbackManager mCallbackManager;
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseDatabase database;
    EditText userEmail;
    EditText userPassword;
    CheckBox rememberMeCheckBox;
    TextView forgetPassword;
    TextView signupText;
    Button loginButton;
    LoginButton facebookLoginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        database = FirebaseDatabase.getInstance("https://falla-beni-1553c-default-rtdb.firebaseio.com/");
        mAuth = FirebaseAuth.getInstance();

        userEmail = binding.loginEmail;
        userPassword = binding.loginPassword;
        rememberMeCheckBox = binding.rememberMeCheckbox;
        forgetPassword = binding.forgetPasswordText;
        loginButton = binding.loginButton;
        signupText = binding.signupRedirectText;


        // SharedPreferences tanımlanması
        sharedPreferences = this.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // Checkbox'ın durumunun kontrolü ve gerekirse kullanıcı adı ve şifrenin EditText'lere yazılması
        boolean rememberMe = sharedPreferences.getBoolean(KEY_REMEMBER_ME, false);

        if (rememberMe) {
            String savedEmail = sharedPreferences.getString(KEY_EMAIL, "");
            String savedPassword = sharedPreferences.getString(KEY_PASSWORD, "");
            userEmail.setText(savedEmail);
            userPassword.setText(savedPassword);
            rememberMeCheckBox.setChecked(true);
        }

        listeners();


    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        if (mAuth.getCurrentUser() != null) {
//            Toast.makeText(LoginActivity.this, "Zaten oturum açılmış", Toast.LENGTH_SHORT).show();
//            startActivity(new Intent(LoginActivity.this, MainActivity.class));
//            finish();
//        } else {
//            Toast.makeText(LoginActivity.this, "Giriş yapabilirsiniz", Toast.LENGTH_SHORT).show();
//        }
//    }

    public void loginUser() {

        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();

        if (Validate.validate(LoginActivity.this, email, password)) {

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            user = mAuth.getCurrentUser();

                            assert user != null;
                            if (user.isEmailVerified()) {
                                Toast.makeText(LoginActivity.this, "Kullanıcı girişi başarılı", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                user.sendEmailVerification();
                                mAuth.signOut();
                                showAlertDialog();

                            }

                        } else {
                            Toast.makeText(LoginActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }

                    });
        }

        // Checkbox'ın durumunu kaydet
        boolean isChecked = rememberMeCheckBox.isChecked();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_REMEMBER_ME, isChecked);
        editor.apply();

        //Eğer beni hatırla işaretliyse, email ve şifreyi kaydet değilse şifre ve maili sil
        if (isChecked) {
            editor.putString(KEY_EMAIL, email);
            editor.putString(KEY_PASSWORD, password);
            editor.apply();
        } else {
            editor.remove(KEY_EMAIL);
            editor.remove(KEY_PASSWORD);
            editor.apply();
        }
    }

    private void showAlertDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("E-Posta Doğrulanmadı");
        builder.setMessage("Lütfen e-postanızı doğrulayın. Doğrulama işlemini yapmadan giriş yapamazsınız!");

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void listeners() {

        signupText.setOnClickListener(v -> {

            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
            finish();
        });

        loginButton.setOnClickListener(v -> loginUser());

        forgetPassword.setOnClickListener(v -> sendPasswordResetEmail());
    }

    private void sendPasswordResetEmail() {

        String email = userEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplication(), "Lütfen e-postanızı giriniz", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplication(), "Şifrenizi sıfırlamanız için size talimatları gönderdik!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplication(), "Şifrenizi sıfırlama için e-posta gönderilemedi!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}