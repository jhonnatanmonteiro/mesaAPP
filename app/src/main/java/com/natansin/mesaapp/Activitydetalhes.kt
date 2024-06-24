package com.natansin.mesaapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.natansin.mesaapp.databinding.ActivityDetalhesBinding
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Activitydetalhes : AppCompatActivity() {

    private lateinit var itemMenu: ItemMenu
    private var quantidade: Int = 1
    private lateinit var binding: ActivityDetalhesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalhesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obter itemMenu do Intent extra
        itemMenu = intent.getSerializableExtra("item_menu") as ItemMenu

        // Configurar o Socket.io
        SocketManager.connect()

        // Configurar o botão para chamar o garçom
        val button = findViewById<FloatingActionButton>(R.id.floatingActionButton2)
        button.setOnClickListener {
            chamarGarcom(itemMenu)
        }

        // Preencher detalhes do itemMenu na UI
        binding.textViewNameDetail.text = itemMenu.name
        binding.textViewDescriptionDetail.text = itemMenu.description
        binding.textViewPriceDetail.text = "R$ ${itemMenu.price}"
        Log.d("Activitydetalhes", "URL da imagem: ${itemMenu.image}")

        // Carregar imagem usando Glide
        if (itemMenu.image.isNotEmpty()) {
            val imageUrl = "http://192.168.1.67:3000" + itemMenu.image
            Glide.with(this)
                .load(imageUrl)
                .into(binding.imageViewItem)
        } else {
            // Caso não haja URL da imagem, você pode definir uma imagem padrão ou placeholder
            binding.imageViewItem.setImageResource(R.drawable.ic_launcher_background)
        }

        // Botão para adicionar quantidade
        binding.btnAddQuantity.setOnClickListener {
            quantidade++
            binding.textViewQuantity.text = quantidade.toString()
        }

        // Botão para remover quantidade
        binding.btnRemoveQuantity.setOnClickListener {
            if (quantidade > 0) {
                quantidade--
                binding.textViewQuantity.text = quantidade.toString()
            }
        }

        // Botão para fazer pedido
        binding.btnPedir.setOnClickListener {
            fazerPedido(itemMenu, quantidade)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //SocketManager.disconnect() // Remove this line to maintain socket connection
    }
    private fun fazerPedido(itemMenu: ItemMenu, quantidade: Int) {
        val descricaoPedido = "Pedido: ${itemMenu.name}, Quantidade: $quantidade"
        val pedido = Pedido("2", descricaoPedido)

        RetrofitInstance.menuApiService.criarPedido(pedido).enqueue(object : Callback<Pedido> {
            override fun onResponse(call: Call<Pedido>, response: Response<Pedido>) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(applicationContext, "Pedido realizado com sucesso", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(applicationContext, "Erro ao fazer pedido: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<Pedido>, t: Throwable) {
                runOnUiThread {
                    Toast.makeText(applicationContext, "Falha na comunicação com o servidor", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
    private fun chamarGarcom(itemMenu: ItemMenu) {
        val mensagem = "Garçom chamado na MESA 2"

        if (SocketManager.socket.connected()) {
            SocketManager.socket.emit("chamar-garcom", mensagem)
            Toast.makeText(applicationContext, "Garçom chamado!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(applicationContext, "Erro ao conectar ao servidor.", Toast.LENGTH_SHORT).show()
        }
    }
}
