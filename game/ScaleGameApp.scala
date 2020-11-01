package game

import scala.swing._
import scala.swing.event._
import java.awt.Color
import java.awt.Graphics2D
import java.awt.GraphicsConfiguration
import java.awt.image.BufferedImage
import javax.swing.border.Border
import scala.collection.mutable.Buffer


object ScaleGameApp extends SimpleSwingApplication {
  
  //icon
  private val pinkSquare = new BufferedImage(50,50,BufferedImage.TYPE_INT_ARGB)
  private val g1: Graphics2D = pinkSquare.createGraphics()
  g1.setColor(new Color(230,220,220))
  g1.fillRect(0, 0, pinkSquare.getWidth, pinkSquare.getHeight)
  private val icon = Swing.Icon(pinkSquare)
  
  //opens a window where user can choose the number of players
  private var numberOfPlayers: Int = Dialog.showInput(first, "Select the number of players", "New game", Dialog.Message.Question, icon, List(2,3,4,5,6), 2).getOrElse(2)
  //a window where user can choose the number of rounds
  private var numberOfRounds: Int = Dialog.showInput(first, "Select the number of rounds", "New game", Dialog.Message.Question, icon, List(5,6,7,8,9,10), 5).getOrElse(5)
  //list of colors for maximum number of players
  private val colors: List[Color] = List(new Color(90,130,220), new Color(90,170,110), new Color(220,120,90), new Color(180,140,200), new Color(250,220,70), new Color(240,180,70))
  //form the list of players by mapping colors and narrowing it down to desired number of players
  private var playerList: List[Player] = colors.map(color => new Player(color)).take(numberOfPlayers)
  //counter for knowing whose turn it is
  var i = 1
  
  var game = new Game(playerList, numberOfRounds)
  
  //separate method for finding the y coordinate for objects on Canvas
  private def findYCoord(scale: Scale): Int = {
    var s = scale
    var y = 500
    while (s.scaleUnderneath!=None) {
      s = s.scaleUnderneath.get
      y -= 60
    }
    y
  }
  
  //separate method for finding the x coordinate for objects on Canvas
  private def findXCoord(scale: Scale): Int = {
    var s = scale
    var x = 350
    while (s.scaleUnderneath!=None) {
      x += (s.locationOnScale.get)*25
      s = s.scaleUnderneath.get
    }
    x
  }
  
  //draws the scales and weights
  class Canvas extends Panel {
    override def paintComponent(g: Graphics2D) = {
      g.setColor(new Color(230,220,220))
      g.fillRect(0, 0, size.width, size.height)
      g.setColor(Color.black)
      for (scale <- game.scales) {
        val x = findXCoord(scale)
        val y = findYCoord(scale)
        g.setColor(Color.darkGray)
        g.fillRect(x-25*scale.radius, y, 25*(scale.radius*2+1), 25)
        g.fillPolygon(Array(x+12, x+22, x+2), Array(y+20, y+60, y+60), 3)
        g.setColor(Color.white)
        g.drawString(scale.name.toString(), x+8, y+55)
        g.drawString("radius: " + scale.radius.toString, x-12, y+18)
        for (weight <- scale.weights) {
          g.setColor(weight.owner.color)
          g.fillRect(x+weight.spot*25, y-25-weight.onTop*25, 25, 25)
        }
      }
    }
  }
  
  //canvas for player colors
  class CanvasPL(color: Color) extends Panel {
    
    override def paintComponent(g: Graphics2D) {
      if (color==game.players(i-1).color) {
        maximumSize = new Dimension(20, 20)
        minimumSize = new Dimension(20, 20)
        preferredSize = new Dimension(20, 20)
        border = Swing.BeveledBorder(Swing.Raised, color, Color.darkGray)
      } else {
        maximumSize = new Dimension(15, 15)
        minimumSize = new Dimension(15,15)
        preferredSize = new Dimension(15, 15)
        border = Swing.EmptyBorder(0, 0, 0, 0)
      }
      g.clearRect(0, 0, size.width, size.height)
      g.setColor(color)
      g.fillRect(0, 0, size.width, size.height)
      
    }
  }
  
  //frame for the Dialogs in numberOfPlayers, numberOfRounds and new game
  def first = new Frame
  
  //main frame
  def top = new MainFrame {
    
    title = "Scale Game"
    size = new Dimension(1000, 700)
    
    var canvas = new Canvas {
      preferredSize = new Dimension(700,700)
    }
    
    val plrs = new Label {
      text = "Players:"
      font = new Font(this.font.getName(), 1, 20)
      border = Swing.EmptyBorder(20,0,20,0)
    }
    
    val instruction = new Label {
      text = "Type the location for your weight \n(for example 'AR1' for scale A, right side, spot 1)"
      font = new Font(this.font.getName(), 1, 12)
      border = Swing.EmptyBorder(20, 0, 20, 0)
    }
    
    val changePoints = new Label {
      text = "A"
    }
    
    val textArea = new TextArea() {
      maximumSize = new Dimension (20, 10)
      minimumSize = new Dimension (20, 10)
      preferredSize = new Dimension(20, 10)
      font = new Font(this.font.getName(), 1, 14)
    }
    
    val currentPlr = new Label {
      text = "Current player: "
      border = Swing.EmptyBorder(10, 10, 10, 10)
    }
    
    val button = new Button("OK") {
      background = new Color(230,220,220)
      border = Swing.EmptyBorder(10, 10, 10, 10)
      listenTo(mouse.moves)
      reactions += {
        case e: MouseEntered => background = new Color(210,180,180)
        case f: MouseExited => background = new Color(230,220,220)
      }
    }
    
    val labelList = Buffer[Label]()
    val canvasList = Buffer[CanvasPL]()
    for (plr <- game.players) {
      labelList += new Label {
        text = plr.score.toString
        border = Swing.EmptyBorder(15,15,15,15)
      }
      canvasList += new CanvasPL(plr.color)
    }
    
    
    contents = new GridPanel(1,3) {
      contents += canvas
      contents += new BoxPanel(Orientation.Vertical) {
        contents += new BoxPanel(Orientation.Horizontal) {
          contents += new Label {
            text = " "
            border = Swing.EmptyBorder(0,20,0,0)
          }
          contents += plrs
          contents += new Label {
            text = " "
            border = Swing.EmptyBorder(0,0,0,0)
          }
        }
        for (i <- 0 until playerList.size) {
          contents += new BoxPanel(Orientation.Horizontal) {
            contents += labelList(i)
            contents += canvasList(i)
          }
        }
        contents += new BorderPanel() {
          this.add(instruction, BorderPanel.Position.North)
          this.add(new ScrollPane(textArea) {
          
          maximumSize = new Dimension (20, 10)
          minimumSize = new Dimension (20, 10)
          preferredSize = new Dimension(20, 10)
          border = Swing.EmptyBorder(0, 20, 0, 20)
        }, BorderPanel.Position.Center)
          this.add(new Label() {
            text = " "
            border = Swing.EmptyBorder(0,190,0,0)
          }, BorderPanel.Position.West)
          this.add(new Label() {
            text = " "
            border = Swing.EmptyBorder(0,150,0,0)
          }, BorderPanel.Position.East)
          this.add(new Label() {
            text = " "
            border = Swing.EmptyBorder(4,0,0,0)
          }, BorderPanel.Position.South)
          minimumSize = new Dimension(500, 100)
          maximumSize = new Dimension(500, 100)
        }
        
        contents += new BoxPanel(Orientation.Vertical) {
          contents += new Label(){
            text=" "
          }
          contents += button
        }
      }
    }
    listenTo(textArea)
    listenTo(button)
    reactions += {
      case ButtonClicked(`button`) => {
        
        if (game.turn(textArea.text, game.players(i-1))) {
          for (j <- 0 until playerList.size) {
            labelList(j).text = playerList(j).score.toString
          }
          canvas.repaint()
        }
        
        if (i==numberOfPlayers) {
          game.newRound()
          if (game.isOver) {
            //find the winner(s)
            var wnr = game.players.sortBy(p => p.score).reverse.head
            var allWnrs = game.players.filter(plr => plr.score==wnr.score)
            var winner = new BufferedImage(100,100,BufferedImage.TYPE_INT_ARGB)
            val g2: Graphics2D = winner.createGraphics()
            var x = 0
            for (w <- allWnrs) {
              g2.setColor(w.color)
              g2.fillRect(x, 0, winner.getWidth/allWnrs.size, winner.getHeight)
              x += winner.getWidth/allWnrs.size
            }
            val iconB = Swing.Icon(winner)
            
            //asks if the players want to play again, starts a new game if yes, otherwise closes the game
            Dialog.showConfirmation(first, "Congratulations! Wanna play again?", "The winner", Dialog.Options.YesNo, Dialog.Message.Question, iconB) match {
              case Dialog.Result.Yes => {
                numberOfPlayers = Dialog.showInput(first, "Select number of players", "New game", Dialog.Message.Question, icon, List(2,3,4,5,6), 2).getOrElse(2)
                numberOfRounds = Dialog.showInput(first, "Select number of rounds", "New game", Dialog.Message.Question, icon, List(5,6,7,8,9,10), 5).getOrElse(5)
                playerList = colors.map(color => new Player(color)).take(numberOfPlayers)
                game = new Game(playerList, numberOfRounds)
                this.dispose()
                ScaleGameApp.main(Array())
              }
              case _ => ScaleGameApp.quit()
            }
          }
          canvas.repaint()
          i = 1
        } else i += 1
      }
      textArea.text = ""
    }
  }

}