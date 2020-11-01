package game

//game checks whether the input is correct in the Game class

class Weight(val spotAsString: String, var owner: Player) {
  
  //number of how many weights this weight is on top of
  var onTop = 0
  
  val spotSign = {
    spotAsString(1) match {
      case 'L' => -1
      case 'R' => 1
      case _ => 0
    }
  }
  val spot = spotSign * spotAsString(2).toString.toInt
  var value = 0
}