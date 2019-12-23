package com.warehousenesia.id;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.warehousenesia.id.Model.DataShopperCustomer;
import com.warehousenesia.id.Service.ApiService;
import com.warehousenesia.id.Service.ApiUtil;
import com.warehousenesia.id.Service.GetClient;
import com.warehousenesia.id.adapter.EnduserAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ListEnduser extends AppCompatActivity {
    private RecyclerView rvlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_enduser);

        rvlist = findViewById(R.id.detail_data);
        getEnduser(rvlist);

    }

    private void getEnduser(RecyclerView rvlist) {
        Retrofit retrofit = GetClient.getClient(ApiUtil.Base_url);
        ApiService apiService = retrofit.create(ApiService.class);

        String waktu = getIntent().getStringExtra("waktu");

        Call<ArrayList<DataShopperCustomer>> call = apiService.getListEnduser(waktu);
        call.enqueue(new Callback<ArrayList<DataShopperCustomer>>() {
            @Override
            public void onResponse(Call<ArrayList<DataShopperCustomer>> call, Response<ArrayList<DataShopperCustomer>> response) {
                EnduserAdapter enduserAdapter = new EnduserAdapter(getApplication(), response.body());
                rvlist.setLayoutManager(new LinearLayoutManager(getApplication()));
                rvlist.setAdapter(enduserAdapter);
            }

            @Override
            public void onFailure(Call<ArrayList<DataShopperCustomer>> call, Throwable t) {

            }
        });
    }

}
