package com.pennapps.labs.pennmobile.more.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.more.classes.Contact
import com.pennapps.labs.pennmobile.databinding.PhoneSaveListItemBinding

class PhoneSaveAdapter(
    context: Context,
    contacts: List<Contact?>,
    s: MutableList<Contact>,
    size: Int,
) : ArrayAdapter<Contact?>(
        context,
        R.layout.phone_save_list_item,
        contacts,
    ) {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val selections: MutableList<Contact> = s
    private val state: BooleanArray = BooleanArray(size)

    override fun getView(
        pos: Int,
        view: View?,
        parent: ViewGroup,
    ): View {
        val currentPerson = getItem(pos)
        val itemBinding: PhoneSaveListItemBinding =
            if (view != null) {
                PhoneSaveListItemBinding.bind(view)
            } else {
                PhoneSaveListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            }

        val nameTv = itemBinding.supportName
        val supportPhoneTv = itemBinding.supportPhone
        val checkbox = itemBinding.phoneSaveCheckbox
        nameTv.text = currentPerson?.name
        supportPhoneTv.text = currentPerson?.phone
        itemBinding.supportPhoneIcon.visibility = if (currentPerson?.isURL == true) View.GONE else View.VISIBLE
        checkbox?.setOnCheckedChangeListener(null)
        checkbox?.isChecked = state[pos]
        checkbox?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (!state[pos]) {
                checkbox.isChecked = true
                selections.add(Contact(nameTv?.text.toString(), supportPhoneTv.text.toString()))
                state[pos] = true
            } else {
                itemBinding.phoneSaveCheckbox.isChecked = false
                for (p in selections) {
                    if (p.name == nameTv?.text.toString()) {
                        selections.remove(p)
                        break
                    }
                }
                state[pos] = false
            }
        }
        itemBinding.root.setOnClickListener { checkbox.isChecked = !checkbox.isChecked }
        return itemBinding.root
    }

    init {
        for (i in state.indices) {
            state[i] = true
        }
    }
}
