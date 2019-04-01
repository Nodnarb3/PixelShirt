package com.adafruit.bluefruit.le.connect.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import android.os.SystemClock;

import com.adafruit.bluefruit.le.connect.BluefruitApplication;
import com.adafruit.bluefruit.le.connect.BuildConfig;
import com.adafruit.bluefruit.le.connect.R;
import com.adafruit.bluefruit.le.connect.app.neopixel.NeopixelColorPickerFragment;
import com.larswerkman.holocolorpicker.ColorPicker;


import java.util.ArrayList;

public class ControllerColorPickerFragment extends Fragment implements ColorPicker.OnColorChangedListener, View.OnTouchListener {
    // Log
    @SuppressWarnings("unused")
    private final static String TAG = ControllerColorPickerFragment.class.getSimpleName();

    // Constants
    private final static boolean kPersistValues = true;
    private final static String kPreferences = "ColorPickerActivity_prefs";
    private final static String kPreferences_color = "color";

    public int height;
    public int width;
    public int third1;
    public int third2;
    public int third3;
    public int length1;
    public int length2;
    public int length3;
    public int length4;

    // Data
    private int mSelectedColor;
    private ControllerColorPickerFragmentListener mListener;

    Chronometer mChronometer;

    // region Lifecycle
    @SuppressWarnings("UnnecessaryLocalVariable")
    public static ControllerColorPickerFragment newInstance() {
        ControllerColorPickerFragment fragment = new ControllerColorPickerFragment();
        return fragment;
    }

    public ControllerColorPickerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(R.string.colorpicker_title);
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_controller_colorpicker, container, false);
        v.setOnTouchListener(this::onTouch);
        return v;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mChronometer = view.findViewById(R.id.simpleChronometer);

        height = getScreenHeight();
        width = getScreenWidth();

        third1 = width / 3;
        third2 = 2*(width / 3);
        third3 = width;

        length1 = height / 4;
        length2 = 2*(height / 4);
        length3 = 3*(height / 4);
        length4 = height;

        startTimer();
    }

    public void startTimer(){
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.start();
        long i = SystemClock.elapsedRealtime();
        long j = mChronometer.getBase();
        Log.d("time"," "+  (i - j));
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        int action = event.getActionMasked();

        int index = event.getActionIndex();

        int evX = (int) event.getX(index);
        int evY = (int) event.getY(index);

        switch (action) {
            case MotionEvent.ACTION_UP:
                //break;
            case MotionEvent.ACTION_POINTER_DOWN:
                sendColor(evX, evY);
                //break;
            case MotionEvent.ACTION_MOVE:
                sendColor(evX, evY);
                //break;
            case MotionEvent.ACTION_DOWN:
                sendColor(evX, evY);
                //break;
        }
        return true;
    }

    public void sendColor(int x, int y){

        //top left (red 255 0 0)
        if(x <= third1 && y <= length1){
            mSelectedColor = 0xFF0000;
            mListener.onSendColorComponents(mSelectedColor);
        }

        //top middle (orange 255 165 0)
        if(x <= third2 && x > third1 && y <= length1){
            mSelectedColor = 0xFFA500;
            mListener.onSendColorComponents(mSelectedColor);
        }

        //top right (yellow, 255 255 0)
        if(x <= third3 && x > third2 && y <=length1){
            mSelectedColor = 0xFFFF00;
            mListener.onSendColorComponents(mSelectedColor);

        }

        //row 2 left (green 0 128 0)
        if(x <= third1 && y > length1 && y <= length2){
            mSelectedColor = 0x008000;
            mListener.onSendColorComponents(mSelectedColor);
        }

        //row 2 middle (blue 0 0 255)
        if(x <= third2 && x > third1 && y > length1 && y <= length2){
            mSelectedColor = 0x0000FF;
            mListener.onSendColorComponents(mSelectedColor);
        }

        //row 2 right (purple 128 0 128)
        if(x <= third3 && x > third2 && y > length1 && y <= length2){
            mSelectedColor = 0x800080;
            mListener.onSendColorComponents(mSelectedColor);
        }

        //row 3 left (pink 255 192 203)
        if(x <= third1 && y > length2 && y <= length3){
            mSelectedColor = 0xFFC0CB;
            mListener.onSendColorComponents(mSelectedColor);
        }

        //row 3 middle (white 255 255 255)
        if(x <= third2 && x > third1 && y > length2 && y <= length3){
            mSelectedColor = 0xFFFFFF;
            mListener.onSendColorComponents(mSelectedColor);
        }

        //row 3 right (light purple 237 221 237 )
        if(x <= third3 && x > third2 && y > length2 && y <= length3){
            mSelectedColor = 0xEDDDED;
            mListener.onSendColorComponents(mSelectedColor);
        }

        //row 4 left (brown 165 42 42)
        if(x <= third1 && y > length3 && y <= length4){
            mSelectedColor = 0xA52A2A;
            mListener.onSendColorComponents(mSelectedColor);
        }

        //row 4 middle (magenta 255 0 255)
        if(x <= third2 && x > third1 && y > length3 && y <= length4){
            mSelectedColor = 0xFF00FF;
            mListener.onSendColorComponents(mSelectedColor);
        }

        //row 4 right (tea green 197 250 192)
        if(x <= third3 && x > third2 && y > length3 && y <= length4){
            mSelectedColor = 0xC5FAC0;
            mListener.onSendColorComponents(mSelectedColor);
        }
    }


    @Override
    public void onStop() {

        final Context context = getContext();
        // Preserve values
        if (context != null && kPersistValues) {
            SharedPreferences settings = context.getSharedPreferences(kPreferences, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(kPreferences_color, mSelectedColor);
            editor.apply();
        }

        super.onStop();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NeopixelColorPickerFragment.NeopixelColorPickerFragmentListener) {
            mListener = (ControllerColorPickerFragmentListener) context;
        } else if (getTargetFragment() instanceof ControllerColorPickerFragmentListener) {
            mListener = (ControllerColorPickerFragmentListener) getTargetFragment();
        }  else {
            throw new RuntimeException(context.toString() + " must implement NeopixelColorPickerFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
        mSelectedColor = color;

    }


    // endregion

    // region
    interface ControllerColorPickerFragmentListener {
        void onSendColorComponents(int color);
    }

    // endregion
}