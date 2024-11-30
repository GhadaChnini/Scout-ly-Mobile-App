package com.example.myapplication.Fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.Models.Customer;
import com.example.myapplication.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditAccountFragment extends Fragment {
    private EditText editName, editUsername, editPassword, editJob;
    private Spinner spinnerGender;
    private DatePicker datePicker;
    private Button buttonSave;

    private Customer currentCustomer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_account_activity, container, false);

        // Initialize views
        editName = view.findViewById(R.id.editName);
        editUsername = view.findViewById(R.id.editUsername);
        editPassword = view.findViewById(R.id.editPassword);
        editJob = view.findViewById(R.id.editJob);
        spinnerGender = view.findViewById(R.id.spinnerGender);
        datePicker = view.findViewById(R.id.datePicker);
        buttonSave = view.findViewById(R.id.buttonSave);

        // Load current user data
        loadCustomerData();

        // Save button functionality
        buttonSave.setOnClickListener(v -> saveCustomerData());

        return view;
    }

    private void loadCustomerData() {
        // Fetch customer data (hardcoded for now; replace with actual database or API call)
        currentCustomer = new Customer("Ghada", "Ghada17", "ghada123", "Female", "2001-01-17", "Student");

        // Populate the form with customer data
        editName.setText(currentCustomer.getName());
        editUsername.setText(currentCustomer.getUsername());
        editPassword.setText(currentCustomer.getPassword());
        editJob.setText(currentCustomer.getJob());

        // Set gender in spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(), R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adapter);
        if (currentCustomer.getGender().equalsIgnoreCase("Male")) {
            spinnerGender.setSelection(0);
        } else if (currentCustomer.getGender().equalsIgnoreCase("Female")) {
            spinnerGender.setSelection(1);
        }

        // Set birth date in DatePicker
        String birthDate = currentCustomer.getBirthDate(); // Format: "YYYY-MM-DD"
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date date = sdf.parse(birthDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void saveCustomerData() {
        // Get updated data from form fields
        String updatedName = editName.getText().toString().trim();
        String updatedUsername = editUsername.getText().toString().trim();
        String updatedPassword = editPassword.getText().toString().trim();
        String updatedJob = editJob.getText().toString().trim();
        String updatedGender = spinnerGender.getSelectedItem().toString();

        int updatedYear = datePicker.getYear();
        int updatedMonth = datePicker.getMonth() + 1; // DatePicker months are 0-based
        int updatedDay = datePicker.getDayOfMonth();

        // Format birth date as "YYYY-MM-DD"
        String updatedBirthDate = updatedYear + "-" + (updatedMonth < 10 ? "0" + updatedMonth : updatedMonth) + "-"
                + (updatedDay < 10 ? "0" + updatedDay : updatedDay);

        // Validate fields
        if (TextUtils.isEmpty(updatedName) || TextUtils.isEmpty(updatedUsername) || TextUtils.isEmpty(updatedPassword) || TextUtils.isEmpty(updatedJob)) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update current customer object
        currentCustomer.setName(updatedName);
        currentCustomer.setUsername(updatedUsername);
        currentCustomer.setPassword(updatedPassword);
        currentCustomer.setGender(updatedGender);
        currentCustomer.setJob(updatedJob);
        currentCustomer.setBirthDate(updatedBirthDate);

        // Save customer data (replace with actual save logic for database or API call)
        Toast.makeText(getContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();

        // Debugging: Print updated customer data
        System.out.println("Updated Customer: " + currentCustomer.getName() + ", " +
                currentCustomer.getUsername() + ", " +
                currentCustomer.getBirthDate() + ", " +
                currentCustomer.getGender() + ", " +
                currentCustomer.getJob());
    }
}
