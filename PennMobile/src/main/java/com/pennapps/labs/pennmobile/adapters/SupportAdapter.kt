package com.pennapps.labs.pennmobile.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.classes.Contact
import kotlinx.android.synthetic.main.support_list_item.view.support_name
import kotlinx.android.synthetic.main.support_list_item.view.support_phone
import kotlinx.android.synthetic.main.support_list_item.view.support_phone_icon

class SupportAdapter(context: Context, contacts: List<Contact?>) : ArrayAdapter<Contact?>(context, R.layout.support_list_item, contacts) {
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup,
    ): View {
        val currentPerson = getItem(position)

        val view = convertView ?: inflater.inflate(R.layout.support_list_item, parent, false)

        view.support_name?.text = currentPerson?.name

        if (currentPerson?.phoneWords == "") {
            view.support_phone?.text = currentPerson.phone
        } else {
            view.support_phone?.text = currentPerson?.phoneWords + " (" + currentPerson?.phone + ")"
        }
        if (currentPerson?.isURL == true) {
            view.support_phone_icon?.visibility = View.GONE
        } else {
            view.support_phone_icon?.visibility = View.GONE
        }
        view.setOnClickListener { v ->
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
        return view
    }
}
