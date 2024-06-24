package com.natansin.mesaapp

import io.socket.client.IO
import io.socket.client.Socket
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.io.Serializable
import java.net.URISyntaxException

interface MenuApiService {

    // Rota para criar um novo item no menu
    @POST("api/items")
    fun criarItemMenu(@Body item: NovoItemMenu): Call<ItemMenu>

    // Rota para obter todos os itens do menu
    @GET("api/items")
    fun obterItensMenu(): Call<List<ItemMenu>>

    // Rota para deletar um item do menu pelo ID
    @DELETE("api/items/{id}")
    fun deletarItemMenu(@Path("id") id: Long): Call<Void>

    // Rota para criar um novo pedido
    @POST("api/pedidos")
    fun criarPedido(@Body pedido: Pedido): Call<Pedido>
}

data class Pedido(
    val mesa: String,        // Mesa onde o pedido está sendo feito
    val descricao: String,   // Descrição do pedido
)

data class NovoItemMenu(
    val name: String,
    val price: Double,
    val description: String,
    val image: String       // URL da imagem
)

data class ItemMenu(
    val id: Long,
    val name: String,
    val price: Double,
    val description: String,
    val image: String,
) : Serializable

object RetrofitInstance {

    private const val BASE_URL = "http://192.168.1.67:3000/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val menuApiService: MenuApiService by lazy {
        retrofit.create(MenuApiService::class.java)
    }
}

object SocketManager {
    private const val SERVER_URL = "http://192.168.1.67:3000"  // Corrigido para ":3000"

    lateinit var socket: Socket

    fun connect() {
        try {
            val opts = IO.Options()
            opts.forceNew = true
            socket = IO.socket(SERVER_URL, opts)
            socket.connect()
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    fun disconnect() {
        socket.disconnect()
    }
}
