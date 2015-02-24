package edu.washington.norimori.awty;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class MainActivity extends ActionBarActivity {

    private EditText message;
    private EditText phnNum;
    private EditText freq;
    private Button btnAction;

    private Intent alarmIntent;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Message Text
        message = (EditText) findViewById(R.id.editMessage);

        //Restrict phone number formatting to (###) ###-#### or # (###) ###-####.
        phnNum = (EditText) findViewById(R.id.editPhone);
        phnNum.setInputType(InputType.TYPE_CLASS_PHONE);
        phnNum.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        //Restrict frequency to positive ints
        freq = (EditText) findViewById(R.id.editFrequency);
        freq.setInputType(InputType.TYPE_CLASS_NUMBER);

        //Start or Stop button of alarm
        btnAction = (Button) findViewById(R.id.btnAction);

        //Listen for complete and valid inputs
        message.addTextChangedListener(mTextWatcher);
        phnNum.addTextChangedListener(mTextWatcher);
        freq.addTextChangedListener(mTextWatcher);

        //Check once for empty values upon initial app load
        checkValidation();

        //Intent to call alarm toast.
        alarmIntent = new Intent(MainActivity.this, AlarmReceiver.class);

        //Check if alarm already exists, and allow option to terminate that alarm with "Stop" button.
        boolean alarmUp = (PendingIntent.getBroadcast(MainActivity.this, 0, alarmIntent, PendingIntent.FLAG_NO_CREATE) != null);
        if(alarmUp) {
            btnAction.setEnabled(true);
            btnAction.setText("Stop");
        }

        //Start or Stop alarm
        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnAction.getText().toString().equals("Start")) {
                    btnAction.setText("Stop");
                    alarmIntent.putExtra("message", message.getText().toString());
                    alarmIntent.putExtra("phnNum", phnNum.getText().toString());
                    pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                    alarmStart();
                } else { //Button Text is "Stop"
                    btnAction.setText("Start");
                    alarmStop();
                }
            }
        });
    }

    //Start alarm. Display message every given interval.
    public void alarmStart() {
        long interval = TimeUnit.MINUTES.toMillis(Long.valueOf(freq.getText().toString()));
        AlarmManager manager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        Log.d("yay", "Alarm Started!");
    }

    //Stop alarm
    public void alarmStop() {
        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class); //Recreate Intent
        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        Log.d("yay", "Alarm Cancelled...");
    }

    //Checks if any inputs are empty and if Phone Number is incomplete.
    //Enable button if all inputs are complete and valid.
    private void checkValidation() {
        if((message.getText().toString().length() == 0) || (phnNum.getText().toString().length() < 14) || (freq.getText().toString().length() == 0)) {
            btnAction.setEnabled(false);
        } else {
            btnAction.setEnabled(true);
        }
    }

    //Watch for text change and validate each time.
    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            checkValidation();
        }
    };

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
}
