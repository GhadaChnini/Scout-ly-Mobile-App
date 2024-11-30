package com.example.myapplication.Activities;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Datbase.CommerceDBHelper;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class DeleteUserActivity extends AppCompatActivity {
    CommerceDBHelper database;
    Spinner useresSpinner;
    ArrayAdapter spinnerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_user);
        database=new CommerceDBHelper(getApplicationContext());
        useresSpinner=(Spinner) findViewById(R.id.spinner);
        Button deleteBtn=(Button) findViewById(R.id.button4);
        getUsers();
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an AlertDialog
                new android.app.AlertDialog.Builder(DeleteUserActivity.this)
                        .setMessage("Are you sure you want to delete this user?")
                        .setCancelable(false)  // Set to false to prevent cancellation by clicking outside the dialog
                        .setPositiveButton("Yes", new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(android.content.DialogInterface dialog, int id) {
                                // Perform the deletion here
                                String selectedUser = useresSpinner.getSelectedItem().toString();
                                database.DeleteAccount(selectedUser);
                                Toast.makeText(getApplicationContext(), "User " + selectedUser + " deleted", Toast.LENGTH_SHORT).show();
                                getUsers();  // Refresh the user list after deletion
                            }
                        })
                        .setNegativeButton("No", null)  // Do nothing when 'No' is clicked
                        .show();
            }
        });

    }
    private void getUsers(){
        Cursor cursor=database.allUsers();
        List<String> usersName=new ArrayList<String>();
        if(cursor!=null){
            while (!cursor.isAfterLast()){
                usersName.add(cursor.getString(0));
                cursor.moveToNext();
            }
            spinnerAdapter=new ArrayAdapter(getApplicationContext(), R.layout.spinner_item,usersName);
            useresSpinner.setAdapter(spinnerAdapter);
        }
    }
}