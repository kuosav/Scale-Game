package game

import scala.collection.mutable.Buffer
import scala.util.Random


class Game(val players: List[Player], val numberOfRounds: Int) {
  
  private var scalenames = Vector('B','C','D','E','F','G','H','I','J','K','L','M')
  var currentRound = 1
  val scales = Buffer[Scale](new Scale('A', numberOfRounds, None, None)) //radius is numberOfRounds just for fun and also big enough for a bigger game
  
  def turn(i: String, currentPlayer: Player): Boolean = {
    val input = i.toUpperCase()
    if (input.length==3 && input(0).isLetter && (input(1)=='L' || input(1)=='R') && input(2).isDigit && scales.map(s => s.name).contains(input(0)) && scales.filter(s => s.name==input(0)).head.radius>=input(2).toString().toInt) {
      scales.filter(s => s.name==input(0)).head.addWeight(new Weight(input, currentPlayer))
    } else {
      false
    }
  }
  
  def newRound(): Unit = {
    if (!this.isOver) {
      val selection = this.scales.filter(s => s.scaleSpots.nonEmpty)
      //val onWhichScale: Scale = selection(Random.nextInt(selection.size))
      val onWhichScale: Scale = selection.last
      val spot = onWhichScale.spotForNewScale()
      var newScale = new Scale(scalenames.head, Random.nextInt(4)+1, Some(onWhichScale), Some(spot))
      this.scales += newScale
      this.scalenames = this.scalenames.tail
      this.currentRound += 1
    }
  }
  
  def isOver: Boolean = this.currentRound > this.numberOfRounds || this.scales.filter(s => s.scaleSpots.nonEmpty).isEmpty
}