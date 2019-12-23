package com.warehousenesia.id.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.warehousenesia.id.ListPurchaseUser;
import com.warehousenesia.id.Model.DataShopperCustomer;
import com.warehousenesia.id.R;

import java.util.ArrayList;

public class EnduserAdapter extends RecyclerView.Adapter<EnduserAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private ArrayList<DataShopperCustomer> list;

    public EnduserAdapter(Context context, ArrayList<DataShopperCustomer> list){
        inflater = LayoutInflater.from(context);
        this.list = list;
    }

    @NonNull
    @Override
    public EnduserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_enduser, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EnduserAdapter.ViewHolder holder, int position) {
        holder.nama.setText(list.get(position).getNama_lengkap());
        String id_transaksi = list.get(position).getId_transaksi();
        String id_customer = list.get(position).getId_customer();
        holder.detail.setOnClickListener(view -> {
            Intent intent = new Intent(holder.itemView.getContext(), ListPurchaseUser.class);
            intent.putExtra("id_transaksi", id_transaksi);
            intent.putExtra("id_customer", id_customer);
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView nama;
        ImageView detail;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nama = itemView.findViewById(R.id.nama_lengkap);
            detail = itemView.findViewById(R.id.detail);

        }
    }
}
