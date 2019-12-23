package com.warehousenesia.id.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.warehousenesia.id.AddToKatalog;
import com.warehousenesia.id.CustomItem;
import com.warehousenesia.id.MainActivity;
import com.warehousenesia.id.Model.DataRequestProduct;
import com.warehousenesia.id.R;
import com.warehousenesia.id.Service.ApiService;
import com.warehousenesia.id.Service.ApiUtil;
import com.warehousenesia.id.Service.GetClient;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RequestShopperAdapter extends RecyclerView.Adapter<RequestShopperAdapter.ViewHolder> {

    private ArrayList<DataRequestProduct> ProductList;

    public RequestShopperAdapter(Context context, ArrayList<DataRequestProduct> ProductList){
        this.ProductList = ProductList;
    }

    @NonNull
    @Override
    public RequestShopperAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request_shopper, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestShopperAdapter.ViewHolder holder, int position) {

        holder.nama_pro.setText(ProductList.get(position).getNama_barang());
        holder.deskripsi_pro.setText(ProductList.get(position).getDeskripsi());
        holder.kategori_pro.setText(ProductList.get(position).getKategori());
        holder.status_pro.setText(ProductList.get(position).getStatus());

        Glide.with(holder.itemView.getContext())
                .load("http://192.168.100.17:1997/"+ProductList.get(position).getGambar())
                .apply(new RequestOptions().override(360, 360))
                .into(holder.gambar_pro);

        double acak = ((Math.floor(1000 + Math.random() * 900)));
        int random = (int) acak;
        String id_neg = ProductList.get(position).getId_negara();

        if (id_neg.equals("1")){
            id_neg = "JPPS"+random;
        }else if (id_neg.equals("2")){
            id_neg = "HKPS"+random;
        }else if (id_neg.equals("3")){
            id_neg = "AUPS"+random;
        }else if (id_neg.equals("4")){
            id_neg = "MLPS"+random;
        }else if (id_neg.equals("5")){
            id_neg = "SGPS"+random;
        }else if (id_neg.equals("6")){
            id_neg = "KOPS"+random;
        }else if (id_neg.equals("7")){
            id_neg = "THPS"+random;
        }else {
            id_neg = ProductList.get(position).getId_negara();
        }

        String akhir = id_neg;

        holder.tambah.setOnClickListener(view -> {
            Intent intent = new Intent(holder.itemView.getContext(), AddToKatalog.class);
            intent.putExtra("id_produk", akhir);
            intent.putExtra("id", ProductList.get(position).getId());
            intent.putExtra("id_negara", ProductList.get(position).getId_negara());
            intent.putExtra("nama_barang", ProductList.get(position).getNama_barang());
            holder.itemView.getContext().startActivity(intent);
        });

        holder.buang.setOnClickListener(view -> {
            String id = ProductList.get(position).getId();
            String status = "Tolak Request";
            String kode = akhir;
            Retrofit retrofit = GetClient.getClient(ApiUtil.Base_url);
            ApiService apiService = retrofit.create(ApiService.class);
            Call<ResponseBody> call = apiService.EditRequest(status, kode, id);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Intent intent = new Intent(holder.itemView.getContext(), MainActivity.class);
                    holder.itemView.getContext().startActivity(intent);
                    Toast.makeText(holder.itemView.getContext(), "Sukses", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        });

    }

    @Override
    public int getItemCount() {
        return ProductList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView nama_pro, deskripsi_pro, kategori_pro, status_pro, idmember;
        ImageView gambar_pro, tambah, buang;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nama_pro = itemView.findViewById(R.id.txt_item_product);
            deskripsi_pro = itemView.findViewById(R.id.txt_item_description);
            kategori_pro = itemView.findViewById(R.id.txt_item_category);
            status_pro = itemView.findViewById(R.id.txt_item_status);
            idmember = itemView.findViewById(R.id.idmember);
            gambar_pro = itemView.findViewById(R.id.img_item_product);
            tambah = itemView.findViewById(R.id.terima);
            buang = itemView.findViewById(R.id.tolak);

        }
    }
}
