package com.example.auto;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class CardViewHolder extends RecyclerView.ViewHolder {

    ImageView image_of_car;
    TextView name_of_car;
    Button view_car;
    public CardViewHolder(@NonNull View itemView){
        super(itemView);
        image_of_car = itemView.findViewById(R.id.imageViewCardViewCarPhoto);
        name_of_car = itemView.findViewById(R.id.textViewCardViewNameOfCar);
        view_car = itemView.findViewById(R.id.buttonCardViewDetails);
    }
}
