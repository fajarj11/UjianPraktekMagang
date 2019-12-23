package com.warehousenesia.id;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.warehousenesia.id.Model.DataShopperCustomer;
import com.warehousenesia.id.Service.ApiService;
import com.warehousenesia.id.Service.ApiUtil;
import com.warehousenesia.id.Service.GetClient;
import com.warehousenesia.id.adapter.ShopperCustomerAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SuperData extends AppCompatActivity {
    private RecyclerView rvlist;
    SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_data);

        sharedPrefManager = new SharedPrefManager(getApplication());

        rvlist = findViewById(R.id.detail_data);
        getCustomer(rvlist);

    }

    private void getCustomer(RecyclerView rvlist) {
        Retrofit retrofit = GetClient.getClient(ApiUtil.Base_url);
        ApiService apiService = retrofit.create(ApiService.class);

        String id_agent = sharedPrefManager.getSpIdmember();

        Call<ArrayList<DataShopperCustomer>> call = apiService.getShopperCustomer(id_agent);
        call.enqueue(new Callback<ArrayList<DataShopperCustomer>>() {
            @Override
            public void onResponse(Call<ArrayList<DataShopperCustomer>> call, Response<ArrayList<DataShopperCustomer>> response) {
                ShopperCustomerAdapter shopperCustomerAdapter = new ShopperCustomerAdapter(getApplication(), response.body());
                rvlist.setLayoutManager(new LinearLayoutManager(getApplication()));
                rvlist.setAdapter(shopperCustomerAdapter);
            }

            @Override
            public void onFailure(Call<ArrayList<DataShopperCustomer>> call, Throwable t) {

            }
        });
    }
}
