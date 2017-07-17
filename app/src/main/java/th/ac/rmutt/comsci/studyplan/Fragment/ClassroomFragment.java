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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import th.ac.rmutt.comsci.studyplan.Activity.ClassroomActivity;
import th.ac.rmutt.comsci.studyplan.Classroom;
import th.ac.rmutt.comsci.studyplan.ClassroomViewHolder;
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

    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseReg;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    private FirebaseRecyclerAdapter<Classroom, ClassroomViewHolder> firebaseRecyclerAdapter;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_classroom, container, false);

        TextView textViewAddClassRoom = (TextView) v.findViewById(R.id.textViewAddClassRoom);
        textViewAddClassRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplication(), ClassroomActivity.class);
                startActivity(intent);
            }
        });

        startFirebase();

        recyclerViewClassReg = (RecyclerView) v.findViewById(R.id.recyclerViewClassReg);

        recyclerViewClassReg.setHasFixedSize(true);
        recyclerViewClassReg.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewClassReg.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Classroom, ClassroomViewHolder>(
                        Classroom.class,
                        R.layout.view_classroom_reg,
                        ClassroomViewHolder.class,
                        mDatabaseReg
                ) {

                @Override
                protected void populateViewHolder(ClassroomViewHolder viewHolder, Classroom model, int position) {
                    viewHolder.setSubject_id(model.getSubject_id());
                    viewHolder.setSubject_name(model.getSubject_name());
                    viewHolder.setSec(model.getSec());
                    viewHolder.setUsername(model.getUsername());
                    viewHolder.setImage(getApplicationContext(), model.getImage());

                }
        };

        firebaseRecyclerAdapter.notifyDataSetChanged();
        recyclerViewClassReg.setAdapter(firebaseRecyclerAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerViewClassReg.setLayoutManager(layoutManager);

        return v;
    }

    private void startFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("ClassRoom");
        mDatabaseReg = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid()).child("RegClass");
    }
}
