package dev.tas.fallabeni;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import dev.tas.fallabeni.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private RequestQueue requestQueue;
    private Response.Listener<String> responseListener;
    private Response.ErrorListener responseErrorListener;
    private StringRequest stringRequest;
    private String dailyHoroscopeText;
    private String dailyHoroscopeTitle;
    FirebaseDatabase database;
    FirebaseAuth mAuth;

    TextView username;
    TextView balance;
    ImageView coffeeFortune;
    ImageView tarotFortune;
    ImageView advSupportBtn;
    RelativeLayout mainPageLayout;
    ImageView aquaris, pisces, aries, taurus, gemini, capricorne, cancer, leo, virgo, libra, scorpio, sagittarius;
    TextView dailyHoroscope;
    TextView dailyTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance("https://falla-beni-1553c-default-rtdb.firebaseio.com/");


        getUserInfoFromDatabase();

        username = binding.textViewUsername;
        balance = binding.textViewBalance;
        coffeeFortune = binding.imgViewCoffeeFortune;
        tarotFortune = binding.imgViewTarotFortune;
        mainPageLayout = binding.mainPage;
        advSupportBtn = binding.advisorySupportButton;
        capricorne = binding.capricorne;
        aquaris = binding.aquaris;
        pisces = binding.pisces;
        aries = binding.aries;
        taurus = binding.taurus;
        gemini = binding.gemini;
        cancer = binding.cancer;
        leo = binding.leo;
        virgo = binding.virgo;
        libra = binding.libra;
        scorpio = binding.scorpio;
        sagittarius = binding.sagittarius;

        listeners();

    }

    private void listeners() {

        coffeeFortune.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CoffeeFortuneActivity.class);
            startActivity(intent);
            finish();
        });

        advSupportBtn.setOnClickListener(v -> createPopUpWindow());

        capricorne.setOnClickListener(v -> getDailyHoroscope("https://www.mynet.com/kadin/burclar-astroloji/oglak-burcu-gunluk-yorumu.html"));
        aquaris.setOnClickListener(v -> getDailyHoroscope("https://www.mynet.com/kadin/burclar-astroloji/kova-burcu-gunluk-yorumu.html"));
        pisces.setOnClickListener(v -> getDailyHoroscope("https://www.mynet.com/kadin/burclar-astroloji/balik-burcu-gunluk-yorumu.html"));
        aries.setOnClickListener(v -> getDailyHoroscope("https://www.mynet.com/kadin/burclar-astroloji/koc-burcu-gunluk-yorumu.html"));
        taurus.setOnClickListener(v -> getDailyHoroscope("https://www.mynet.com/kadin/burclar-astroloji/boga-burcu-gunluk-yorumu.html"));
        gemini.setOnClickListener(v -> getDailyHoroscope("https://www.mynet.com/kadin/burclar-astroloji/ikizler-burcu-gunluk-yorumu.html"));
        cancer.setOnClickListener(v -> getDailyHoroscope("https://www.mynet.com/kadin/burclar-astroloji/yengec-burcu-gunluk-yorumu.html"));
        leo.setOnClickListener(v -> getDailyHoroscope("https://www.mynet.com/kadin/burclar-astroloji/aslan-burcu-gunluk-yorumu.html"));
        virgo.setOnClickListener(v -> getDailyHoroscope("https://www.mynet.com/kadin/burclar-astroloji/basak-burcu-gunluk-yorumu.html"));
        libra.setOnClickListener(v -> getDailyHoroscope("https://www.mynet.com/kadin/burclar-astroloji/terazi-burcu-gunluk-yorumu.html"));
        scorpio.setOnClickListener(v -> getDailyHoroscope("https://www.mynet.com/kadin/burclar-astroloji/akrep-burcu-gunluk-yorumu.html"));
        sagittarius.setOnClickListener(v -> getDailyHoroscope("https://www.mynet.com/kadin/burclar-astroloji/yay-burcu-gunluk-yorumu.html"));

    }

    private void getUserInfoFromDatabase() {

        if (mAuth != null) {
            String userID = mAuth.getUid();
            DatabaseReference reference = database.getReference("Registered Users").child(userID);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            String retrievedUsername = user.getUserName();
                            String retrievedBalance = String.valueOf(user.getBalance());
                            username.setText(retrievedUsername);
                            balance.setText(retrievedBalance);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("DatabaseError", "Veritabanı işlemi iptal edildi veya hata meydana geldi: " + error.getMessage());
                }
            });
        } else {
            // Kullanıcı oturum açmamış
            Log.e("UserIDError", "Kullanıcı oturum açmamış");
        }
    }

    private void createPopUpWindow() {

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popUpView = inflater.inflate(R.layout.activity_main_popup, null);

        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;

        PopupWindow popupWindow = new PopupWindow(popUpView, width, height, focusable);
        popupWindow.setBackgroundDrawable(new ColorDrawable(
                android.graphics.Color.TRANSPARENT));
        mainPageLayout.post(() -> popupWindow.showAtLocation(mainPageLayout, Gravity.BOTTOM, 0, 0));

        popUpView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });


    }

    private void createHoroscopePopUpWindow() {

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popUpView = inflater.inflate(R.layout.activity_horoscope_popup, null);

        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;

        PopupWindow popupWindow = new PopupWindow(popUpView, width, height, focusable);
        popupWindow.setBackgroundDrawable(new ColorDrawable(
                android.graphics.Color.TRANSPARENT));

        dailyHoroscope = popUpView.findViewById(R.id.dailyHoroscopeText);
        dailyTitle = popUpView.findViewById(R.id.dailyHoroscopeTitle);
        dailyTitle.setText(dailyHoroscopeTitle);
        dailyHoroscope.setText(dailyHoroscopeText);
        mainPageLayout.post(() -> popupWindow.showAtLocation(mainPageLayout, Gravity.CENTER, 0, 0));

        popUpView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }

    private void stringListenerSettings() {

        responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                parseResponse(s);
                createHoroscopePopUpWindow();
            }
        };

        responseErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(MainActivity.this, volleyError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void parseResponse(String s) {

        Document document = Jsoup.parse(s);
        Elements contextual = document.getElementById("contextual").getAllElements();
        Elements divElements = document.select("div.horoscope-detail-title");
        for (Element divElement : divElements) {
            // Her bir div elementi için h1 elementini içinde arayın
            Element h1Element = divElement.selectFirst("h1.mb-0");
            dailyHoroscopeTitle = h1Element.text();
        }

        dailyHoroscopeText = contextual.text();
    }

    private void getDailyHoroscope(String URL) {

        requestQueue = Volley.newRequestQueue(MainActivity.this);
        stringListenerSettings();
        stringRequest = new StringRequest(Request.Method.GET, URL, responseListener, responseErrorListener);
        requestQueue.add(stringRequest);
    }
}