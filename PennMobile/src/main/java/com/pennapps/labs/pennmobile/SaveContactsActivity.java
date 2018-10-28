package com.pennapps.labs.pennmobile;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.provider.ContactsContract;

import com.pennapps.labs.pennmobile.adapters.PhoneSaveAdapter;
import com.pennapps.labs.pennmobile.classes.Person;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SaveContactsActivity extends AppCompatActivity {

    private List<Person> contacts_list;
    private List<Person> selected;
    private Set<String> current_numbers;
    private Set<String> current_names;
    private final int permission_read = 0;

    @BindView(R.id.contacts_save_list)
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_contacts);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        current_numbers = new HashSet<>();
        current_names = new HashSet<>();
        loadCurrent();
        loadData();
        PhoneSaveAdapter adapter = new PhoneSaveAdapter(this, contacts_list, selected);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.findViewById(R.id.phone_save_radiobutton).performClick();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.phone_save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.support_contacts_add) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS},
                        permission_read);
            } else {
                addContacts();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addContacts() {

        for (Person p : selected) {

            if (this.current_names.contains(p.name) || current_numbers.contains(p.phone)) {
                Log.d("###Duplicate", p.name+" - "+p.phone);
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
                ContentProviderResult[] results = getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                current_names.add(p.name);
                current_numbers.add(p.phone);
                for (ContentProviderResult result : results) {
                    Log.d("###CPResult", result.toString());
                }
            } catch (RemoteException e) {
                Toast.makeText(this, "error saving"+ e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            } catch (OperationApplicationException e) {
                Toast.makeText(this, "error saving"+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        Toast.makeText(this, selected.size() + " contacts saved", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case permission_read: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
        Cursor cursor = getContentResolver()
                .query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null);
        if (cursor == null) return;
        cursor.moveToFirst();
        do {
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.NAME_RAW_CONTACT_ID));
            Cursor phones = getContentResolver()
                    .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                            null,
                            null);
            if (phones != null) {
                while (phones.moveToNext()) {
                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    current_numbers.add(phoneNumber);
                    current_names.add(name);
                }
                phones.close();
            }
        } while (cursor.moveToNext());
        Log.d("###Current Phone Size", ""+current_numbers.size());

        cursor.close();
    }
}
