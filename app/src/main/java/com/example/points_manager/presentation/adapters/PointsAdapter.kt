package com.example.points_manager.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.points_manager.R
import com.example.points_manager.databinding.LayoutPointItemBinding
import com.example.points_manager.domain.models.Point

class PointsAdapter: RecyclerView.Adapter<PointsAdapter.ViewHolder>() {

    private var points: List<Point> = emptyList()
    var clickListener: ((Point) -> Unit)? = null

    class ViewHolder(private val binding: LayoutPointItemBinding): RecyclerView.ViewHolder(binding.root) {

        companion object {
            private val titleRes = R.string.item_title
            private val coordsRes = R.string.item_coords
        }
        fun bind(point: Point) = with(binding) {
            title.text = root.context.getString(titleRes).format(point.id.toString())
            coords.text = root.context.getString(coordsRes).format(point.latitude.toString(), point.longitude.toString())
        }
    }

    class DiffCallback(
        private val oldItems: List<Point>,
        private val newItems: List<Point>
    ): DiffUtil.Callback() {
        override fun getOldListSize() = oldItems.size

        override fun getNewListSize() = newItems.size

        fun getOldItem(position: Int) = oldItems[position]

        fun getNewItem(position: Int) = newItems[position]

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            getOldItem(oldItemPosition).id == getNewItem(newItemPosition).id

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            getOldItem(oldItemPosition).latitude.equals(getNewItem(newItemPosition).latitude) &&
                    getOldItem(oldItemPosition).longitude.equals(getNewItem(newItemPosition).longitude)
    }

    fun update(newPoints: List<Point>) {
        val productDiffUtilCallback = DiffCallback(oldItems = points, newItems = newPoints)
        val productDiffResult = DiffUtil.calculateDiff(productDiffUtilCallback)

        points = newPoints
        productDiffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(LayoutPointItemBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount() = points.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val point = points[position]
        holder.bind(point)
        holder.itemView.setOnClickListener {
            clickListener?.invoke(point)
        }
    }

}