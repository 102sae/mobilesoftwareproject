package com.course.mydietapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;


public class AddfoodActivity extends AppCompatActivity {
    private RatingBar ratingBar;
    EditText addFood;
    EditText count;
    TimePicker time;
    EditText place;
    ImageView image;
    public FoodDao cFoodDao; // food db
    private Uri imageUri;

    private static final int REQUEST_CODE = 1; // 사진 요청 코드

    public static String getCurrentDate() { //현재 날짜 가져옴
        Date nowDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return format.format(nowDate);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfood);

        FoodDatabase database = Room.databaseBuilder(getApplicationContext(), FoodDatabase.class, "MyDietApp")
                .fallbackToDestructiveMigration() //스키마(Database) 변경 가능
                .allowMainThreadQueries()         //main Thread에서 DD에 입출력 가능
                .build();
        cFoodDao = database.foodDao(); //인터페이스 객체 할당

        ratingBar = findViewById(R.id.ratingBar);
        addFood = findViewById(R.id.addFoodEditText);
        count = findViewById(R.id.amount);
        time = findViewById(R.id.time);
        place = findViewById(R.id.place);
        image = findViewById(R.id.imageView);
        Button ImgBtn = findViewById(R.id.ImageBtn);
        Button SvgBtn = findViewById(R.id.postSaveBtn);

        Button MapBtn = (Button)findViewById(R.id.loactionBtn);

        MapBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
                startActivity(intent);
            }
        });

        ImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE); // 갤러리 열기

                startActivityForResult(intent,REQUEST_CODE);

            }
        });

        //place google map
        Intent intent = getIntent(); //전달할 데이터를 받을 Intent
        //text 키값으로 데이터를 받는다. String을 받아야 하므로 getStringExtra()를 사용함
        String placeName = intent.getStringExtra("placeName");

        place.setText(placeName);


        SvgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 입력하면 캘린더로 화면 전환
                Intent intent = new Intent(getApplicationContext(), CalendarActivity.class);

                //데이터 삽입
                Food food = new Food();
                food.setName(addFood.getText().toString());
                food.setDate(getCurrentDate());
                food.setImage(imageUri.toString());
                food.setAmount(count.getText().toString()+"인분");
                String stringTime = time.getCurrentHour()+ ":"+time.getCurrentMinute().toString();
                food.setTime(stringTime);
                food.setReview(ratingBar.getRating());
                food.setPlace(place.getText().toString());
                cFoodDao.setInsertFood(food);

                startActivity(intent);
            }

        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    Uri uri = data.getData();

                    Glide.with((getApplicationContext())).load(uri).override(Target.SIZE_ORIGINAL).placeholder(R.mipmap.ic_launcher).into((image));
                    imageUri = uri;

                } catch (Exception e) {
                    Log.d("fail_msg","error");
                }
            } else if (resultCode == RESULT_CANCELED) {

            }
        }
    }
}