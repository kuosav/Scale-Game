package game

import scala.collection.mutable.Buffer
import scala.Array.range
import scala.util.Random

class Scale(val name: Char, val radius: Int, val scaleUnderneath: Option[Scale], val locationOnScale: Option[Int]) {
  var imbalance = 0
  var scaleWeight = 0
  val weights = Buffer[Weight]()
  //spots where a new scale can be added: everytime a scale or a weight is added to a new spot, it is removed from this list.
  val scaleSpots = range(-this.radius, radius+1, 1).toBuffer
  scaleSpots -= 0
  //spots where a new weight can be added: everytime a scale is added, it is removed from this list.
  private val weightSpots = range(-this.radius, radius+1, 1).toBuffer
  
  
  //calculates a new balance for all the scales affected
  private def newBalance(spot: Int): Unit = {
    var s = this
    var w = spot
    while (s.scaleUnderneath!=None) {
      s.imbalance += w
      w = this.locationOnScale.get
      s = s.scaleUnderneath.get
    }
    s.imbalance += w
  }
  
  //checks if any of the scales affected turn imbalanced: returns true if weight can be added.
  def canAddWeight(spot: Int): Boolean = {
    this.scaleUnderneath match {
      case Some(scale) => {
        (Math.abs(spot + this.imbalance) <= this.radius) && scale.canAddWeight(this.locationOnScale.get)
      }
      case None => Math.abs(spot + this.imbalance) <= this.radius
    }
  }
  
  
  //calculates the points for a weight
  def newValue(spot: Int): Int = {
    this.scaleUnderneath match {
      case Some(scale) => Math.abs(spot * scale.newValue(this.locationOnScale.get))
      case None => Math.abs(spot)
    }
  }
  
  //a spot on this scale where a new scale is soon added
  def spotForNewScale(): Int = {
    var scaleSpot = this.scaleSpots(Random.nextInt(this.scaleSpots.size))
    this.scaleSpots -= scaleSpot
    this.weightSpots -= scaleSpot
    scaleSpot
  }
  
  //adds a new weight on this scale
  def addWeight(weight: Weight): Boolean = {
    //checks whether the spot contains a scale and whether it can be added to that spot
    if (!this.weightSpots.contains(weight.spot) || !this.canAddWeight(weight.spot)) {
      return false
    }
      
    //checks if there are other weights in the same spot
    if (this.weights.filter(_.spot==weight.spot).nonEmpty) {
      //removes points from previously added weights and gives them to current player
      for (w <- this.weights.filter(_.spot==weight.spot)) {
        w.owner.score -= w.value
        weight.owner.score += w.value
        w.owner = weight.owner
      }
      //lets the GUI know how high it should draw the weight
      weight.onTop += this.weights.filter(_.spot==weight.spot).size
    } else {
      this.scaleSpots -= weight.spot
    }
    //calculates the points this weight is worth
    weight.value = this.newValue(weight.spot)
    //gives the points to the player
    weight.owner.score += weight.value
    //updates the balance of every scale underneath:
    this.newBalance(weight.spot)
    //adds the weight to the collection of weights on this scale:
    this.weights += weight
    true
  }
}