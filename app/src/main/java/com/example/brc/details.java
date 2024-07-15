package com.example.brc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class details extends AppCompatActivity {
    TextView text;
    EditText editText;
    Button saveButton;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("data");

        // Get views
        editText = findViewById(R.id.texti);
        text = findViewById(R.id.textView2);
        saveButton = findViewById(R.id.button);

        // Retrieve data from intent
        Intent intent = getIntent();
        String value = intent.getStringExtra("data");

        // Display the value
        text.setText(value);

        // Get the current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }
        String userEmail = user.getEmail();

        // Set up button click listener to save data
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get current date and time
                String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                String currentTime = new SimpleDateFormat("HH:mm:ss").format(new Date());

                // Get user input data
                String userInput = editText.getText().toString();

                // Create a data map to store in Firebase
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("intentData", value);
                dataMap.put("userInput", userInput);
                dataMap.put("currentTime", currentTime);
                dataMap.put("userEmail", userEmail);

                // Store data in Firebase under the current date
                databaseReference.child(currentDate).push().setValue(dataMap);

                // Show a toast message
                Toast.makeText(details.this, "Data saved", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(details.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
