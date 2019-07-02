package com.pawstime;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.widget.ImageView;
import android.widget.TextView;

public class PetCardView extends CardView {
    public TextView name;
    public ImageView picture;
    public PetCardView(@NonNull Context context) {
        super(context);
        inflate(getContext(), R.layout.pet_card_item_layout, this);
        name = findViewById(R.id.petCardItemName);
    }

    public void setName(String newName) {
        name.setText(newName);
    }

//    public void setPicture() {}
}
