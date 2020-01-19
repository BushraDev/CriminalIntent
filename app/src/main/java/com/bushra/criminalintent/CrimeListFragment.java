package com.bushra.criminalintent;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.ArrayList;


public class CrimeListFragment extends Fragment {

    RecyclerView crimeRecyclerView;
    CrimeAdapter crimeAdapter;
    private boolean mSubtitleVisible;
    private Callbacks mCallbacks;
    private Crime mCrime;
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
    private int a = -1;
    ArrayList<Crime> crimes;


    public interface Callbacks {
        void onCrimeSelected(Crime crime);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.new_crime:

                Crime crime = new Crime();
                CrimeLab.getInctance(getActivity()).addCrime(crime);
                updateUI();
                mCallbacks.onCrimeSelected(crime);
                return true;

            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle() {
        CrimeLab crimeLab = CrimeLab.getInctance(getActivity());
        int crimeCount = crimeLab.getCrimes().size();
        String subtitle = getString(R.string.subtitle_format, crimeCount);
        if (!mSubtitleVisible) {
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v =  inflater.inflate(R.layout.fragment_crime_list, container, false);

        crimeRecyclerView = v.findViewById(R.id.crimes_recycler_view);
        crimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


//        toolbar = v.findViewById(R.id.tool_bar);
//        getActivity().sets

        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        updateUI();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    public void updateUI() {

        CrimeLab crimeLab = CrimeLab.getInctance(getActivity());
        ArrayList<Crime> crimes = crimeLab.getCrimes();

        new ItemTouchHelper(simpleItemTouchCallback).attachToRecyclerView(crimeRecyclerView);

        if (crimeAdapter == null) {

            crimeAdapter = new CrimeAdapter(crimes);
            crimeRecyclerView.setAdapter(crimeAdapter);

        } else {
            if (a < 0) {
                crimeAdapter.notifyDataSetChanged();
            }else {
                crimeAdapter.setCrimes(crimes);
                crimeAdapter.notifyItemChanged(a);
                a = -1;
            }
            updateSubtitle();
        }
    }




    public class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {


        public CrimeAdapter(ArrayList<Crime> crime) {
            crimes = crime;
        }

        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new CrimeHolder(layoutInflater, parent);

        }

        @Override
        public void onBindViewHolder(@NonNull CrimeHolder holder, int position) {

            Crime crime = crimes.get(position);
            a = position;
            holder.bind(crime);

        }

        @Override
        public int getItemCount() {
            return crimes.size();
        }

        public void setCrimes(ArrayList<Crime> crime) {
            crimes = crime;
        }

    }



    public class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;
        TextView titleTextView,dateTextView;


        public CrimeHolder(LayoutInflater inflater, ViewGroup parent) {

            super(inflater.inflate(R.layout.crimes_lists, parent, false));
            itemView.setOnClickListener(this);

            imageView = itemView.findViewById(R.id.shackles_imageView);
            titleTextView = itemView.findViewById(R.id.tv_title);
            dateTextView = itemView.findViewById(R.id.tv_date);

        }

        public void bind(Crime crime) {
            mCrime = crime;

            if (mCrime.iscSolved() == true) {
//                imageView.setImageResource(R.drawable.shackles);
                imageView.setVisibility(View.VISIBLE);
            } else {
//                imageView.setImageResource(R.drawable.ic_launcher_foreground);
                imageView.setVisibility(View.INVISIBLE);
            }

            titleTextView.setText(mCrime.getcTitle());

            String formatDate = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(mCrime.getcDate());

//            LocalDate date = LocalDate.now();
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MM dd");
//            String text = date.format(formatter);
//            LocalDate parsedDate = LocalDate.parse(text, formatter);

            dateTextView.setText(formatDate);

        }

        @Override
        public void onClick(View view) {

            a = getAdapterPosition();
            mCallbacks.onCrimeSelected(mCrime);

        }


    }


    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT) {

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            crimes.remove(viewHolder.getAdapterPosition());
            CrimeLab.getInctance(getActivity()).deleteCrime(mCrime);
            crimeAdapter.notifyDataSetChanged();

        }
    };


}
