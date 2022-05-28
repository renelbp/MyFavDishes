package com.example.myfavdishes.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myfavdishes.databinding.ItemCustomListBinding
import com.example.myfavdishes.view.activities.AddUpdateDishActivity

/**THIS ADAPTER IS GONNA BE USED IN A RECYCLER VIEW OF A CUSTOM DIALOG*/

class CustomListItemAdapter(
    private val activity: Activity,
     private val listItems: List<String>,
     private val selection: String
): RecyclerView.Adapter<CustomListItemAdapter.ViewHolder>() {

    class ViewHolder(vBinding: ItemCustomListBinding): RecyclerView.ViewHolder(vBinding.root) {
        val tvText = vBinding.tvText
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCustomListBinding
            .inflate(LayoutInflater.from(activity),parent,false )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listItems[position]
        holder.tvText.text = item

        holder.itemView.setOnClickListener{
            if(activity is AddUpdateDishActivity){
                activity.selectedListItem(item, selection)
            }
        }
    }

    override fun getItemCount(): Int {
        return listItems.size
    }
}