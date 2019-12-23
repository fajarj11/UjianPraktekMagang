package com.warehousenesia.id;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.warehousenesia.id.Model.DataShopperCustomer;
import com.warehousenesia.id.Service.ApiService;
import com.warehousenesia.id.Service.ApiUtil;
import com.warehousenesia.id.Service.GetClient;
import com.warehousenesia.id.adapter.PurchaseuserAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ListPurchaseUser extends AppCompatActivity {
    private RecyclerView rvlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_purchase_user);

        rvlist = findViewById(R.id.barang);
        getBarang(rvlist);

    }

    private void getBarang(RecyclerView rvlist) {
        Retrofit retrofit = GetClient.getClient(ApiUtil.Base_url);
        ApiService apiService = retrofit.create(ApiService.class);

        String id_cus = getIntent().getStringExtra("id_customer");
        String id_tran = getIntent().getStringExtra("id_transaksi");

        Call<ArrayList<DataShopperCustomer>> call = apiService.getBarangEnduser(id_tran, id_cus);
        call.enqueue(new Callback<ArrayList<DataShopperCustomer>>() {
            @Override
            public void onResponse(Call<ArrayList<DataShopperCustomer>> call, Response<ArrayList<DataShopperCustomer>> response) {
                PurchaseuserAdapter purchaseuserAdapter = new PurchaseuserAdapter(getApplication(), response.body());
                rvlist.setLayoutManager(new GridLayoutManager(getApplication(), 2));
                rvlist.setAdapter(purchaseuserAdapter);
            }

            @Override
            public void onFailure(Call<ArrayList<DataShopperCustomer>> call, Throwable t) {

            }
        });
    }

}
