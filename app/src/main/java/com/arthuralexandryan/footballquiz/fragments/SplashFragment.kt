package com.arthuralexandryan.footballquiz.fragments

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.Point
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.arthuralexandryan.footballquiz.FQ_Application
import com.arthuralexandryan.footballquiz.R
import com.arthuralexandryan.footballquiz.constants.Constant.INIT_DB
import com.arthuralexandryan.footballquiz.databinding.SplashBinding
import com.arthuralexandryan.footballquiz.db_app.DB_Helper
import com.arthuralexandryan.footballquiz.utils.Prefer

class SplashFragment : Fragment() {

    private var _binding: SplashBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var dbHelper: DB_Helper
    private var mWidth: Float = 0f
    private var mHeight: Float = 0f
    private var loc: IntArray? = null
    private var xList: FloatArray? = null
    private var yList: FloatArray? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        
        val display = requireActivity().windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        mWidth = size.x.toFloat()
        mHeight = size.y.toFloat()
    }

    override fun onStart() {
        super.onStart()
        calculateAnimationPoints()

        object : CountDownTimer(300, 300) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                if (isAdded) {
                    loc = IntArray(2)
                    binding.loadingBall.getLocationOnScreen(loc!!)
                    animation(xList!!, yList!!)
                }
            }
        }.start()
    }

    private fun calculateAnimationPoints() {
        val x = floatArrayOf( 671.0f, 703.04224f, 703.04224f, 713.99744f, 722.97833f, 731.8826f, 741.6804f, 751.33356f, 761.9798f, 769.976f, 777.91364f, 784.022f, 790.084f, 796.0881f, 801.9059f, 807.9636f, 813.9549f, 818.9701f, 825.0636f, 831.0313f, 837.05194f, 842.0492f, 846.0387f, 850.00867f, 854.01025f, 857.988f, 860.9008f, 862.9704f, 864.9584f, 866.9577f, 868.94794f, 870.95166f, 873.0002f, 874.8553f, 876.84265f, 877.8643f, 878.0f, 878.0f, 878.94305f, 879.0f, 879.0f, 879.0f, 879.0f, 879.0f, 879.0f, 879.0f, 879.0f, 877.041f, 875.02356f, 873.05365f, 871.06726f, 869.01556f, 867.17163f, 865.1661f, 861.3292f, 857.0129f, 853.0746f, 849.1846f, 845.15f, 841.0903f, 837.1572f, 833.20593f, 829.3843f, 825.3146f, 821.3708f, 817.14996f, 811.2475f, 805.1305f, 799.2125f, 793.2326f, 785.36365f, 778.50116f, 771.3594f, 764.4555f, 757.16223f, 749.1863f, 741.1182f, 732.1648f, 722.12836f, 712.2374f, 702.5456f, 692.5637f, 682.7426f, 672.85596f, 662.2359f, 652.118f, 640.1479f, 630.0911f, 619.0948f, 607.184f, 595.6085f, 583.8772f, 571.003f, 558.1897f, 546.24994f, 534.25183f, 524.19745f, 515.18207f, 507.208f, 499.43582f, 492.41983f, 487.15396f, 484.16843f, 482.03616f, 480.0f, 480.0f)
        val y = floatArrayOf( 1210.5f, 1207.0f, 1200.9639f, 1190.0026f, 1180.0271f, 1170.1467f, 1157.4474f, 1143.9331f, 1130.0243f, 1119.03f, 1109.108f, 1100.9707f, 1091.86f, 1081.8533f, 1072.1569f, 1062.0607f, 1053.0602f, 1044.0598f, 1033.894f, 1023.94775f, 1012.8961f, 999.8278f, 987.884f, 977.97833f, 967.97437f, 959.024f, 949.4958f, 939.148f, 929.2081f, 920.1692f, 912.2083f, 904.19324f, 897.9994f, 891.4341f, 883.62946f, 876.4071f, 868.2424f, 861.1864f, 854.17084f, 847.1819f, 840.2187f, 834.172f, 828.4772f, 822.4032f, 816.42413f, 810.4832f, 804.15875f, 798.12305f, 792.07074f, 786.16095f, 781.13446f, 776.0311f, 771.5149f, 767.33215f, 762.4938f, 758.0129f, 752.1119f, 747.2769f, 742.22504f, 737.13544f, 732.23584f, 727.30896f, 723.3843f, 718.47186f, 714.3708f, 710.14996f, 706.165f, 702.087f, 698.14166f, 694.1551f, 690.1818f, 686.2506f, 683.1198f, 680.15186f, 677.10815f, 675.0466f, 671.0591f, 668.03296f, 664.05133f, 661.0475f, 658.2182f, 656.11273f, 654.1485f, 652.1712f, 650.0472f, 648.0236f, 645.0493f, 643.01825f, 641.019f, 639.03064f, 637.10144f, 635.1462f, 633.1672f, 631.0316f, 629.0416f, 627.042f, 625.0395f, 623.04553f, 622.052f, 622.0f, 621.0f, 621.0f, 621.0f, 621.0f, 620.0f, 620.0f)

        xList = FloatArray(x.size)
        yList = FloatArray(y.size)

        val heightScale = if (mHeight > 1920) {
            mHeight * 1.114f / 1920
        } else {
            mHeight * 0.914f / 1920
        }
        val widthScale = (mWidth * 0.914f) / 1080

        for (i in x.indices) {
            x[i] *= widthScale
        }
        for (i in y.indices) {
            y[i] *= heightScale
        }
        for (i in x.indices) {
            xList!![i] = x[i] - x[0]
        }
        for (i in y.indices) {
            y[i] = y[i] - y[y.size - 1]
            y[i] *= -1f
        }
        for (i in y.size - 1 downTo 0) {
            yList!![y.size - 1 - i] = y[i]
        }
    }

    private fun initDBs() {
        if (Prefer.getBooleanPreference(requireContext(), INIT_DB, true)) {
            Prefer.setBooleanPreference(requireContext(), INIT_DB, false)
            Log.e("DB", "Initialization DB")
            FQ_Application.getInstance().setDB(dbHelper, true)
        }
    }

    private fun animation(x: FloatArray, y: FloatArray) {
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(
            ObjectAnimator.ofFloat(binding.loadingBall, "translationY", *y),
            ObjectAnimator.ofFloat(binding.loadingBall, "translationX", *x),
            ObjectAnimator.ofFloat(binding.loadingBall, "scaleX", 1f, 0.15f),
            ObjectAnimator.ofFloat(binding.loadingBall, "scaleY", 1f, 0.15f),
            ObjectAnimator.ofFloat(binding.loadingBall, "rotation", 0f, -1800f)
        )

        animatorSet.duration = 5000
        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                initDBs()
            }

            override fun onAnimationEnd(animation: Animator) {
                if (isAdded) {
                    findNavController().navigate(R.id.action_splash_to_start)
                }
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        animatorSet.start()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        val versionName = try {
            val pInfo = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
            pInfo.versionName
        } catch (e: Exception) {
            "1.0"
        }
        binding.textVersion.text = "${getString(R.string.version)} $versionName"
        dbHelper = DB_Helper()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
