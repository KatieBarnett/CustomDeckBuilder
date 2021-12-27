package dev.katiebarnett.decktagram.util

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import dev.katiebarnett.decktagram.BuildConfig
import dev.katiebarnett.decktagram.util.AnalyticsConstants.PARAM_APP_VERSION_CODE
import dev.katiebarnett.decktagram.util.AnalyticsConstants.PARAM_APP_VERSION_NAME
import dev.katiebarnett.decktagram.util.AnalyticsConstants.PARAM_CARD_COUNT
import dev.katiebarnett.decktagram.util.AnalyticsConstants.PARAM_DEBUG_MODE
import dev.katiebarnett.decktagram.util.AnalyticsConstants.PARAM_DECK_COUNT
import dev.katiebarnett.decktagram.util.AnalyticsConstants.PARAM_DRAWN_CARD_COUNT
import dev.katiebarnett.decktagram.util.AnalyticsConstants.PARAM_GAME_COUNT
import dev.katiebarnett.decktagram.util.AnalyticsConstants.PARAM_IMAGE_COUNT
import dev.katiebarnett.decktagram.util.AnalyticsConstants.PARAM_REMAINING_CARD_COUNT
import dev.katiebarnett.decktagram.util.AnalyticsConstants.PARAM_SET_IMAGE_QUALITY
import dev.katiebarnett.decktagram.util.AnalyticsConstants.PARAM_SET_IMAGE_STORE_IN_GALLERY

fun FirebaseAnalytics.logScreenView(screen: AnalyticsScreen) {
    val bundle = Bundle()
    bundle.putString(PARAM_APP_VERSION_NAME, BuildConfig.APP_VERSION_NAME)
    bundle.putString(PARAM_APP_VERSION_CODE, BuildConfig.APP_VERSION_CODE.toString())
    bundle.putString(PARAM_DEBUG_MODE, BuildConfig.DEBUG.toString())
    bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screen.name)
    logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
}

fun FirebaseAnalytics.logAction(action: AnalyticsAction) {
    val bundle = Bundle()
    bundle.putString(PARAM_APP_VERSION_NAME, BuildConfig.APP_VERSION_NAME)
    bundle.putString(PARAM_APP_VERSION_CODE, BuildConfig.APP_VERSION_CODE.toString())
    bundle.putString(PARAM_DEBUG_MODE, BuildConfig.DEBUG.toString())
    when(action) {
        is CameraDone -> {
            bundle.putInt(PARAM_IMAGE_COUNT, action.imageCount)
        }
        is CameraCancel -> {
            bundle.putInt(PARAM_IMAGE_COUNT, action.imageCount)
        }
        is DeleteDeck -> {
            bundle.putInt(PARAM_CARD_COUNT, action.cardCount)
        }
        is DeleteGame -> {
            bundle.putInt(PARAM_DECK_COUNT, action.deckCount)
        }
        is OpenAllCards -> {
            bundle.putInt(PARAM_CARD_COUNT, action.totalCardCount)
            bundle.putInt(PARAM_DRAWN_CARD_COUNT, action.drawnCardCount)
            bundle.putInt(PARAM_REMAINING_CARD_COUNT, action.remainingCardCount)
        }
        is OpenDrawnCards -> {
            bundle.putInt(PARAM_CARD_COUNT, action.totalCardCount)
            bundle.putInt(PARAM_DRAWN_CARD_COUNT, action.drawnCardCount)
            bundle.putInt(PARAM_REMAINING_CARD_COUNT, action.remainingCardCount)
        }
        is OpenRemainingCards -> {
            bundle.putInt(PARAM_CARD_COUNT, action.totalCardCount)
            bundle.putInt(PARAM_DRAWN_CARD_COUNT, action.drawnCardCount)
            bundle.putInt(PARAM_REMAINING_CARD_COUNT, action.remainingCardCount)
        }
        is SetImageQuality -> {
            bundle.putString(PARAM_SET_IMAGE_QUALITY, action.quality)
        }
        is SetImageStoreInGallery -> {
            bundle.putBoolean(PARAM_SET_IMAGE_STORE_IN_GALLERY, action.storeInGallery)
        }
        is AddCardsToDeck -> {
            bundle.putInt(PARAM_CARD_COUNT, action.cardCount)
        }
        is AddDeckToGame -> {
            bundle.putInt(PARAM_DECK_COUNT, action.deckCount)
        }
        is AddGame -> {
            bundle.putInt(PARAM_GAME_COUNT, action.gameCount)
        }
        is ResetDeck -> {
            bundle.putInt(PARAM_CARD_COUNT, action.cardCount)
        }
        DrawCard, UndoDrawCard, FeedbackMenuItem -> {
            // Do nothing
        }
    }
    logEvent(action.actionName, bundle)
}
