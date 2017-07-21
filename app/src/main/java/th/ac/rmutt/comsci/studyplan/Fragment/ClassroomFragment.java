package th.ac.rmutt.comsci.studyplan.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import th.ac.rmutt.comsci.studyplan.Activity.ClassStatusActivity;
import th.ac.rmutt.comsci.studyplan.Activity.ClassroomActivity;
import th.ac.rmutt.comsci.studyplan.Adapter.Classroom;
import th.ac.rmutt.comsci.studyplan.Adapter.ClassroomViewHolder;
import th.ac.rmutt.comsci.studyplan.R;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class ClassroomFragment extends Fragment {

    public ClassroomFragment() {
        // Required empty public constructor
    }

    private RecyclerView recyclerViewClassReg;
    private TextView key;

    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseReg;
    private DatabaseReference mDatabaseGetReg;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    private View mView;
    private FirebaseRecyclerAdapter<Classroom, ClassroomViewHolder> firebaseRecyclerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_classroom, container,  false);

        TextView textViewAddClassRoom = (TextView) mView.findViewById(R.id.textViewAddClassRoom);
        textViewAddClassRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplication(), ClassroomActivity.class);
                startActivity(intent);
            }
        });

        startFirebase();

        recyclerViewClassReg = (RecyclerView) mView.findViewById(R.id.recyclerViewClassReg);
        key = (TextView) mView.findViewById(R.id.key);

        recyclerViewClassReg.setHasFixedSize(true);
        recyclerViewClassReg.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewClassReg.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Classroom, ClassroomViewHolder>(
                Classroom.class,
                R.layout.view_classroom_reg,
                ClassroomViewHolder.class,
                mDatabaseGetReg
        ) {
            @Override
            protected void populateViewHolder(final ClassroomViewHolder viewHolder,final Classroom model, int position) {

                final String class_key = getRef(position).getKey();

                mDatabase.child(class_key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String id = dataSnapshot.child("subject_id").getValue().toString();
                        String name = dataSnapshot.child("subject_name").getValue().toString();
                        String sec = dataSnapshot.child("sec").getValue().toString();
                        String username = dataSnapshot.child("username").getValue().toString();
                        String image = dataSnapshot.child("image").getValue().toString();

                        viewHolder.setSubject_id(id);
                        viewHolder.setSubject_name(name);
                        viewHolder.setSec(sec);
                        viewHolder.setUsername(username);
                        viewHolder.setImage(getApplicationContext(), image);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent singleClassIntent = new Intent(getActivity().getApplication(), ClassStatusActivity.class);
                        singleClassIntent.putExtra("class_id", class_key);
                        startActivity(singleClassIntent);
                    }
                });
            }
        };

        firebaseRecyclerAdapter.notifyDataSetChanged();
        recyclerViewClassReg.setAdapter(firebaseRecyclerAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerViewClassReg.setLayoutManager(layoutManager);

        return mView;
    }

    private void startFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        String currentUserId = mCurrentUser.getUid();

        DatabaseReference s = FirebaseDatabase.getInstance().getReference();

        mDatabaseReg = s.child("RegClass");
        mDatabaseGetReg = s.child("UserGetClass").child(currentUserId);
        mDatabase = s.child("ClassRoom");

    }
}


