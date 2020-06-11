package com.example.balanceverattempt.LoggedInNav;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.balanceverattempt.LoggedInNav.activities.StayFitFragment;
import com.example.balanceverattempt.LoggedInNav.activities.TrainYourBrainFragment;
import com.example.balanceverattempt.R;

public class ExploreActivitiesFragment extends Fragment implements View.OnClickListener{

    CardView stayFitCardView, trainBrainCardView, cultureCardView, foodLoveCardView, socializeCardView;
    CardView gamesCardView, stayTunedCardView, addNewCatCardView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore_activities, container, false);

        stayFitCardView = view.findViewById(R.id.stayFitCardView);
        trainBrainCardView = view.findViewById(R.id.trainYourBrainCardView);
        cultureCardView = view.findViewById(R.id.cultureCardView);
        foodLoveCardView = view.findViewById(R.id.foodLoveCardView);
        socializeCardView = view.findViewById(R.id.socializeCardView);
        gamesCardView = view.findViewById(R.id.gamesCardView);
        stayTunedCardView = view.findViewById(R.id.stayTunedCardView);
        addNewCatCardView = view.findViewById(R.id.addNewCategoryCardView);

        stayFitCardView.setOnClickListener(this);
        trainBrainCardView.setOnClickListener(this);
        cultureCardView.setOnClickListener(this);
        foodLoveCardView.setOnClickListener(this);
        socializeCardView.setOnClickListener(this);
        gamesCardView.setOnClickListener(this);
        stayTunedCardView.setOnClickListener(this);
        addNewCatCardView.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.stayFitCardView:
                openStayFitFragment();
                break;
            case R.id.trainYourBrainCardView:
                openTrainYourBrainFragment();
                break;
            default:
                Log.d("EXPLORE_ACTIVITIES", "On Click not configured.");
                break;
        }
    }

    private void openTrainYourBrainFragment() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.add(R.id.logged_fragment_container, new TrainYourBrainFragment(), "TRAIN_YOUR_BRAIN_FRAGMENT").commit();
    }

    private void openStayFitFragment() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.add(R.id.logged_fragment_container, new StayFitFragment(), "STAY_FIT_FRAGMENT").commit();
    }
}
