package com.example.brc;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private FirebaseAuth auth;
    private DatabaseReference database;
    Calendar calendar ;
    EditText dateInput;
    Button selectDateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        calendar = Calendar.getInstance();



        Toolbar toolbar = findViewById(R.id.tool);
        setSupportActionBar(toolbar);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dateInput = findViewById(R.id.dateInput);
        selectDateButton = findViewById(R.id.selectDateButton);
        if (dateInput.getText().toString().isEmpty()) {
            setCurrentDateToInput();
        }
        // Set up the date picker dialog
        selectDateButton.setOnClickListener(v -> {
            // Get the current date
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Create a DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    MainActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // The month returned is 0-based, so you need to add 1
                        String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        // Set the date in the EditText
                        dateInput.setText(selectedDate);
                    },
                    year, month, day
            );

            // Show the dialog
            datePickerDialog.show();
        });


        itemAdapter = new ItemAdapter(new ArrayList<>(), new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, String text) {
                if(text.equals("admin") ){
                    Intent intent1 = new Intent(MainActivity.this, MainActivity2.class);
                    startActivity(intent1);
                }else{
                Intent intent = new Intent(MainActivity.this, details.class);
                intent.putExtra("data",text);

                startActivity(intent);}
            }
        }); // Initialize with an empty list
        recyclerView.setAdapter(itemAdapter);

        checkAuthorizationAndLoadData();


    }
    private void setCurrentDateToInput() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = sdf.format(calendar.getTime());
        dateInput.setText(currentDate);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, Logininfo.class);
            startActivity(intent);
            // Handle profile action
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkAuthorizationAndLoadData() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            // User is logged in, fetch user role and load data
            fetchUserRoleAndLoadData(currentUser.getEmail());
        } else {
            // User is not logged in, show a message or redirect to login
            Toast.makeText(this, "You need to be logged in to view this list.", Toast.LENGTH_SHORT).show();
            // Redirect to login activity if necessary
        }
    }

    private void fetchUserRoleAndLoadData(String email) {
        database.child("users").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String userRole = userSnapshot.child("role").getValue(String.class);
                        // Load items based on user role
                        loadItemsByRole(userRole);
                        break;
                    }
                } else {
                    Toast.makeText(MainActivity.this, "User data not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Error fetching user role: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadItemsByRole(String userRole) {
        database.child("items").orderByChild("allowedRoles/" + userRole).equalTo(true)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<Item> itemList = new ArrayList<>();
                        for (DataSnapshot document : dataSnapshot.getChildren()) {
                            Item item = document.getValue(Item.class);
                            itemList.add(item);
                        }
                        itemAdapter.updateData(itemList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(MainActivity.this, "Error loading items: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
