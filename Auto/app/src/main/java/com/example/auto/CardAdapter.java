package com.example.auto;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardViewHolder> {
    List<Car> car_list;
    Context context;
    Bitmap bitmap, decoded_bitmap;

    public CardAdapter(Context context, List<Car> car_list){
        this.context = context;
        this.car_list = car_list;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CardViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_of_car, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        holder.name_of_car.setText(car_list.get(position).get_model_plus_platenumber());
        if(!car_list.get(position).get_image().isEmpty()){
            set_image(holder.image_of_car,car_list.get(position).get_image());
        }
        else {
            holder.image_of_car.setImageResource(R.drawable.default_photo_for_car);
        }
        String platenumber = car_list.get(position).get_platenumber();

        holder.view_car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CarDetails.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("platenumber", platenumber);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return car_list.size();
    }

    public void setItems(List<Car> car_list){
        this.car_list = car_list;
        notifyDataSetChanged();
    }

    private String get_image(@NonNull ImageView image_view){
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

    private void set_image(ImageView image_view, String string_image){
        byte[] decoded_byte_array = Base64.getDecoder().decode(string_image);
        decoded_bitmap = BitmapFactory.decodeByteArray(decoded_byte_array, 0, decoded_byte_array.length);
        image_view.setImageBitmap(decoded_bitmap);
    }
}