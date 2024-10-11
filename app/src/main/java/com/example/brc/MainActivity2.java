package com.example.brc;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity2 extends AppCompatActivity {
    private DatabaseReference dbRef;
    private Spinner dateSpinner;
    private Button toAddTask ;
    private RecyclerView dataRecyclerView;
    private DataAdapter dataAdapter;
    private List<String> dateList;
    private Map<String, List<DataEntry>> dataMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);




        dbRef = FirebaseDatabase.getInstance().getReference("data");

        // Initialize layout components
        dateSpinner = findViewById(R.id.dateSpinner);
        dataRecyclerView = findViewById(R.id.dataRecyclerView);
        dataRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dataAdapter = new DataAdapter();
        dataRecyclerView.setAdapter(dataAdapter);
        toAddTask = findViewById(R.id.addtask) ;
        dateList = new ArrayList<>();
        dataMap = new HashMap<>();
        toAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity2.this, MainActivity3.class) ;
                startActivity(intent);
            }
        });
        // Load data from Firebase
        loadDataFromFirebase();

    }

    private void loadDataFromFirebase() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dateList.clear();
                dataMap.clear();

                for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                    String date = dateSnapshot.getKey();
                    List<DataEntry> entries = new ArrayList<>();

                    for (DataSnapshot entrySnapshot : dateSnapshot.getChildren()) {
                        Map<String, Object> data = (Map<String, Object>) entrySnapshot.getValue();
                        DataEntry dataEntry = new DataEntry(
                                data.get("currentTime").toString(),
                                data.get("intentData").toString(),
                                data.get("userEmail").toString(),
                                data.get("userInput").toString()
                        );
                        entries.add(dataEntry);
                    }

                    dataMap.put(date, entries);
                    dateList.add(date);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity2.this, android.R.layout.simple_spinner_item, dateList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                dateSpinner.setAdapter(adapter);

                dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selectedDate = dateList.get(position);
                        List<DataEntry> entries = dataMap.get(selectedDate);
                        dataAdapter.setData(entries);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("MainActivity", "loadData:onCancelled", error.toException());
            }
        });
    }
}
