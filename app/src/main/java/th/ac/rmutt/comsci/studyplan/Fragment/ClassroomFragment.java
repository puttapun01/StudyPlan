package th.ac.rmutt.comsci.studyplan.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import th.ac.rmutt.comsci.studyplan.Activity.ClassroomActivity;
import th.ac.rmutt.comsci.studyplan.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ClassroomFragment extends Fragment {

    public ClassroomFragment() {
        // Required empty public constructor
    }


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

        return v;
    }

}
