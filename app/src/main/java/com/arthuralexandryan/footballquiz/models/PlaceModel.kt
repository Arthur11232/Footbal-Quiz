package com.arthuralexandryan.footballquiz.models

import android.util.Log
import android.view.View
import android.widget.TextView
import com.arthuralexandryan.footballquiz.R
import com.arthuralexandryan.footballquiz.views.CircleImageView

class PlaceModel(builder: PlaceModelBuilder) {
    private val view: View
    private val imageRes: Int
    private val vs1: String?
    private val vs2: String?
    private val listener: PlaceModelInterface?

    init {
        this.view = builder.view
        this.imageRes = builder.imageRes
        this.vs1 = builder.vs1
        this.vs2 = builder.vs2
        this.listener = builder.placeModel
    }

    class PlaceModelBuilder(val view: View) {
        internal var imageRes: Int = 0
        internal var vs1: String? = null
        internal var vs2: String? = null
        internal var placeModel: PlaceModelInterface? = null

        fun setImage(imageRes: Int): PlaceModelBuilder {
            this.imageRes = imageRes
            return this
        }

        fun setVersusFirst(vs1: String): PlaceModelBuilder {
            this.vs1 = vs1
            return this
        }

        fun setVersusSecond(vs2: String): PlaceModelBuilder {
            this.vs2 = vs2
            return this
        }

        fun setListener(listener: PlaceModelInterface): PlaceModelBuilder{
            this.placeModel = listener
            return this
        }

        fun build(): PlaceModel {
            initView()
            return PlaceModel(this)
        }

        private fun initView() {
            val image = view.findViewById<CircleImageView>(R.id.vs_item)
            val text1 = view.findViewById<TextView>(R.id.vs_text1)
            val text2 = view.findViewById<TextView>(R.id.vs_text2)

            imageRes.let {
                image.setImageResource(it)
            }

            vs1.let {
                text1.text = it
                text1.visibility = View.VISIBLE
            }

            vs2.let {
                text2.text = it
                text2.visibility = View.VISIBLE
            }

            placeModel.let {
                image.setOnClickListener {
                    Log.e(javaClass.name, "barev axpers")
                    placeModel?.onClickPlace()
                }
            }

            view.invalidate()
        }
    }
}
