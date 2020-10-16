package com.pennapps.labs.pennmobile

import android.Manifest
import android.content.ContentProviderOperation
import android.content.OperationApplicationException
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.RemoteException
import android.provider.ContactsContract
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.ListFragment
import com.pennapps.labs.pennmobile.adapters.PhoneSaveAdapter
import com.pennapps.labs.pennmobile.classes.Person
import java.util.*

class SaveContactsFragment : ListFragment() {
    private lateinit var mActivity: MainActivity
    private var contactsList: MutableList<Person> = ArrayList()
    private var selected: MutableList<Person> = ArrayList()
    private var currentNumbers: MutableSet<String> = HashSet()
    private var currentNames: MutableSet<String> = HashSet()

    override fun onActivityCreated(save: Bundle?) {
        super.onActivityCreated(save)
        mActivity = activity as MainActivity
        mActivity.closeKeyboard()
        loadData()
        for (p in contactsList) {
            selected.add(p)
        }
        listView.adapter = PhoneSaveAdapter(mActivity, contactsList, selected, contactsList.size)
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_CONTACTS)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(mActivity, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity, arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS),
                    permission_read)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, save: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_save_contacts, container, false)
        setHasOptionsMenu(true)
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        mActivity.menuInflater.inflate(R.menu.phone_save_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.support_contacts_add) {
            if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(mActivity, Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mActivity, arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS),
                        permission_read)
            } else {
                loadCurrent()
                addContacts()
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        mActivity.setTitle(R.string.save_contacts)
    }

    private fun addContacts() {
        for (p in selected) {
            if (currentNames.contains(p.name) || currentNumbers.contains(p.phone)) {
                continue
            }
            val ops = ArrayList<ContentProviderOperation>()
            val id = ops.size
            ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                    .build())
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, id)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, p.name)
                    .build())
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, id)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, p.phone)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .build())
            try {
                val results = mActivity.contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
                currentNames.add(p.name)
                currentNumbers.add(p.phone)
            } catch (e: RemoteException) {
                Toast.makeText(mActivity, "Could not save contacts", Toast.LENGTH_SHORT).show()
            } catch (e: OperationApplicationException) {
                Toast.makeText(mActivity, "Could not save contacts", Toast.LENGTH_SHORT).show()
            }
        }
        Toast.makeText(mActivity, selected.size.toString() + " contact" + (if (selected.size > 1 || selected.size == 0) "s" else "") + " saved", Toast.LENGTH_SHORT).show()
        val frag = SupportFragment()
        val fragmentManager = mActivity.supportFragmentManager
        fragmentManager.beginTransaction()
                .replace((requireView().parent as ViewGroup).id, frag, "SUPPORT_FRAGMENT")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commitAllowingStateLoss()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            permission_read -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadCurrent()
                    addContacts()
                }
            }
        }
    }

    private fun loadData() {
        contactsList = ArrayList()
        selected = ArrayList()
        contactsList.add(Person("Penn Police General", "(215) 898-7297"))
        contactsList.add(Person("Penn Police Emergencies/MERT", "(215) 573-3333"))
        contactsList.add(Person("Penn Walk", "215-898-9255", "215-898-WALK"))
        contactsList.add(Person("Penn Ride", "215-898-7433", "215-898-RIDE"))
        contactsList.add(Person("Help Line", "215-898-4357", "215-898-HELP"))
        contactsList.add(Person("CAPS", "(215) 898-7021"))
        contactsList.add(Person("Special Services", "(215) 898-6600"))
        contactsList.add(Person("Women's Center", "(215) 898-8611"))
        contactsList.add(Person("Student Health Services", "(215) 746-3535"))
    }

    private fun loadCurrent() {
        currentNumbers.clear()
        currentNames.clear()
        val cursor = mActivity.contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
                ?: return
        if (cursor.moveToFirst()) {
            do {
                val name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                currentNames.add(name)
            } while (cursor.moveToNext())
        }
        cursor.close()
    }

    companion object {
        const val permission_read = 123
    }
}