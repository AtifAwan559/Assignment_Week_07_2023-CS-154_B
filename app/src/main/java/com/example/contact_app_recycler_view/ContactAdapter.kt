package com.example.contact_app_recycler_view

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class ContactAdapter(
    private var contactList: MutableList<Contact>,
    private val listener: OnContactActionListener
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>(), Filterable {

    private var contactListFull: List<Contact> = ArrayList(contactList)

    interface OnContactActionListener {
        fun onItemClick(position: Int)
        fun onEditClick(position: Int)
        fun onDeleteClick(position: Int)
    }

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvContactName: TextView = itemView.findViewById(R.id.tvContactName)
        val tvContactPhone: TextView = itemView.findViewById(R.id.tvContactPhone)
        val btnEdit: Button = itemView.findViewById(R.id.btnEdit)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
        val ivProfile: ImageView = itemView.findViewById(R.id.ivProfile)
        val tvProfileInitials: TextView = itemView.findViewById(R.id.tvProfileInitials)
        val viewProfileBackground: View = itemView.findViewById(R.id.viewProfileBackground)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_item_contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val currentContact = contactList[position]

        holder.tvContactName.text = currentContact.name
        holder.tvContactPhone.text = currentContact.phone
        
        if (currentContact.profileImageUri != null) {
            holder.ivProfile.visibility = View.VISIBLE
            holder.tvProfileInitials.visibility = View.GONE
            holder.viewProfileBackground.visibility = View.GONE
            holder.ivProfile.setImageURI(Uri.parse(currentContact.profileImageUri))
        } else {
            holder.ivProfile.visibility = View.GONE
            holder.tvProfileInitials.visibility = View.VISIBLE
            holder.viewProfileBackground.visibility = View.VISIBLE
            
            val initial = if (currentContact.name.isNotEmpty()) {
                currentContact.name[0].uppercaseChar().toString()
            } else {
                "?"
            }
            holder.tvProfileInitials.text = initial
            
            // Optional: Generate a random color based on the name
            val colors = intArrayOf(0xFFF44336.toInt(), 0xFFE91E63.toInt(), 0xFF9C27B0.toInt(), 0xFF673AB7.toInt(), 0xFF3F51B5.toInt(), 0xFF2196F3.toInt())
            val colorIndex = Math.abs(currentContact.name.hashCode()) % colors.size
            holder.viewProfileBackground.setBackgroundColor(colors[colorIndex])
        }

        holder.itemView.setOnClickListener {
            listener.onItemClick(position)
        }

        holder.btnEdit.setOnClickListener {
            listener.onEditClick(position)
        }

        holder.btnDelete.setOnClickListener {
            listener.onDeleteClick(position)
        }
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    fun updateList(newList: List<Contact>) {
        contactList = newList.toMutableList()
        contactListFull = ArrayList(contactList)
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return contactFilter
    }

    private val contactFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList = mutableListOf<Contact>()

            if (constraint == null || constraint.isEmpty()) {
                filteredList.addAll(contactListFull)
            } else {
                val filterPattern = constraint.toString().lowercase(Locale.ROOT).trim()

                for (item in contactListFull) {
                    if (item.name.lowercase(Locale.ROOT).contains(filterPattern)) {
                        filteredList.add(item)
                    }
                }
            }

            val results = FilterResults()
            results.values = filteredList
            return results
        }

        @Suppress("UNCHECKED_CAST")
        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            contactList.clear()
            contactList.addAll(results?.values as List<Contact>)
            notifyDataSetChanged()
        }
    }
}