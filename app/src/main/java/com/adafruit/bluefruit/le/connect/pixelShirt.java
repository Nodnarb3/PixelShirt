package com.adafruit.bluefruit.le.connect;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.adafruit.bluefruit.le.connect.app.CommonHelpFragment;
import com.adafruit.bluefruit.le.connect.app.MqttSettingsCodeReaderFragment;
import com.devs.vectorchildfinder.VectorChildFinder;
import com.larswerkman.holocolorpicker.ColorPicker;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link pixelShirt.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link pixelShirt#newInstance} factory method to
 * create an instance of this fragment.
 */
public class pixelShirt extends android.support.v4.app.Fragment implements ColorPicker.OnColorChangedListener, View.OnTouchListener{
    private MqttSettingsCodeReaderFragment.OnFragmentInteractionListener mListener;
    ImageView drawGrid;
    ImageView mask;


    public pixelShirt() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment pixelShirt.
     */
    // TODO: Rename and change types and number of parameters
    public static pixelShirt newInstance() {
        pixelShirt fragment = new pixelShirt();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pixel_shirt, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        drawGrid = view.findViewById(R.id.drawGrid);
        mask = view.findViewById(R.id.mask);
        VectorChildFinder vectorFinder = new VectorChildFinder(this.getContext(), R.drawable.ic_5x9mask, drawGrid);
        for(int i = 1; i <= 45; i++)
        {
            vectorFinder.findPathByName(String.valueOf(i)).setFillColor(Color.WHITE);
        }
        drawGrid.invalidate();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MqttSettingsCodeReaderFragment.OnFragmentInteractionListener) {
            mListener = (MqttSettingsCodeReaderFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_help, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentActivity activity = getActivity();

        switch (item.getItemId()) {
            case R.id.action_help:
                if (activity != null) {
                    FragmentManager fragmentManager = activity.getSupportFragmentManager();
                    if (fragmentManager != null) {
                        CommonHelpFragment helpFragment = CommonHelpFragment.newInstance(getString(R.string.colorpicker_help_title), getString(R.string.colorpicker_help_text));
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                                .replace(R.id.contentLayout, helpFragment, "Help");
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // endregion

    // region OnColorChangedListener

    @SuppressWarnings("PointlessBitwiseExpression")
    @Override
    public void onColorChanged(int color) {
        // Save selected color
        return;
    }


    // endregion

    // region
    interface ControllerColorPickerFragmentListener {
        void onSendColorComponents(int color);
    }

    // endregion
}
