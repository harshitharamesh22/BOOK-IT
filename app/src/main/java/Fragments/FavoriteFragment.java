package Fragments;


import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.projectfirebase.soen341.root.Adapters.ListItemAdapter;
import com.projectfirebase.soen341.root.Listing;
import com.projectfirebase.soen341.root.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FavoriteFragment extends Fragment implements View.OnClickListener {

    private FirebaseAuth authRef = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference itemsRef = rootRef.child("Items");
    private DatabaseReference currentUserRef;
    private DatabaseReference favRef;

    private ArrayList<Listing> listingsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ListItemAdapter mAdapter;

    private TextView fav_message_tv;

    public FavoriteFragment() {
        // Required empty public constructor
    }

    public static FavoriteFragment newInstance() {
        return new FavoriteFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        currentUserRef = rootRef.child("Users").child(user.getUid());
        favRef = currentUserRef.child("Favorites");



        final View currentView = view;
        favRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren())
                    populateFavoritesList(currentView, dataSnapshot);
                else
                    setMessage(currentView, R.string.no_favorites);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        return view;
    }

    public void populateFavoritesList(View view, final DataSnapshot favRef) {
        recyclerView = (RecyclerView) view.findViewById(R.id.fav_recycler_view);
        mAdapter = new ListItemAdapter(listingsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);

        itemsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listingsList.clear();
                Map<String, Object> favMap = (HashMap<String, Object>) favRef.getValue();
                Map<String, Object> itemsMap = (HashMap<String, Object>) dataSnapshot.getValue();

                for (String key : favMap.keySet()) {
                    Object itemMap = itemsMap.get(key);
                    if (itemMap instanceof Map) {
                        Map<String, Object> itemObj = (Map<String, Object>) itemMap;
                        if(!(Boolean) itemObj.get("InProcess") && !(Boolean) itemObj.get("Bought")) {
                            String name = (String) itemObj.get("Name");
                            Double price = ((Number) itemObj.get("Price")).doubleValue();
                            String url = (String) itemObj.get("ImageURL");

                            Listing item = new Listing(key, name, price, url);

                            listingsList.add(item);
                        }
                    }
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void setMessage(View view, int messageID) {
        fav_message_tv = (TextView) view.findViewById(R.id.fav_message);
        fav_message_tv.setText(messageID);
    }

    @Override
    public void onClick(View v) {
        String s = "hello";
        int i = 10;
        long l = SystemClock.currentThreadTimeMillis();
    }
}
