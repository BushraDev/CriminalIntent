package com.bushra.criminalintent;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.vistrav.ask.Ask;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class CrimeFragment extends Fragment {

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_IMAGE = "DialogImage";
    private static final String DIALOG_TIME = "DialogTime";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_PHOTO= 2;
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    private static final int REQUEST_CONTACT = 3;

    private Crime mCrime;
    private EditText cEditText;
    private Button cButton,cButton2;
    private CheckBox cCheckBox;
    private Button cSuspectButton;
    private Button cReportButton;
    private Button cCalltButton;
    private ImageButton cPhotoButton;
    private ImageView cPhotoView;
    private File cPhotoFile;
    Bitmap bitmap;
    private Callbacks mCallbacks;

    int Height;
    int Width;


    public interface Callbacks {
        void onCrimeUpdated(Crime crime);
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
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.getInctance(getActivity()).getCrime(crimeId);
        cPhotoFile = CrimeLab.getInctance(getActivity()).getPhotoFile(mCrime);
        setHasOptionsMenu(true);

        Ask.on(getActivity()).forPermissions(Manifest.permission.CALL_PHONE).go();

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
        MenuItem deleteItem = menu.findItem(R.id.delete_crime);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.delete_crime:

                CrimeLab.getInctance(getActivity()).deleteCrime(mCrime);
                getActivity().finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.getInctance(getActivity()).updateCrime(mCrime);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        cEditText = v.findViewById(R.id.crime_title);
        cEditText.setText(mCrime.getcTitle());

        cEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mCrime.setcTitle(charSequence.toString());
                updateCrime();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        cButton = v.findViewById(R.id.crime_date);
        String formatDate = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(mCrime.getcDate());
        cButton.setText(formatDate);

        cButton2 = v.findViewById(R.id.crime_time);
        String formatTime = timeFormat.format(mCrime.getcTime());
        cButton2.setText(formatTime);

//        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
//
//            @Override
//            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//
//                myCalendar.set(Calendar.YEAR, year);
//                myCalendar.set(Calendar.MONTH, monthOfYear);
//                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//                updateLabel();
//
//            }
//
//        };

        cButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getcDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
                } else {
                    Intent intent = DatePickerActivity.createIntent(getActivity(), mCrime.getcDate());
                    startActivityForResult(intent, REQUEST_DATE);
                }

            }
        });

        String formatDate2 = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(mCrime.getcDate());
        cButton.setText(formatDate2);

        cButton2.setVisibility(View.INVISIBLE);
        cButton2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                FragmentManager manager = getFragmentManager();
                TimePickerFragment dialog = TimePickerFragment.newInstance(mCrime.getcTime());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                dialog.show(manager, DIALOG_TIME);


            }
        });



        cCheckBox = v.findViewById(R.id.crime_solved);
        cCheckBox.setChecked(mCrime.iscSolved());
        cCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mCrime.setcSolved(b);
                updateCrime();
            }
        });


        cReportButton = v.findViewById(R.id.crime_report);
        cReportButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent i = ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText(getCrimeReport())
                        .setSubject(getString(R.string.crime_report_subject))
                        .setChooserTitle(getString(R.string.send_report))
                        .createChooserIntent();
                startActivity(i);

//                Intent i = new Intent(Intent.ACTION_SEND);
//                i.setType("text/plain");
//                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
//                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
//                i = Intent.createChooser(i, getString(R.string.send_report));
//                startActivity(i);
            }
        });


        final Intent pickContact = new Intent(Intent.ACTION_PICK);
//                , ContactsContract.Contacts.CONTENT_URI);
        pickContact.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);

        cSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
        cSuspectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        if (mCrime.getcSuspect() != null) {
            cSuspectButton.setText(mCrime.getcSuspect());
        }

        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {
            cSuspectButton.setEnabled(false);
        }

        cCalltButton = v.findViewById(R.id.crime_call);
        if (mCrime.getcSuspect() == null) {
            cCalltButton.setEnabled(false);
            cCalltButton.setText(R.string.call_suspect);
        } else {
            cCalltButton.setText(getString(R.string.crime_call_text, mCrime.getcSuspect()));
        }
        cCalltButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mCrime.getcSuspectNumber() != null) {
                    Intent intent = new Intent(Intent.ACTION_CALL,
                            Uri.parse("tel:" + mCrime.getcSuspectNumber()));
                    startActivity(intent);
                }
            }
        });


        cPhotoButton = v.findViewById(R.id.crime_camera);

        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = cPhotoFile != null && captureImage.resolveActivity(packageManager) != null;
        cPhotoButton.setEnabled(canTakePhoto);

        cPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = FileProvider.getUriForFile(getActivity(),
                        "com.bushra.criminalintent.fileprovider",
                        cPhotoFile);

                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                List<ResolveInfo> cameraActivities = getActivity()
                        .getPackageManager().queryIntentActivities(captureImage,
                                PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo activity : cameraActivities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName,
                            uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }

                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        cPhotoView = v.findViewById(R.id.crime_photo);
        ViewTreeObserver observer = cPhotoView.getViewTreeObserver();


        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Height = cPhotoView.getHeight();
                Width = cPhotoView.getWidth();
                updatePhotoView(Width,Height);

            }
        });

        cPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentManager manager = getFragmentManager();
                ImageFragment dialog = ImageFragment.newInstance(bitmap);
                dialog.show(manager, DIALOG_IMAGE);

            }
        });

        return v;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DATE ) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setcDate(date);
            String formatDate = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(mCrime.getcDate());
            updateCrime();
            cButton.setText(formatDate);
        } else if (requestCode == REQUEST_CONTACT && data != null) {

            Uri contactUri = data.getData();

            String[] queryFields = new String[] {
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER
            };

            Cursor c = getActivity().getContentResolver()
                    .query(contactUri, queryFields, null, null, null);



            String suspectId;

            try {

                if (c.getCount() == 0) {
                    return;
                }

                c.moveToFirst();
                String suspect = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

//                String suspect = c.getString(0);
                mCrime.setcSuspect(suspect);
                updateCrime();
                cSuspectButton.setText(suspect);

                suspectId = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                cCalltButton.setEnabled(true);
                cCalltButton.setText(getString(R.string.crime_call_text, mCrime.getcSuspect()));

            } finally {
                c.close();
            }

            contactUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            queryFields = new String[] {ContactsContract.CommonDataKinds.Phone.NUMBER};
            c = getActivity().getContentResolver()
                    .query(contactUri, queryFields, ContactsContract.CommonDataKinds.Phone.NUMBER + " = ? ",
                            new String[] {suspectId}, null);

            try {
                if (c.getCount() == 0) {
                    return;
                }

                c.moveToFirst();
                String number = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    mCrime.setcSuspectNumber(number);

            } finally {
                c.close();
            }

        } else if (requestCode == REQUEST_PHOTO) {
        Uri uri = FileProvider.getUriForFile(getActivity(),
                "com.bushra.criminalintent.fileprovider",
                cPhotoFile);
        getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        updateCrime();
        updatePhotoView(Width,Height);
    }


        if (requestCode == REQUEST_TIME) {

//            Date time = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
//            mCrime.setcTime(time);
////            mCrime.setcTime(mCrime.getcDate());
////            String formatTime = timeFormat.getDateTimeInstance(timeFormat.SHORT, timeFormat.SHORT).format(mCrime.getcTime());
//            String formatTime = timeFormat.format(mCrime.getcTime());
//            cButton2.setText(formatTime);
        }

    }

    private void updateCrime() {
        CrimeLab.getInctance(getActivity()).updateCrime(mCrime);
        mCallbacks.onCrimeUpdated(mCrime);
    }

    private String getCrimeReport() {
        String solvedString = null;
        if (mCrime.iscSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }
        String dateFormat = "EEE, MMM dd";
        String dateString = android.text.format.DateFormat.format(dateFormat, mCrime.getcDate()).toString();
        String suspect = mCrime.getcSuspect();

        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }
        String report = getString(R.string.crime_report,
                mCrime.getcTitle(), dateString, solvedString, suspect);
        return report;
    }


    private void updatePhotoView(int width , int height) {
        if (cPhotoFile == null || !cPhotoFile.exists()) {
            cPhotoView.setImageDrawable(null);
        } else {
            bitmap = PictureUtils.getScaledBitmap(cPhotoFile.getPath(), width, height);
            cPhotoView.setImageBitmap(bitmap);
        }
    }


    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
