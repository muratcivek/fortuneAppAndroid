package dev.tas.fallabeni;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public abstract class PermittedTask {

    private ActivityResultLauncher<String> launcher;
    private String permission;
    private AppCompatActivity activity;

    public PermittedTask(AppCompatActivity activity, String permission) {
        this.activity = activity;
        this.permission = permission;
        launcher = activity.registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                granted();
            } else {
                denied();
            }
        });
    }

    public abstract void granted();

    protected void denied() {
        // Kullanıcı izni reddettiğinde yapılacak işlemler buraya eklenmeli
        // Örneğin, kullanıcıyı bilgilendirme toast mesajı gösterilebilir.
    }

    private void showRequestPermissionRationale() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("İzin Gerekli")
                .setMessage("Uygulamanın bu işlemi gerçekleştirebilmesi için izne ihtiyacı var. Devam etmek istiyor musunuz?")
                .setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        launcher.launch(permission);
                    }
                })
                .setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        denied();
                    }
                })
                .show();
    }

    public void run() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED) {
                granted();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    showRequestPermissionRationale();
                } else {
                    launcher.launch(permission);
                }
            }
        }
    }


}
