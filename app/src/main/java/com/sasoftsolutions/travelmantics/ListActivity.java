package com.sasoftsolutions.travelmantics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static androidx.recyclerview.widget.LinearLayoutManager.*;

public class ListActivity extends AppCompatActivity {
    ArrayList<TravelDeal> deals;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildListerner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.insert_menu:
                Intent intent =new Intent(this, DealActivity.class);
                startActivity(intent);
                return true;

            case R.id.logout_menu:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("Logout", "User Logged Out");
                                FirebaseUtil.attachListener();
                            }
                        });
                FirebaseUtil.detachListener();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class DealAdapter extends RecyclerView.Adapter<DealAdapter.DealViewHolder> {
        ArrayList<TravelDeal> deals;
        private FirebaseDatabase mFirebaseDatabase;
        private DatabaseReference mDatabaseReference;
        private ChildEventListener mChildListerner;

        public DealAdapter(){
         //  FirebaseUtil.openFbReference("travelDeals",this);
            mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
            mDatabaseReference = FirebaseUtil.mDatabaseReference;
            deals =FirebaseUtil.mDeals;
            mChildListerner = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    TravelDeal td = dataSnapshot.getValue(TravelDeal.class);
                    Log.d("Deal: ", td.getTitle());
                    td.setId(dataSnapshot.getKey());
                    deals.add(td);
                    notifyItemInserted(deals.size()-1);
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
            };
            mDatabaseReference.addChildEventListener(mChildListerner);
        }

        @NonNull
        @Override
        public DealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            View itemView = LayoutInflater.from(context)
                    .inflate(R.layout.rv_row, parent, false);
            return new DealViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull DealViewHolder holder, int position) {

            TravelDeal deal = deals.get(position);
            holder.bind(deal);
        }

        @Override
        public int getItemCount() {
            return deals.size();
        }

        public class DealViewHolder extends RecyclerView.ViewHolder{
                TextView tvTitle;
                TextView tvDescription;
                TextView tvPrice;
            public DealViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
                tvDescription =itemView.findViewById(R.id.tvDescription);
                tvPrice = itemView.findViewById(R.id.tvPrice);

            }
            public void bind(TravelDeal deal) {
                tvTitle.setText(deal.getTitle());
                tvDescription.setText(deal.getDescription());
                tvPrice.setText(deal.getPrice());
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUtil.detachListener();
    }

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseUtil.openFbReference("travelDeals", this);
        RecyclerView rvDeals  =  findViewById(R.id.rvDeals);
        final DealAdapter adapter = new DealAdapter();
        rvDeals.setAdapter(adapter);
        @SuppressLint("WrongConstant") LinearLayoutManager dealsLayoutManager =
                new LinearLayoutManager(this, VERTICAL, false);
        rvDeals.setLayoutManager(dealsLayoutManager);
        FirebaseUtil.attachListener();
    }
}
