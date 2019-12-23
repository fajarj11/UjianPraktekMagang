package com.warehousenesia.id;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.warehousenesia.id.Model.DataAll;
import com.warehousenesia.id.Service.ApiService;
import com.warehousenesia.id.Service.ApiUtil;
import com.warehousenesia.id.Service.GetClient;
import com.warehousenesia.id.Service.ProductService;
import com.warehousenesia.id.adapter.AllAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Keranjang extends AppCompatActivity implements View.OnClickListener {
    SharedPrefManager sharedPrefManager;
    TextView idcustomer, total_item, total_weight, total_harga1, transfee, shipfee, devfee, total_harga2;
    Button checkOUT;
    ApiService apiServices;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keranjang);

        sharedPrefManager = new SharedPrefManager(getApplication());
        RecyclerView rvlist = findViewById(R.id.cart);
        String id = sharedPrefManager.getSPNama();
        getCart(rvlist, id);

        String username = sharedPrefManager.getSPNama();

        String level = sharedPrefManager.getSpLevel();
        String negara_ship = sharedPrefManager.getSP_ShipNegara();
        total_item = findViewById(R.id.total_item);
        total_item.setText(sharedPrefManager.getSP_TotalItemKeranjang());

        total_weight = findViewById(R.id.total_weight);
        double tw = Double.parseDouble(String.valueOf(sharedPrefManager.getSP_TotalWeightKeranjang()));
        double akhir_tw = tw / 1000;
        total_weight.setText(String.valueOf(akhir_tw));

        total_harga1 = findViewById(R.id.total_harga1);
        int total = 0;

        if(level.equals("PLATINUM")){
            double jumlah = 15000 * 0.05;
            double akhir = 15000 + jumlah;
            total = (int) akhir;
        }else if(level.equals("GOLD")){
            double jumlah = 15000 * 0.075;
            double akhir = 15000 + jumlah;
            total = (int) akhir;
        }else if(level.equals("SILVER")){
            double jumlah = 15000 * 0.1;
            double akhir = 15000 + jumlah;
            total = (int) akhir;
        }
        double th1 = Double.parseDouble(sharedPrefManager.getSP_TotalHargaKeranjang());
        double akhir_th1 = total * th1;
        double fee10 = akhir_th1 * 0.1;
        double akhir_totprice = akhir_th1 + fee10;


        transfee = findViewById(R.id.transfee_cart);

        apiServices = ApiUtil.getPaketService();
        final double[] bayarall = {0};
        final double[] tranall = {0};
        Call<ResponseBody> call = apiServices.getlistChart(username);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    JSONArray array = new JSONArray(response.body().string());

                    double tot_akhir = 0;

                    for (int i = 0; i < array.length(); i++){

                        double tot_harga = Double.parseDouble(array.getJSONObject(i).getString("total_harga"));

                        tot_akhir += tot_harga;

                        double total_all = tot_akhir;

                        double bea_masuk = 0.0;
                        double ppn = 0.0;
                        double pph = 0.0;
                        double ppnbh = 0.0;

                        if(total_all > 5){

                            bea_masuk = total_all * 0.1;

                            ppn = total_all * 0.1;

                            pph = total_all * 0.075;

                            ppnbh = total_all * 0.1;

                        }

                        double all = total_all + bea_masuk + ppn + pph + ppnbh;
                        double bayar = all * 15750;
                        double feetran10 = bayar * 0.1;

                        bayarall[0] += bayar;
                        tranall[0] += feetran10;


                    }
                    Log.d("BAYAR_ALL", String.valueOf(bayarall[0]));
                    total_harga1.setText(String.format("%.0f",bayarall[0]));
                    transfee.setText(String.format("%.0f",tranall[0]));

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });


        shipfee = findViewById(R.id.shipping_fee);
        devfee = findViewById(R.id.delivery_fee);
        total_harga2 = findViewById(R.id.total_harga2);

        idcustomer = findViewById(R.id.keranjang_idcustomer);

        Retrofit ret = GetClient.getClient(ApiUtil.Base_url);
        ApiService apiService = ret.create(ApiService.class);
        Call<ResponseBody> ship = apiService.getShipping();
        int finalTotal = total;
        int finalTotal1 = total;
        ship.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (level.equals("PLATINUM")){
                    try {
                        JSONArray array = new JSONArray(response.body().string());

                        if (negara_ship.equals("Japan")){
                            String shippla1 = array.getJSONObject(0).getString("platinum");
                            double plaship = Double.parseDouble(shippla1);
                            double akhir_plaship1 = akhir_tw * plaship;
                            double akhir_plaship2 = finalTotal * akhir_plaship1;
                            double akhir_th1 = finalTotal1 * th1;
                            double fee10 = akhir_th1 * 0.1;
                            double akhir_totprice = bayarall[0] + tranall[0];
                            double akhir_sumprice = akhir_totprice + akhir_plaship2;
                            shipfee.setText(String.valueOf(akhir_plaship2));
                            total_harga2.setText(String.format("%.0f",akhir_sumprice));
                        }
                        else if (negara_ship.equals("Hongkong")){
                            String shippla2 = array.getJSONObject(1).getString("platinum");
                            double plaship = Double.parseDouble(shippla2);
                            double akhir_plaship1 = akhir_tw * plaship;
                            double akhir_plaship2 = finalTotal * akhir_plaship1;
                            double akhir_th1 = finalTotal1 * th1;
                            double fee10 = akhir_th1 * 0.1;
                            double akhir_totprice = bayarall[0] + tranall[0];
                            double akhir_sumprice = akhir_totprice + akhir_plaship2;
                            total_harga2.setText(String.format("%.0f",akhir_sumprice));
                            shipfee.setText(String.valueOf(akhir_plaship2));
                        }
                        else if (negara_ship.equals("Australia")){
                            String shippla3 = array.getJSONObject(2).getString("platinum");
                            double plaship = Double.parseDouble(shippla3);
                            double akhir_plaship1 = akhir_tw * plaship;
                            double akhir_plaship2 = finalTotal * akhir_plaship1;
                            double akhir_th1 = finalTotal1 * th1;
                            double fee10 = akhir_th1 * 0.1;
                            double akhir_totprice = bayarall[0] + tranall[0];
                            double akhir_sumprice = akhir_totprice + akhir_plaship2;
                            total_harga2.setText(String.valueOf(akhir_sumprice));
                            shipfee.setText(String.valueOf(akhir_plaship2));
                        }
                        else if (negara_ship.equals("Malaysia")){
                            String shippla4 = array.getJSONObject(3).getString("platinum");
                            double plaship = Double.parseDouble(shippla4);
                            double akhir_plaship1 = akhir_tw * plaship;
                            double akhir_plaship2 = finalTotal * akhir_plaship1;
                            double akhir_th1 = finalTotal1 * th1;
                            double fee10 = akhir_th1 * 0.1;
                            double akhir_totprice = bayarall[0] + tranall[0];
                            double akhir_sumprice = akhir_totprice + akhir_plaship2;
                            total_harga2.setText(String.format("%.0f",akhir_sumprice));
                            shipfee.setText(String.valueOf(akhir_plaship2));
                        }
                        else if (negara_ship.equals("Singapore")){
                            String shippla5 = array.getJSONObject(4).getString("platinum");
                            double plaship = Double.parseDouble(shippla5);
                            double akhir_plaship1 = akhir_tw * plaship;
                            double akhir_plaship2 = finalTotal * akhir_plaship1;
                            double akhir_th1 = finalTotal1 * th1;
                            double fee10 = akhir_th1 * 0.1;
                            double akhir_totprice = bayarall[0] + tranall[0];
                            double akhir_sumprice = akhir_totprice + akhir_plaship2;
                            total_harga2.setText(String.format("%.0f",akhir_sumprice));
                            shipfee.setText(String.valueOf(akhir_plaship2));
                        }
                        else if (negara_ship.equals("Korea Selatan")){
                            String shippla6 = array.getJSONObject(5).getString("platinum");
                            double plaship = Double.parseDouble(shippla6);
                            double akhir_plaship1 = akhir_tw * plaship;
                            double akhir_plaship2 = finalTotal * akhir_plaship1;
                            double akhir_th1 = finalTotal1 * th1;
                            double fee10 = akhir_th1 * 0.1;
                            double akhir_totprice = bayarall[0] + tranall[0];
                            double akhir_sumprice = akhir_totprice + akhir_plaship2;
                            total_harga2.setText(String.format("%.0f",akhir_sumprice));
                            shipfee.setText(String.valueOf(akhir_plaship2));
                        }
                        else if (negara_ship.equals("Thailand")){
                            String shippla7 = array.getJSONObject(6).getString("platinum");
                            double plaship = Double.parseDouble(shippla7);
                            double akhir_plaship1 = akhir_tw * plaship;
                            double akhir_plaship2 = finalTotal * akhir_plaship1;
                            double akhir_th1 = finalTotal1 * th1;
                            double fee10 = akhir_th1 * 0.1;
                            double akhir_totprice = bayarall[0] + tranall[0];
                            double akhir_sumprice = akhir_totprice + akhir_plaship2;
                            total_harga2.setText(String.format("%.0f",akhir_sumprice));
                            shipfee.setText(String.valueOf(akhir_plaship2));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else if(level.equals("GOLD")){
                    try {
                        JSONArray array = new JSONArray(response.body().string());

                        if (negara_ship.equals("Japan")){
                            String shipgold1 = array.getJSONObject(0).getString("gold");
                            double plaship = Double.parseDouble(shipgold1);
                            double akhir_plaship1 = akhir_tw * plaship;
                            double akhir_plaship2 = finalTotal * akhir_plaship1;
                            double akhir_th1 = finalTotal1 * th1;
                            double fee10 = akhir_th1 * 0.1;
                            double akhir_totprice = bayarall[0] + tranall[0];
                            double akhir_sumprice = akhir_totprice + akhir_plaship2;
                            total_harga2.setText(String.format("%.0f",akhir_sumprice));
                            shipfee.setText(String.valueOf(akhir_plaship2));
                        }
                        else if (negara_ship.equals("Hongkong")){
                            String shipgold2 = array.getJSONObject(1).getString("gold");
                            double plaship = Double.parseDouble(shipgold2);
                            double akhir_plaship1 = akhir_tw * plaship;
                            double akhir_plaship2 = finalTotal * akhir_plaship1;
                            double akhir_th1 = finalTotal1 * th1;
                            double fee10 = akhir_th1 * 0.1;
                            double akhir_totprice = bayarall[0] + tranall[0];
                            double akhir_sumprice = akhir_totprice + akhir_plaship2;
                            total_harga2.setText(String.format("%.0f",akhir_sumprice));
                            shipfee.setText(String.valueOf(akhir_plaship2));
                        }
                        else if (negara_ship.equals("Australia")){
                            String shipgold3 = array.getJSONObject(2).getString("gold");
                            double plaship = Double.parseDouble(shipgold3);
                            double akhir_plaship1 = akhir_tw * plaship;
                            double akhir_plaship2 = finalTotal * akhir_plaship1;
                            double akhir_th1 = finalTotal1 * th1;
                            double fee10 = akhir_th1 * 0.1;
                            double akhir_totprice = bayarall[0] + tranall[0];
                            double akhir_sumprice = akhir_totprice + akhir_plaship2;
                            total_harga2.setText(String.format("%.0f",akhir_sumprice));
                            shipfee.setText(String.valueOf(akhir_plaship2));
                        }
                        else if (negara_ship.equals("Malaysia")){
                            String shipgold4 = array.getJSONObject(3).getString("gold");
                            double plaship = Double.parseDouble(shipgold4);
                            double akhir_plaship1 = akhir_tw * plaship;
                            double akhir_plaship2 = finalTotal * akhir_plaship1;
                            double akhir_th1 = finalTotal1 * th1;
                            double fee10 = akhir_th1 * 0.1;
                            double akhir_totprice = bayarall[0] + tranall[0];
                            double akhir_sumprice = akhir_totprice + akhir_plaship2;
                            total_harga2.setText(String.format("%.0f",akhir_sumprice));
                            shipfee.setText(String.valueOf(akhir_plaship2));
                        }
                        else if (negara_ship.equals("Singapore")){
                            String shipgold5 = array.getJSONObject(4).getString("gold");
                            double plaship = Double.parseDouble(shipgold5);
                            double akhir_plaship1 = akhir_tw * plaship;
                            double akhir_plaship2 = finalTotal * akhir_plaship1;
                            double akhir_th1 = finalTotal1 * th1;
                            double fee10 = akhir_th1 * 0.1;
                            double akhir_totprice = bayarall[0] + tranall[0];
                            double akhir_sumprice = akhir_totprice + akhir_plaship2;
                            total_harga2.setText(String.format("%.0f",akhir_sumprice));
                            shipfee.setText(String.valueOf(akhir_plaship2));
                        }
                        else if (negara_ship.equals("Korea Selatan")){
                            String shipgold6 = array.getJSONObject(5).getString("gold");
                            double plaship = Double.parseDouble(shipgold6);
                            double akhir_plaship1 = akhir_tw * plaship;
                            double akhir_plaship2 = finalTotal * akhir_plaship1;
                            double akhir_th1 = finalTotal1 * th1;
                            double fee10 = akhir_th1 * 0.1;
                            double akhir_totprice = bayarall[0] + tranall[0];
                            double akhir_sumprice = akhir_totprice + akhir_plaship2;
                            total_harga2.setText(String.format("%.0f",akhir_sumprice));
                            shipfee.setText(String.valueOf(akhir_plaship2));
                        }
                        else if (negara_ship.equals("Thailand")){
                            String shipgold7 = array.getJSONObject(6).getString("gold");
                            double plaship = Double.parseDouble(shipgold7);
                            double akhir_plaship1 = akhir_tw * plaship;
                            double akhir_plaship2 = finalTotal * akhir_plaship1;
                            double akhir_th1 = finalTotal1 * th1;
                            double fee10 = akhir_th1 * 0.1;
                            double akhir_totprice = bayarall[0] + tranall[0];
                            double akhir_sumprice = akhir_totprice + akhir_plaship2;
                            total_harga2.setText(String.format("%.0f",akhir_sumprice));
                            shipfee.setText(String.valueOf(akhir_plaship2));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else if (level.equals("SILVER")){
                    try {
                        JSONArray array = new JSONArray(response.body().string());

                        if (negara_ship.equals("Japan")){
                            String shipsil1 = array.getJSONObject(0).getString("silver");
                            double plaship = Double.parseDouble(shipsil1);
                            double akhir_plaship1 = akhir_tw * plaship;
                            double akhir_plaship2 = finalTotal * akhir_plaship1;
                            double akhir_th1 = finalTotal1 * th1;
                            double fee10 = akhir_th1 * 0.1;
                            double akhir_totprice = bayarall[0] + tranall[0];
                            double akhir_sumprice = akhir_totprice + akhir_plaship2;
                            total_harga2.setText(String.format("%.0f",akhir_sumprice));
                            shipfee.setText(String.valueOf(akhir_plaship2));
                        }
                        else if (negara_ship.equals("Hongkong")){
                            String shipsil2 = array.getJSONObject(1).getString("silver");
                            double plaship = Double.parseDouble(shipsil2);
                            double akhir_plaship1 = akhir_tw * plaship;
                            double akhir_plaship2 = finalTotal * akhir_plaship1;
                            double akhir_th1 = finalTotal1 * th1;
                            double fee10 = akhir_th1 * 0.1;
                            double akhir_totprice = bayarall[0] + tranall[0];
                            double akhir_sumprice = akhir_totprice + akhir_plaship2;
                            total_harga2.setText(String.format("%.0f",akhir_sumprice));
                            shipfee.setText(String.valueOf(akhir_plaship2));
                        }
                        else if (negara_ship.equals("Australia")){
                            String shipsil3 = array.getJSONObject(2).getString("silver");
                            double plaship = Double.parseDouble(shipsil3);
                            double akhir_plaship1 = akhir_tw * plaship;
                            double akhir_plaship2 = finalTotal * akhir_plaship1;
                            double akhir_th1 = finalTotal1 * th1;
                            double fee10 = akhir_th1 * 0.1;
                            double akhir_totprice = bayarall[0] + tranall[0];
                            double akhir_sumprice = akhir_totprice + akhir_plaship2;
                            total_harga2.setText(String.format("%.0f",akhir_sumprice));
                            shipfee.setText(String.valueOf(akhir_plaship2));
                        }
                        else if (negara_ship.equals("Malaysia")){
                            String shipsil4 = array.getJSONObject(3).getString("silver");
                            double plaship = Double.parseDouble(shipsil4);
                            double akhir_plaship1 = akhir_tw * plaship;
                            double akhir_plaship2 = finalTotal * akhir_plaship1;
                            double akhir_th1 = finalTotal1 * th1;
                            double fee10 = akhir_th1 * 0.1;
                            double akhir_totprice = bayarall[0] + tranall[0];
                            double akhir_sumprice = akhir_totprice + akhir_plaship2;
                            total_harga2.setText(String.format("%.0f",akhir_sumprice));
                            shipfee.setText(String.valueOf(akhir_plaship2));
                        }
                        else if (negara_ship.equals("Singapore")){
                            String shipsil5 = array.getJSONObject(4).getString("silver");
                            double plaship = Double.parseDouble(shipsil5);
                            double akhir_plaship1 = akhir_tw * plaship;
                            double akhir_plaship2 = finalTotal * akhir_plaship1;
                            double akhir_th1 = finalTotal1 * th1;
                            double fee10 = akhir_th1 * 0.1;
                            double akhir_totprice = bayarall[0] + tranall[0];
                            double akhir_sumprice = akhir_totprice + akhir_plaship2;
                            total_harga2.setText(String.format("%.0f",akhir_sumprice));
                            shipfee.setText(String.valueOf(akhir_plaship2));
                        }
                        else if (negara_ship.equals("Korea Selatan")){
                            String shipsil6 = array.getJSONObject(5).getString("silver");
                            double plaship = Double.parseDouble(shipsil6);
                            double akhir_plaship1 = akhir_tw * plaship;
                            double akhir_plaship2 = finalTotal * akhir_plaship1;
                            double akhir_th1 = finalTotal1 * th1;
                            double fee10 = akhir_th1 * 0.1;
                            double akhir_totprice = bayarall[0] + tranall[0];
                            double akhir_sumprice = akhir_totprice + akhir_plaship2;
                            total_harga2.setText(String.format("%.0f",akhir_sumprice));
                            shipfee.setText(String.valueOf(akhir_plaship2));
                        }
                        else if (negara_ship.equals("Thailand")){
                            String shipsil7 = array.getJSONObject(6).getString("silver");
                            double plaship = Double.parseDouble(shipsil7);
                            double akhir_plaship1 = akhir_tw * plaship;
                            double akhir_plaship2 = finalTotal * akhir_plaship1;
                            double akhir_th1 = finalTotal1 * th1;
                            double fee10 = akhir_th1 * 0.1;
                            double akhir_totprice = bayarall[0] + tranall[0];
                            double akhir_sumprice = akhir_totprice + akhir_plaship2;
                            total_harga2.setText(String.format("%.0f",akhir_sumprice));
                            shipfee.setText(String.valueOf(akhir_plaship2));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

        checkOUT = findViewById(R.id.checkout);
        checkOUT.setOnClickListener(this);
    }

    private void getCart(final RecyclerView rvlist, String id) {
        Retrofit retrofit = GetClient.getClient(ApiUtil.Base_url);
        ProductService apiService = retrofit.create(ProductService.class);

        Call<ArrayList<DataAll>> call = apiService.getKeranjang(id);
        call.enqueue(new Callback<ArrayList<DataAll>>() {
            @Override
            public void onResponse(Call<ArrayList<DataAll>> call, Response<ArrayList<DataAll>> response) {
                AllAdapter allAdapter= new AllAdapter(getApplication(), response.body());
                rvlist.setLayoutManager(new LinearLayoutManager(getApplication()));
                rvlist.setAdapter(allAdapter);
            }

            @Override
            public void onFailure(Call<ArrayList<DataAll>> call, Throwable t) {
                Log.e("ERROR", t.getMessage());
            }
        });
    }

    @Override
    public void onClick(View view) {
        Date date = new Date();
        String waktu = String.valueOf(date);
        String is_finish = "no";
        String idagent = sharedPrefManager.getSpIdmember();
        addTransaction(
                waktu,
                total_harga2.getText().toString(),
                total_weight.getText().toString(),
                is_finish,
                idagent,
                transfee.getText().toString(),
                shipfee.getText().toString(),
                total_item.getText().toString());


    }

        private void addTransaction(String waktu,
                                    String total_bayar,
                                    String total_berat,
                                    String is_finish,
                                    String agentid,
                                    String transactionfee,
                                    String shippingfee,
                                    String total_barang) {
        Retrofit rfit = GetClient.getClient(ApiUtil.Base_url);
        ApiService as = rfit.create(ApiService.class);
        Call<ResponseBody> addtran = as.addTransaction(waktu, total_bayar, total_berat, is_finish, agentid, transactionfee, shippingfee, total_barang);
        addtran.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String username = sharedPrefManager.getSPNama();

                try {
                    JSONObject obj = new JSONObject(response.body().string());

                    String id = obj.getString("newId");

                    Retrofit retrofitss = GetClient.getClient(ApiUtil.Base_url);
                    ApiService apiServicess = retrofitss.create(ApiService.class);
                    Call<ResponseBody> chart = apiServicess.getlistChart(username);
                    chart.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                            try {
                                JSONArray array = new JSONArray(response.body().string());

                                for (int i = 0; i < array.length(); i++){

                                    String id_cus = array.getJSONObject(i).getString("id_customer");
                                    String id_agent = sharedPrefManager.getSpIdmember();
                                    String id_produk = array.getJSONObject(i).getString("id_produk");
                                    String nama_produk = array.getJSONObject(i).getString("nama_produk");
                                    String harga = array.getJSONObject(i).getString("harga");
                                    String jumlah_beli = array.getJSONObject(i).getString("jumlah_beli");
                                    String total_harga = array.getJSONObject(i).getString("total_harga");
                                    String catatan = array.getJSONObject(i).getString("catatan");
                                    String bpom = array.getJSONObject(i).getString("bpom");
                                    String id_negara = array.getJSONObject(i).getString("id_negara");
                                    String id_kategori = array.getJSONObject(i).getString("kategori");
                                    String berat = array.getJSONObject(i).getString("berat");
                                    String tot_berat = array.getJSONObject(i).getString("total_berat");
                                    Date date = new Date();
                                    String waktu2 = String.valueOf(date);

                                    ApiService apiService = ApiUtil.getPaketService();

                                    Call<ResponseBody> detail = apiService.addDetailTransaction(
                                            id,
                                            id_agent,
                                            id_cus,
                                            id_produk,
                                            nama_produk,
                                            harga,
                                            jumlah_beli,
                                            total_harga,
                                            catatan,
                                            bpom,
                                            id_negara,
                                            id_kategori,
                                            berat,
                                            tot_berat,
                                            waktu2);

                                    detail.enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                                            if(response.isSuccessful()){

                                                clearCart(id_agent);
                                                Intent intent = new Intent(getApplication(), MainActivity.class);
                                                startActivity(intent);
                                                Toast.makeText(getApplication(), "Checkout Success", Toast.LENGTH_SHORT).show();
                                                finish();

                                            }

                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                Log.d("ERROR_NJER", t.getMessage());
                                        }
                                    });

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplication(), e.toString(), Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void clearCart(String idagent) {
        Retrofit retrofit3 = GetClient.getClient(ApiUtil.Base_url);
        ApiService apiService3 = retrofit3.create(ApiService.class);
        Call<ResponseBody> clearcart = apiService3.clearCart(idagent);
        clearcart.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
}
