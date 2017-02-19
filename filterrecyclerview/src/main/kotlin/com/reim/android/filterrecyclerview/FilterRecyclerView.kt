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

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewPropertyAnimatorListener
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*

/**
 * FilterRecyclerView is RecyclerView for incremental search.
 *
 * How to setup this view.
 * 1) You must create adapter what is extends FilterRecyclerAdapter. And set it by setAdapter.
 * 2) Set items of RecyclerView by initializeItemList.
 *
 * This view has custom attr.
 * - toolbarColor   toolbar's background color that has filter form.
 * - backDrawable   back button's drawable resource id.
 * - resetDrawable  reset button's drawable resource id.
 * - filterHint     EditText's hint in filter form.
 *
 * @see FilterRecyclerAdapter
 */
class FilterRecyclerView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : RelativeLayout(context, attrs, defStyle) {

    private val layoutBar: LinearLayout by lazy {
        findViewById(R.id.view_filter_recycler_layout_bar) as LinearLayout
    }

    private val buttonBack: ImageButton by lazy {
        findViewById(R.id.view_filter_recycler_button_back) as ImageButton
    }

    private val buttonReset: ImageButton by lazy {
        findViewById(R.id.view_filter_recycler_button_reset) as ImageButton
    }

    private val editWord: EditText by lazy {
        findViewById(R.id.view_filter_recycler_edit_word) as EditText
    }

    private val recyclerView: RecyclerView by lazy {
        findViewById(R.id.view_filter_recycler_view) as RecyclerView
    }

    var isOpened: Boolean = false
        private set

    init {

        setBackgroundColor(ResourcesCompat.getColor(resources, R.color.filter_recycler_view_background_color, null))

        isFocusableInTouchMode = true

        LayoutInflater.from(context).inflate(R.layout.view_filter_recycler, this, true)

        context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.FilterRecyclerView,
                0,
                0).let {

            try {

                layoutBar.setBackgroundColor(it.getColor(R.styleable.FilterRecyclerView_toolBarColor,
                        ResourcesCompat.getColor(resources, R.color.filter_recycler_view_background_color, null)))

                if (it.hasValue(R.styleable.FilterRecyclerView_backDrawable)) {
                    buttonBack.setImageDrawable(it.getDrawable(R.styleable.FilterRecyclerView_backDrawable))
                }
                if (it.hasValue(R.styleable.FilterRecyclerView_resetDrawable)) {
                    buttonReset.setImageDrawable(it.getDrawable(R.styleable.FilterRecyclerView_resetDrawable))
                }
                if (it.hasValue(R.styleable.FilterRecyclerView_filterHint)) {
                    editWord.hint = it.getString(R.styleable.FilterRecyclerView_filterHint)
                }
            } finally {
                it.recycle()
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            elevation = resources.getDimensionPixelOffset(R.dimen.filter_recycler_view_elevation).toFloat()
        }

        visibility = INVISIBLE
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                visibility = GONE
                viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))

        buttonBack.setOnClickListener {
            if (isOpened) {
                close()
            }
        }

        buttonReset.setOnClickListener {
            editWord.editableText.clear()
        }

        editWord.setOnEditorActionListener { view, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard()
                clearFocus()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        editWord.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
            }

            override fun beforeTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {

                recyclerView.adapter ?: return

                val text = charSequence.toString()

                (recyclerView.adapter as Filterable).filter.filter(text)

                if (text.trim().isNotEmpty()) {
                    buttonReset.visibility = View.VISIBLE
                } else {
                    buttonReset.visibility = View.GONE
                }
            }
        })
    }

    /**
     * Set Adapter for this view.
     *
     * @param adapter adapter what is extends FilterRecyclerAdapter
     * @see FilterRecyclerAdapter
     */
    fun <T : FilterableItem> setAdapter(adapter: FilterRecyclerAdapter<T>) {
        recyclerView.adapter = adapter
    }

    /**
     * Initialize adapter's item.
     *
     * @param itemList item list of adapter. item must implements FilterableItem.
     * @see FilterableItem
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : FilterableItem> initializeItemList(itemList: List<T>) {
        requireNotNull(recyclerView.adapter)
        (recyclerView.adapter as FilterRecyclerAdapter<T>).initializeItem(itemList)
    }

    /**
     * Open view.
     *
     * @param withAnimate if you want to open with animation, set true.
     */
    fun open(withAnimate: Boolean = true) {
        if (!isOpened) {
            requestFocus()
            if (withAnimate) {
                showWithAnimation()
            } else {
                show()
            }
        }
    }

    /**
     * Close view, and clear EditText.
     */
    fun close() {
        visibility = GONE
        hideKeyboard()
        editWord.editableText.clear()
        editWord.clearFocus()
        isOpened = false
    }

    private fun show() {
        visibility = VISIBLE
        isOpened = true
        editWord.requestFocus()
        showKeyboard()
    }

    private fun showWithAnimation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            animateCircularReveal()
        } else {
            fadeIn()
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun animateCircularReveal() {
        val point = getDisplaySize()

        val cx = point.x - TypedValue.applyDimension(1, 24.0f, resources.displayMetrics).toInt()
        val cy = point.y / 2

        val finalRadius = Math.max(point.x, point.y).toFloat()

        val anim = ViewAnimationUtils.createCircularReveal(this, cx, cy, 0.0f, finalRadius)

        visibility = VISIBLE

        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
                show()
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
            }
        })
        anim.start()
    }

    private fun fadeIn() {

        visibility = VISIBLE
        alpha = 0f

        ViewCompat.animate(this)
                .alpha(1.0f)
                .setDuration(300)
                .setListener(object : ViewPropertyAnimatorListener {
                    override fun onAnimationStart(view: View) {
                        view.isDrawingCacheEnabled = true
                    }

                    override fun onAnimationEnd(view: View) {
                        view.isDrawingCacheEnabled = false
                    }

                    override fun onAnimationCancel(view: View) {
                    }
                })
    }

    private fun showKeyboard() {
        val manager = context.applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.toggleSoftInput(1, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard() {
        val manager = context.applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(editWord.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    private fun getDisplaySize(): Point {
        val display = (context.applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        val point = Point()
        display.getSize(point)
        return point
    }
}
