package dev.katiebarnett.decktagram.models

data class DeckState(
    val drawnCards: List<Card> = listOf(),
    val remainingCards: List<Card> = listOf(),
    var lastModified: Long = System.currentTimeMillis()
) {
    val needsReset: Boolean
        get() = drawnCards.isEmpty() && remainingCards.isEmpty()
    
    val totalSize: Int
        get() = drawnCards.size + remainingCards.size
}