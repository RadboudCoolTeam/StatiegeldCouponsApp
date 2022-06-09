package io.github.textrecognisionsample.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import io.github.textrecognisionsample.R;
import io.github.textrecognisionsample.model.web.WebUser;
import io.github.textrecognisionsample.util.Result;
import io.github.textrecognisionsample.util.Util;

public class AccountCreate extends AppCompatActivity {

    private byte[] imageData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_create);

        ImageButton avatar = findViewById(R.id.createAccountAvatar);

        EditText email = findViewById(R.id.accountCreateEmail);
        EditText password = findViewById(R.id.accountCreatePassword);
        EditText name = findViewById(R.id.accountCreateName);

        MaterialToolbar materialToolbar = findViewById(R.id.account_bar_create);
        materialToolbar.setNavigationOnClickListener(view -> {
            Intent intent = new Intent(AccountCreate.this, AccountLogin.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                        Result.AVATAR_IMAGE_PICK);
            }
        });

        Button create = findViewById(R.id.accountCreateButton);
        create.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                AsyncTask.execute(() -> {
                    try {
                        WebUser user = new WebUser(email.getText().toString(), password.getText().toString(), imageData);
                        user.name = name.getText().toString();

                        String id = Util.createAccount(user, view.getContext());

                        if (id != null) {

                            user.id = Long.parseLong(id);

                            Util.getWebUser().updateUser(user);

                            runOnUiThread(() -> Toast.makeText(view.getContext(), "You are logged in: " + user.id,
                                    Toast.LENGTH_SHORT).show());

                            Util.setIsLoggedIn(true);

                            Util.syncDatabases(getApplicationContext());

                            Intent intent = new Intent(AccountCreate.this, Account.class);
                            startActivity(intent);
                        } else {

                            runOnUiThread(() -> Toast.makeText(view.getContext(), "Unable to create an account",
                                    Toast.LENGTH_SHORT).show());
                        }

                    } catch (IOException e) {
                        // ignored
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == Result.AVATAR_IMAGE_PICK) {
                try {
                    Uri currentUri = data.getData();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), currentUri);

                    bitmap = Util.resizeBitmap(bitmap, Util.getAccountAvatarImageSize(this));

                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    imageData = outputStream.toByteArray();

                    Bitmap finalBitmap = bitmap;
                    runOnUiThread(
                            () -> {
                                ImageButton avatar = findViewById(R.id.createAccountAvatar);

                                avatar.setImageBitmap(finalBitmap);

                                avatar.invalidate();
                            }
                    );

                } catch (IOException e) {
                    // ignored
                }
            }
        }
    }
}