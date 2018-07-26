package com.baianat.app.moltencake

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager

        val items = ArrayList<Person>()
        val person1 = Person(1, "Ahmed", R.drawable.ahmed)
        items.add(person1)
        val person2 = Person(2, "SpGulf", R.drawable.spgulf)
        items.add(person2)



        val adapter = KotlinMoltenAdapter(R.layout.category_card,
                items,
                Person::class.java,
                intArrayOf(R.id.categoryName, R.id.categoryImageView),
                arrayOf("name", "photo")
        )

        recyclerView.adapter = adapter
    }
}
