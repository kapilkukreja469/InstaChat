package com.example.instachat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.example.instachat.RecyclerViewPojo.ItemDesign;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText mTextMessage;
    Button sendMsg;
    FirebaseDatabase database;
    DatabaseReference myRef;
    RecyclerView recyclerView;
    List<ItemDesign> messagesList = new ArrayList<>();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_chat:
                    mTextMessage.setText("");
                    return true;
                case R.id.navigation_call:
                    Intent i=new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+"9729625776"));
                    startActivity(i);
                    return true;
                case R.id.navigation_profile:
                    Intent intent =new Intent(MainActivity.this,Profile.class);
                    startActivity(intent);
                    return true;
            }
            return false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        sendMsg = findViewById(R.id.send);
        mTextMessage = findViewById(R.id.message);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        recyclerView = findViewByI  d(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final MyAdapter adapter = new MyAdapter();
        recyclerView.setAdapter(adapter);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("message");
        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = mTextMessage.getText().toString();
                mTextMessage.setText("");
                ItemDesign itemDesign = new ItemDesign();
                itemDesign.setMessage(msg);
                myRef.push().setValue(itemDesign);

                Toast.makeText(MainActivity.this, "message send successfully", Toast.LENGTH_SHORT).show();
                SimpleDateFormat dateFormat=new SimpleDateFormat("dd hh:mm:ss");
                String format =dateFormat.format(new Date());
                itemDesign.setTimeStamp(format);

            }
        });
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ItemDesign itemDesign = dataSnapshot.getValue(ItemDesign.class);
                messagesList.add(itemDesign);
                adapter.notifyDataSetChanged();
                recyclerView.getLayoutManager().scrollToPosition(messagesList.size()-1);
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.recycler_design,parent,false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ItemDesign itemDesign = messagesList.get(position);
            holder.message.setText(itemDesign.getMessage());
            holder.time.setText(itemDesign.getTimeStamp());
        }
        @Override
        public int getItemCount() {
            return messagesList.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder{
            TextView message,time;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                message=itemView.findViewById(R.id.msg);
                time=itemView.findViewById(R.id.time);
            }
        }
    }
}