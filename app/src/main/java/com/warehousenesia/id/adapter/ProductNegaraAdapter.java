package com.warehousenesia.id.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.warehousenesia.id.DetailProduct;
import com.warehousenesia.id.Model.DataProduct;
import com.warehousenesia.id.R;

import java.util.ArrayList;


public class ProductNegaraAdapter extends RecyclerView.Adapter<ProductNegaraAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<DataProduct> ProductList;

    public ProductNegaraAdapter(Context context, ArrayList<DataProduct> ProductList){
        inflater = LayoutInflater.from(context);
        this.ProductList = ProductList;
    }

    @NonNull
    @Override
    public  ProductNegaraAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i){
        View view = inflater.inflate(R.layout.item_product_country, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ProductNegaraAdapter.ViewHolder viewHolder, final int pb) {

        viewHolder.nama_pro.setText(ProductList.get(pb).getNama_produk());
        viewHolder.harga_pro.setText("$ " + ProductList.get(pb).getHarga());
        viewHolder.nagara_pro.setText(ProductList.get(pb).getNama_negara());
        Glide.with(viewHolder.itemView.getContext())
                .load("http://192.168.100.17:1997/"+ProductList.get(pb).getGambar())
                .apply(new RequestOptions().override(360, 360))
                .into(viewHolder.gambar_pro);
        viewHolder.detail_pro.setOnClickListener(view -> {
            Intent intent = new Intent(viewHolder.itemView.getContext(), DetailProduct.class);
            intent.putExtra("p_name", ProductList.get(pb).getNama_produk());
            intent.putExtra("p_deskripsi", ProductList.get(pb).getDeskripsi());
            intent.putExtra("p_harga", ProductList.get(pb).getHarga());
            intent.putExtra("p_negara", ProductList.get(pb).getNama_negara());
            intent.putExtra("p_berat", ProductList.get(pb).getBerat());
            intent.putExtra("id_negara", ProductList.get(pb).getId_negara());
            intent.putExtra("p_kategori", ProductList.get(pb).getNama_kategori());
            intent.putExtra("p_gambar", "http://192.168.100.17:1997/"+ProductList.get(pb).getGambar());
            viewHolder.itemView.getContext().startActivity(intent);
        });

    }

    @Override
    public int getItemCount(){
//        final int limit = 12;
//        if (ProductList.size() > limit){
//            return limit;
//        }else{
//            return ProductList.size();
//        }
        return ProductList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView nama_pro, harga_pro, nagara_pro, id_negara;
        ImageView gambar_pro;
        CardView detail_pro;

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            nama_pro = itemView.findViewById(R.id.txt_item_product);
            harga_pro = itemView.findViewById(R.id.txt_item_price);
            nagara_pro = itemView.findViewById(R.id.txt_item_country);
            gambar_pro = itemView.findViewById(R.id.img_item_product);
            id_negara = itemView.findViewById(R.id.id_negara);
            detail_pro = itemView.findViewById(R.id.card_item);

        }

    }

//    @Override
//    public int getItemCount(){return ProductList.size();}
//    public class ViewHolder extends RecyclerView.ViewHolder{
//
//        TextView nama_pro, harga_pro, nagara_pro, id_negara;
//        ImageView gambar_pro;
//        CardView detail_pro;
//
//        public ViewHolder(@NonNull View itemView){
//            super(itemView);
//            nama_pro = itemView.findViewById(R.id.txt_item_product);
//            harga_pro = itemView.findViewById(R.id.txt_item_price);
//            nagara_pro = itemView.findViewById(R.id.txt_item_country);
//            gambar_pro = itemView.findViewById(R.id.img_item_product);
//            id_negara = itemView.findViewById(R.id.id_negara);
//            detail_pro = itemView.findViewById(R.id.card_item);
//
//        }
//
//    }
}
