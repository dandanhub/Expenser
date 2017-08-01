package cmu.edu.expenser;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ItemActivity extends AppCompatActivity {

    private static ImageView mMainImage;
    private static EditText totalEditText;
    private static EditText dateEditText;
    private static Spinner categorySpinner;
    private static EditText peopleEditText;
    private static Button saveItemButton;
    private static Button deleteItemButton;
    private static String partFilename = "";
    private static  Button splitButton;

    private String itemId;
    private String userId;
    private String total;
    private String date;
    private String category;
    private String people;
    private String photoUri;

    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        itemId = getIntent().getStringExtra("itemId");
        userId = getIntent().getStringExtra("userId");

        totalEditText = (EditText) findViewById(R.id.totalEditText);
        total = getIntent().getStringExtra("total");
        totalEditText.setText(total);

        dateEditText = (EditText) findViewById(R.id.dateEditText);
        date = getIntent().getStringExtra("date");
        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new ItemActivity.DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
        dateEditText.setText(date);

        categorySpinner = (Spinner) findViewById(R.id.categorySpinner);
        String[] items = new String[]{"Meal", "Shopping", "House", "Transportation", "Others"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, items);
        categorySpinner.setAdapter(adapter);

        category = getIntent().getStringExtra("category");
        switch (category) {
            case "Meal":
                categorySpinner.setSelection(0);
                break;
            case "Shopping":
                categorySpinner.setSelection(1);
                break;
            case "House":
                categorySpinner.setSelection(2);
                break;
            case "Transportation":
                categorySpinner.setSelection(3);
                break;
            case "Others":
                categorySpinner.setSelection(4);
                break;
            default:
                break;
        }

        peopleEditText = (EditText) findViewById(R.id.peopleEditText);
        people = getIntent().getStringExtra("people");
        peopleEditText.setText(people);

        photoUri = getIntent().getStringExtra("photoUri");
        mMainImage = (ImageView) findViewById(R.id.main_image);
        if (photoUri != null && photoUri.length() != 0) {
            String storeFilename = "/photo_" + photoUri + ".jpg";
            bitmap = getImageFileFromSDCard(storeFilename);
            mMainImage.setImageBitmap(bitmap);
        }
        else {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable=true;
            bitmap = BitmapFactory.decodeResource(
                    getApplicationContext().getResources(),
                    R.drawable.placeholder,
                    options);
            mMainImage.setImageBitmap(bitmap);
        }

        saveItemButton = (Button) findViewById(R.id.saveItemButton);
        saveItemButton.setOnClickListener(saveItemButtonClicked);

        deleteItemButton = (Button) findViewById(R.id.deleteItemButton);
        deleteItemButton.setOnClickListener(deleteItemButtonClicked);

        // splitButton
        splitButton = (Button) findViewById(R.id.splitButton);
        splitButton.setOnClickListener(splitButtonClicked);
    }

    private Bitmap getImageFileFromSDCard(String filename) {
        Bitmap bitmap = null;
        File imageFile = new File(Environment.getExternalStorageDirectory() + filename);
        try {
            FileInputStream fis = new FileInputStream(imageFile);
            bitmap = BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    View.OnClickListener saveItemButtonClicked = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Context context = getApplicationContext();
            updateItem();
            finish();
        }
    };

    View.OnClickListener deleteItemButtonClicked = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Context context = getApplicationContext();
            deleteItem();
            finish();
        }
    };

    View.OnClickListener splitButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            splitExpense();
        }
    };

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
            dialog.getDatePicker().setMaxDate(c.getTimeInMillis());
            return dialog;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateString = sdf.format(calendar.getTime());
            dateEditText.setText(dateString);
        }
    }

    private void saveItem() {
        ItemDAO dbhelper = new ItemDAO(this);
        String userId = "test";
        double total = Double.valueOf(totalEditText.getText().toString());
        String dateString = dateEditText.getText().toString();
        String category = categorySpinner.getSelectedItem().toString();
        int people = Integer.valueOf(peopleEditText.getText().toString());
        dbhelper.insertItem(userId, total, dateString, category, people, partFilename);
    }

    private void updateItem() {
        ItemDAO dbhelper = new ItemDAO(this);
        String userId = "test";
        double total = Double.valueOf(totalEditText.getText().toString());
        String dateString = dateEditText.getText().toString();
        String category = categorySpinner.getSelectedItem().toString();
        int people = Integer.valueOf(peopleEditText.getText().toString());
        dbhelper.updateItem(Integer.valueOf(itemId), total, dateString, category, people, partFilename);
    }

    private void deleteItem() {
        ItemDAO dbhelper = new ItemDAO(this);
        dbhelper.deleteItem(userId, total, date, category, people, photoUri);
    }

    private void splitExpense() {
        // amount
        String amount = totalEditText.getText().toString();
        if (amount.trim().toString().equals("")) {
            // TODO: alert amount is empty
            Toast.makeText(getApplicationContext(), "Amount is empty. Please input amount",
                    Toast.LENGTH_LONG).show();
            return;
        }
        double amountNum = Double.parseDouble(amount);
        if (Double.compare(amountNum, 0) == 0) {
            // TODO: alert amount is empty
            Toast.makeText(getApplicationContext(), "Amount is empty. Please input amount",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // number of people
        String people = peopleEditText.getText().toString();
        if (people.trim().toString().equals("")) {
            // TODO: alert people is empty
            Toast.makeText(getApplicationContext(), "Number of people is empty. Please input the number of people to share the bill.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        int peopleNum = Integer.parseInt(people);
        if (peopleNum <= 1) {
            // TODO: alert people is empty
            Toast.makeText(getApplicationContext(), "Number of people should be greater than 1. Please the input number of people to share the bill.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        double dividedNum = amountNum / peopleNum;
        Log.d("ItemActivity", "DividedNum: " + dividedNum);
        Toast.makeText(getApplicationContext(), "Everyone should pay you $" + dividedNum + ".",
                Toast.LENGTH_LONG).show();

        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(bitmap)
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();

        ShareDialog.show(this, content);
    }
}