# Scale Game

## 1. Description

Scale Game is a project for the course Programming Studio 2.

The game is designed for 2-6 players and the players can choose the number of rounds between 5-10. It has a graphical user interface which utilizes Scala Swing and its predecessor Java AWT libraries.

## 2. Instructions

### 2.1 The goal

The goal of the game is to add weights onto the scales while maintaining sufficient balance on all of the scales.

### 2.2 The rules

The players must keep the balance of every scale in mind when placing their weights. The *imbalance* of a scale can be anything from 0 (completely balanced) to the size of its *radius* (nearly collapsing). The radius of the scale means the distance from the center to either end.  
  
An empty scale always has an imbalance of 0. When a weight is added directly onto a scale, the imbalance is affected depending on the spot of the scale. If the weight is at the end of the scale, the imbalance is now equal to the radius (i.e. nearly collapsing). If another weight is now added to the other end of the weight, the imbalance becomes 0 again.  
  
Now, a scale may be located on top of other scales. An empty scale is extremely lightweight and therefore weighs nothing. However, the weight of each weight is 1, and thus the weight of a scale equals the sum of the weights on top of it. This includes all superpositioned scales as well.  
  
On every round, each player gets to try to place one weight onto the scale(s). If the player manages to place the weight so that every scale stays in balance, the player receives points and the weight can stay on the scale. If the imbalance on *any* scale becomes too great, the weight will not be added onto the scales and the player receives 0 points.  
  
After every round, a new scale appears on top of an existing one.

### 2.3 The points

Players can collect points by successfully placing weights on top of scales. The number of points a player gets from each weight depends on its spot. The points will be calculated with the following formula:

*points = the distance from the weight to the center of scale N \* the distance from scale N to the center of scale N-1 \* ... \* the distance from scale 2 to the center of scale 1*  
  
Or if the scale does not have any scales underneath it, the points will equal the distance from the weight to the center of the scale.  
  
An example: There are three scales. Scale C is on top of scale B, at a distance of 3 from the center, and scale B is on top of scale A, at a distance of 4 from the center. If a player manages to successfully place a weight onto scale C, spot 2 from the center, the player receives 2 \* 3 \* 4 = 24 points. If a weight is successfully placed onto scale A, distance 5 from the center, the player gets 5 points.  

A player can also steal other players' points by placing weights on top of other players weights. In that situation, the points from those weights will be transferred to the player who has the topmost weight, plus the points that the player would get in any case. However, if any scale becomes imbalanced, player will get 0 points (i.e. no points will be transferred).

### 2.4 The winner

The player who manages to tactically place their weights and thus earn the most points is the winner.

### 2.5 The interface

When running the program, two pop-up windows appear. The first one asks to select the number of players (2 by default) and the second one asks to select the number of rounds (5 by default). After completing these selections, the main window opens. The players then choose which color belongs to whom, and the game can begin.  
  
The interface has a text box where the players write the spot to which they are attempting to place the weight. The spot consists of three characters: the letter of the scale (visible on each scale), L or R (left or right side from the center of the scale), and the distance from the center of the scale.  
  
An example: If a player wants to put a weight to the right end of scale A that has a radius of 5, the player will have to write "AR5" into the text field and click "OK"-button in order to place the weight.  
  
When all the rounds have been played, the program opens a new pop-up with the winner(s) color(s) and asks if the players want to play again.

## 3. The structure of the program

![Class diagram](/class-diagram.png)

- ***Weight***: Represents a weight that has been placed onto a scale. Keeps track of its owner, spot on a scale, value (i.e. the points a player has received from it), and the number of weights it is directly on top of.
- ***Player***: Represents a player in the game. A player has a signature color and keeps track of its current score.
- ***Scale***: A scale has an unique letter and a size. It keeps track of its imbalance and the weights that have been placed directly onto it. It also knows if there is an another scale underneath it. This class contains the most important functions of the game, and they are described in the algorithms section.
- ***Game***: keeps track of the players, scales, turns, and rounds of the game.
- ***ScaleGameApp***: the graphical user interface of the game.

## 4. Algorithms

The main functionalities of the game utilize recursion. Checking imbalance and calculating points are taken care of in the Scale class. When a player attempts to place a weight, the Scale class checks if the weight can be placed in the desired spot and if the scales stay balanced. If the attempt passes these requirements, the Scale class calculates the points for the affected players and new imbalances for the affected scales.

Method canAddWeight receives a spot to which a weight is desired to be placed. It recursively checks that for every scale, the absolute value of the sum of the parameter and the current imbalance of the scale is less than or equal to the radius of the scale.

Now, if the canAddWeight returns true, the imbalances of all affected scales must be updated. The Scale class does this with the newBalance method: it adds the weight's (or scale's) spot (right side is positive, left side is negative) to the imbalance on every affected scale.

Lastly, the points are calculated with the newValue method. This is a recursive method that multiplies the weight's spot with the scale's spot on the scale directly underneath it until there is no scale underneath, and returns an absolute value of the result.

## 5. Testing

Majority of testing was done by playing the game and checking if the program is working as expected. In addition to this, there is a Tests-class for unit testing.

## 6. Known issues

Currently, a new scale always appears on top of the topmost scale unless there is no space left on it. This is to prevent scales from covering other scales. Even though it was not classified as a bug during the course, it was quite a lazy way to avoid the problem.  
  
In ScaleGameApp, there are methods for calculating X and Y coordinates for the scales. This could have been simplified by keeping track of the height level of the scale in the Scale class. It would also be a part of the solution to the first problem.  
  
Finally, the way to place weights by writing the spot is quite unintuitive. A better way would have been to place weights by clicking on the spot. The reason why the text field was chosen was just because it was mentioned in the project description.
