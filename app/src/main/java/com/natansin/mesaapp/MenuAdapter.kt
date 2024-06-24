package com.natansin.mesaapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MenuAdapter(private val listener: ItemClickListener) : RecyclerView.Adapter<MenuAdapter.ViewHolder>() {

    private var itensMenu: List<ItemMenu> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_menu, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemMenu = itensMenu[position]
        holder.bind(itemMenu)
        holder.itemView.setOnClickListener {
            listener.onItemClick(itemMenu)
        }
    }

    override fun getItemCount(): Int {
        return itensMenu.size
    }

    fun update(data: List<ItemMenu>) {
        itensMenu = data
        notifyDataSetChanged()
    }

    interface ItemClickListener {
        fun onItemClick(itemMenu: ItemMenu)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        private val textViewDescription: TextView = itemView.findViewById(R.id.textViewDescription)
        private val textViewPrice: TextView = itemView.findViewById(R.id.textViewPrice)
        private val btnViewDetails: Button = itemView.findViewById(R.id.btnViewDetails)
        private val imageViewItem: ImageView = itemView.findViewById(R.id.imageViewItem) // Adicionando ImageView

        fun bind(itemMenu: ItemMenu) {
            textViewName.text = itemMenu.name
            textViewDescription.text = itemMenu.description
            textViewPrice.text = "R$ ${itemMenu.price}"

            // Carregar a imagem usando Glide
            Glide.with(itemView.context)
                .load("http://192.168.1.67:3000" + itemMenu.image)
                .into(imageViewItem)  // Atribui a URL da imagem

            // Configuração do clique no botão "Ver Detalhes"
            btnViewDetails.setOnClickListener {
                listener.onItemClick(itemMenu)
            }
        }
    }
}
