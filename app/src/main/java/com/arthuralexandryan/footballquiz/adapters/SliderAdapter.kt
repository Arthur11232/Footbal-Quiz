package com.arthuralexandryan.footballquiz.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.arthuralexandryan.footballquiz.R
import com.arthuralexandryan.footballquiz.interfaces.ChooseGame
import com.arthuralexandryan.footballquiz.models.placeDescription.DescriptionModel

class SliderAdapter(
    private val context: Context,
    private val slideImage: IntArray,
    private val slideText: Array<String>,
    private val slideBg: IntArray,
    private val top5Descriptions: List<DescriptionModel>,
    private val chooseGame: ChooseGame,
    private val isTop5: Boolean
) : PagerAdapter() {

    var position: Int = 0
        private set

    override fun getCount(): Int = slideImage.size

    override fun isViewFromObject(view: View, obj: Any): Boolean = view == obj

    override fun getPageTitle(position: Int): CharSequence? = super.getPageTitle(position)

    @SuppressLint("StringFormatMatches")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.slayd_layout, container, false)

        val slideImageView = view.findViewById<ImageView>(R.id.place_flag)
        val placeBg = view.findViewById<RelativeLayout>(R.id.place)

        slideImageView.setImageResource(slideImage[position])
        placeBg.setBackgroundResource(slideBg[position])
        this.position = position

        if (isTop5) {
            initDescriptionViews(view, position)
        }

        slideImageView.setOnClickListener {
            chooseGame.onChoose(slideText[position], position)
        }

        container.addView(view)
        return view
    }

    private fun initDescriptionViews(view: View, position: Int) {
        val desc = top5Descriptions[position]
        view.findViewById<TextView>(R.id.text_place_position).apply {
            text = desc.name
            visibility = View.VISIBLE
        }
        view.findViewById<TextView>(R.id.text_place_confederation).apply {
            text = desc.conFed
            visibility = View.VISIBLE
        }
        view.findViewById<TextView>(R.id.text_place_federation).apply {
            text = desc.federation
            visibility = View.VISIBLE
        }
        view.findViewById<TextView>(R.id.text_place_league).apply {
            text = desc.league
            visibility = View.VISIBLE
        }
        view.findViewById<TextView>(R.id.text_place_date).apply {
            text = desc.date
            visibility = View.VISIBLE
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as RelativeLayout)
    }
}
