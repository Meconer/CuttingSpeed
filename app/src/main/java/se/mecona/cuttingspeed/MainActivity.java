package se.mecona.cuttingspeed;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {
    private double diameter = 20;
    private double cutSpeed = 100;
    private double rpm;

    private EditText diameterEditText;
    private EditText rpmEditText;
    private EditText cutSpeedEditText;

    private SeekBar rpmSeekBar;
    private SeekBar cutSpeedSeekBar;

    private boolean updating = false;
    private enum SourceEnum { RPM, DIAMETER, CUTSPEED, NONE };
    private SourceEnum source = SourceEnum.NONE;
    private SourceEnum previousSource = SourceEnum.CUTSPEED;

    private final String LOG_TAG = "CuttingSpeed: ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupViews();

        setEventHandlers();
    }

    private void setEventHandlers() {
        diameterEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if ( !updating) {
                    updateView(SourceEnum.DIAMETER);
                }
            }
        });

        rpmEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if ( !updating) {
                    updateView(SourceEnum.RPM);
                }
            }
        });

        cutSpeedEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if ( !updating) {
                    updateView(SourceEnum.CUTSPEED);
                }
            }
        });
    }

    private void updateView(SourceEnum newSource) {
        updating = true;
        if ( source != newSource ) {
            previousSource = source;
            source = newSource;
        }
        if ( previousSource == SourceEnum.NONE ) {
            if ( newSource == SourceEnum.DIAMETER ) previousSource = SourceEnum.CUTSPEED;
            if ( newSource == SourceEnum.CUTSPEED ) previousSource = SourceEnum.DIAMETER;
            if ( newSource == SourceEnum.RPM ) previousSource = SourceEnum.DIAMETER;
        }

        getValuesFromTextFields(source);

        updating = false;
    }

    private void getValuesFromTextFields( SourceEnum source ) {
        switch (source) {
            case DIAMETER:
                diameter = getValueFromTextField( diameterEditText );
                Log.d(LOG_TAG, "Source diameter, value = " + diameter);
                if ( previousSource == SourceEnum.RPM ) {
                    cutSpeed = getCutSpeedFromRpmAndDia( rpm, diameter );
                    cutSpeedEditText.setText( cutSpeedToString( cutSpeed ));
                } else if ( previousSource == SourceEnum.CUTSPEED ) {
                    rpm = getRpmFromCutSpeedAndDia( cutSpeed, diameter );
                    rpmEditText.setText( rpmToString( rpm ));
                }
                break;
            case RPM:
                rpm = getValueFromTextField( rpmEditText );
                Log.d(LOG_TAG, "Source rpm, value = " + rpm);
                if ( previousSource == SourceEnum.DIAMETER ) {
                    cutSpeed = getCutSpeedFromRpmAndDia( rpm, diameter );
                    cutSpeedEditText.setText( cutSpeedToString( cutSpeed ));
                } else if ( previousSource == SourceEnum.CUTSPEED ) {
                    diameter = getDiameterFromCutSpeedAndRPM( cutSpeed, rpm );
                    diameterEditText.setText( diameterToString( diameter ));
                }
                break;
            case CUTSPEED:
                cutSpeed = getValueFromTextField( cutSpeedEditText );
                Log.d(LOG_TAG, "Source cutSpeed, value = " + cutSpeed);
                if ( previousSource == SourceEnum.DIAMETER ) {
                    rpm = getRpmFromCutSpeedAndDia( cutSpeed, diameter );
                    rpmEditText.setText( rpmToString( rpm ));
                } else if ( previousSource == SourceEnum.RPM ) {
                    diameter = getDiameterFromCutSpeedAndRPM( cutSpeed, rpm );
                    diameterEditText.setText( diameterToString( diameter ));
                }
                break;
            default:
                break;
        }
        Log.d(LOG_TAG, "previousSource " + previousSource);

    }

    private double getValueFromTextField(EditText editText) {
        String text = editText.getText().toString();
        if (text.isEmpty()) text = "0";
        return Double.parseDouble( text );
    }

    private void setupViews() {
        diameterEditText = (EditText) findViewById(R.id.diameterEditText);
        rpmEditText = (EditText) findViewById(R.id.rpmEditText);
        cutSpeedEditText = (EditText) findViewById( R.id.cutSpeedEditText);

        rpmSeekBar = (SeekBar) findViewById(R.id.rpmSeekBar);
        cutSpeedSeekBar = (SeekBar) findViewById( R.id.cutSpeedSeekBar);

        String diameterText = diameterToString(diameter);
        diameterEditText.setText( diameterText);

        String cutSpeedText = cutSpeedToString(cutSpeed);
        cutSpeedEditText.setText( cutSpeedText );

        String rpmText = rpmToString( getRpmFromCutSpeedAndDia( cutSpeed, diameter));
        rpmEditText.setText(rpmText);
    }

    private String diameterToString(double diameter) {
        return String.format("%.1f", diameter);
    }

    private String rpmToString(double rpm) {
        return String.format("%.0f", rpm);
    }

    private String cutSpeedToString(double cutSpeed) {
        return String.format("%.0f", cutSpeed);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public double getCutSpeedFromRpmAndDia( double rpm, double diameter ) {
        return Math.PI * diameter * rpm / 1000.0;
    }

    public double getRpmFromCutSpeedAndDia( double cutSpeed, double diameter ) {
        return 1000.0 * cutSpeed / ( Math.PI * diameter );
    }

    private double getDiameterFromCutSpeedAndRPM(double cutSpeed, double rpm) {
        return 1000.0 * cutSpeed / ( rpm * Math.PI );
    }

}