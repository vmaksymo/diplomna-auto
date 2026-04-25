package com.example.auto;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Calendar;

public class AddCar extends AppCompatActivity {
    ImageView image_of_car;
    EditText model, platenumber, insurance, vignette, inspection;
    TextView errorDebug;
    Button add;
    ImageButton Close;
    CardView layout;
    Boolean is_default_photo;
    Bitmap bitmap, decoded_bitmap;
    String username, string_image, string_model, string_platenumber, string_insurance, string_vignette, string_inspection;
    CarViewModel car_view_model;
    public static final String SHARED_PREFS = "sharedPrefs";
    private static final String LOCAL_HOST = "http://192.168.1.2";


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate (Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.add_car);

        defines();
        pop_up();

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();








        






        add.setAllCaps(false);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insert_data();
                clear_all_data();
                Toast.makeText(getApplicationContext(),"Car is added successfully!",Toast.LENGTH_SHORT).show();
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    finish();
                }, 500);
            }
        });




        insurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCalendarBox(insurance);
            }
        });


        vignette.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCalendarBox(vignette);
            }
        });


        inspection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCalendarBox(inspection);
            }
        });



        Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



    }

    private void defines(){
        layout = (CardView) findViewById(R.id.cardViewAddCar);
        car_view_model = new ViewModelProvider(this).get(CarViewModel.class);
        image_of_car = (ImageView) findViewById(R.id.imageViewAddCarImageOfCar);
        model = (EditText) findViewById(R.id.editTextAddCarModel);
        platenumber = (EditText) findViewById(R.id.editTextAddCarPlatenumber);
        add = (Button) findViewById(R.id.buttonAddCarAddCar);
        model = (EditText) findViewById(R.id.editTextAddCarModel);
        platenumber = (EditText) findViewById(R.id.editTextAddCarPlatenumber);
        insurance = (EditText) findViewById(R.id.editTextAddCarInsurance);
        vignette = (EditText) findViewById(R.id.editTextAddCarVignette);
        inspection = (EditText) findViewById(R.id.editTextAddCarInspection);
        errorDebug = (TextView) findViewById(R.id.textView);
        Close = (ImageButton) findViewById(R.id.imageButtonAddCarClose);
        username = getIntent().getStringExtra("username");
        car_view_model.set_username(username);

        is_default_photo = true;
    }

    private void pop_up(){


        ViewGroup.LayoutParams layoutParams = layout.getLayoutParams();
        int width = layoutParams.width;
        getWindow().setLayout(width, ListPopupWindow.WRAP_CONTENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -100;
        getWindow().setAttributes(params);

        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        ViewGroup root = (ViewGroup) getWindow().getDecorView().getRootView();
        apply_dim(root);
    }

    private static void apply_dim(ViewGroup parent){
        Drawable dim = new ColorDrawable(Color.BLACK);
        dim.setBounds(0, 0, parent.getWidth(), parent.getHeight());
        dim.setAlpha(200);

        ViewGroupOverlay overlay = parent.getOverlay();
        overlay.add(dim);
    }

    private void openCalendarBox(TextView textView) {

        DatePickerDialog datePickerDialog = new DatePickerDialog( AddCar.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                textView.setText(year + "/" + month + "/" + dayOfMonth);
            }
        }, getIntent().getIntExtra("yearToday",LocalDate.now().getYear()),
                getIntent().getIntExtra("monthToday",LocalDate.now().getMonthValue()),
                getIntent().getIntExtra("dayToday",LocalDate.now().getDayOfMonth()));
        datePickerDialog.updateDate(LocalDate.now().getYear(),LocalDate.now().getMonthValue()-1,LocalDate.now().getDayOfMonth());
        datePickerDialog.show();
    }

    private void insert_data(){
        get_data_from_all_edit_texts();
        if (string_model.isEmpty() || string_platenumber.isEmpty() ||
                string_insurance.isEmpty() ||
                string_vignette.isEmpty() ||
                string_inspection.isEmpty())
            Toast.makeText(getApplicationContext(), "All fields are mandatory!", Toast.LENGTH_SHORT).show();
        else {
            car_view_model.insert_data(
                    string_model,
                    string_platenumber,
                    string_insurance,
                    string_vignette,
                    string_inspection);
        }
        if(!is_default_photo) {
            string_image = image_into_string(image_of_car);
            car_view_model.insert_image(string_image, string_platenumber);
        }
    }

    private void clear_all_data(){
        model.setText(null);
        platenumber.setText(null);
        insurance.setText(null);
        vignette.setText(null);
        inspection.setText(null);
    }

    private void get_data_from_all_edit_texts(){
        string_model = get_data_from_one_edit_text(model);
        string_platenumber = get_data_from_one_edit_text(platenumber);
        string_insurance = get_data_from_one_edit_text(insurance);
        string_vignette = get_data_from_one_edit_text(vignette);
        string_inspection = get_data_from_one_edit_text(inspection);
    }
    private String get_data_from_one_edit_text(EditText edit_text){
        return edit_text.getText().toString();
    }
    private String image_into_string(@NonNull ImageView image_view){
        Drawable drawable = image_view.getDrawable();
        bitmap = null;
        if(drawable instanceof BitmapDrawable){
            bitmap = ((BitmapDrawable) image_view.getDrawable()).getBitmap();
        }
        else {
            bitmap = Bitmap.createBitmap(
                    image_view.getWidth(),
                    image_view.getHeight(),
                    Bitmap.Config.ARGB_8888
            );
            Canvas canvas = new Canvas(bitmap);
            image_view.draw(canvas);
        }
        ByteArrayOutputStream byte_array_output_stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byte_array_output_stream);
        byte[] bytes = byte_array_output_stream.toByteArray();
        final String image = Base64.getEncoder().encodeToString(bytes);
        return image;
    }

}
