package com.warehousenesia.id.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.warehousenesia.id.Model.DataShopperCustomer;
import com.warehousenesia.id.R;

import java.util.ArrayList;

public class PurchaseuserAdapter extends RecyclerView.Adapter<PurchaseuserAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private ArrayList<DataShopperCustomer> list;

    public PurchaseuserAdapter(Context context, ArrayList<DataShopperCustomer> list){
        inflater = LayoutInflater.from(context);
        this.list = list;
    }

    @NonNull
    @Override
    public PurchaseuserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_purchaseuser, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PurchaseuserAdapter.ViewHolder holder, int position) {
        holder.nama.setText(list.get(position).getNama_produk());
        holder.jumlah.setText(list.get(position).getJumlah_barang());
        holder.catatan.setText(list.get(position).getCatatan());
        Glide.with(holder.itemView.getContext())
                .load("http://192.168.100.17:1997/"+list.get(position).getGambar())
                .apply(new RequestOptions().override(360, 360))
                .into(holder.gambar);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView nama, jumlah, catatan;
        ImageView gambar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nama = itemView.findViewById(R.id.txt_item_product);
            jumlah = itemView.findViewById(R.id.jumlah);
            catatan = itemView.findViewById(R.id.catatan);
            gambar = itemView.findViewById(R.id.img_item_product);

        }
    }
}
