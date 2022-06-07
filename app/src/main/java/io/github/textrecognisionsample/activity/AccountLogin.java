package io.github.textrecognisionsample.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.AsyncTaskLoader;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import io.github.textrecognisionsample.R;
import io.github.textrecognisionsample.model.user.UserDao;
import io.github.textrecognisionsample.model.user.UserData;
import io.github.textrecognisionsample.model.user.UserDatabase;
import io.github.textrecognisionsample.model.web.WebUser;
import io.github.textrecognisionsample.model.web.WebUserJsonSerializer;
import io.github.textrecognisionsample.util.ByteArrayToBase64TypeAdapter;
import io.github.textrecognisionsample.util.Util;

public class AccountLogin extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_login);

        EditText email = findViewById(R.id.accountLoginEmail);
        EditText password = findViewById(R.id.accountLoginPassword);

        ImageView imageView = findViewById(R.id.accountLoginLogo);
        imageView.setBackgroundResource(R.drawable.icon);

        GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeAdapter(WebUser.class, new WebUserJsonSerializer())
                .registerTypeAdapter(byte[].class, new ByteArrayToBase64TypeAdapter());
        Gson gson = gsonBuilder.create();

        MaterialToolbar materialToolbar = findViewById(R.id.account_bar_login);
        materialToolbar.setNavigationOnClickListener(view -> {
            Intent intent = new Intent(AccountLogin.this, Home.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        Button login = findViewById(R.id.accountLoginButton);

        login.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {

                AsyncTask.execute(() -> {
                    try {
                        WebUser user = new WebUser(email.getText().toString(), password.getText().toString(), null);

                        WebUser userFromApi = Util.authUser(user, view.getContext());

                        if (userFromApi != null) {

                            Util.getWebUser().updateUser(userFromApi);

                            runOnUiThread(() -> Toast.makeText(view.getContext(), "You are logged in: " + userFromApi.id,
                                    Toast.LENGTH_SHORT).show());

                            Util.setIsLoggedIn(true);

                            Util.syncDatabases(getApplicationContext());

                            UserDao userDao = UserDatabase.getInstance(getApplicationContext()).userDao();

                            if (userDao.getAll().size() > 0) {
                                userDao.nukeTable();
                            }

                            userDao.insert(new UserData(gson.toJson(userFromApi, WebUser.class)));

                            Intent intent = new Intent(AccountLogin.this, Account.class);
                            startActivity(intent);
                        } else {

                            runOnUiThread(() -> Toast.makeText(view.getContext(), "You are unable to log in :(",
                                    Toast.LENGTH_SHORT).show());
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });


            }
        });

        Button newAcc = findViewById(R.id.accountLoginCreateNew);

        newAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountLogin.this, AccountCreate.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
    }
}
