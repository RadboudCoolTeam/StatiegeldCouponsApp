package io.github.textrecognisionsample.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import io.github.textrecognisionsample.R;

public class MainActivity extends AppCompatActivity {

    private final int TAKE_PICTURE_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button select = findViewById(R.id.button);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CameraX.class);
                startActivityForResult(intent, CameraX.TAKE_PICTURE_CODE);
            }
        });

        Button home = findViewById(R.id.home_button);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Home.class);
                startActivity(intent);
            }
        });
    }

    @SuppressLint("UnsafeOptInUsageError")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CameraX.TAKE_PICTURE_CODE) {
                String text = data.getStringExtra("barcode");
                String shop = data.getStringExtra("shop");
                EditText edit = findViewById(R.id.editTextTextMultiLine);
                edit.setText(text + "\n" + shop);
            }
        }
    }


}