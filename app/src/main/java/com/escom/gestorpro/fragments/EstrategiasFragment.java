package com.escom.gestorpro.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.escom.gestorpro.BuzonActivity;
import com.escom.gestorpro.activities.ConceptosActivity;
import com.escom.gestorpro.activities.DatosAnalisisActivity;
import com.escom.gestorpro.R;
import com.escom.gestorpro.activities.BuenasPracticasActivity;
import com.escom.gestorpro.activities.EstrategiasActivity;
import com.escom.gestorpro.activities.MainActivity;
import com.escom.gestorpro.activities.PostDetailActivity;
import com.escom.gestorpro.models.Buzon;
import com.escom.gestorpro.providers.AuthProvider;
import com.escom.gestorpro.providers.BuzonProvider;
import com.escom.gestorpro.providers.UserProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EstrategiasFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EstrategiasFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private String url = "";
    Button mButtonBuenasPracticas;
    Button mButtonEstrategias;
    Button mButtonConceptos;
    Button mButtonAnalisisDatos;
    Button mButtonBuzon;
    Button mButtonMiEmpresa;

    AuthProvider mAuthProvider;

    public EstrategiasFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EstrategiasFragment.
     */
    public static EstrategiasFragment newInstance(String param1, String param2) {
        EstrategiasFragment fragment = new EstrategiasFragment();
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
        View view = inflater.inflate(R.layout.fragment_estrategias, container, false);

        mAuthProvider = new AuthProvider();

        mButtonBuenasPracticas = view.findViewById(R.id.btnBuenasPracticas);
        mButtonAnalisisDatos = view.findViewById(R.id.btnAnalisisDatos);
        mButtonEstrategias = view.findViewById(R.id.btnEstrategias);
        mButtonConceptos = view.findViewById(R.id.btnConceptosFund);
        mButtonBuzon = view.findViewById(R.id.btnBuzon);
        mButtonMiEmpresa = view.findViewById(R.id.btnMiEmpresa);

        mButtonBuenasPracticas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToBuenasPracticas();
            }
        });

        mButtonAnalisisDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAnalisisDatos();
            }
        });

        mButtonEstrategias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToEstrategias();
            }
        });

        mButtonConceptos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToConceptos();
            }
        });

        mButtonBuzon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BuzonActivity.class);
                startActivity(intent);
            }
        });
        setHasOptionsMenu(true);

        return view;
    }

    private void goToConceptos() {
        Intent intent = new Intent(getActivity(), ConceptosActivity.class);
        startActivity(intent);
    }

    private void goToEstrategias() {
        Intent intent = new Intent(getActivity(), EstrategiasActivity.class);
        startActivity(intent);
    }

    private void goToAnalisisDatos() {
        Intent intent = new Intent(getActivity(), DatosAnalisisActivity.class);
        startActivity(intent);
    }

    private void goToBuenasPracticas() {
        Intent intent = new Intent(getActivity(), BuenasPracticasActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_logout) {
            logout();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        mAuthProvider.logout();
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}