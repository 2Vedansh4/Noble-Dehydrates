package com.example.brc;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private FirebaseAuth auth;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = findViewById(R.id.tool);
        setSupportActionBar(toolbar);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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
