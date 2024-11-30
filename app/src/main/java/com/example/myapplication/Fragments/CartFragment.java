package com.example.myapplication.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adapters.CartAdapter;
import com.example.myapplication.Datbase.CommerceDBHelper;
import com.example.myapplication.Models.Order;
import com.example.myapplication.Models.OrderDetails;
import com.example.myapplication.Models.UnconfirmedProducts;
import com.example.myapplication.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartFragment extends Fragment {
    private FusedLocationProviderClient fusedLocationProviderClient;
    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private ArrayList<UnconfirmedProducts> data = new ArrayList<>();
    private CommerceDBHelper database;
    private double productsCost;
    private TextView origprice_txt, delivery_txt, total_text, location_txt;
    private Spinner rat_spinner;
    private Button confirm_btn;
    private int userId;

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        // Initialize Views
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
        recyclerView = view.findViewById(R.id.list_cart);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        database = new CommerceDBHelper(getContext());
        origprice_txt = view.findViewById(R.id.orig_price_txt);
        delivery_txt = view.findViewById(R.id.deliv_price_txt);
        total_text = view.findViewById(R.id.total_price_txt);
        location_txt = view.findViewById(R.id.location_txt);
        confirm_btn = view.findViewById(R.id.confirm_btm);
        rat_spinner = view.findViewById(R.id.spinner_rating);

        // Set Rating Spinner Adapter
        List<Integer> ratingList = new ArrayList<>();
        for (int i = 1; i <= 5; i++) ratingList.add(i);
        ArrayAdapter<Integer> rateAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, ratingList);
        rat_spinner.setAdapter(rateAdapter);

        // Get User ID
        userId = requireActivity().getIntent().getExtras().getInt("user_id");

        // Set Listeners
        view.findViewById(R.id.refresh_btn).setOnClickListener(v -> refreshCart());
        view.findViewById(R.id.get_loc_btn).setOnClickListener(v -> fetchLocation());
        confirm_btn.setOnClickListener(v -> confirmOrder());

        // Load Initial Data
        refreshCart();
        return view;
    }

    private void refreshCart() {
        getUnconfirmedProducts(userId);
        double someDoubleValue = 0.0;  // Replace this with the actual value if needed

        // Use the class-level adapter instead of redeclaring it
        adapter = new CartAdapter(getContext(), data, someDoubleValue);  // Reuse the class-level adapter
        recyclerView.setAdapter(adapter);  // Set the adapter to the RecyclerView
        calculateCost();  // Calculate the total cost
        origprice_txt.setText(String.format(Locale.getDefault(), "%.2f DT", productsCost));
        total_text.setText(String.format(Locale.getDefault(), "%.2f DT", productsCost + 30));
    }



    private void fetchLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    try {
                        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        if (addresses != null && !addresses.isEmpty()) {
                            String address = addresses.get(0).getAddressLine(0);
                            location_txt.setText(address);

                        } else {
                            Toast.makeText(getContext(), "Address not found.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Failed to fetch location.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Location is null.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void confirmOrder() {
        if (location_txt.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Set location or wait for process to complete", Toast.LENGTH_SHORT).show();
        } else if (data.isEmpty()) {
            Toast.makeText(getContext(), "Cart is empty", Toast.LENGTH_SHORT).show();
        } else {
            LocalDate date = LocalDate.now();
            Order order = new Order(userId, location_txt.getText().toString(), date);
            database.makeOrder(order);
            for (UnconfirmedProducts product : data) {
                OrderDetails details = new OrderDetails(product.getId(), product.getQuantity(), Integer.parseInt(rat_spinner.getSelectedItem().toString()));
                database.insertOrderDetails(details);
                database.updateProductQuantity(product.getId(), product.getQuantity());
            }
            database.cartEmpty();
            data.clear();
            refreshCart();
            Toast.makeText(getContext(), "Order Confirmed", Toast.LENGTH_SHORT).show();
        }
    }

    private void calculateCost() {
        productsCost = 0;
        for (UnconfirmedProducts product : data) {
            productsCost += product.getPrice() * product.getQuantity();
        }
    }

    private void getUnconfirmedProducts(int userId) {
        data.clear();
        Cursor cursor = database.getUnconfirmedProducts(userId);
        if (cursor.moveToFirst()) {
            do {
                UnconfirmedProducts product = new UnconfirmedProducts(
                        cursor.getInt(1),
                        cursor.getInt(4),
                        cursor.getString(2),
                        cursor.getBlob(6),
                        cursor.getDouble(3),
                        cursor.getInt(5)
                );
                data.add(product);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }
}
