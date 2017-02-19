/*
 * Copyright (c) 2017. rei-m.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.reim.android.filterrecyclerview

import android.support.v7.widget.RecyclerView
import android.widget.Filter
import android.widget.Filterable

/**
 * Base of FilterRecyclerView's adapter.
 */
abstract class FilterRecyclerAdapter<T : FilterableItem> : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    private val itemList: MutableList<T> = mutableListOf()

    private val filteredItemList: MutableList<T> = mutableListOf()

    private val filter: Filter = object : Filter() {

        private val filteredList: MutableList<T> = mutableListOf()

        override fun performFiltering(constraint: CharSequence?): FilterResults {

            filteredList.clear()

            if (constraint != null && constraint.isNotEmpty()) {
                val filterPattern = constraint.toString()

                itemList.forEach {
                    if (filterByConstraint(it, filterPattern)) {
                        filteredList.add(it)
                    }
                }
            } else {
                filteredList.addAll(itemList)
            }

            return FilterResults().apply {
                values = filteredList
                count = filteredList.size
            }
        }

        @Suppress("UNCHECKED_CAST")
        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            filteredItemList.clear()
            if (results != null) {
                filteredItemList.addAll(results.values as MutableList<T>)
            }
            notifyDataSetChanged()
        }
    }

    protected abstract fun filterByConstraint(item: T, constraint: String): Boolean

    internal fun initializeItem(itemList: List<T>) {
        this.itemList.clear()
        this.itemList.addAll(itemList)
        this.filteredItemList.clear()
        this.filteredItemList.addAll(itemList)
        notifyDataSetChanged()
    }

    fun findItemByPosition(position: Int): T {
        return filteredItemList[position]
    }

    override fun getItemCount(): Int {
        return filteredItemList.size
    }

    override fun getFilter(): Filter {
        return filter
    }
}
