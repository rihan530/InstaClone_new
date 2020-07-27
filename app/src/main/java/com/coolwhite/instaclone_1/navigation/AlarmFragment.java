package com.coolwhite.instaclone_1.navigation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coolwhite.instaclone_1.DTO.ContentDTO;
import com.coolwhite.instaclone_1.MainActivity;
import com.coolwhite.instaclone_1.R;
import com.coolwhite.instaclone_1.databinding.FragmentAlarmBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class AlarmFragment extends Fragment {

    // Data Binding
    private FragmentAlarmBinding binding;

    // Firebase
    private FirebaseAuth auth;
    private DatabaseReference dbRef;
    FirebaseAuth.AuthStateListener authListener;

    //

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_alarm, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.alarmfragment_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(new AlarmRecyclerViewAdapter());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

//        ((MainActivity) getActivity()).getBinding().p
    }

    private class AlarmRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private ArrayList<ContentDTO> contentDTOs;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }
}
