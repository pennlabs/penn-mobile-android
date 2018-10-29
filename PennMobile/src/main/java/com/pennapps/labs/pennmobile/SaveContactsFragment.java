package com.pennapps.labs.pennmobile;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pennapps.labs.pennmobile.adapters.PhoneSaveAdapter;
import com.pennapps.labs.pennmobile.classes.Person;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SaveContactsFragment extends ListFragment {

    private MainActivity mActivity;
    private List<Person> contacts_list;
    private List<Person> selected;
    private Set<String> current_numbers;
    private Set<String> current_names;
    public static final int permission_read = 123;

    @Override
    public void onActivityCreated(Bundle save) {
        super.onActivityCreated(save);
        mActivity = (MainActivity) getActivity();
        mActivity.closeKeyboard();
        loadData();
        getListView().setAdapter(new PhoneSaveAdapter(getActivity(), contacts_list, selected));
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_CONTACTS)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(mActivity, Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity,
                    new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS},
                    permission_read);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle save) {
        View view = inflater.inflate(R.layout.fragment_save_contacts, container, false);
        setHasOptionsMenu(true);
        current_numbers = new HashSet<>();
        current_names = new HashSet<>();
        selected = new ArrayList<>();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        mActivity.getMenuInflater().inflate(R.menu.phone_save_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.support_contacts_add) {
            if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(mActivity, Manifest.permission.READ_CONTACTS)
                            != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mActivity,
                        new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS},
                        permission_read);
            } else {
                loadCurrent();
                addContacts();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.save_contacts);
        mActivity.getActionBarToggle().setDrawerIndicatorEnabled(true);
        mActivity.getActionBarToggle().syncState();
        mActivity.setNav(R.id.nav_support);
    }

    private void addContacts() {
        for (Person p : selected) {
            if (this.current_names.contains(p.name) || current_numbers.contains(p.phone)) {
                continue;
            }

            ArrayList<ContentProviderOperation> ops = new ArrayList<>();
            int id = ops.size();

            ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                    .build());

            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, id)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, p.name)
                    .build());

            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, id)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, p.phone)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .build());
            try {
                ContentProviderResult[] results = mActivity.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                current_names.add(p.name);
                current_numbers.add(p.phone);
            } catch (RemoteException e) {
                Toast.makeText(mActivity, "Could not save contacts", Toast.LENGTH_SHORT).show();
            } catch (OperationApplicationException e) {
                Toast.makeText(mActivity, "Could not save contacts", Toast.LENGTH_SHORT).show();
            }
        }
        Toast.makeText(mActivity, selected.size() + " contact"+ (selected.size() > 1 ? "s" : "") +" saved", Toast.LENGTH_SHORT).show();
        SupportFragment frag = new SupportFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(((ViewGroup)getView().getParent()).getId(), frag, "SUPPORT_FRAGMENT")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case permission_read: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadCurrent();
                    addContacts();
                }
                break;
            }
        }
    }

    private void loadData() {
        contacts_list = new ArrayList<>();
        selected = new ArrayList<>();
        contacts_list.add(new Person("Penn Police General", "(215) 898-7297"));
        contacts_list.add(new Person("Penn Police Emergencies/MERT", "(215) 573-3333"));
        contacts_list.add(new Person("Penn Walk", "215-898-9255", "215-898-WALK"));
        contacts_list.add(new Person("Penn Ride", "215-898-7433", "215-898-RIDE"));
        contacts_list.add(new Person("Help Line", "215-898-4357", "215-898-HELP"));
        contacts_list.add(new Person("CAPS", "(215) 898-7021"));
        contacts_list.add(new Person("Special Services", "(215) 898-6600"));
        contacts_list.add(new Person("Women's Center", "(215) 898-8611"));
        contacts_list.add(new Person("Student Health Services", "(215) 746-3535"));
        contacts_list.add(new Person("Penn Violence Protection", "https://secure.www.upenn.edu/vpul/pvp/gethelp"));
    }

    private void loadCurrent() {
        current_numbers.clear();
        current_names.clear();
        Cursor cursor = mActivity.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null);
        if (cursor == null)
            return;
        cursor.moveToFirst();
        do {
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            current_names.add(name);
        } while (cursor.moveToNext());
        cursor.close();
    }
}
