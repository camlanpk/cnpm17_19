package com.example.cnpm.fragments;
import android.net.wifi.hotspot2.pps.HomeSp;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cnpm.R;
import com.example.cnpm.adapter.HomeAdapter;
import com.example.cnpm.model.HomeModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Home extends Fragment{
    private RecyclerView recyclerView;
    HomeAdapter adapter;
    private FirebaseUser user;
    private List<HomeModel> list;
    public static int LIST_SIZE = 0;

    public Home() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

        list = new ArrayList<>();
        adapter = new HomeAdapter(list, getContext());
        recyclerView.setAdapter(adapter);

        loadDataFromFirestore();

        adapter.OnPressed(new HomeAdapter.OnPressed() {
            @Override
            public void onLiked(int position, String id, String uid, List<String> likeList, boolean isChecked) {
                DocumentReference reference= FirebaseFirestore.getInstance().collection("Users")
                        .document(uid).collection("Post Images")
                        .document(id);

                if(likeList.contains(user.getUid()) && isChecked){
                    likeList.remove(user.getUid());
                }
                else {
                    likeList.add(user.getUid());
                }
                Map<String, Object> map=new HashMap<>();
                reference.update(map);
                }

            @Override
            public void onComment(int position, String id, String comment) {

            }
        });
    }

    private void init(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        }

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }


    private void loadDataFromFirestore() {

        CollectionReference reference = FirebaseFirestore.getInstance().collection("Users")
                .document(user.getUid())
                .collection("Post Images");


        reference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    Log.e("Error: ", error.getMessage());
                    return;
                }

                if (value == null)
                    return;

                list.clear();
                for (QueryDocumentSnapshot snapshot : value) {

                    if (!snapshot.exists())
                        return;

                    HomeModel model = snapshot.toObject(HomeModel.class);

                    list.add(new HomeModel(
                            model.getUserName(),
                            model.getProfileImage(),
                            model.getImageUrl(),
                            model.getUid(),
                            model.getComments(),
                            model.getDescription(),
                            model.getId(),
                            model.getTimestamp(),
                            model.getLikes()
                    ));

                }
                adapter.notifyDataSetChanged();
                LIST_SIZE = list.size();
            }
        });
    }

}
