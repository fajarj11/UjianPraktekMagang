package com.warehousenesia.id;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.warehousenesia.id.Model.DataBank;
import com.warehousenesia.id.Model.DataListPayment;
import com.warehousenesia.id.Service.ApiService;
import com.warehousenesia.id.Service.ApiUtil;
import com.warehousenesia.id.Service.GetClient;
import com.warehousenesia.id.Service.ProductService;
import com.warehousenesia.id.adapter.BankPaymentAdapter;
import com.warehousenesia.id.adapter.ListPaymentAdapter;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ConfirmPayment extends AppCompatActivity implements View.OnClickListener {
    private static final String UPLOAD_URL = "http://192.168.100.17:1997/transaction/confirmpayment";
    private static final int IMAGE_REQUEST_CODE = 3;
    private static final int STORAGE_PERMISSION_CODE = 123;
    private ImageView imageView;
    private TextView tvPath;
    private Button btnChoose, btnUpload;
    private Bitmap bitmap;
    private Uri filePath;
    private RecyclerView rv_bank, rv_payment;
    SharedPrefManager sharedPrefManager;
    TextView id_transaksi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_payment);

        sharedPrefManager = new SharedPrefManager(getApplication());

        imageView = (ImageView)findViewById(R.id.preview);
        tvPath    = (TextView)findViewById(R.id.path);
        btnUpload = (Button)findViewById(R.id.upload);
        btnChoose = (Button)findViewById(R.id.chooseImage);

        requestStoragePermission();

        btnChoose.setOnClickListener(this);
        btnUpload.setOnClickListener(this);

        rv_bank = findViewById(R.id.bank);
        getBank(rv_bank);

        rv_payment = findViewById(R.id.list);
        getListPayment(rv_payment);

        id_transaksi = findViewById(R.id.id_transaksi);
//        id_transaksi.setText(getIntent().getStringExtra("id"));

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("id_transaksi"));
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            String id = intent.getStringExtra("id");
            id_transaksi.setText(id);
        }
    };

    private void getListPayment(RecyclerView rv_payment) {
        Retrofit retrofit = GetClient.getClient(ApiUtil.Base_url);
        ApiService apiService = retrofit.create(ApiService.class);

        String username = sharedPrefManager.getSPNama();

        Call<ArrayList<DataListPayment>> call = apiService.getListPayment(username);
        call.enqueue(new Callback<ArrayList<DataListPayment>>() {
            @Override
            public void onResponse(Call<ArrayList<DataListPayment>> call, Response<ArrayList<DataListPayment>> response) {
                ListPaymentAdapter listPaymentAdapter = new ListPaymentAdapter(getApplication(), response.body());
                rv_payment.setLayoutManager(new LinearLayoutManager(getApplication()));
                rv_payment.setAdapter(listPaymentAdapter);
            }

            @Override
            public void onFailure(Call<ArrayList<DataListPayment>> call, Throwable t) {
                Log.e("ERROR", t.getMessage());
            }
        });
    }

    private void getBank(final RecyclerView rv_bank) {
        Retrofit retrofit = GetClient.getClient(ApiUtil.Base_url);
        ProductService productService = retrofit.create(ProductService.class);

        Call<ArrayList<DataBank>> call = productService.getBank();
        call.enqueue(new Callback<ArrayList<DataBank>>() {
            @Override
            public void onResponse(Call<ArrayList<DataBank>> call, Response<ArrayList<DataBank>> response) {
                BankPaymentAdapter bankPaymentAdapter = new BankPaymentAdapter(getApplication(), response.body());
                rv_bank.setLayoutManager(new LinearLayoutManager(getApplication()));
                rv_bank.setAdapter(bankPaymentAdapter);
            }

            @Override
            public void onFailure(Call<ArrayList<DataBank>> call, Throwable t) {
                Log.e("ERROR", t.getMessage());
            }
        });
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

    @Override
    public void onClick(View view) {
        if(view == btnChoose){
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Complete action using"), IMAGE_REQUEST_CODE);
        }else if(view == btnUpload){
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

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    private void uploadMultipart() {
        String id = id_transaksi.getText().toString();

        //getting the actual path of the image
        String path = getPath(filePath);

        //Uploading code
        JSONObject konfirm = new JSONObject();
        try {

            konfirm.put("id_transaksi", id);
            konfirm.put("username", sharedPrefManager.getSPNama());
            konfirm.put("waktu", getDateTime());
            String uploadId = UUID.randomUUID().toString();

            new MultipartUploadRequest(this, uploadId, UPLOAD_URL)
                    .addFileToUpload(path, "image") //Adding file
                    .addParameter("data", konfirm.toString())
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .startUpload(); //Starting the upload
            Intent intent = new Intent(getApplication(), Order.class);
            startActivity(intent);
            Toast.makeText(getApplication(), "Payment Successful", Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
//        }
        }
    }

}
