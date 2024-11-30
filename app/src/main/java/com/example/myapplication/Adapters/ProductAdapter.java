package com.example.myapplication.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Datbase.CommerceDBHelper;
import com.example.myapplication.Models.Product;
import com.example.myapplication.Models.UnconfirmedProducts;
import com.example.myapplication.R;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private ArrayList<Product> data;
    private Context context;
    private int userId;
    private CommerceDBHelper database;

    public ProductAdapter(Context context, ArrayList<Product> objects, int id) {
        this.data = objects;
        this.userId = id;
        this.context = context;
        this.database = new CommerceDBHelper(context);
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = data.get(position);

        // Set image if available
        if (product.getImage() != null) {
            byte[] imageBytes = product.getImage();
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            holder.prodImage.setImageBitmap(bitmap);
        }

        // Set product details
        holder.nameTxt.setText(product.getName());
        holder.priceTxt.setText(product.getPrice() + " DT");

        // Add to cart functionality
        holder.addBtn.setOnClickListener(v -> {
            UnconfirmedProducts unconfirmedProduct = new UnconfirmedProducts(
                    product.getId(),
                    1,
                    product.getName(),
                    product.getImage(),
                    product.getPrice(),
                    userId
            );
            database.insertUnconfirmedProduct(unconfirmedProduct);
            Toast.makeText(context, "Product added", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    // ViewHolder class
    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView prodImage;
        TextView nameTxt, priceTxt;
        Button addBtn;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            prodImage = itemView.findViewById(R.id.product_image);
            nameTxt = itemView.findViewById(R.id.product_name_txt);
            priceTxt = itemView.findViewById(R.id.produt_price_txt);
            addBtn = itemView.findViewById(R.id.Add_product_btn);
        }
    }
}
