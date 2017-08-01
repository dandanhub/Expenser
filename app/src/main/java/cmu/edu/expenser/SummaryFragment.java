package cmu.edu.expenser;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import android.app.AlertDialog;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import java.lang.reflect.Field;

import static android.R.attr.id;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SummaryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SummaryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SummaryFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private PieChart pieChart;
    private Cursor cursor;
    private Button monthBtn;
    private OnFragmentInteractionListener mListener;
    private static String monthStr;

    // final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
    DatePicker datePicker;
    private Calendar mCalendar = Calendar.getInstance();

    public SummaryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SummaryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SummaryFragment newInstance(String param1, String param2) {
        SummaryFragment fragment = new SummaryFragment();
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

//    View.OnClickListener monthButtonClicked = new View.OnClickListener(){
//        @Override
//        public void onClick(View v) {
//            // Context context = getApplicationContext();
//            Log.d("monthBtn", "clicked");
//            DialogFragment newFragment = new DatePickerFragmentWithoutDay();
//            newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
//        }
//    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_summary, container, false); //set the Adapter
        pieChart = (PieChart) view.findViewById(R.id.chart);
        initializePieChart();

//        monthBtn = (Button) view.findViewById(R.id.monthBtn);
//        monthBtn.setOnClickListener(new OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                // Context context = getApplicationContext();
//                Log.d("monthBtn", "clicked");
//            }
//        });

        return view;
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

    public void onResume() {
        super.onResume();
        refresh();
    }

    public void refresh() {
        initializePieChart();
    }

    public void initializePieChart() {
        List<PieEntry> entries = new ArrayList<>();

        cursor = readMonthItems();
        cursor.moveToFirst();
        // ItemDAO itemDao = new ItemDAO(getContext());
        while (!cursor.isAfterLast()) {
            String category = cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLUMN_CATEGORY));
            double sum = cursor.getDouble(2);
            entries.add(new PieEntry((float)sum, category));
            cursor.moveToNext();
        } // make sure to close the cursor cursor.close();


        PieDataSet set = new PieDataSet(entries, "Monthly Expense");
        set.setColors(new int[]{R.color.orange, R.color.blue, R.color.pink,
                        R.color.violet, R.color.green},
                getContext());
        PieData data = new PieData(set);

        pieChart.setData(data);
        // pieChart.setCenterText("Expenser");
        // pieChart.setCenterTextColor(R.color.white);
        pieChart.setHoleColor(R.color.colorPrimary);
        pieChart.invalidate(); // refresh
    }

    private Cursor readMonthItems() {
        ItemDAO itemDao = new ItemDAO(getContext());
        String userId = "test";

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = sdf.format(calendar.getTime());
        String monthString = dateString.substring(0, dateString.length() - 3);
        return itemDao.getMonthItems(userId, monthString);
        // Log.e("result size", String.valueOf(result.getCount()));
    }
}
