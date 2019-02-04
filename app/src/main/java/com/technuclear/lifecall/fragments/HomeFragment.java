package com.technuclear.lifecall.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.technuclear.lifecall.R;
import com.technuclear.lifecall.activities.MainActivity;
import com.technuclear.lifecall.activities.TrackPatientActivity;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    ImageView siren ;
    ImageView FriendInEmergency ;
    ImageView MyselfInEmergency;

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);

    }

    public void onStart() {
        super.onStart();


         siren = (ImageView) getView().findViewById(R.id.imageView);
         FriendInEmergency = (ImageView) getView().findViewById(R.id.fragment_home_trouble_yes);
         MyselfInEmergency =   (ImageView) getView().findViewById(R.id.fragment_home_emer_yes);

        ImageView emerYes = (ImageView) getView().findViewById(R.id.fragment_home_emer_yes);
        emerYes.setVisibility(View.INVISIBLE);
        emerYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Emergency Initiated. Help is on the way.", Toast.LENGTH_SHORT).show();
            }
        });

        ImageView emerNo = (ImageView) getView().findViewById(R.id.fragment_home_emer_no);
        emerNo.setVisibility(View.INVISIBLE);
        emerNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "No Emergency Requested", Toast.LENGTH_SHORT).show();
            }
        });

        if (MainActivity.emerPref.getBoolean(MainActivity.EMERGENCY_INITIATED_KEY, false))
            emerYes.setVisibility(View.VISIBLE);
        else
            emerNo.setVisibility(View.VISIBLE);

        ImageView troubleYes = (ImageView) getView().findViewById(R.id.fragment_home_trouble_yes);
        troubleYes.setVisibility(View.INVISIBLE);
        troubleYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), TrackPatientActivity.class));
            }
        });
        ImageView troubleNo = (ImageView) getView().findViewById(R.id.fragment_home_trouble_no);
        troubleNo.setVisibility(View.INVISIBLE);
        troubleNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "No Friend in Trouble", Toast.LENGTH_SHORT).show();
            }
        });

        if (MainActivity.friendPref.getBoolean(MainActivity.FRIEND_IN_TROUBLE_KEY, false))
            troubleYes.setVisibility(View.VISIBLE);
        else
            troubleNo.setVisibility(View.VISIBLE);

//        ShowCaseSequence();
    }

        // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

//    public void ShowCaseSequence()
////    {
////
////
////        ShowcaseConfig config = new ShowcaseConfig();
////        config.setDelay(500); // half second between each showcase view
////
////        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(getActivity(), "1");
////
////        sequence.setConfig(config);
////
////
////        sequence.addSequenceItem(siren,
////                "Press this button in case of emergency", "Okay");
////
////        sequence.addSequenceItem(MyselfInEmergency,
////                "If you are in emergency this icon will turn red", "Okay");
////
////        sequence.addSequenceItem(FriendInEmergency,
////                "If you are in emergency this icon will turn red", "Okay");
////
////        sequence.start();
////    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
