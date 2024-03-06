package com.example.dicodingrecyclerview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dicodingrecyclerview.adapter.ListPahlawanAdapter
import com.example.dicodingrecyclerview.models.Pahlawan

class MainActivity : AppCompatActivity() {

    private lateinit var rvPahlawan: RecyclerView
    private val list = ArrayList<Pahlawan>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvPahlawan = findViewById(R.id.rv_pahlawan)
        rvPahlawan.setHasFixedSize(true)

        list.addAll(getListHeroes())

        rvPahlawan.layoutManager = LinearLayoutManager(this)
        val listPahlawanAdapter = ListPahlawanAdapter(list)
        rvPahlawan.adapter = listPahlawanAdapter

    }



    private fun getListHeroes(): ArrayList<Pahlawan> {
        val dataName = resources.getStringArray(R.array.data_name)
        val dataDescription = resources.getStringArray(R.array.data_description)
        val dataPhoto = resources.obtainTypedArray(R.array.data_photo)
        val listHero = ArrayList<Pahlawan>()
        for (i in dataName.indices) {
            val hero = Pahlawan(dataName[i], dataDescription[i], dataPhoto.getResourceId(i, -1))
            listHero.add(hero)
        }
        return listHero

    }
}