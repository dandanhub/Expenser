package cmu.edu.expenser;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.support.annotation.NonNull;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.R.attr.priority;
import static cmu.edu.expenser.R.string.date;
import static cmu.edu.expenser.R.string.total;
import static com.facebook.FacebookSdk.getApplicationContext;

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

    private ListView mListView;
    private Cursor result;
    private ItemCursorAdapter itemAdapter;

    private OnFragmentInteractionListener mListener;

    public class ItemCursorAdapter extends CursorAdapter {
        public ItemCursorAdapter(Context context, Cursor cursor) {
            super(context, cursor, 0);
        }

        // The newView method is used to inflate a new view and return it,
        // you don't bind any data to the view at this point.
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.item_list, parent, false);
        }

        // The bindView method is used to bind all data to a given view
        // such as setting the text on a TextView.
        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            // Find fields to populate in inflated template
            TextView itemTotal = (TextView) view.findViewById(R.id.itemTotal);
            TextView itemDate = (TextView) view.findViewById(R.id.itemDate);
            TextView itemCategory = (TextView) view.findViewById(R.id.itemCategory);
            TextView itemPeople = (TextView) view.findViewById(R.id.itemPeople);
            ImageView mMainImage =  (ImageView) view.findViewById(R.id.main_image);

            // Extract properties from cursor
            double total = cursor.getDouble(cursor.getColumnIndexOrThrow(SQLiteHelper.COLUMN_TOTAL));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(SQLiteHelper.COLUMN_DATE));
            String category = cursor.getString(cursor.getColumnIndexOrThrow(SQLiteHelper.COLUMN_CATEGORY));
            int people = cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteHelper.COLUMN_PEOPLE));
            String photoUri = cursor.getString(cursor.getColumnIndexOrThrow(SQLiteHelper.COLUMN_PHOTOURI));
            double avg = cursor.getDouble(cursor.getColumnIndexOrThrow(SQLiteHelper.COLUMN_AVERAGE));

            Log.d("photoUri", photoUri);
            if (photoUri != null && photoUri.length() != 0) {
                String storeFilename = "/photo_" + photoUri + ".jpg";
                Bitmap bitmap = getImageFileFromSDCard(storeFilename);
                mMainImage.setImageBitmap(bitmap);
            }
            else {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inMutable=true;
                Bitmap bitmap = BitmapFactory.decodeResource(
                        getApplicationContext().getResources(),
                        R.drawable.placeholder,
                        options);
                mMainImage.setImageBitmap(bitmap);
            }

            // Populate fields with extracted properties
            DecimalFormat formatter = new DecimalFormat("#0.00");
            itemTotal.setText(formatter.format(avg));
            itemDate.setText(date);
            itemCategory.setText(category);
            itemPeople.setText(String.valueOf(people));
        }
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

        result = readItems();
        itemAdapter = new ItemCursorAdapter(getContext(), result);
    }

    public void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false); //set the Adapter

        mListView = (ListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(itemAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor cursor = (Cursor) mListView.getItemAtPosition(position);

                int itemId = cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteHelper.COLUMN_ID));
                String userId = cursor.getString(cursor.getColumnIndexOrThrow(SQLiteHelper.COLUMN_USERID));
                double total = cursor.getDouble(cursor.getColumnIndexOrThrow(SQLiteHelper.COLUMN_TOTAL));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(SQLiteHelper.COLUMN_DATE));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(SQLiteHelper.COLUMN_CATEGORY));
                int people = cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteHelper.COLUMN_PEOPLE));
                String photoUri = cursor.getString(cursor.getColumnIndexOrThrow(SQLiteHelper.COLUMN_PHOTOURI));

                Intent listItem = new Intent(getActivity(), ItemActivity.class);
                listItem.putExtra("itemId", String.valueOf(itemId));
                listItem.putExtra("userId", userId);
                listItem.putExtra("total", String.valueOf(total));
                listItem.putExtra("date", date);
                listItem.putExtra("category", category);
                listItem.putExtra("people", String.valueOf(people));
                listItem.putExtra("photoUri", photoUri);

                startActivity(listItem);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.floatBtn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder
                        .setMessage(R.string.dialog_select_prompt)
                        .setPositiveButton(R.string.dialog_select_gallery, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent listItem = new Intent(getContext(), OCRActivity.class);
                                listItem.putExtra("action", "gallery");
                                startActivity(listItem);
                            }
                        })
                        .setNegativeButton(R.string.dialog_select_camera, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent listItem = new Intent(getContext(), OCRActivity.class);
                                listItem.putExtra("action", "camera");
                                startActivity(listItem);
                            }
                        });
                builder.create().show();
            }
        });

        return view;
    }

    public void refresh() {
        Cursor newCursor = readItems();
        result = itemAdapter.swapCursor(newCursor);
        itemAdapter.notifyDataSetChanged();
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
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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

    private Cursor readItems() {
        ItemDAO itemDao = new ItemDAO(getContext());
        String userId = "test";
        result = itemDao.getAllItems(userId);
        // Log.e("result size", String.valueOf(result.getCount()));
        return result;
    }
}
