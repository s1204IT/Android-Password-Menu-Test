package me.s1204.android.test.passwordmenu;

import static android.util.TypedValue.COMPLEX_UNIT_SP;
import static android.view.Gravity.CENTER;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Activity extends android.app.Activity {

    public static String text2sha256(String string) {
        MessageDigest digest;
        String hash = "";
        try {
            digest = MessageDigest.getInstance("SHA-256");
            digest.update(string.getBytes());
            byte[] bytes = digest.digest();
            StringBuilder stringBuilder = new StringBuilder();
            for (byte aByte : bytes) {
                String hex = Integer.toHexString(0xFF & aByte);
                if (hex.length() == 1) {
                    stringBuilder.append('0');
                }
                stringBuilder.append(hex);
            }
            hash = stringBuilder.toString();
        } catch (NoSuchAlgorithmException ignored) {
        }
        return hash;
    }
    @SuppressLint("SetTextI18n")
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

      final LinearLayout linearLayout = new LinearLayout(this);
      final TextView textView = new TextView(this);

      File manager_password = new File(this.getFilesDir(), "password");
      if (manager_password.exists()) {
        String passwordHashFromFileDec = "";
        try {
          passwordHashFromFileDec = (new BufferedReader(new FileReader(manager_password))).readLine();
        } catch (IOException ignored) {
        }
        final String passwordHashFromFile = passwordHashFromFileDec;
        final EditText mEditText = new EditText(this);
        mEditText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD + 1);
        new AlertDialog.Builder(this).setTitle("パスワード")
          .setView(mEditText).setPositiveButton("OK", (dialogInterface, i) -> {
            String passwordFromInputText = text2sha256(mEditText.getText() + "\n");
            if (!passwordFromInputText.equals(passwordHashFromFile)) {
              Toast.makeText(this, "パスワードが正しくありません", Toast.LENGTH_LONG).show();
              finish();
            } else {
              textView.setText("Successfully");
            }
          }).setNegativeButton("キャンセル", (dialogInterface, i) -> onBackPressed()).setOnCancelListener(dialogInterface -> finish()).create()
          .show();
        } else {
          textView.setText("パスワード未設定");
        }
        linearLayout.setGravity(CENTER);
        textView.setTextSize(COMPLEX_UNIT_SP, 50.0F);
        linearLayout.addView(textView, new LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        setContentView(linearLayout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.menu, menu);
      return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      File manager_password = new File(this.getFilesDir(), "password");
      final EditText mEditText;
      mEditText = new EditText(this);
      mEditText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD + 1);
      new AlertDialog.Builder(this)
        .setTitle("新しいパスワード")
        .setView(mEditText)
        .setPositiveButton("OK", (dialogInterface, i) -> {
          try {
            FileOutputStream fileOutputstream = new FileOutputStream(manager_password);
            fileOutputstream.write(text2sha256(mEditText.getText() + "\n").getBytes());
            Toast.makeText(this, "パスワードが変更されました", Toast.LENGTH_LONG).show();
          } catch (IOException ignored) {
          }
        })
        .setNegativeButton("キャンセル", (dialogInterface, i) -> onBackPressed())
        .setNeutralButton("削除", (dialogInterface, i) -> {
          manager_password.delete();
          Toast.makeText(this, "パスワードを削除しました", Toast.LENGTH_LONG).show();
        })
        .setOnCancelListener(dialogInterface -> finish())
        .create()
        .show();
        return true;
    }
}
