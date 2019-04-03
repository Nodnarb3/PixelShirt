package com.adafruit.bluefruit.le.connect.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.os.SystemClock;

import com.adafruit.bluefruit.le.connect.BluefruitApplication;
import com.adafruit.bluefruit.le.connect.BuildConfig;
import com.adafruit.bluefruit.le.connect.R;
import com.adafruit.bluefruit.le.connect.app.neopixel.NeopixelColorPickerFragment;
import com.devs.vectorchildfinder.VectorChildFinder;
import com.devs.vectorchildfinder.VectorDrawableCompat;
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


    // Data
    private ControllerColorPickerFragmentListener mListener;

    ImageView drawGrid;
    ImageView mask;
    ImageButton clearAll;
    View curColor;
    ImageView pointerImage;
    ImageView colorPicker;

    View palette;

    boolean pickingColor = false;

    int[] ledMap = new int[]
            {
                    20, 19, 10, 9,  0,
                    29, 31, 39, 41, 49,
                    21, 18, 11, 8,  1,
                    28, 32, 38, 42, 48,
                    22, 17, 12, 7,  2,
                    27, 61, 37, 43, 47, //special case for shirt pixel 33 since ! = 33 = 0x21
                    23, 16, 13, 6,  3,
                    26, 34, 36, 44, 46,
                    24, 15, 14, 5,  4
            };


    VectorDrawableCompat.VFullPath[] pixels = new VectorDrawableCompat.VFullPath[45];

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
        //activity.requestWindowFeature(activity.getWindow().FEATURE_NO_TITLE);
        if (activity != null) {
            ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar != null) {
//                actionBar.setTitle(R.string.colorpicker_title);
                actionBar.hide();
//                actionBar.setDisplayHomeAsUpEnabled(true);

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
        clearAll = view.findViewById(R.id.clear);
        clearAll.setOnClickListener(v -> {
            sendColor(60, Color.BLACK);
            for (VectorDrawableCompat.VFullPath pixel:pixels) {
                pixel.setFillColor(Color.BLACK);
                drawGrid.invalidate();
            }
        });
        palette = view.findViewById(R.id.palette);
        curColor = view.findViewById(R.id.curColor);
        drawGrid = view.findViewById(R.id.drawGrid);
        mask = view.findViewById(R.id.mask);
        mask.setImageResource(R.drawable.ic_5x9mask);
        colorPicker = view.findViewById(R.id.colorPicker);
        colorPicker.setImageResource(R.drawable.gradient);

        colorPicker.setOnTouchListener((v, event) -> {
            int action = event.getActionMasked();
            int evX = (int) event.getX();
            int evY = (int) event.getY();

            switch (action) {
                case MotionEvent.ACTION_UP:
                    if(pickingColor)
                    {
                        selectedColor = getColorPicker(evX, evY);
                        curColor.setBackgroundColor(selectedColor);
                        removePointer();
                        pickingColor = false;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if(evY < 0 || evX < 0){
                        removePointer();
                        pickingColor = false;
                        break;
                    }
                    if(pickingColor)
                    {
                        selectedColor = getColorPicker(evX, evY);
                        curColor.setBackgroundColor(selectedColor);
                        moveColorIcon(evX + ((View)v.getParent()).getLeft(), evY + palette.getTop());
                    }
                    break;
                case MotionEvent.ACTION_DOWN:
                    selectedColor = getColorPicker(evX, evY);
                    curColor.setBackgroundColor(selectedColor);
                    createPointer(evX + ((View)v.getParent()).getLeft(), evY + palette.getTop());
                    pickingColor = true;
                    break;
            }
            return true;
        });
        VectorChildFinder vectorFinder = new VectorChildFinder(this.getContext(), R.drawable.ic_5x9mask, drawGrid);
        for(int i = 1; i <= 45; i++)
        {
            VectorDrawableCompat.VFullPath path = vectorFinder.findPathByName(String.valueOf(i));
            pixels[i-1] = path;
            pixels[i-1].setFillColor(Color.BLACK);
            pixels[i-1].setStrokeWidth(1);
            pixels[i-1].setStrokeAlpha(1f);
            pixels[i-1].setStrokeColor(Color.WHITE);
            drawGrid.invalidate();
        }
    }

    private void removePointer() {
        ((RelativeLayout)this.getView().findViewById(R.id.mainView)).removeView(pointerImage);
    }

    private void createPointer(int x, int y)
    {
        pointerImage = new ImageView(this.getContext());
        pointerImage.setImageResource(R.drawable.ic_dragblob);
        pointerImage.setColorFilter(selectedColor);
        ((RelativeLayout)this.getView().findViewById(R.id.mainView)).addView(pointerImage);
        pointerImage.setScaleType(ImageView.ScaleType.CENTER);
        pointerImage.setScaleX(1f);
        pointerImage.setScaleY(1f);
        pointerImage.setX(x - (pointerImage.getWidth()/2f));
        pointerImage.setY(y - (pointerImage.getHeight()));

    }

    private void moveColorIcon(int x, int y)
    {
        if(pointerImage != null) {
            pointerImage.setX(x - (pointerImage.getWidth() / 2f));
            pointerImage.setY(y - (pointerImage.getHeight()));
            pointerImage.setColorFilter(selectedColor);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getActionMasked();
        int evX = (int) event.getX();
        int evY = (int) event.getY();

        switch (action) {
            case MotionEvent.ACTION_UP:
                colorGrid(evX, evY);
                break;
            case MotionEvent.ACTION_MOVE:
                colorGrid(evX, evY);
                break;
            case MotionEvent.ACTION_DOWN:
                colorGrid(evX, evY);
                break;
        }
        return true;
    }

    public void sendColor(int pixel, int color){
        Log.v(TAG, "Shirt Pixel: " + pixel);
        mListener.onSendColorComponents(pixel, color);
    }

    private int lastPixel = -1;
    private int lastColor = Color.TRANSPARENT;
    private int selectedColor = Color.GREEN;

    void colorGrid(int x, int y)
    {
        int mask = getMaskColor(R.id.mask, x, y);
        int pixel = Math.round(Color.valueOf(mask).red() * 255);

        if(pixel < 0 || pixel > 45) return;
        pixels[pixel-1].setFillColor(selectedColor);
        drawGrid.invalidate();

        if(lastPixel != pixel || pixels[pixel-1].getFillColor() != lastColor)
        {
            Log.v(TAG, "Logical Pixel: " + (pixel-1));
            sendColor(ledMap[pixel-1], selectedColor);
            lastPixel = pixel;
            lastColor = selectedColor;
        }
    }

    public int getColorPicker(int x, int y)
    {
        colorPicker.setDrawingCacheEnabled(true);
        Bitmap hotspots = Bitmap.createBitmap(colorPicker.getDrawingCache());
        colorPicker.setDrawingCacheEnabled(false);

        if (y > hotspots.getHeight() || x > hotspots.getWidth() || y < 0 || x < 0) {
            return -1;
        }

        return hotspots.getPixel(x, y);
    }

    public int getMaskColor(int hotspot, int x, int y) {
        mask.setDrawingCacheEnabled(true);
        Bitmap hotspots = Bitmap.createBitmap(mask.getDrawingCache());
        mask.setDrawingCacheEnabled(false);

        if (y > hotspots.getHeight() || x > hotspots.getWidth() || y < 0 || x < 0) {
            return -1;
        }

        return hotspots.getPixel(x, y);
    }

    @Override
    public void onStop() {

        final Context context = getContext();
        // Preserve values
        if (context != null && kPersistValues) {
            SharedPreferences settings = context.getSharedPreferences(kPreferences, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            //editor.putInt(kPreferences_color, mSelectedColor);
            //editor.apply();
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
        //mSelectedColor = color;

    }


    // endregion

    // region
    interface ControllerColorPickerFragmentListener {
        void onSendColorComponents(int pixel, int color);
    }

    // endregion
}