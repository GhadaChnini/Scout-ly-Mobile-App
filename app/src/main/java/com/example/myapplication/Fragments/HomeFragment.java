package com.example.myapplication.Fragments;

import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.Adapters.CarouselAdapter;
import com.example.myapplication.Adapters.ProductAdapter;
import com.example.myapplication.Datbase.CommerceDBHelper;
import com.example.myapplication.Models.Product;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private ArrayList<Product> products = new ArrayList<>();
    private ProductAdapter adapter;
    private CommerceDBHelper database;
    Spinner cat_spinner;
    ArrayAdapter Cat_adapter;

    private ViewPager2 carouselViewPager;
    private CarouselAdapter carouselAdapter;

    private Handler carouselHandler;
    private Runnable carouselRunnable;

    private boolean isScrollingForward = true;
    private boolean isAutoScrolling = false;  // Flag to track auto-scroll state

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize the ViewPager2
        carouselViewPager = view.findViewById(R.id.carousel_view_pager);

        // Set up the carousel data
        List<Integer> images = new ArrayList<>();
        images.add(R.drawable.image1); // Replace with your drawable resources
        images.add(R.drawable.image2);
        images.add(R.drawable.image3);

        // Set up the adapter
        carouselAdapter = new CarouselAdapter(images);
        carouselViewPager.setAdapter(carouselAdapter);

        // Auto-scroll setup
        carouselHandler = new Handler(Looper.getMainLooper());
        carouselRunnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = carouselViewPager.getCurrentItem();

                if (isScrollingForward) {
                    // Scroll forward
                    if (currentItem == images.size() - 1) {
                        isScrollingForward = false;  // Change direction
                    } else {
                        carouselViewPager.setCurrentItem(currentItem + 1, true);
                    }
                } else {
                    // Scroll backward
                    if (currentItem == 0) {
                        isScrollingForward = true;  // Change direction
                    } else {
                        carouselViewPager.setCurrentItem(currentItem - 1, true);
                    }
                }

                // Schedule the next scroll
                carouselHandler.postDelayed(this, 3000); // Change image every 3 seconds
            }
        };

        // Start the auto-scroll (only once)
        startAutoScroll();

        // Pause auto-scroll on manual interaction
        carouselViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                    stopAutoScroll();  // Stop the auto-scroll when the user starts interacting
                } else if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    startAutoScroll();  // Restart auto-scroll when the user stops interacting
                }
            }
        });

        int userId = requireActivity().getIntent().getExtras().getInt("user_id");
        recyclerView = view.findViewById(R.id.products_recycler_view);
        database = new CommerceDBHelper(getContext());

        cat_spinner = view.findViewById(R.id.home_cat_spinnrer);
        cat_spinner.setAdapter(Cat_adapter);

        // Set up the RecyclerView with a GridLayoutManager for two columns
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Initialize the adapter with products and userId
        adapter = new ProductAdapter(getContext(), products, userId);

        // Attach the adapter to the RecyclerView
        recyclerView.setAdapter(adapter);

        // Load products
        getAllProduct();
        getAllcategory();

        cat_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (cat_spinner.getSelectedItem().toString().equals("All")) {
                    getAllProduct();
                    adapter = new ProductAdapter(getContext(), products, userId);
                    recyclerView.setAdapter(adapter);
                } else {
                    searchByCategory(cat_spinner.getSelectedItem().toString());
                    adapter = new ProductAdapter(getContext(), products, userId);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        return view;
    }

    public void getAllProduct() {
        products.clear();
        Cursor cursor = database.getAllProducts();

        if (cursor != null) {
            while (!cursor.isAfterLast()) {
                // Map cursor data to Product model
                Product product = new Product(
                        cursor.getInt(3),   // Quantity
                        cursor.getInt(4),   // Category ID
                        cursor.getString(1), // Product name
                        cursor.getBlob(5),  // Image blob
                        cursor.getDouble(2) // Price
                );
                product.setId(cursor.getInt(0));
                products.add(product);
                cursor.moveToNext();
            }
        }
        // Notify adapter of data change
        adapter.notifyDataSetChanged();
    }

    protected void getAllcategory() {
        Cursor cursor = database.getCategory();
        List<String> cate = new ArrayList<>();
        cate.add("All");
        if (cursor != null) {
            while (!cursor.isAfterLast()) {
                cate.add(cursor.getString(1));
                cursor.moveToNext();
            }
            Cat_adapter = new ArrayAdapter(getContext(), R.layout.spinner_item, cate);
            cat_spinner.setAdapter(Cat_adapter);
        }
    }

    private void searchByCategory(String name) {
        products.clear();
        ArrayList<Product> filterlist = new ArrayList<>();
        String cat_id = database.getCatId(name);
        Cursor cursor = database.getProductsByCategory(Integer.parseInt(cat_id));
        Product product;
        if (cursor != null) {
            while (!cursor.isAfterLast()) {
                product = new Product(Integer.parseInt(cursor.getString(3)),
                        Integer.parseInt(cursor.getString(4)),
                        cursor.getString(1), cursor.getBlob(5),
                        Double.parseDouble(cursor.getString(2)));
                product.setId(Integer.parseInt(cursor.getString(0)));
                products.add(product);
                cursor.moveToNext();
            }
        }
    }

    private void startAutoScroll() {
        // Start the auto-scroll only if it's not already running
        if (!isAutoScrolling) {
            carouselHandler.postDelayed(carouselRunnable, 3000);
            isAutoScrolling = true;
        }
    }

    private void stopAutoScroll() {
        // Remove the Runnable to stop auto-scroll
        carouselHandler.removeCallbacks(carouselRunnable);
        isAutoScrolling = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clean up the handler to prevent memory leaks
        carouselHandler.removeCallbacks(carouselRunnable);
    }
}
