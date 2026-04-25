package com.example.auto;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.Gravity;
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
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.lifecycle.ViewModelProvider;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.StandardProtocolFamily;
import java.security.Key;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.List;

public class CarDetails extends AppCompatActivity {
    Notifications notification;
    ImageView image_of_car;
    Bitmap bitmap, decoded_bitmap;
    EditText model, platenumber, insurance, vignette, inspection;
    TextView Debug;
    Button update_car, delete_car, update, discard, confirm_deletion;
    ImageButton close, add_photo, remove_photo;
    CardView layout;
    View spacer_1, spacer_2, dummy_spacer;
    String string_image, string_model, string_platenumber, string_insurance, string_vignette, string_inspection;
    Boolean is_default_photo, is_photo_changed;
    ActivityResultLauncher<Intent> get_image_from_storage_launcher;
    CarViewModel car_view_model;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate (Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.car_details);

        defines();
        pop_up();
        get_data();
        Debug.setText(string_image);
        disable_all_edit_texts();
        default_buttons_visibility(true);
        update_buttons_visibility(false);
        delete_buttons_visibility(false);
        Log.d("CarDetails_configure_pick_image_launcher","In onCreate func start");
        configure_pick_image_launcher();
        Log.d("CarDetails_configure_pick_image_launcher","In onCreate func end");
        //Debug.setText("test");











        update_car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enable_all_edit_texts();
                default_buttons_visibility(false);
                update_buttons_visibility(true);
                if(string_image == null || string_image.isEmpty()){
                    remove_photo.setVisibility(View.GONE);
                }
            }
        });

        delete_car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                default_buttons_visibility(false);
                delete_buttons_visibility(true);
            }
        });

        discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get_data();
                disable_all_edit_texts();
                default_buttons_visibility(true);
                update_buttons_visibility(false);
                delete_buttons_visibility(false);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update_data();
                disable_all_edit_texts();
                default_buttons_visibility(true);
                update_buttons_visibility(false);
            }
        });

        confirm_deletion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DeletingCarChecker","platenumber = " + platenumber.getText().toString());
                car_view_model.delete_data(platenumber.getText().toString());
                Toast.makeText(getApplicationContext(),"Car is deleted successfully!",Toast.LENGTH_SHORT).show();
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    finish();
                }, 1500);
            }
        });

        add_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pick_image();
            }
        });

        remove_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image_of_car.setImageResource(R.drawable.default_photo_for_car);
                is_default_photo = true;
                remove_photo.setVisibility(View.GONE);
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


        close = (ImageButton) findViewById(R.id.imageButtonCarDetailsClose);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



    }

    private void defines(){
        layout = (CardView) findViewById(R.id.cardViewCarDetails);
        car_view_model = new ViewModelProvider(this).get(CarViewModel.class);
        delete_car = (Button) findViewById(R.id.buttonCarDetailsDeleteCar);
        update_car = (Button) findViewById(R.id.buttonCarDetailsUpdateCar);
        update = (Button) findViewById(R.id.buttonCarDetailsUpdate);
        discard = (Button) findViewById(R.id.buttonCarDetailsDiscard);
        confirm_deletion = (Button) findViewById(R.id.buttonCarDetailsConfirmDeletion);
        add_photo = (ImageButton) findViewById(R.id.imageButtonCarDetailsAddPhoto);
        remove_photo = (ImageButton) findViewById(R.id.imageButtonCarDetailRemovePhoto);
        model = (EditText) findViewById(R.id.editTextCarDetailsModelOfCar);
        platenumber = (EditText) findViewById(R.id.editTextCarDetailsPlateNumber);
        insurance = (EditText) findViewById(R.id.editTextCarDetailsInsurance);
        vignette = (EditText) findViewById(R.id.editTextCarDetailsVignette);
        inspection = (EditText) findViewById(R.id.editTextCarDetailsTech);
        Debug = (TextView) findViewById(R.id.textViewCarDatailsDebug);
        image_of_car = (ImageView) findViewById(R.id.imageViewCarDetailsImageOfCar);
        spacer_1 = (View) findViewById(R.id.viewCarDetailsSpacer1);
        spacer_2 = (View) findViewById(R.id.viewCarDetailsSpacer2);
        dummy_spacer = (View) findViewById(R.id.viewCarDetailsDummySpacer);
        string_platenumber = getIntent().getStringExtra("platenumber");
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
        LocalDate today = LocalDate.now();
        DatePickerDialog datePickerDialog = new DatePickerDialog( CarDetails.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                String date = String.format("%04d-%02d-%02d",year,month+1,dayOfMonth);
                textView.setText(date);
            }
        }, today.getYear(),
                today.getMonthValue() -1,
                today.getDayOfMonth());
        //datePickerDialog.updateDate(today.getYear(), today.getMonthValue() -1,today.getDayOfMonth());
        datePickerDialog.show();
    }

    private void enable_all_edit_texts(){
        enable_disable_one_edit_text(model,true);
        enable_disable_one_edit_text(insurance,true);
        enable_disable_one_edit_text(vignette,true);
        enable_disable_one_edit_text(inspection,true);
    }

    private void disable_all_edit_texts(){
        enable_disable_one_edit_text(model,false);
        enable_disable_one_edit_text(platenumber,false);
        enable_disable_one_edit_text(insurance,false);
        enable_disable_one_edit_text(vignette,false);
        enable_disable_one_edit_text(inspection,false);
    }

    private void enable_disable_one_edit_text(EditText editText, Boolean state){
        editText.setEnabled(state);
        editText.setTextColor(state ? Color.BLACK : Color.DKGRAY);
    }

    private void default_buttons_visibility(Boolean state){
        int view = state ? View.VISIBLE : View.GONE;
        update_car.setVisibility(view);
        spacer_1.setVisibility(view);
        delete_car.setVisibility(view);
    }

    private void update_buttons_visibility(Boolean state){
        int view = state ? View.VISIBLE : View.GONE;
        int view_inverted = state ? View.GONE : View.VISIBLE;
        update.setVisibility(view);
        spacer_2.setVisibility(view);
        discard.setVisibility(view);
        add_photo.setVisibility(view);
        remove_photo.setVisibility(view);
        dummy_spacer.setVisibility(view_inverted);
    }
    private void delete_buttons_visibility(Boolean state){
        int view = state ? View.VISIBLE : View.GONE;
        confirm_deletion.setVisibility(view);
        spacer_2.setVisibility(view);
        discard.setVisibility(view);
    }

    private void set_data(Car car){
        if(!car.get_image().isEmpty()){
            decode_and_set_image(car.get_image());
            is_default_photo = false;
        } else { is_default_photo = true; }
        model.setText(car.get_model());
        platenumber.setText(car.get_platenumber());
        insurance.setText(car.get_insurance());
        vignette.setText(car.get_vignette());
        inspection.setText(car.get_inspection());
    }

    private void get_data(){
        car_view_model.get_data(string_platenumber);
        car_view_model.get_car_data().observe(this, car_data -> {
            if (car_data != null) {
                set_data(car_data);
                //notification.show_car_notification(this, "car_channel_id",car_data);
            }
        });
        is_photo_changed = false;
    }

    private void update_data(){
        get_data_from_all_edit_texts();
        if (string_model.isEmpty() || platenumber.toString().isEmpty() ||
                string_insurance.isEmpty() ||
                string_vignette.isEmpty() ||
                string_inspection.isEmpty())
            Toast.makeText(getApplicationContext(), "All fields are mandatory!", Toast.LENGTH_SHORT).show();
        else {
            car_view_model.update_data(
                    string_model,
                    string_platenumber,
                    string_insurance,
                    string_vignette,
                    string_inspection);
            if(is_photo_changed){
                string_image = image_into_string(image_of_car);
                car_view_model.insert_image(string_image, string_platenumber);
            }
            else if (is_default_photo) {
                car_view_model.remove_image(string_platenumber);
            }
        }
    }

    private void decode_and_set_image(String image){
        byte[] decoded_byte_array = Base64.getDecoder().decode(image);
        decoded_bitmap = BitmapFactory.decodeByteArray(decoded_byte_array, 0, decoded_byte_array.length);
        image_of_car.setImageBitmap(decoded_bitmap);
        string_image = Base64.getEncoder().encodeToString(decoded_byte_array);
    }

    private String encode_image(Bitmap bitmap_image){
        ByteArrayOutputStream byte_array_output_stream = new ByteArrayOutputStream();
        bitmap_image.compress(Bitmap.CompressFormat.JPEG, 100, byte_array_output_stream);
        byte[] bytes = byte_array_output_stream.toByteArray();
        return string_image = Base64.getEncoder().encodeToString(bytes);
    }

    private void get_data_from_all_edit_texts(){
        string_model = get_data_from_one_edit_text(model);
        string_insurance = get_data_from_one_edit_text(insurance);
        string_vignette = get_data_from_one_edit_text(vignette);
        string_inspection = get_data_from_one_edit_text(inspection);
    }

    private String get_data_from_one_edit_text(EditText edit_text){
        return edit_text.getText().toString();
    }

    private void pick_image(){
        Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
        get_image_from_storage_launcher.launch(intent);
    }

    private void configure_pick_image_launcher(){
        Log.d("CarDetails_configure_pick_image_launcher","Start of function");
        get_image_from_storage_launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Log.d("CarDetails_configure_pick_image_launcher","Start of onActivityResult");
                if (result.getResultCode() == Activity.RESULT_OK){
                    Log.d("CarDetails_configure_pick_image_launcher","RESULT_OK");
                    Intent data = result.getData();
                    Uri uri = data.getData();
                    try {
                        Log.d("CarDetails_configure_pick_image_launcher","Start of try{}");
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        image_of_car.setImageBitmap(bitmap);
                        encode_image(bitmap);
                        is_photo_changed = true;
                        Log.d("CarDetails_configure_pick_image_launcher","is_default_photo - " + is_photo_changed);
                        remove_photo.setVisibility(View.VISIBLE);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
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
