package com.pennapps.labs.pennmobile.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.classes.Contact
import kotlinx.android.synthetic.main.phone_save_list_item.view.*

class PhoneSaveAdapter(context: Context, contacts: List<Contact?>, s: MutableList<Contact>, size: Int) : ArrayAdapter<Contact?>(context, R.layout.phone_save_list_item, contacts) {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val selections: MutableList<Contact> = s
    private val state: BooleanArray = BooleanArray(size)

    override fun getView(pos: Int, view: View?, parent: ViewGroup): View {
        val currentPerson = getItem(pos)
        val view = view ?: inflater.inflate(R.layout.phone_save_list_item, parent, false)

        val nameTv = view.support_name
        val supportPhoneTv = view.support_phone
        val checkbox = view.phone_save_checkbox
        nameTv?.text = currentPerson?.name
        supportPhoneTv?.text = currentPerson?.phone
        view.support_phone_icon?.visibility = if (currentPerson?.isURL == true) View.GONE else View.VISIBLE
        checkbox?.setOnCheckedChangeListener(null)
        checkbox?.isChecked = state[pos]
        checkbox?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (!state[pos]) {
                checkbox.isChecked = true
                selections.add(Contact(nameTv?.text.toString(), supportPhoneTv.text.toString()))
                state[pos] = true
            } else {
                view.phone_save_checkbox?.isChecked = false
                for (p in selections) {
                    if (p.name == nameTv?.text.toString()) {
                        selections.remove(p)
                        break
                    }
                }
                state[pos] = false
            }
        }
        view.setOnClickListener { checkbox.isChecked = !checkbox.isChecked }
        return view
    }

    init {
        for (i in state.indices) {
            state[i] = true
        }
    }
}