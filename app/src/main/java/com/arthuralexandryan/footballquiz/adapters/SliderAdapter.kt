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
import com.arthuralexandryan.footballquiz.db_app.Score.FQ_Scores
import com.arthuralexandryan.footballquiz.db_app.Top5.*
import com.arthuralexandryan.footballquiz.db_app.UFA.*
import com.arthuralexandryan.footballquiz.db_app.World.DB_World_Championship
import com.arthuralexandryan.footballquiz.interfaces.ChooseGame
import com.arthuralexandryan.footballquiz.models.GameObjectScores
import com.arthuralexandryan.footballquiz.models.GameObjectSerializable
import com.arthuralexandryan.footballquiz.models.placeDescription.DescriptionModel
import com.arthuralexandryan.footballquiz.utils.CategoryType
import io.realm.Realm

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
            chooseGame.onChoose(getQuestions(slideText[position]), getPlaceScores(slideText[position]), position)
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

    fun getPlaceScores(s: String): GameObjectScores {
        val scores = GameObjectScores()
        val fqScores = Realm.getDefaultInstance().where(FQ_Scores::class.java).findFirst() ?: return scores
        when (s) {
            "fransia" -> {
                scores.place_score = fqScores.top5Scores.france_score
                scores.place_score_answer = fqScores.top5Scores.france_answered
            }
            "germania" -> {
                scores.place_score = fqScores.top5Scores.germany_score
                scores.place_score_answer = fqScores.top5Scores.germany_answered
            }
            "italia" -> {
                scores.place_score = fqScores.top5Scores.italy_score
                scores.place_score_answer = fqScores.top5Scores.italy_answered
            }
            "anglia" -> {
                scores.place_score = fqScores.top5Scores.england_score
                scores.place_score_answer = fqScores.top5Scores.england_answered
            }
            "ispania" -> {
                scores.place_score = fqScores.top5Scores.spain_score
                scores.place_score_answer = fqScores.top5Scores.spain_answered
            }
            "supercup" -> {
                scores.place_score = fqScores.ufaScores.superCup_score
                scores.place_score_answer = fqScores.ufaScores.superCup_answered
            }
            "evropaliga" -> {
                scores.place_score = fqScores.ufaScores.europaLeague_score
                scores.place_score_answer = fqScores.ufaScores.europaLeague_answered
            }
            "ligachemp" -> {
                scores.place_score = fqScores.ufaScores.championsLeague_score
                scores.place_score_answer = fqScores.ufaScores.championsLeague_answered
            }
            "kubki" -> {
                scores.place_score = fqScores.ufaScores.europaChampionship_score
                scores.place_score_answer = fqScores.ufaScores.europaChampionship_answered
            }
            "kubok_mira" -> {
                scores.place_score = fqScores.world
                scores.place_score_answer = fqScores.world_answered
            }
        }
        return scores
    }


    private fun getQuestions(s: String): List<GameObjectSerializable> {
        val realm = Realm.getDefaultInstance()
        return when (s) {
            "fransia" -> realm.where(DB_France::class.java).findAll().map { item ->
                GameObjectSerializable(item.question, item.answer_A, item.answer_B, item.answer_C, item.answer_D, item.right_answer, item.isAnswered, "France", CategoryType.TOP5!!)
            }
            "germania" -> realm.where(DB_Germany::class.java).findAll().map { item ->
                GameObjectSerializable(item.question, item.answer_A, item.answer_B, item.answer_C, item.answer_D, item.right_answer, item.isAnswered, "Germany", CategoryType.TOP5!!)
            }
            "italia" -> realm.where(DB_Italy::class.java).findAll().map { item ->
                GameObjectSerializable(item.question, item.answer_A, item.answer_B, item.answer_C, item.answer_D, item.right_answer, item.isAnswered, "Italy", CategoryType.TOP5!!)
            }
            "anglia" -> realm.where(DB_England::class.java).findAll().map { item ->
                GameObjectSerializable(item.question, item.answer_A, item.answer_B, item.answer_C, item.answer_D, item.right_answer, item.isAnswered, "England", CategoryType.TOP5!!)
            }
            "ispania" -> realm.where(DB_Spain::class.java).findAll().map { item ->
                GameObjectSerializable(item.question, item.answer_A, item.answer_B, item.answer_C, item.answer_D, item.right_answer, item.isAnswered, "Spain", CategoryType.TOP5!!)
            }
            "supercup" -> realm.where(DB_Super_Cup::class.java).findAll().map { item ->
                GameObjectSerializable(item.question, item.answer_A, item.answer_B, item.answer_C, item.answer_D, item.right_answer, item.isAnswered, "Super Cup", CategoryType.UEFA!!)
            }
            "evropaliga" -> realm.where(DB_Europa_League::class.java).findAll().map { item ->
                GameObjectSerializable(item.question, item.answer_A, item.answer_B, item.answer_C, item.answer_D, item.right_answer, item.isAnswered, "European League", CategoryType.UEFA!!)
            }
            "ligachemp" -> realm.where(DB_Champions_League::class.java).findAll().map { item ->
                GameObjectSerializable(item.question, item.answer_A, item.answer_B, item.answer_C, item.answer_D, item.right_answer, item.isAnswered, "Champions League", CategoryType.UEFA!!)
            }
            "kubki" -> realm.where(DB_Europ_Championship::class.java).findAll().map { item ->
                GameObjectSerializable(item.question, item.answer_A, item.answer_B, item.answer_C, item.answer_D, item.right_answer, item.isAnswered, "Europe Championship", CategoryType.UEFA!!)
            }
            "kubok_mira" -> realm.where(DB_World_Championship::class.java).findAll().map { item ->
                GameObjectSerializable(item.question, item.answer_A, item.answer_B, item.answer_C, item.answer_D, item.right_answer, item.isAnswered, "World Cup", CategoryType.WORLD_CUP!!)
            }
            else -> emptyList()
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as RelativeLayout)
    }
}
