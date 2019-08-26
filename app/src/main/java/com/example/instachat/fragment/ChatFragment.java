package com.example.instachat.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instachat.MainActivity;
import com.example.instachat.R;
import com.example.instachat.RecyclerViewPojo.ItemDesign;
import com.example.instachat.UserLogin;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ChatFragment extends Fragment {

    private EditText mTextMessage;
    Button sendMsg;
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseUser firebaseUser;
    RecyclerView recyclerView;
    List<ItemDesign> messagesList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_fragment, container, false);
        sendMsg = view.findViewById(R.id.send);
        mTextMessage =view.findViewById(R.id.message);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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
                SimpleDateFormat s = new SimpleDateFormat("dd-MMM hh:mm:ss");
                String format = s.format(new Date());
                itemDesign.setTimeStamp(format);
                itemDesign.setuId(firebaseUser.getPhoneNumber());
                myRef.push().setValue(itemDesign);

                Toast.makeText(getContext(), "message send successfully", Toast.LENGTH_SHORT).show();
            }
        });
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ItemDesign itemDesign = dataSnapshot.getValue(ItemDesign.class);
                messagesList.add(itemDesign);
                adapter.notifyDataSetChanged();
                recyclerView.getLayoutManager().scrollToPosition(messagesList.size() - 1);
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
        return view;
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.recycler_design, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ItemDesign itemDesign = messagesList.get(position);
            if (firebaseUser.getPhoneNumber().equals(itemDesign.getuId())) {
                holder.cardView1.setVisibility(View.VISIBLE);
                holder.cardView2.setVisibility(View.GONE);
                holder.message.setText(itemDesign.getMessage());
                holder.time.setText(itemDesign.getTimeStamp());
                holder.uID.setText(itemDesign.getuId());
            } else {
                holder.cardView2.setVisibility(View.VISIBLE);
                holder.cardView1.setVisibility(View.GONE);
                holder.message2.setText(itemDesign.getMessage());
                holder.time2.setText(itemDesign.getTimeStamp());
                holder.uID2.setText(itemDesign.getuId());
            }
        }

        @Override
        public int getItemCount() {
            return messagesList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView message, time, uID;
            TextView message2, time2, uID2;
            CardView cardView1,cardView2;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                uID = itemView.findViewById(R.id.userId);
                message = itemView.findViewById(R.id.msg);
                time = itemView.findViewById(R.id.time);
                cardView1 = itemView.findViewById(R.id.card1);
                cardView2 = itemView.findViewById(R.id.card2);
                uID2 = itemView.findViewById(R.id.userId2);
                message2 = itemView.findViewById(R.id.msg2);
                time2 = itemView.findViewById(R.id.time2);
            }
        }
    }
}