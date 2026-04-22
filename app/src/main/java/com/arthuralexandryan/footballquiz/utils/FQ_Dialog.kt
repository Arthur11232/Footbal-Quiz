package com.arthuralexandryan.footballquiz.utils

import android.app.Activity
import com.arthuralexandryan.footballquiz.models.DHM


fun openComingSoon(activity: Activity, dhm: DHM){
    DialogManager.showComingSoonDialog(activity, dhm)
}
