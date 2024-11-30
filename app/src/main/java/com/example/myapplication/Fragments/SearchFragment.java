package com.example.myapplication.Fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adapters.ProductAdapter;
import com.example.myapplication.Datbase.CommerceDBHelper;
import com.example.myapplication.Models.Product;
import com.example.myapplication.R;

import java.util.ArrayList;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class SearchFragment extends Fragment {
    ArrayList<Product> data=new ArrayList<Product>();
    ProductAdapter adapter;
    RecyclerView searchRecyclerView;
    EditText text_search;
    ImageButton search_btn;
    ImageButton voice_btn;
    ImageButton camera_btn;
    int userId;
    CommerceDBHelper database;
    private ArrayList<Product> products = new ArrayList<>();
    int voiceCode = 1;
    int cameraRequest = 1888;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        userId = getActivity().getIntent().getExtras().getInt("user_id");
        text_search = view.findViewById(R.id.search_text);
        search_btn = view.findViewById(R.id.search_btn);
        voice_btn = view.findViewById(R.id.mice_btn);
        camera_btn = view.findViewById(R.id.camera_btn);
        searchRecyclerView = view.findViewById(R.id.search_recyclerView);
        database = new CommerceDBHelper(getContext());

        // Setup RecyclerView
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        voice_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                startActivityForResult(intent, voiceCode);
            }
        });

        camera_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                    intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes
                    startActivityForResult(intent, 0);
                } catch (Exception e) {
                    Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
                    Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
                    startActivity(marketIntent);
                }
            }
        });

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchQuery = text_search.getText().toString();

                if (searchQuery.isEmpty()) {
                    // If search query is empty, show a Toast message
                    Toast.makeText(getContext(), "Please enter a product name", Toast.LENGTH_SHORT).show();
                } else {
                    // Fetch data based on search query
                    getdata(searchQuery);

                    // Check if data is found and show a Toast accordingly
                    if (data.isEmpty()) {
                        Toast.makeText(getContext(), "No products found", Toast.LENGTH_SHORT).show();
                    } else {
                        // If products are found, show a success message
                        Toast.makeText(getContext(), "Showing results for " + searchQuery, Toast.LENGTH_SHORT).show();
                    }

                    // Update RecyclerView with the adapter
                    adapter = new ProductAdapter(getContext(), data, userId);
                    searchRecyclerView.setAdapter(adapter);
                }
            }
        });


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent Internaldata) {
        if (requestCode == voiceCode && resultCode == getActivity().RESULT_OK) {
            ArrayList<String> text_fromVoice = Internaldata.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            text_search.setText(text_fromVoice.get(0));
            getdata(text_fromVoice.get(0));
            adapter = new ProductAdapter(getContext(), data, userId);
            searchRecyclerView.setAdapter(adapter);
        }

        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = Internaldata.getStringExtra("SCAN_RESULT");
                text_search.setText(contents);
                getdata(contents);
                adapter = new ProductAdapter(getContext(), data, userId);
                searchRecyclerView.setAdapter(adapter);
            }
            if (resultCode == RESULT_CANCELED) {
                // Handle cancel
            }
        }
    }

    protected void getdata(String name) {
        data.clear();
        Product product;
        Cursor cursor = database.getProductByName(name);
        if (cursor != null) {
            // 0prodid integer,1 prodname tex,2 price real,3 quantity integer,4 cat_id integer,5image blob
            while (!cursor.isAfterLast()) {
                product = new Product(cursor.getInt(3), cursor.getInt(4), cursor.getString(1), cursor.getBlob(5), cursor.getDouble(2));
                product.setId(cursor.getInt(0));
                data.add(product);
                cursor.moveToNext();
            }
        }
    }
}
