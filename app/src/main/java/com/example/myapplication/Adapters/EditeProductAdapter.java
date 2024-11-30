package com.example.myapplication.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.Activities.EditeProductActivity;
import com.example.myapplication.Datbase.CommerceDBHelper;
import com.example.myapplication.Models.Product;
import com.example.myapplication.R;

import java.util.ArrayList;

public class EditeProductAdapter extends BaseAdapter {
    private ArrayList<Product> data;
    private Context context;
    CommerceDBHelper database;
    TextView prod_name_txt;
    ImageView image;
    Button edite;
    Button delete;

    public EditeProductAdapter(Context context, ArrayList<Product> objects) {
        data = objects;
        this.context = context;
        //sharedPreferences=getContext().getSharedPreferences("cart",Context.MODE_PRIVATE);
        //getSelectedProducts();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.editeproduct_item, parent, false);
        }
        prod_name_txt = (TextView) convertView.findViewById(R.id.textView11);
        prod_name_txt.setText(data.get(position).getName());
        database = new CommerceDBHelper(this.context);
        delete=(Button)convertView.findViewById(R.id.button);
        edite=(Button)convertView.findViewById(R.id.button3);
        image=(ImageView)convertView.findViewById(R.id.delete_edit_imageView);
        if (data.get(position).getImage() != null) {
            byte[] image_byte = data.get(position).getImage();
            Bitmap bmp = BitmapFactory.decodeByteArray(image_byte, 0, image_byte.length);
            image.setImageBitmap(bmp);
        }

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int positionToDelete = position;  // Store the position in a local variable

                // Show confirmation dialog with proper context
                new android.app.AlertDialog.Builder(v.getContext())
                        .setMessage("Are you sure you want to delete this product?")
                        .setCancelable(false) // Make the dialog non-cancelable
                        .setPositiveButton("Yes", (dialog, id) -> {
                            try {
                                // Proceed with deleting the product from the database
                                int productId = data.get(positionToDelete).getId();  // Capture the productId
                                database.deleteProduct(productId);  // Delete from the database

                                // Update the data list and notify adapter
                                data.remove(positionToDelete);  // Remove the item from the list
                                notifyDataSetChanged();  // Update the ListView

                                // Show a toast confirming the deletion
                                Toast.makeText(v.getContext(), "Product deleted", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(v.getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", (dialog, id) -> {
                            // Nothing happens if 'No' is clicked
                        })
                        .show();
            }
        });


        edite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent.getContext(), EditeProductActivity.class);
                intent.putExtra("product_id", data.get(position).getId());
                parent.getContext().startActivity(intent);
            }
        });
        return convertView;
    }
}
