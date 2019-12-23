package com.warehousenesia.id;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.warehousenesia.id.Model.DataCategory;
import com.warehousenesia.id.Service.ApiService;
import com.warehousenesia.id.Service.ApiUtil;
import com.warehousenesia.id.Service.GetClient;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AddToKatalog extends AppCompatActivity implements View.OnClickListener {
    private static final String UPLOAD_URL = "http://192.168.100.17:1997/product/addproduct";
    private static final int IMAGE_REQUEST_CODE = 3;
    private static final int STORAGE_PERMISSION_CODE = 123;
    private ImageView imageView;
    private TextView tvPath;
    private Button btnUpload, upl_img;
    private Bitmap bitmap;
    private Uri filePath;
    private EditText idProduk, namaProduk, harga, berat, deskripsi;
    List<DataCategory> list = new ArrayList<>();
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_katalog);

        spinner = findViewById(R.id.kategori);

        ApiService apiService = ApiUtil.getPaketService();

        DataCategory dataCategory1 = new DataCategory("", "", "Pilih Kategori");

        list.add(dataCategory1);
        Call<ArrayList<DataCategory>> call = apiService.getCategory();
        call.enqueue(new Callback<ArrayList<DataCategory>>() {
            @Override
            public void onResponse(Call<ArrayList<DataCategory>> call, Response<ArrayList<DataCategory>> response) {
                list.addAll(response.body());
            }

            @Override
            public void onFailure(Call<ArrayList<DataCategory>> call, Throwable t) {

            }
        });

        ArrayAdapter<DataCategory> adapter = new ArrayAdapter<DataCategory>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        idProduk = findViewById(R.id.id_produk);
        namaProduk = findViewById(R.id.nama_produk);
        harga = findViewById(R.id.harga);
        berat = findViewById(R.id.berat);
        deskripsi = findViewById(R.id.deskripsi);

        idProduk.setText(getIntent().getStringExtra("id_produk"));
        idProduk.setFocusable(false);
        idProduk.setBackgroundColor(Color.LTGRAY);
        namaProduk.setText(getIntent().getStringExtra("nama_barang"));

        imageView = findViewById(R.id.image);
        btnUpload = findViewById(R.id.submit);
        upl_img = findViewById(R.id.chooseImage);
        tvPath    = findViewById(R.id.path);

        requestStoragePermission();

        btnUpload.setOnClickListener(this);
        upl_img.setOnClickListener(this);

    }

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    public void onClick(View view){
        if (view == upl_img){
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Complete action using"), IMAGE_REQUEST_CODE);
        }else if (view == btnUpload){
            String id = getIntent().getStringExtra("id");
            String status = "Barang Di katalog";
            String kode = getIntent().getStringExtra("id_produk");
            Retrofit retrofit = GetClient.getClient(ApiUtil.Base_url);
            ApiService apiService = retrofit.create(ApiService.class);
            Call<ResponseBody> call = apiService.EditRequest(status, kode, id);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
            uploadMultipart();
        }
    }

    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                tvPath.setText("Path: ".concat(getPath(filePath)));
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    private void uploadMultipart() {

        String id_pro = idProduk.getText().toString();
        String nama_pro = namaProduk.getText().toString();
        String hrg = harga.getText().toString();
        String brt = berat.getText().toString();
        String desk = deskripsi.getText().toString();
        String id_neg = getIntent().getStringExtra("id_negara");

        DataCategory dataCategory = (DataCategory) spinner.getSelectedItem();
        String id_kategori = dataCategory.getId();

        //getting the actual path of the image
        String path = getPath(filePath);

        JSONObject konfirm = new JSONObject();

        try{

            konfirm.put("id", id_pro);
            konfirm.put("nama_produk", nama_pro);
            konfirm.put("harga", hrg);
            konfirm.put("berat", brt);
            konfirm.put("deskripsi", desk);
            konfirm.put("verified", 1);
            konfirm.put("id_negara", id_neg);
            konfirm.put("id_kategori", id_kategori);

            String uploadId = UUID.randomUUID().toString();
            //Creating a multi part request
            new MultipartUploadRequest(this, uploadId, UPLOAD_URL)
                    .addFileToUpload(path, "gambar") //Adding file
                    .addParameter("data", konfirm.toString())
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .startUpload(); //Starting the upload

            Intent intent = new Intent(getApplication(), MainActivity.class);
            startActivity(intent);
            Toast.makeText(getApplication(), "Add Successful", Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception exc) {
            Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

}
