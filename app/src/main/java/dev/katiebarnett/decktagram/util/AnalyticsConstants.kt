package dev.katiebarnett.decktagram.util

object AnalyticsConstants {
    const val PARAM_APP_VERSION_NAME = "app_version_name"
    const val PARAM_APP_VERSION_CODE = "app_version_code"
    const val PARAM_DEBUG_MODE = "debug_mode"
    const val PARAM_IMAGE_COUNT = "image_count"
    const val PARAM_DECK_COUNT = "deck_count"
    const val PARAM_CARD_COUNT = "card_count"
    const val PARAM_GAME_COUNT = "game_count"
    const val PARAM_DRAWN_CARD_COUNT = "drawn_card_count"
    const val PARAM_REMAINING_CARD_COUNT = "remaining_card_count"
    const val PARAM_SET_IMAGE_QUALITY = "set_image_quality"
    const val PARAM_SET_IMAGE_STORE_IN_GALLERY = "set_image_store_in_gallery"
}

sealed class AnalyticsScreen(val name: String)
object HomeScreen: AnalyticsScreen(name = "home")
object GameScreen: AnalyticsScreen(name = "game")
object DeckScreen: AnalyticsScreen(name = "deck")
object CameraScreen: AnalyticsScreen(name = "camera")
object SettingsScreen: AnalyticsScreen(name = "settings")
object AboutScreen: AnalyticsScreen(name = "about")
object ViewImageScreen: AnalyticsScreen(name = "view_image")

sealed class AnalyticsAction(val actionName: String)
object FeedbackMenuItem: AnalyticsAction(actionName = "open_feedback_menu_item")
data class CameraDone(val imageCount: Int): AnalyticsAction(actionName = "camera_done")
data class CameraCancel(val imageCount: Int): AnalyticsAction(actionName = "camera_cancel")
data class DeleteGame(val deckCount: Int): AnalyticsAction(actionName = "delete_game")
data class DeleteDeck(val cardCount: Int): AnalyticsAction(actionName = "delete_deck")
data class ResetDeck(val cardCount: Int): AnalyticsAction(actionName = "reset_deck")
data class ResetGame(val deckCount: Int): AnalyticsAction(actionName = "reset_reset_game")
object DrawCard: AnalyticsAction(actionName = "draw_card")
object UndoDrawCard: AnalyticsAction(actionName = "undo_draw_card")
data class AddCardsToDeck(val cardCount: Int): AnalyticsAction(actionName = "add_cards_to_deck")
data class AddDeckToGame(val deckCount: Int): AnalyticsAction(actionName = "add_deck_to_game")
data class AddGame(val gameCount: Int): AnalyticsAction(actionName = "add_game")
data class SetImageQuality(val quality: String): AnalyticsAction(actionName = "set_image_quality")
data class SetImageStoreInGallery(val storeInGallery: Boolean): AnalyticsAction(actionName = "set_image_store_in_gallery")
data class OpenAllCards(val totalCardCount: Int, val drawnCardCount: Int, val remainingCardCount: Int): AnalyticsAction(actionName = "open_all_cards")
data class OpenDrawnCards(val totalCardCount: Int, val drawnCardCount: Int, val remainingCardCount: Int): AnalyticsAction(actionName = "open_drawn_cards")
data class OpenRemainingCards(val totalCardCount: Int, val drawnCardCount: Int, val remainingCardCount: Int): AnalyticsAction(actionName = "open_remaining_cards")