package com.natansin.mesaapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), MenuAdapter.ItemClickListener {

    private lateinit var menuAdapter: MenuAdapter
    private lateinit var recyclerViewMenu: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configurar RecyclerView
        recyclerViewMenu = findViewById(R.id.recyclerview_menu)
        menuAdapter = MenuAdapter(this)
        recyclerViewMenu.layoutManager = LinearLayoutManager(this)
        recyclerViewMenu.adapter = menuAdapter

        // Configurar o FloatingActionButton para chamar o garçom
        val fabChamarGarcom = findViewById<FloatingActionButton>(R.id.floatingActionButton)
        fabChamarGarcom.setOnClickListener {
            chamarGarcom()
        }

        // Obter itens do menu da API ao criar a atividade
        obterItensMenu()
        SocketManager.connect()



    }
    override fun onResume() {
        super.onResume()
        SocketManager.connect()
    }

    override fun onPause() {
        super.onPause()
    }


    private fun obterItensMenu() {
        RetrofitInstance.menuApiService.obterItensMenu().enqueue(object : Callback<List<ItemMenu>> {
            override fun onResponse(call: Call<List<ItemMenu>>, response: Response<List<ItemMenu>>) {
                if (response.isSuccessful) {
                    val itensMenu = response.body()
                    menuAdapter.update(itensMenu ?: emptyList())
                } else {
                    Toast.makeText(applicationContext, "Erro ao obter itens do menu", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ItemMenu>>, t: Throwable) {
                Log.e("MainActivity", "Falha na comunicação com o servidor: ${t.message}")
                Toast.makeText(applicationContext, "Falha na comunicação com o servidor: ${t.message}", Toast.LENGTH_SHORT).show()
            }

        })
    }


    private fun chamarGarcom() {
        val mensagem = "Garçom chamado na MESA 2"

        if (SocketManager.socket.connected()) {
            SocketManager.socket.emit("chamar-garcom", mensagem)
            Toast.makeText(applicationContext, "Garçom chamado!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(applicationContext, "Erro ao conectar ao servidor.", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onItemClick(itemMenu: ItemMenu) {
        // Ao clicar em um item do menu, abrir Activity de detalhes
        val intent = Intent(this, Activitydetalhes::class.java)
        intent.putExtra("item_menu", itemMenu)
        startActivity(intent)
    }
}
