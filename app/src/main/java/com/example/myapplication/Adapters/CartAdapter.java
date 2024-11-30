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

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Datbase.CommerceDBHelper;
import com.example.myapplication.Models.UnconfirmedProducts;
import com.example.myapplication.R;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private ArrayList<UnconfirmedProducts> data;
    private Context context;
    private CommerceDBHelper database;
    private double productsCost;  // Field to store the total product cost

    // Constructor updated to accept productsCost
    public CartAdapter(Context context, ArrayList<UnconfirmedProducts> data, double productsCost) {
        this.context = context;
        this.data = data;
        this.database = new CommerceDBHelper(context);
        this.productsCost = productsCost;  // Store the productsCost
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CartViewHolder holder, int position) {
        UnconfirmedProducts currentItem = data.get(position);

        // Bind UI components
        holder.productName.setText(currentItem.getName());
        holder.productPrice.setText(currentItem.getPrice() + " DT");
        holder.productQuantity.setText(String.valueOf(currentItem.getQuantity()));

        // Handle product image
        if (currentItem.getImage() != null) {
            Bitmap bmp = BitmapFactory.decodeByteArray(currentItem.getImage(), 0, currentItem.getImage().length);
            holder.productImage.setImageBitmap(bmp);
        } else {
            holder.productImage.setImageResource(R.drawable.default_image); // Fallback image
        }

        // Increase quantity
        holder.addQuan.setOnClickListener(v -> {
            int quan = currentItem.getQuantity() + 1;
            boolean success = database.updateQuantity(quan, currentItem.getId());
            if (success) {
                currentItem.setQuantity(quan);
                productsCost += currentItem.getPrice();  // Update the total cost when quantity changes
                notifyDataSetChanged();
            } else {
                Toast.makeText(context, "Quantity exceeded available stock!", Toast.LENGTH_SHORT).show();
            }
        });

        // Decrease quantity
        holder.decQuan.setOnClickListener(v -> {
            int quan = currentItem.getQuantity() - 1;
            if (quan > 0) {
                boolean success = database.updateQuantity(quan, currentItem.getId());
                if (success) {
                    currentItem.setQuantity(quan);
                    productsCost -= currentItem.getPrice();  // Update the total cost when quantity changes
                    notifyDataSetChanged();
                }
            } else {
                Toast.makeText(context, "Minimum quantity is 1!", Toast.LENGTH_SHORT).show();
            }
        });

        // Remove product with confirmation
        holder.removeBtn.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(context)
                    .setMessage("Are you sure you want to remove this product from your cart?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> {
                        // Remove product from database and cart
                        database.removeUnconfirmedProduct(currentItem.getId());
                        productsCost -= currentItem.getPrice() * currentItem.getQuantity();  // Adjust the total cost when a product is removed
                        data.remove(position);  // Remove the item from the data list
                        notifyDataSetChanged();  // Refresh the RecyclerView
                        Toast.makeText(context, "Product removed!", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", null) // Do nothing on 'No'
                    .show();
        });
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    // ViewHolder class
    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productPrice, productQuantity;
        Button removeBtn;
        TextView addQuan, decQuan;

        public CartViewHolder(View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.imageView2);
            productName = itemView.findViewById(R.id.textView17);
            productPrice = itemView.findViewById(R.id.textView18);
            productQuantity = itemView.findViewById(R.id.textView19);
            removeBtn = itemView.findViewById(R.id.remove_btn);
            addQuan = itemView.findViewById(R.id.textView20);
            decQuan = itemView.findViewById(R.id.textView21);
        }
    }

    // Getter for productsCost
    public double getProductsCost() {
        return productsCost;
    }
}
