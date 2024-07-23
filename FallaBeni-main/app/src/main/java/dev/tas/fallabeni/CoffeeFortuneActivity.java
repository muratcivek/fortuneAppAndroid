package dev.tas.fallabeni;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.textclassifier.ConversationActions;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;



import java.io.IOException;

import dev.tas.fallabeni.databinding.ActivityCoffeeFortuneBinding;

public class CoffeeFortuneActivity extends AppCompatActivity {

    private ActivityCoffeeFortuneBinding binding;
    private PermittedTask galleryPermissionTask;
    private ActivityResultLauncher<Intent> galleryLauncher;


    Toolbar backIcon;
    RadioButton generalFortune;
    RadioButton loveFortune;
    RadioButton careerFortune;
    RadioButton healthFortune;
    RadioButton lastClickedRadioButton;
    ImageView addCoffeePhoto1;
    ImageView addCoffeePhoto2;
    ImageView addCoffeePhoto3;
    ImageView lastClickedImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCoffeeFortuneBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        lastClickedRadioButton = null;
        lastClickedImage = null;

        backIcon = binding.toolbarBackIcon;
        addCoffeePhoto1 = binding.imageViewAddPhoto1;
        addCoffeePhoto2 = binding.imageViewAddPhoto2;
        addCoffeePhoto3 = binding.imageViewAddPhoto3;
        generalFortune = binding.btnGeneral;
        loveFortune = binding.btnLove;
        careerFortune = binding.btnCareer;
        healthFortune = binding.btnHealth;

        galleryPermissionTask = new PermittedTask(this, Manifest.permission.READ_MEDIA_IMAGES) {
            @Override
            public void granted() {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryLauncher.launch(galleryIntent);
            }

            @Override
            protected void denied() {
                Toast.makeText(CoffeeFortuneActivity.this, "Galeriye erişim izni reddedildi", Toast.LENGTH_SHORT).show();
            }
        };

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                Uri selectedImageUri = result.getData().getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    int targetWidth = (int) getResources().getDimension(R.dimen.space_128dp);
                    int targetHeight = (int) getResources().getDimension(R.dimen.space_128dp);
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true);
                    Bitmap roundedBitmap = getRoundedBitmap(scaledBitmap);
                    if (lastClickedImage == addCoffeePhoto1) {
                        addCoffeePhoto1.setImageBitmap(roundedBitmap);
                    } else if (lastClickedImage == addCoffeePhoto2) {
                        addCoffeePhoto2.setImageBitmap(roundedBitmap);
                    } else if (lastClickedImage == addCoffeePhoto3) {
                        addCoffeePhoto3.setImageBitmap(roundedBitmap);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });



        listeners();
    }

    private void listeners() {

        backIcon.setOnClickListener(v -> {
            Intent intent = new Intent(CoffeeFortuneActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        generalFortune.setOnClickListener(v -> {
            onClickRadioButton(generalFortune);
        });

        loveFortune.setOnClickListener(v -> {
            onClickRadioButton(loveFortune);
        });

        careerFortune.setOnClickListener(v -> {
            onClickRadioButton(careerFortune);
        });

        healthFortune.setOnClickListener(v -> {
            onClickRadioButton(healthFortune);
        });

        addCoffeePhoto1.setOnClickListener(v -> {
            lastClickedImage = addCoffeePhoto1;
            galleryPermissionTask.run();
        });

        addCoffeePhoto2.setOnClickListener(v -> {
            lastClickedImage = addCoffeePhoto2;
            galleryPermissionTask.run();
        });

        addCoffeePhoto3.setOnClickListener(v -> {
            lastClickedImage = addCoffeePhoto3;
            galleryPermissionTask.run();
        });
    }


    // RadioButton'a tıklandığında rengini değiştirir
    private void onClickRadioButton(RadioButton button) {

        if (!button.isSelected()) {
            button.setSelected(true);
            if (lastClickedRadioButton != null && lastClickedRadioButton != button) {
                lastClickedRadioButton.setSelected(false);
            }
        } else {
            button.setSelected(false);
            if (lastClickedRadioButton == button) {
                lastClickedRadioButton = null;
                return;
            }
        }
        lastClickedRadioButton = button;
    }

    public Bitmap getRoundedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, bitmap.getWidth() / 2, bitmap.getHeight() / 2, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }




}