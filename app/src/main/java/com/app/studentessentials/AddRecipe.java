package com.app.studentessentials;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.app.studentessentials.JavaClasses.GlobalVariables;

import static com.app.studentessentials.JavaClasses.GlobalVariables.firebase_base_url;

public class AddRecipe extends AppCompatActivity {

    ImageView btn_back, img_photo;
    TextView txt_upload;
    EditText edt_title, edt_desc, edt_ingredient, edt_instruction, edt_calory, edt_protein, edt_fat, edt_carb, edt_tip;
    Spinner spin_recipe;
    Button btn_recipe;
    File img_file;
    Uri mCropImageUri;
    private StorageReference storageRef;
    private ProgressDialog pd;
    SharedPreferences sharedPreferences;
    List<String> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        // [START get_storage_ref]
        storageRef = FirebaseStorage.getInstance().getReference();
        // [END get_storage_ref]

        btn_back = (ImageView) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        initLayout();
        initClickFunc();
        pd = new ProgressDialog(this);
        pd.setMessage("Uploading...");
    }

    private void initClickFunc(){
        btn_recipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateFields()){
                    uploadPhoto();
                }
            }
        });

        img_file = null;
        txt_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSelectImageClick(view);
            }
        });
    }

    private void initLayout(){

        sharedPreferences = getSharedPreferences(GlobalVariables._package , Context.MODE_PRIVATE);

        img_photo = (ImageView) findViewById(R.id.img_add_recipe);
        txt_upload = (TextView) findViewById(R.id.txt_add_recipe_upload);
        spin_recipe = (Spinner) findViewById(R.id.spin_add_recipe);
        edt_title = (EditText) findViewById(R.id.edt_add_recipe_name);
        edt_desc = (EditText) findViewById(R.id.edt_add_recipe_desc);
        edt_ingredient = (EditText) findViewById(R.id.edt_add_recipe_ingredient);
        edt_instruction = (EditText) findViewById(R.id.edt_add_recipe_instruction);
        edt_calory = (EditText) findViewById(R.id.edt_add_recipe_calories);
        edt_protein = (EditText) findViewById(R.id.edt_add_recipe_protein);
        edt_fat = (EditText) findViewById(R.id.edt_add_recipe_fat);
        edt_carb = (EditText) findViewById(R.id.edt_add_recipe_carb);
        edt_tip = (EditText) findViewById(R.id.edt_add_recipe_tip);
        btn_recipe = (Button) findViewById(R.id.btn_add_recipe);

        categories = new ArrayList<String>();
        categories.add("Snack");
        categories.add("Breakfast");
        categories.add("Lunch");
        categories.add("Dinner");
        categories.add("Drink");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spin_recipe.setAdapter(dataAdapter);
    }

    private Boolean validateFields(){
        if(edt_title.getText().toString().trim().length() == 0){
            Toast.makeText(this, "Please type recipe title.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(img_file == null){
            Toast.makeText(this, "Please select recipe photo.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(edt_desc.getText().toString().length() == 0){
            Toast.makeText(this, "Please type recipe description.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(edt_ingredient.getText().toString().length() == 0){
            Toast.makeText(this, "Please type recipe ingredients.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(edt_instruction.getText().toString().length() == 0){
            Toast.makeText(this, "Please type recipe instructions.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void uploadPhoto(){
        String filepath = img_file.getAbsolutePath();
        String photoName = img_file.getName();

        final StorageReference reference = storageRef.child("recipes").child(photoName);
        UploadTask uploadTask = reference.putFile(Uri.fromFile(img_file));

        pd.show();
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddRecipe.this, "Uploading failed", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    uploadRecipe(downloadUrl);
                }
            });
    }

    private void uploadRecipe(Uri uri){
        final  String var_email = sharedPreferences.getString("email", "");
        final String downurl = uri.toString();
        String url = firebase_base_url+"Recipes.json";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                Firebase reference = new Firebase(firebase_base_url+"Recipes/"+"foods");

                String id = reference.push().getKey();
                reference.child(id).child("name").setValue(edt_title.getText().toString().trim());
                reference.child(id).child("image").setValue(downurl);
                String cate_str = categories.get(spin_recipe.getSelectedItemPosition());
                reference.child(id).child("category").setValue(cate_str);
                reference.child(id).child("description").setValue(edt_desc.getText().toString());
                reference.child(id).child("ingredient").setValue(edt_ingredient.getText().toString());
                reference.child(id).child("instruction").setValue(edt_instruction.getText().toString());
                reference.child(id).child("calory").setValue(edt_calory.getText().toString());
                reference.child(id).child("protein").setValue(edt_protein.getText().toString());
                reference.child(id).child("fat").setValue(edt_fat.getText().toString());
                reference.child(id).child("carb").setValue(edt_carb.getText().toString());
                reference.child(id).child("tip").setValue(edt_tip.getText().toString());
                reference.child(id).child("user").setValue(var_email);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                        Intent intent = new Intent(AddRecipe.this, Receipe.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                }, 3000);
            }

        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //System.out.println("" + volleyError );
                pd.dismiss();
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(this);
        rQueue.add(request);
    }

    public static String EncodeString(String string) {
        return string.replace(".", ",");
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .start(this);
    }

    @SuppressLint("NewApi")
    public void onSelectImageClick(View view) {
        if (CropImage.isExplicitCameraPermissionRequired(this)) {
            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE);
        } else {
            CropImage.startPickImageActivity(this);
        }
    }

    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // handle result of pick image chooser
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);

            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                mCropImageUri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
            } else {
                // no permissions required or already granted, can start crop image activity
                startCropImageActivity(imageUri);
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE ){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                img_file = new File(saveImageToInternalStorageFromUri(this, result.getUri()));
                Bitmap myBitmap = BitmapFactory.decodeFile(img_file.getAbsolutePath());
                img_photo.setImageBitmap(myBitmap);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CropImage.startPickImageActivity(this);
            } else {
                Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // required permissions granted, start crop image activity
                startCropImageActivity(mCropImageUri);
            } else {
                Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
            }
        }
    }

    public static String saveImageToInternalStorageFromUri(Context mContext, Uri uri) {

        ContextWrapper cw = new ContextWrapper(mContext);
        //File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        long time= System.currentTimeMillis();
        String path = "R_" + time + ".jpg";
        //File mypath = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES), path);
        File mypath = new File(Environment.getExternalStorageDirectory(), path);
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(mypath);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return mypath.getAbsolutePath();
    }
}
