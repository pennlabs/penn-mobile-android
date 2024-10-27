package com.pennapps.labs.pennmobile.more.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.more.classes.Contact
import com.pennapps.labs.pennmobile.databinding.SupportListItemBinding

class SupportAdapter(
    context: Context,
    contacts: List<Contact?>,
) : ArrayAdapter<Contact?>(context, R.layout.support_list_item, contacts) {
    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup,
    ): View {
        val currentPerson = getItem(position)
        val itemBinding: SupportListItemBinding =
            if (convertView != null) {
                SupportListItemBinding.bind(convertView)
            } else {
                SupportListItemBinding.inflate(LayoutInflater.from(context), parent, false)
            }

        itemBinding.supportName.text = currentPerson?.name

        if (currentPerson?.phoneWords == "") {
            itemBinding.supportPhone.text = currentPerson.phone
        } else {
            itemBinding.supportPhone.text = currentPerson?.phoneWords + " (" + currentPerson?.phone + ")"
        }
        if (currentPerson?.isURL == true) {
            itemBinding.supportPhoneIcon.visibility = View.GONE
        } else {
            itemBinding.supportPhoneIcon.visibility = View.GONE
        }
        itemBinding.root.setOnClickListener { v ->
            val intent: Intent
            if (currentPerson?.isURL == true) {
                intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(currentPerson.phone)
            } else {
                val uri = "tel:" + currentPerson?.phone
                intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse(uri)
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            v.context.startActivity(intent)
        }
        return itemBinding.root
    }
}
