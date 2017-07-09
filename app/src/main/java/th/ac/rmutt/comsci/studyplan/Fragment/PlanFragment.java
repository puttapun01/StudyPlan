package th.ac.rmutt.comsci.studyplan.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import th.ac.rmutt.comsci.studyplan.Activity.PlanActivity;
import th.ac.rmutt.comsci.studyplan.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlanFragment extends AppCompatDialogFragment {

    public PlanFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_plan, container, false);
        TextView textViewAddPlan = (TextView) v.findViewById(R.id.textViewAddPlan);
        textViewAddPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplication(), PlanActivity.class);
                startActivity(intent);
            }
        });
        return v;

    }


}
