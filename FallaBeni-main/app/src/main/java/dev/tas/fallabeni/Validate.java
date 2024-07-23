package dev.tas.fallabeni;

import android.app.Activity;
import android.util.Patterns;
import android.widget.Toast;

public class Validate {


    public static boolean validateEmailAddress(String emailAddress) {

        return Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches();
    }

    public static boolean validatePassword(String password) {

        return password != null && password.length() > 7;
    }

    public static boolean validate(Activity activity, String emailAddress, String password, String confirmPassword) {

        if (!password.equals(confirmPassword)) {

            Toast.makeText(activity, "Girilen şifreler aynı değil!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!validateEmailAddress(emailAddress)) {

            Toast.makeText(activity, "Girilen e-posta kurallara uygun değil!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!validatePassword(password)) {
            Toast.makeText(activity, "Girilen şifre kurallara uygun değil!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public static boolean validate(Activity activity, String emailAddress, String password) {

        if (!validateEmailAddress(emailAddress)) {

            Toast.makeText(activity, "Girilen e-posta kurallara uygun değil!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!validatePassword(password)) {
            Toast.makeText(activity, "Girilen şifre kurallara uygun değil!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
