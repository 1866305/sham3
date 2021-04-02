package com.example.myapplicationsowh6a;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    ImageView Imagepro, CoverPhoto;
    TextView Namepro, EmailPro, Phonepro;
    FloatingActionButton floatingActionButton;

    ProgressDialog progressDialog;

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int PICK_IMAGE_GALLERY_REQUEST = 300;
    private static final int PICK_IMAGE_CAMERA_REQUEST = 400;

    String cameraRequesrPermissions[];
    String storageRequesrPermissions[];

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        cameraRequesrPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storageRequesrPermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        Imagepro = view.findViewById(R.id.ImageID);
        CoverPhoto = view.findViewById(R.id.CoverPhoto);
        Namepro = view.findViewById(R.id.NameProfilr);
        EmailPro = view.findViewById(R.id.EmailProfile);
        Phonepro = view.findViewById(R.id.PhoneProfile);
        floatingActionButton = view.findViewById(R.id.FloatinngActionkey);
        progressDialog = new ProgressDialog(getActivity());

        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String name = "" + ds.child("Name").getValue();
                    String email = "" + ds.child("Email").getValue();
                    String phone = "" + ds.child("Phone").getValue();
                    String image = "" + ds.child("Image").getValue();
                    String CoverPhoto2 = "" + ds.child("Cover").getValue();

                    Namepro.setText(name);
                    EmailPro.setText(email);
                    Phonepro.setText(phone);
                    try {
                        Picasso.get().load(image).into(Imagepro);
                    } catch (Exception e) {
                        Picasso.get().load(R.drawable.ic_edit_default_face_white).into(Imagepro);
                    }
                    try {
                        Picasso.get().load(CoverPhoto2).into(CoverPhoto);
                    } catch (Exception e) {

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowEditProfileDialog();
            }
        });

        return view;
    }


    private boolean CheckStoragePermissions() {
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void RequestStoragepermissions() {
        ActivityCompat.requestPermissions(getActivity(), storageRequesrPermissions, STORAGE_REQUEST_CODE);


    }

    private boolean CheckCameraPermissions() {
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE == (PackageManager.PERMISSION_GRANTED;
        return result && result1;
    }

    //   private  void RequestCamerapermissions()
    // {
    // ActivityCompat.requestPermissions(getActivity(), cameraRequesrPermissions, CAMERA_REQUEST_CODE);


    //}


    private void ShowEditProfileDialog() {
        String options[] = {"Edit Profile Picture", "Edit Cover", "Edit Name", "Edit Phone"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Action");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (which == 0) {
                    progressDialog.setMessage("Updating Profile Picture");
                    ShowImageDialog();

                } else if (which == 1) {
                    progressDialog.setMessage("Updating Cover");

                } else if (which == 2) {
                    progressDialog.setMessage("Updating Name");
                } else if (which == 3) {
                    progressDialog.setMessage("Updating Phone");
                }

            }
        });

        builder.create().show();
        ;

    }

    private void ShowImageDialog() {
        String options[] = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Pick Image from ");
        builder.setItems(options, (dialog, which) -> {

            if (which == 0) {

            } else if (which == 1) {

            }


        });

        builder.create().show();
        ;

    }


}