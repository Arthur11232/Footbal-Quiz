package com.arthuralexandryan.footballquiz.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.arthuralexandryan.footballquiz.R
import com.arthuralexandryan.footballquiz.adapters.SliderAdapter
import com.arthuralexandryan.footballquiz.databinding.ActivityCategoryPageBinding
import com.arthuralexandryan.footballquiz.databinding.LayoutVersusBinding
import com.arthuralexandryan.footballquiz.databinding.ScoreboardBinding
import com.arthuralexandryan.footballquiz.db_app.DB_Helper
import com.arthuralexandryan.footballquiz.db_app.Score.FQ_Scores
import com.arthuralexandryan.footballquiz.db_app.Versus.DB_VS_Ronaldo_Messi
import com.arthuralexandryan.footballquiz.interfaces.ChooseGame
import com.arthuralexandryan.footballquiz.models.GameObjectScores
import com.arthuralexandryan.footballquiz.models.GameObjectSerializable
import com.arthuralexandryan.footballquiz.models.ScoreboardModel
import com.arthuralexandryan.footballquiz.models.TarberakArrays
import com.arthuralexandryan.footballquiz.models.primeryColorModel.ColorModel
import com.arthuralexandryan.footballquiz.utils.CategoryType
import com.arthuralexandryan.footballquiz.utils.Constants
import io.realm.Realm

class CategoryPageFragment : Fragment(), ChooseGame {

    private var _binding: ActivityCategoryPageBinding? = null
    private val binding get() = _binding!!

    private var _versusBinding: LayoutVersusBinding? = null
    private val versusBinding get() = _versusBinding!!

    private lateinit var dbHelper: DB_Helper
    private lateinit var arrays: TarberakArrays

    private var slideImage: IntArray = intArrayOf()
    private var slideBg: IntArray = intArrayOf()
    private var slideArrowsLeft: IntArray = intArrayOf()
    private var slideArrowsRight: IntArray = intArrayOf()
    private var slideText: Array<String> = emptyArray()
    private var titles: List<String> = emptyList()
    private var tab: Int = 0

    private var categoryType: String? = null
    private var categoryName: String = ""
    private var itemScoreText: GameObjectScores? = null

    private var top5Titles: List<String> = emptyList()
    private var ufaTitles: List<String> = emptyList()
    private var worldTitles: List<String> = emptyList()
    private var scoreBoardScores: List<ScoreboardModel> = emptyList()
    private var darkPrimaryColorList: List<ColorModel> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        categoryType = arguments?.getString("gameScore")

        return if (categoryType.equals("vsRM", ignoreCase = true)) {
            _versusBinding = LayoutVersusBinding.inflate(inflater, container, false)
            versusBinding.root
        } else {
            _binding = ActivityCategoryPageBinding.inflate(inflater, container, false)
            binding.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = DB_Helper()
        arrays = TarberakArrays

        initTitleList()
        processArguments(arguments)
    }

    override fun onStart() {
        super.onStart()
        categoryType?.let { refreshScores() }
    }

    private fun refreshScores() {
        if (categoryType.equals("vsRM", ignoreCase = true)) {
            scoreBoardScores = listOf(dbHelper.ronMessiScoreBoard)
            _versusBinding?.let { updateScoreboard(0, it.scoreboard) }
        } else {
            scoreBoardScores = when (categoryType) {
                "top5" -> dbHelper.top5Scores
                "uefa"  -> dbHelper.getUFAScores()
                "world" -> dbHelper.worldScores
                else    -> emptyList()
            }
            _binding?.let { inflateScores(tab) }
        }
    }

    private fun processArguments(bundle: Bundle?) {
        if (bundle == null) return
        categoryType = bundle.getString("gameScore")

        if (categoryType.equals("vsRM", ignoreCase = true)) {
            setupVersusMode()
        } else {
            setupCategoryMode()
        }
    }

    private fun setupCategoryMode() {
        _binding ?: return

        binding.onBack.setOnClickListener { findNavController().navigateUp() }

        when (categoryType) {
            "top5" -> {
                categoryName = getString(R.string.top_5_tournament)
                slideImage = arrays.top5_image
                slideText = arrays.top5_text
                slideBg = arrays.top5_bgs
                titles = top5Titles
                slideArrowsLeft = arrays.leftPlaceTop5
                slideArrowsRight = arrays.rightPlaceTop5
                binding.next.setBackgroundResource(slideArrowsRight[0])
                scoreBoardScores = dbHelper.top5Scores
                initSlider(true)
                darkPrimaryColorList = TarberakArrays.getPrimaryColorTop5()
            }
            "uefa" -> {
                categoryName = getString(R.string.uefa)
                slideImage = arrays.ufa_image
                slideText = arrays.ufa_text
                slideBg = arrays.ufa_bgs
                titles = ufaTitles
                slideArrowsLeft = arrays.leftPlaceUFA
                slideArrowsRight = arrays.rightPlaceUFA
                binding.next.setBackgroundResource(slideArrowsRight[0])
                scoreBoardScores = dbHelper.getUFAScores()
                initSlider(false)
                darkPrimaryColorList = TarberakArrays.getPrimaryColorUEFA()
            }
            "world" -> {
                categoryName = getString(R.string.world_championship)
                slideImage = arrays.world_image
                slideBg = arrays.world_bg
                slideText = arrays.world_text
                titles = worldTitles
                scoreBoardScores = dbHelper.worldScores
                initSlider(false)
                darkPrimaryColorList = listOf(TarberakArrays.getPrimaryColorWorld())
            }
        }

        initPrimaryColor(0)
        updateWorkArea()
        setupListeners()
    }

    private fun setupVersusMode() {
        _versusBinding ?: return
        categoryName = getString(R.string.versus)
        scoreBoardScores = listOf(dbHelper.ronMessiScoreBoard)

        versusBinding.vs1.vsItem.setImageResource(arrays.rm_image[0])
        versusBinding.vs2.vsItem.setImageResource(arrays.rm_image[1])

        versusBinding.onBack.setOnClickListener { findNavController().navigateUp() }

        val startVersus = View.OnClickListener { onChoose(getVersusQuestions(), getItemScore("vsRM"), 0) }
        versusBinding.vs1.root.setOnClickListener(startVersus)
        versusBinding.vs2.root.setOnClickListener(startVersus)

        updateScoreboard(0, versusBinding.scoreboard)
    }

    private fun initSlider(isTop5: Boolean) {
        val sliderAdapter = SliderAdapter(
            requireContext(), slideImage, slideText, slideBg,
            arrays.getTop5Descriptions(requireContext()), this, isTop5
        )
        binding.slideViewPager.adapter = sliderAdapter
        tab = binding.slideViewPager.currentItem
    }

    private fun setupListeners() {
        binding.slideViewPager.addOnPageChangeListener(object : androidx.viewpager.widget.ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                tab = position
                if (slideArrowsRight.isNotEmpty()) binding.next.setBackgroundResource(slideArrowsRight[position])
                if (slideArrowsLeft.isNotEmpty()) binding.beck.setBackgroundResource(slideArrowsLeft[position])
                inflateScores(position)
                initPrimaryColor(position)
            }
            override fun onPageScrollStateChanged(state: Int) {}
        })

        binding.beck.setOnClickListener {
            if (tab > 0) {
                tab--
                binding.slideViewPager.currentItem = tab
            }
        }

        binding.next.setOnClickListener {
            if (tab < titles.size - 1) {
                tab++
                binding.slideViewPager.currentItem = tab
            }
        }
    }

    private fun updateWorkArea() {
        itemScoreText = getItemScore(categoryType ?: return)
        inflateScores(0)
    }

    private fun inflateScores(position: Int) {
        _binding?.let { updateScoreboard(position, it.scoreboard) }
    }

    private fun updateScoreboard(position: Int, scoreboard: ScoreboardBinding) {
        if (scoreBoardScores.isEmpty()) return
        val model = scoreBoardScores[position]
        scoreboard.placeQuestion.text = model.placeScore.toString()
        scoreboard.placeAnswer.text = model.placeAnswered.toString()
        scoreboard.categoryAnswers.text = model.categoryAnswered.toString()
        scoreboard.categoryScore.text = model.categoryScore.toString()
        scoreboard.placeNameScores.text = categoryName
    }

    private fun initPrimaryColor(tab: Int) {
        if (darkPrimaryColorList.isEmpty() || tab >= darkPrimaryColorList.size) return
        val color: ColorModel = darkPrimaryColorList[tab]
        activity?.window?.statusBarColor = Color.argb(color.alfa, color.red, color.green, color.blue)
    }

    private fun initTitleList() {
        top5Titles = resources.getStringArray(R.array.Top5Titles).toList()
        ufaTitles = resources.getStringArray(R.array.UFATitles).toList()
        worldTitles = resources.getStringArray(R.array.WorldChampionship).toList()
    }

    private fun getItemScore(gameScore: String): GameObjectScores {
        val score = GameObjectScores()
        val realm = Realm.getDefaultInstance()
        val fq = realm.where(FQ_Scores::class.java).findFirst() ?: return score

        when (gameScore) {
            "top5" -> {
                score.place_score = fq.top5
                score.place_score_answer = fq.top5_answered
                score.category_score = Constants.Top5
            }
            "uefa" -> {
                score.place_score = fq.ufa
                score.place_score_answer = fq.ufa_answered
                score.category_score = Constants.UFA
            }
            "world" -> {
                score.place_score = fq.world
                score.place_score_answer = fq.world_answered
                score.category_score = Constants.World
            }
            "vsRM" -> {
                score.place_score = fq.vsRM
                score.place_score_answer = fq.vsRM_answered
                score.category_score = Constants.VSRM
            }
        }
        return score
    }

    private fun getVersusQuestions(): List<GameObjectSerializable> {
        val results = Realm.getDefaultInstance().where(DB_VS_Ronaldo_Messi::class.java).findAll()
        return results.map { item ->
            GameObjectSerializable(
                item.question, item.answer_A, item.answer_B,
                item.answer_C, item.answer_D, item.right_answer,
                item.isAnswered, "Versus R_M", CategoryType.RON_MES
            )
        }
    }

    override fun onChoose(question: List<GameObjectSerializable>, placeScores: GameObjectScores, position: Int) {
        val args = Bundle().apply {
            putParcelableArray("harcer", question.toTypedArray())
            putParcelable("achokner", placeScores)
            putParcelable("cat_score", getItemScore(categoryType ?: return))
            putString("title", categoryName)
            putString("category", categoryType)
        }
        findNavController().navigate(R.id.action_category_to_play, args)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _versusBinding = null
    }
}
