package com.app.studentessentials;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.app.studentessentials.Gsons.RewardModel;
import com.app.studentessentials.JavaClasses.GlobalVariables;

public class CardScan extends AppCompatActivity {

    TextView _txt_bar_code;
    ImageView _img_bar_code , btn_back, iv_card;
    String card_name;
    int card_pos;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    List<RewardModel> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_scan);

        card_pos = getIntent().getIntExtra("card_pos", 0);
        getListItems();
        card_name = items.get(card_pos).card_name;

        _txt_bar_code =(TextView)findViewById(R.id.txt_bar_code);
        _img_bar_code=(ImageView)findViewById(R.id.img_bar_code);
        iv_card = (ImageView)findViewById(R.id.img_gift);
        iv_card.setImageResource(items.get(card_pos).image);
        sharedPreferences = getSharedPreferences(GlobalVariables._package , Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        String img_path = sharedPreferences.getString(card_name+"_img", "");
        if( img_path.length() > 0){
            loadImageFromStorage(img_path);
            _txt_bar_code.setText(sharedPreferences.getString(card_name+"_code", ""));
        }

        btn_back = (ImageView) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        ActivityCompat.requestPermissions(CardScan.this,
                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);
        //Scan Button
        Button buttonBarCodeScan = findViewById(R.id.buttonScan);
        buttonBarCodeScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //initiate scan with our custom scan activity
                new IntentIntegrator(CardScan.this).setCaptureActivity(Scanner.class).initiateScan();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //We will get scan results here
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        //check for null
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Scan Cancelled", Toast.LENGTH_LONG).show();
            } else {

                _txt_bar_code.setText(result.getContents());

                String barCode=String.valueOf(result.getContents()); // Whatever you need to encode in the QR code
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                try {
                    BitMatrix bitMatrix = multiFormatWriter.encode(barCode, BarcodeFormat.CODABAR,1200,400);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    _img_bar_code.setDrawingCacheEnabled(true);

                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

                 //   String savedImageURL = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "BarCodeScanner", "Image of BarCode");

// TESCO.png
                    String path = saveToInternalStorage(bitmap);

                  //  System.out.println("---------------------"+ path);
                    editor.putString(card_name+"_img" , path);
                    editor.putString(card_name+"_code" , result.getContents());
                    editor.commit();

                    loadImageFromStorage(path);


                 //   Uri savedImageURI = Uri.parse(savedImageURL);

                    //Display the saved image to ImageView
                  //  _img_bar_code.setImageURI(savedImageURI);

                   // _img_bar_code.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
                //show dialogue with result
                //showResultDialogue(result.getContents());
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    //method to construct dialogue with scan results
    public void showResultDialogue(final String result) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Scan Result")
                .setMessage("Scanned result is " + result)
                .setPositiveButton("Copy result", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("Scan Result", result);
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(CardScan.this, "Result copied to clipboard", Toast.LENGTH_SHORT).show();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults)
    {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(CardScan.this, "Permission denied to write your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,card_name+".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    private void loadImageFromStorage(String path)
    {

        try {
            File f=new File(path, card_name+".jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
//            ImageView img=(ImageView)findViewById(R.id.imgPicker);
              _img_bar_code.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }

    public void getListItems() {

        items = new ArrayList<>();
        items.add(new RewardModel("1","TESCO", R.drawable.reward_tesco));
        items.add(new RewardModel("2","SAINSBURY", R.drawable.reward_sainsbury));
        items.add(new RewardModel("3","WAITROSE", R.drawable.reward_waitrose));
        items.add(new RewardModel("4","M&S", R.drawable.reward_ms));
        items.add(new RewardModel("5","ICELAND", R.drawable.reward_iceland));
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
