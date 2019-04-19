package me.aidengaripoli.dynamicdevicedisplay.elements;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

import me.aidengaripoli.dynamicdevicedisplay.OnFragmentInteractionListener;
import me.aidengaripoli.dynamicdevicedisplay.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RangeInputFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RangeInputFragment extends DynamicFragment {
    public static final String RANGE_INPUT = "rangeinput";

    private static final String ARG_BUTTON_LABEL = "buttonLabel";
    private static final String ARG_MIN = "min";
    private static final String ARG_MAX = "max";

    private static final int ARG_BUTTON_LABEL_INDEX = 0;
    private static final int ARG_MIN_INDEX = 1;
    private static final int ARG_MAX_INDEX = 2;

    private String buttonLabel;
    private int value;
    private int max;
    private int min;
    private int range;

    private EditText valueView;
    private SeekBar seekBarView;

    public RangeInputFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param label           Parameter 1.
     * @param displaySettings Parameter 2.
     * @return A new instance of fragment RangeInputFragment.
     */
    public static RangeInputFragment newInstance(String label, ArrayList<String> displaySettings) {
        RangeInputFragment fragment = new RangeInputFragment();

        Bundle args = new Bundle();
        args.putString(ARG_LABEL, label);
        args.putString(ARG_BUTTON_LABEL, displaySettings.get(ARG_BUTTON_LABEL_INDEX));
        args.putInt(ARG_MIN, Integer.parseInt(displaySettings.get(ARG_MIN_INDEX)));
        args.putInt(ARG_MAX, Integer.parseInt(displaySettings.get(ARG_MAX_INDEX)));
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            label = getArguments().getString(ARG_LABEL);
            buttonLabel = getArguments().getString(ARG_BUTTON_LABEL);
            min = getArguments().getInt(ARG_MIN);
            max = getArguments().getInt(ARG_MAX);
            value = 0;

            range = max - min;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_range_input, container, false);

        TextView labelView = view.findViewById(R.id.slider_label);
        labelView.setText(label);

        Button buttonView = view.findViewById(R.id.slider_button);
        buttonView.setText(buttonLabel);
        buttonView.setOnClickListener(v -> sendMessage(String.valueOf(valueView.getText())));

        valueView = view.findViewById(R.id.slider_value);
        seekBarView = view.findViewById(R.id.slider);

        valueView.setText(String.valueOf(value));

        float progress = (float) (value - min) / range * 100;
        seekBarView.setProgress((int) progress);

        valueView.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                String value = valueView.getText().toString();
                int numValue;

                if (value.isEmpty()) {
                    numValue = min;
                } else {
                    numValue = Integer.parseInt(value);

                    if (numValue < min) {
                        numValue = min;
                    } else if (numValue > max) {
                        numValue = max;
                    }
                }
                valueView.setText(String.valueOf(numValue));

                final float sliderProgress = (float) (numValue - min) / range * 100;
                seekBarView.setProgress((int) sliderProgress);
            }
            return true;
        });

        seekBarView.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int value = (int) (range * ((float) progress / 100) + min);
                valueView.setText(String.valueOf(value));
            }
        });

        return view;
    }

    @Override
    public void updateFragmentData(String data) {

    }
}
