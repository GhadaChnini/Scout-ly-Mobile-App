package com.example.myapplication.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Models.Customer;
import com.example.myapplication.R;

public class EditAccountActivity extends AppCompatActivity {

    private EditText editName, editUsername, editPassword, editJob;
    private Spinner spinnerGender;
    private DatePicker datePicker;
    private Button buttonSave;

    private Customer customer; // To hold the customer data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_account_activity);

        // Initialize UI components
        editName = findViewById(R.id.editName);
        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        editJob = findViewById(R.id.editJob);
        spinnerGender = findViewById(R.id.spinnerGender);
        datePicker = findViewById(R.id.datePicker);
        buttonSave = findViewById(R.id.buttonSave);

        // Assume you get the customer object from an intent or a database
        customer = getCustomerData(); // Get the current customer data

        // Pre-fill the fields with existing customer data
        editName.setText(customer.getName());
        editUsername.setText(customer.getUsername());
        editPassword.setText(customer.getPassword());
        editJob.setText(customer.getJob());

        // For gender, you can implement a proper gender spinner adapter here

        // Set an OnClickListener for the save button
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve updated data
                String updatedName = editName.getText().toString();
                String updatedUsername = editUsername.getText().toString();
                String updatedPassword = editPassword.getText().toString();
                String updatedJob = editJob.getText().toString();
                String updatedGender = spinnerGender.getSelectedItem().toString();  // Get selected gender
                int birthYear = datePicker.getYear();
                int birthMonth = datePicker.getMonth();
                int birthDay = datePicker.getDayOfMonth();
                String updatedBirthDate = birthYear + "-" + (birthMonth + 1) + "-" + birthDay;  // Format birth date

                // Create a new Customer object with the updated data
                Customer updatedCustomer = new Customer(updatedName, updatedUsername, updatedPassword, updatedGender, updatedBirthDate, updatedJob);

                // Update the customer in your database or wherever you store it
                updateCustomerInDatabase(updatedCustomer);

                // Show a success message
                Toast.makeText(EditAccountActivity.this, "Account updated successfully!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to get current customer data (replace with your actual data fetching logic)
    private Customer getCustomerData() {
        // Example: Return a dummy customer for now
        return new Customer("John Doe", "johndoe", "password123", "Male", "1990-01-01", "Engineer");
    }

    // Method to update the customer in your database or data source (replace with actual logic)
    private void updateCustomerInDatabase(Customer customer) {
        // Implement your database update logic here
    }
}
