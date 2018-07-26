package com.baianat.app.moltencake

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.baianat.app.annotations.BindTo
import com.baianat.app.annotations.ResLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.lang.reflect.Field

class MoltenAdapter<out T : Any>(@ResLayout private val layout: Int, private val items: List<T>?, modelClass: Class<T>) :
        RecyclerView.Adapter<MoltenAdapter<T>.MoltenViewHolder>() {
    private val fields = ArrayList<Field>()
    private var listener : OnItemSelectedListener<T>? = null
    private var placeholder = -1
    private var animate = true

    init {
        for (field in modelClass.fields) {
            for (anno in field.annotations) {
                if (anno is BindTo) {
                    fields.add(field)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoltenViewHolder {
        return MoltenViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(layout, parent, false)
        )
    }

    override fun getItemCount(): Int {
       return items?.size ?: 0
    }

    override fun onBindViewHolder(holder: MoltenViewHolder, position: Int) {

        val model = items?.get(position)

        for (field in fields) {
            val view = holder.viewsMap[field.name]
            when (view) {
                is TextView -> {

                    view.text = getTextValue(field.get(model))
                }

                is ImageView -> {
                    val value = field.get(model)
                    val image = when(value) {
                        is String -> {
                             value
                        }

                        is Int -> {
                            value
                        }

                        else -> {
                            -1
                        }
                    }

                    var requestOptions = if (placeholder != -1) {
                        RequestOptions.placeholderOf(placeholder)
                    } else {
                        RequestOptions()
                    }

                    if (!animate) {
                        requestOptions = requestOptions.dontAnimate()
                    }

                    Glide.with(holder.itemView.context)
                            .load(image)
                            .apply(requestOptions)
                            .into(view)

                }
            }
        }
    }

    private fun getTextValue(value: Any?) : String {
        return when(value) {
            is String -> {
                value
            }
            is Int -> {
                value.toString()
            }
            is Float -> {
                value.toString()
            }
            is Long -> {
                value.toString()
            }
            is Double -> {
                value.toString()
            }
            else -> {
                ""
            }
        }
    }

    fun setOnItemSelectedListener(listener: OnItemSelectedListener<T>) {
        this.listener = listener
    }

    fun removeOnItemSelectedListener() {
        listener = null
    }

    fun dontAnimateGlide() {
        animate = false
    }

    fun animateGlide() {
        animate = true
    }

    fun setPlaceholderForImageView(resId: Int) {
        placeholder = resId
    }

    inner class MoltenViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView), View.OnClickListener{

        val viewsMap = HashMap<String, View>()

        init {
            for (field in fields ) {
                val resId = field.getAnnotation(BindTo::class.java).resId
                val v : View? = itemView?.findViewById(resId)
                viewsMap[field.name] = v!!
            }
        }

        override fun onClick(v: View?) {
           listener?.onItemSelected(items?.get(adapterPosition)!!)
        }
    }

}

