package com.example.internalstorage_hw522;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    public final static String loginFileName = "login.txt";
    public final static String passwordFileName = "password.txt";

    private SharedPreferences sharedPref;
    private static String FLAG = "flag";
    CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        getFromSharedPref();
    }

    private void init() {
        final EditText mLoginEdTxt = findViewById(R.id.et_login);
        final EditText mPasswordEdTxt = findViewById(R.id.et_password);
        Button mLogin = findViewById(R.id.btn_login);
        Button mRegistration = findViewById(R.id.btn_registration);
        checkBox = findViewById(R.id.checkBox);

        sharedPref = getSharedPreferences("My", MODE_PRIVATE);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor myEditor = sharedPref.edit();
                boolean check = checkBox.isChecked();
                myEditor.putBoolean(FLAG, check);
                myEditor.apply();
            }
        });

        mRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String nLogin = mLoginEdTxt.getText().toString();
                final String nPassword = mPasswordEdTxt.getText().toString();
                if (nLogin.isEmpty() || nPassword.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Введите логин и пароль", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean isLoginWritten = writeToFile(nLogin, loginFileName);
                boolean isPasswordWritten = writeToFile(nPassword, passwordFileName);
                if (isLoginWritten && isPasswordWritten) {
                    Toast.makeText(MainActivity.this, "Регистрация прошла успешно", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Данные не были записаны", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String nLogin = mLoginEdTxt.getText().toString();
                final String nPassword = mPasswordEdTxt.getText().toString();
                if (nLogin.isEmpty() || nPassword.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Введите логин и пароль", Toast.LENGTH_SHORT).show();
                    return;
                }
                String savedLogin = readFromFile(loginFileName);
                String savedPassword = readFromFile(passwordFileName);
                if (nLogin.equals(savedLogin) && nPassword.equals(savedPassword)) {
                    Toast.makeText(MainActivity.this, "Логин и пароль правильный", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Логин и пароль неправильный", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean writeToFile(String str, String fileName) {

        if (checkBox.isChecked()) {
            if (isExternalStorageWritable()) {
                FileWriter fileWriter = null;
                File file = new File(getApplicationContext().getExternalFilesDir(null), fileName);
                try {
                    fileWriter = new FileWriter(file);
                    fileWriter.append(str);
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                } finally {
                    try {
                        fileWriter.close();
                        return true;

                    } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            }
            return false;
        } else {

            // Создадим файл и откроем поток для записи данных
            // Обеспечим переход символьных потока данных к байтовым потокам.
            // Запишем текст в поток вывода данных, буферизуя символы так, чтобы обеспечить эффективную запись отдельных символов.
            // Осуществим запись данных
            // Закроем поток
            try (FileOutputStream fos = openFileOutput(fileName, MODE_PRIVATE);
                 OutputStreamWriter osw = new OutputStreamWriter(fos);
                 BufferedWriter bw = new BufferedWriter(osw)) {
                bw.write(str);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    private String readFromFile(String fileName) {
        StringBuilder sb = new StringBuilder();
        if (checkBox.isChecked()) {
            if (isExternalStorageWritable()) {
                File file = new File(getApplicationContext().getExternalFilesDir(null), fileName);
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String s;
                    while ((s = reader.readLine()) != null) {
                        sb.append(s);
                    }
                    return sb.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        } else {
            // Получим входные байты из файла которых нужно прочесть.
            // Декодируем байты в символы
            // Читаем данные из потока ввода, буферизуя символы так, чтобы обеспечить эффективную запись отдельных символов.
            //StringBuilder sb = new StringBuilder();
            try (FileInputStream fis = openFileInput(fileName);
                 InputStreamReader isr = new InputStreamReader(fis);
                 BufferedReader br = new BufferedReader(isr);
            ) {
                String s;
                while ((s = br.readLine()) != null) {
                    sb.append(s);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return sb.toString();
    }

    private void getFromSharedPref() {
        boolean chBx = sharedPref.getBoolean(FLAG, false);
        checkBox.setChecked(chBx);
    }

    //  проверка доступности внешнего хранилища
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}