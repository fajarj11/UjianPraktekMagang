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

import com.warehousenesia.id.ListEnduser;
import com.warehousenesia.id.Model.DataShopperCustomer;
import com.warehousenesia.id.R;

import java.util.ArrayList;

public class ShopperCustomerAdapter extends RecyclerView.Adapter<ShopperCustomerAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private ArrayList<DataShopperCustomer> list;

    public ShopperCustomerAdapter(Context context, ArrayList<DataShopperCustomer> list){
        inflater = LayoutInflater.from(context);
        this.list = list;
    }

    @NonNull
    @Override
    public ShopperCustomerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_superdata, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopperCustomerAdapter.ViewHolder holder, int position) {
        holder.tanggal.setText(list.get(position).getWaktu());
        String waktu = list.get(position).getWaktu();
        holder.detail.setOnClickListener(view -> {
            Intent intent = new Intent(holder.itemView.getContext(), ListEnduser.class);
            intent.putExtra("waktu", waktu);
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tanggal;
        ImageView detail;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tanggal = itemView.findViewById(R.id.tanggal);
            detail = itemView.findViewById(R.id.detail);

        }
    }
}
