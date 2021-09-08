package com.sisterhood.mapsproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sisterhood.mapsproject.models.LocationModel1;

import java.util.ArrayList;

import static android.view.LayoutInflater.from;
import static com.sisterhood.mapsproject.R.id.elastic;
import static com.sisterhood.mapsproject.R.id.submissions_list_recyclerView;

public class ViewSubmissionsActivity extends AppCompatActivity {
    private static final String TAG = "ViewSubmissionsActivity";
    private Context context = ViewSubmissionsActivity.this;

    private ProgressDialog progressDialog;

    private ArrayList<LocationModel1> tasksArrayList = new ArrayList<>();
    private ArrayList<LocationModel1> tasksArrayListAll = new ArrayList<>();
    private ArrayList<String> ratingsArrayList = new ArrayList<>();
    private ArrayList<String> descriptionsArrayList = new ArrayList<>();

    private RecyclerView conversationRecyclerView;
    private RecyclerViewAdapterMessages adapter;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_submissions);

        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        EditText cityNameEt = findViewById(R.id.city_name_edittext_submissions);
        cityNameEt.addTextChangedListener(cityNameEtTextWatcher());

        databaseReference.child("locations").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!snapshot.exists()) {
                    Toast.makeText(context, "No data exists", Toast.LENGTH_SHORT).show();
                    return;
                }

                tasksArrayList.clear();
                tasksArrayListAll.clear();
                ratingsArrayList.clear();
                descriptionsArrayList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    LocationModel1 model = dataSnapshot.getValue(LocationModel1.class);

                    tasksArrayList.add(model);
                    tasksArrayListAll.add(model);

                    if (dataSnapshot.child("rating").exists()) {
                        ratingsArrayList.add(dataSnapshot.child("rating").getValue(String.class));
                    } else {
                        ratingsArrayList.add("null");
                    }

                    if (dataSnapshot.child("desc").exists()) {
                        descriptionsArrayList.add(dataSnapshot.child("desc").getValue(String.class));
                    } else {
                        descriptionsArrayList.add("null");
                    }

                }

                initRecyclerView();
                progressDialog.dismiss();
//                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16);//25
//                mMap.animateCamera(cameraUpdate);
//                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(context, error.toException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private TextWatcher cityNameEtTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
    }

    private void initRecyclerView() {

        conversationRecyclerView = findViewById(submissions_list_recyclerView);
        conversationRecyclerView.addItemDecoration(new DividerItemDecoration(conversationRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        adapter = new RecyclerViewAdapterMessages();
        //        LinearLayoutManager layoutManagerUserFriends = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        //    int numberOfColumns = 3;
        //int mNoOfColumns = calculateNoOfColumns(getApplicationContext(), 50);
        //  recyclerView.setLayoutManager(new GridLayoutManager(this, mNoOfColumns));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        conversationRecyclerView.setLayoutManager(linearLayoutManager);
        conversationRecyclerView.setHasFixedSize(true);
        conversationRecyclerView.setNestedScrollingEnabled(false);

        conversationRecyclerView.setAdapter(adapter);

        //    if (adapter.getItemCount() != 0) {

        //        noChatsLayout.setVisibility(View.GONE);
        //        chatsRecyclerView.setVisibility(View.VISIBLE);

        //    }

    }

    private class RecyclerViewAdapterMessages extends Adapter
            <RecyclerViewAdapterMessages.ViewHolderRightMessage> implements Filterable {

        @NonNull
        @Override
        public ViewHolderRightMessage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = from(parent.getContext()).inflate(R.layout.layout_submissions_list, parent, false);
            return new ViewHolderRightMessage(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolderRightMessage holder, int position) {

            LocationModel1 locationModel = tasksArrayList.get(position);

            holder.descriptionTv.setText(descriptionsArrayList.get(position));
            holder.ratingTv.setText(ratingsArrayList.get(position));
            holder.longitudeTv.setText(locationModel.getLongitude() + "");
            holder.latitudeTv.setText(locationModel.getLatitude() + "");
            holder.cityNameTv.setText(locationModel.getCityName());
            holder.nameTv.setText(locationModel.getName());
            holder.dateTimeTv.setText(locationModel.getDateTime());

            holder.parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ViewSubmissionsActivity.this, MapsActivity.class);
                    intent.putExtra("lat", locationModel.getLatitude());
                    intent.putExtra("long", locationModel.getLongitude());
                    startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            if (tasksArrayList == null)
                return 0;
            return tasksArrayList.size();
        }

        public class ViewHolderRightMessage extends ViewHolder {

            TextView dateTimeTv, nameTv, cityNameTv, latitudeTv, longitudeTv, ratingTv, descriptionTv;
            LinearLayout parentLayout;

            public ViewHolderRightMessage(@NonNull View v) {
                super(v);
                dateTimeTv = v.findViewById(R.id.dateTimeTvLayout);
                descriptionTv = v.findViewById(R.id.descTvLayout);
                nameTv = v.findViewById(R.id.nameTvLayout);
                parentLayout = v.findViewById(R.id.parent_layout_submissions);
                cityNameTv = v.findViewById(R.id.cityNameTvLayout);
                cityNameTv = v.findViewById(R.id.cityNameTvLayout);
                latitudeTv = v.findViewById(R.id.latitudeTvLayout);
                longitudeTv = v.findViewById(R.id.longitudeTvLayout);
                ratingTv = v.findViewById(R.id.ratingTvLayout);

            }
        }

        @Override
        public Filter getFilter() {
            return cityNamesFilter;
        }

        private Filter cityNamesFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                ArrayList<LocationModel1> filteredList = new ArrayList<>();
                Log.d(TAG, "performFiltering: ");
                if (constraint == null
                        || constraint.length() == 0
                        || constraint.toString().trim().equals("")
                        || constraint.toString() == null) {
                    Log.d(TAG, "performFiltering: if (constraint == null || constraint.length() == 0 || constraint.toString().trim().equals(\"\")) {");
                    filteredList.addAll(tasksArrayListAll);
                } else {
                    Log.d(TAG, "performFiltering: } else {");
                    String filterPattern = constraint.toString().toLowerCase().trim();

                    for (LocationModel1 item : tasksArrayListAll) {
                        if (item.getCityName() != null)
                            if (item.getCityName().toLowerCase().contains(filterPattern)) {
                                filteredList.add(item);
                            }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (tasksArrayList == null) {
                    Log.d(TAG, "publishResults: if (mData == null) {");
                    return;
                }
                if (results.values == null) {
                    Log.d(TAG, "publishResults: if ( results.values == null){");
                    return;
                }
                tasksArrayList.clear();
                tasksArrayList.addAll((ArrayList<LocationModel1>) results.values);
                notifyDataSetChanged();
                Log.d(TAG, "publishResults: done");
            }
        };

    }

//    private static class LocationModel {
//
//        private String name, dateTime, cityName;
//        private double latitude, longitude;
//
//        public LocationModel(String name, String dateTime, String cityName, double latitude, double longitude) {
//            this.name = name;
//            this.dateTime = dateTime;
//            this.cityName = cityName;
//            this.latitude = latitude;
//            this.longitude = longitude;
//        }
//
//        public String getCityName() {
//            return cityName;
//        }
//
//        public void setCityName(String cityName) {
//            this.cityName = cityName;
//        }
//
//        public String getDateTime() {
//            return dateTime;
//        }
//
//        public void setDateTime(String dateTime) {
//            this.dateTime = dateTime;
//        }
//
//        public double getLatitude() {
//            return latitude;
//        }
//
//        public void setLatitude(double latitude) {
//            this.latitude = latitude;
//        }
//
//        public double getLongitude() {
//            return longitude;
//        }
//
//        public void setLongitude(double longitude) {
//            this.longitude = longitude;
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        public void setName(String name) {
//            this.name = name;
//        }
//
//        LocationModel() {
//        }
//    }

}