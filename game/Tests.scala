package game

import org.junit.Test
import org.junit.Assert._
import scala.collection.mutable.Buffer

class Tests {
  
  //scales
  val firstScale = new Scale('A', 3, None, None)
  val secondScale = new Scale('B', 2, Some(firstScale), Some(-1))
  val thirdScale = new Scale('C', 3, Some(firstScale), Some(2))
  val fourthScale = new Scale('D', 2, Some(secondScale), Some(-1))
  
  //players
  val firstPlayer = new Player(java.awt.Color.blue)
  val secondPlayer = new Player(java.awt.Color.red)
  
  //weights
  val weightA = new Weight("BR2", firstPlayer)
  val weightB = new Weight("BR2", firstPlayer)
  val weightC = new Weight("AR3", firstPlayer)
  val weightD = new Weight("DR2", firstPlayer)
  val weightE = new Weight("AR3", secondPlayer)
  
  //setup
  firstScale.scaleSpots -= secondScale.locationOnScale.get
  firstScale.scaleSpots -= thirdScale.locationOnScale.get
  secondScale.scaleSpots -= fourthScale.locationOnScale.get
  
  val game = new Game(List(firstPlayer, secondPlayer), 3)
  
  @Test def testCanAddWeight() {
    assertEquals("Should be able to add weight to scale A",    true,   firstScale .canAddWeight(weightC.spot))
    firstScale.addWeight(weightC)
    assertEquals("Should be able to add weight to scale B",    true,   secondScale.canAddWeight(weightA.spot))
    secondScale.addWeight(weightA)
    assertEquals("Shouldn't be able to add weight to scale B", false,  secondScale.canAddWeight(weightB.spot))
    assertEquals("Should be able to add weight to scale D",    true,   fourthScale.canAddWeight(weightD.spot))
    
  }
  
  @Test def testScoreCalculation() {
    assertEquals("Weight's value on scale A", 3, firstScale.newValue(weightC.spot) )
    assertEquals("Weight's value on scale B", 2, secondScale.newValue(weightB.spot))
    assertEquals("Weight's value on scale C", 2, thirdScale.newValue(-1)           )
    assertEquals("Weight's value on scale D", 2, fourthScale.newValue(weightD.spot))
  }
  
  @Test def testAddWeight() {
    firstScale.addWeight(weightC)
    assertTrue("firstScale's scaleSpots shouldn't contain weight's spot", !firstScale.scaleSpots.contains(weightC.spot))
    assertEquals("firstPlayer's score should be 3",  3, firstPlayer.score)
    assertEquals("1 firstScale's imbalance should be 3", 3, firstScale.imbalance)
    assertTrue("Should be able to add weightA", secondScale.canAddWeight(weightA.spot))
    
    secondScale.addWeight(weightA)
    assertTrue("secondScale's scaleSpots shouldn't contain weight's spot", !secondScale.scaleSpots.contains(weightA.spot))
    assertEquals("firstPlayer's score should be 5",  5, firstPlayer.score)
    assertEquals("2 secondScale's imbalance should be 2", 2, secondScale.imbalance)
    assertEquals("2 firstScale's imbalance should be 2", 2, firstScale.imbalance)
    assertTrue("Should be able to add weightD", fourthScale.canAddWeight(weightD.spot))
    assertTrue("Should be able to add left", fourthScale.canAddWeight(-2))
    
    fourthScale.addWeight(weightD)
    assertTrue("fourthScale's scaleSpots shouldn't contain weight's spot", !fourthScale.scaleSpots.contains(weightD.spot))
    assertEquals("firstPlayer's score should be 7",  7, firstPlayer.score)
    assertEquals("3 fourthScale's imbalance should be 2", 2, fourthScale.imbalance)
    assertEquals("3 secondScale's imbalance should be 1", 1, secondScale.imbalance)
    assertEquals("3 firstScale's imbalance should be 1", 1, firstScale.imbalance)
    
    assertTrue("Shouldn't be able to add weightE", !firstScale.canAddWeight(weightE.spot))
    
    firstScale.addWeight(weightE)
    assertEquals("firstPlayer's score should be 7",  7, firstPlayer.score)
    assertEquals("secondPlayer's score should be 0", 0, secondPlayer.score)
    assertEquals("4 fourthScale's imbalance should be 2", 2, fourthScale.imbalance)
    assertEquals("4 secondScale's imbalance should be 1", 1, secondScale.imbalance)
    assertEquals("4 firstScale's imbalance should be 1", 1, firstScale.imbalance)
  }
  
  @Test def testGame() {
    game.turn("AR1", firstPlayer)
    game.turn("AL4", secondPlayer)
    game.newRound()
    assertEquals("firstPlayer's score should be 1", 1, firstPlayer.score)
    assertEquals("secondPlayer's score should be 0", 0, secondPlayer.score)
  }
}