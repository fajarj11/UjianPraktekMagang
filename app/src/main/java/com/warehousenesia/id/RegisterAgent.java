package com.warehousenesia.id;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.warehousenesia.id.Post.INodeJS;
import com.warehousenesia.id.Post.RetrofitClient;
import com.warehousenesia.id.Service.ApiService;
import com.warehousenesia.id.Service.ApiUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegisterAgent extends AppCompatActivity implements View.OnClickListener{

//    INodeJS myApi;
//    CompositeDisposable compositeDisposable = new CompositeDisposable();

    EditText edit_fullname, edit_companyname, edit_address, edit_provinsi, edit_kota, edit_kodepos, edit_idagent;
    Button btn_register;

    SharedPrefManager sharedPrefManager;

//    @Override
//    protected void onStop(){
//        compositeDisposable.clear();
//        super.onStop();
//    }
//
//    @Override
//    protected void onDestroy(){
//        compositeDisposable.clear();
//        super.onDestroy();
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_agent);
//        Retrofit retrofit = RetrofitClient.getInstance();
//        myApi = retrofit.create(INodeJS.class);

        sharedPrefManager = new SharedPrefManager(this);

        btn_register = findViewById(R.id.agent_regisbtn);

        edit_fullname = findViewById(R.id.edt_fullname);
        edit_companyname = findViewById(R.id.edt_companyName);
        edit_address = findViewById(R.id.edt_address);
        edit_provinsi = findViewById(R.id.edt_provinsi);
        edit_kota = findViewById(R.id.edt_kota);
        edit_kodepos = findViewById(R.id.edt_kodepos);
        edit_idagent = findViewById(R.id.edt_idagent);

//        edit_fullname.setText(sharedPrefManager.getSPNama());
        edit_idagent.setText(sharedPrefManager.getSpIdagent());

        btn_register.setOnClickListener(this);

        if (sharedPrefManager.getSPSudahLogin()) {
            startActivity(new Intent(getApplication(), MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
        }
    }

    @Override
    public void onClick(View view){
        if (edit_fullname.getText().toString().isEmpty()) {
            Toast.makeText(this, "Tolong isi Fullname", Toast.LENGTH_SHORT).show();
        }
        else if (edit_companyname.getText().toString().isEmpty()) {
            Toast.makeText(this, "Tolong isi Companyname", Toast.LENGTH_SHORT).show();
        }
        else if (edit_address.getText().toString().isEmpty()) {
            Toast.makeText(this, "Tolong isi Address", Toast.LENGTH_SHORT).show();
        }
        else if (edit_provinsi.getText().toString().isEmpty()) {
            Toast.makeText(this, "Tolong isi Provinsi", Toast.LENGTH_SHORT).show();
        }
        else if (edit_kota.getText().toString().isEmpty()) {
            Toast.makeText(this, "Tolong isi Kota", Toast.LENGTH_SHORT).show();
        }
        else if (edit_kodepos.getText().toString().isEmpty()) {
            Toast.makeText(this, "Tolong isi Kodepos", Toast.LENGTH_SHORT).show();
        }
        else {
            registerAgent(
                    edit_idagent.getText().toString(),
                    edit_fullname.getText().toString(),
                    edit_companyname.getText().toString(),
                    edit_address.getText().toString(),
                    edit_provinsi.getText().toString(),
                    edit_kota.getText().toString(),
                    Integer.parseInt(edit_kodepos.getText().toString()));
            Intent intent = new Intent(getApplication(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void registerAgent(String id_agent,
                               String fullname,
                               String companyname,
                               String address,
                               String provinsi,
                               String kota,
                               Integer kodepos) {

        final String status = sharedPrefManager.getSpStatus();

        ApiService apiService = ApiUtil.getPaketService();
        Call<ResponseBody> call = apiService.registerAgent(id_agent, fullname, companyname, address, provinsi, kota, kodepos);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        String id_agent = object.getString("id_agent");
                        sharedPrefManager.saveSPString(SharedPrefManager.SP_IDAGENT, id_agent);
                        sharedPrefManager.saveSPString(SharedPrefManager.SP_STATUS, status);

//                        Intent intent = new Intent(getApplication(), MainActivity.class);
//                        startActivity(intent);
//                        Toast.makeText(getApplication(), "Registrasi Success", Toast.LENGTH_SHORT).show();
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplication(), "Registrasi Success", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    //        compositeDisposable.add(myApi.registerAgent(fullname, companyname, address, kota, provinsi, kodepos)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<String>() {
//                    @Override
//                    public void accept(String s) throws Exception {
//                        Intent intent = getIntent();
//                        String level = intent.getStringExtra("T_LEVEL");
//                        Toast.makeText(getApplication(), level, Toast.LENGTH_SHORT).show();
//                        Toast.makeText(getApplication(), "Registrasi Success", Toast.LENGTH_SHORT).show();
//                    }
//                }));
}
