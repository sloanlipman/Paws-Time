package com.pawstime;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.widget.TextView;

import com.pawstime.activities.BaseActivity;
import com.pawstime.activities.HomePage;


public class PetCard extends CardView {
    public ConstraintLayout layout;
    public CardView petCard;
    public TextView petCardName;
    public de.hdodenhof.circleimageview.CircleImageView picture;
    public PetCard(@NonNull Context context) {
        super(context);
        inflate(getContext(), R.layout.pet_card_layout, this);
        layout = findViewById(R.id.petCardLayout);
        petCard = findViewById(R.id.petCard);
        petCardName = findViewById(R.id.petCardName);
        picture = findViewById(R.id.petCardPicture);

        petCard.setOnClickListener(v -> click(context));
        picture.setOnClickListener(v -> click(context));
    }

    public void setName(String newName) {
        petCardName.setText(newName);
    }

    public String getName() {
        return petCardName.getText().toString();
    }

    public void click(Context context) {
        HomePage.clickPetCard(getName(), context);
    }

    public void setPicture(String path) {
        BaseActivity.loadPic(ImageSaver.ICON_SIZE, picture, path);
    }
}
