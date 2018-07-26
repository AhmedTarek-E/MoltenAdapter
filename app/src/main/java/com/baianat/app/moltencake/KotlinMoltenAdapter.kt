package com.baianat.app.moltencake

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.baianat.app.annotations.BindTo
import com.baianat.app.annotations.ResId
import com.baianat.app.annotations.ResLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties

class KotlinMoltenAdapter<out T : Any>(@ResLayout private val layout: Int, private val items: List<T>?, modelClass: Class<T>) :
        RecyclerView.Adapter<KotlinMoltenAdapter<T>.MoltenViewHolder>() {
    //private val fields = ArrayList<Field>()
    private var resIds : IntArray = IntArray(0)
    private var names : Array<String>? = null
    private val properties = ArrayList<KProperty1<*, *>>()
    private var listener : OnItemSelectedListener<T>? = null
    private var placeholder = -1
    private var animate = true

    constructor(
            @ResLayout layout: Int,
            items: List<T>?,
            modelClass: Class<T>,
            @ResId resIds : IntArray,
            names : Array<String>
    ) : this(layout, items, modelClass) {
        this.resIds = resIds
        this.names = names
    }



    init {

        if (resIds.isEmpty()) {
            modelClass.kotlin.declaredMemberProperties
                    .filter { it.annotations.any { annotation -> annotation is BindTo } }
                    .forEach {
                        properties.add(it)
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

        if (resIds.isEmpty()) {
            for (property in properties) {
                val view = holder.viewsMap[property.name]
                bindValue(view, property.name, position)
            }
        } else {
            for (i : Int in 0..(resIds.size-1)) {
                val view = holder.viewsMap[names!![i]]
                bindValue(view, names!![i], position)
            }
        }
    }

    private fun bindValue(view : View?, propertyName: String, position: Int) {
        val model = items?.get(position)
        when (view) {
            is TextView -> {
                val value = readProperty(model!!, propertyName)
                view.text = getTextValue(value)
            }

            is ImageView -> {
                val value = readProperty(model!!, propertyName)
                val image = when (value) {
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

                Glide.with(view.context)
                        .load(image)
                        .apply(requestOptions)
                        .into(view)

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

    private fun readProperty(instance: T, propertyName: String): Any? {
        val clazz = instance.javaClass.kotlin
        @Suppress("UNCHECKED_CAST")
        return clazz.declaredMemberProperties.first {
            it.name == propertyName }.get(instance)
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
            if (resIds.isEmpty()) {
                for (property in properties) {
                    var resId = -1
                    property.annotations.filter { it is BindTo }
                            .forEach {
                                if (it is BindTo) {
                                    resId = it.resId
                                }
                            }

                    val v: View? = itemView?.findViewById(resId)
                    viewsMap[property.name] = v!!
                }
            } else {
                for (i : Int in 0..(resIds.size - 1)) {
                    val resId = resIds[i]

                    val v: View? = itemView?.findViewById(resId)
                    viewsMap[names!![i]] = v!!
                }
            }
        }

        override fun onClick(v: View?) {
            listener?.onItemSelected(items?.get(adapterPosition)!!)
        }
    }

}

