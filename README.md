
Fun board game against the computer! Whoever will win?

This project is made with the intention of learning functional programming and the Clojure programming language in particular. It presents the user with a board game called "Alquerque" where they would be able to choose which move to make, and the board then updates in real time. Games are to be played against the computer which draws its moves after each succssive turn of the player. The game is won (or lost, depending on your situation) when there are only one type of piece remaining on the board.

Upon login, the user is presented with the main menu screen:

  *********************************************************************
            
      Welcome stanmil to "Play against computer- the app"! This
      interactive game will have you competing against your family and
      friends in no time. But first, you need to beat the computer in
      a one-to-one matchup. Can you do it?

      To get started you must choose one of the options below:

      1 -> play alquerque (easy)
      2 -> play alquerque (medium)
      3 -> play alquerque (hard)
      4 -> game statistics
      5 -> just for fun
      
      Press any other key to quit.
            
    *********************************************************************

    Please enter menu item number:

A number is picked by typing in the proper key and pressing enter. If any other key is pressed apart from numbers 1-5, the user is promptly logged out. Upon game session's end, the user will be relocated back to this menu screen.
Here is what the "easy" board looks like (board of default size 5):

    Here's your board:


       A   B   C   D   E   

    1  B ─ B ─ B ─ B ─ B
       | \ | / | \ | / |
    2  B ─ B ─ B ─ B ─ B
       | / | \ | / | \ |
    3  B ─ B ─   ─ R ─ R
       | \ | / | \ | / |
    4  R ─ R ─ R ─ R ─ R
       | / | \ | / | \ |
    5  R ─ R ─ R ─ R ─ R

    Human Score:  0
    Computer Score:  0


    Please enter user color [B] or [R]:

As you can see, the user is presented with the board and prompted to pick a color. Once that's done, the menu explaining how to play the game is displayed to the user. It reads as follows:

Welcome to alquerque, the board game. Here you play against the AI.
    Upon starting the game you were prompted to choose a color. Now you are meant to
    play a session against an AI with given piece color. To make a turn input
    the coordinates of the first field (Ex. 1A), followed by a '-' symbol and then the field
    you want your piece to go (Ex. 1C). Example: 1A-1B or 1A-1C. You may also use lowercase letters
    if that suits you better, but larger boards may have both uppercase and lowercase letters, 
    so watch out. If your input is invalid, you will get another chance at making a move! Upon 
    "eating" the opponents piece, you get another move. Same goes for the AI. If you want to 
    quit the game, press the Q key (gives the AI a victory).
    The player whose only pieces remain
    wins! Good luck!

Players (the human and the AI) then take turns to try to beat each other. If the human tries to quit, upon pressing Q, this dialog is shown to them:

    Are you sure you want to quit? 
                    Quitting gives a victory to the computer.
                    [Y] or [N]
    Please enter your choice:

If the player presses 'Y', the game ends and computer is rewarded with a win. If the user inputs 'N', the game is back to normal. Otherwise, the user is prompted to input again, until the input is valid. Note: a player may want to use a system interrupt (ctrl + C or cmd + C) to stop a running game. While this is effective, it is not fair against other players and can be regarded as cheating or foul play. It is thusly discouraged. 

Other difficulties lend themselves to using boards of different sizes. And so we have the size 7 board:

       A   B   C   D   E   F   G   

    1  B ─ B ─ B ─ B ─ B ─ B ─ B
       | \ | / | \ | / | \ | / |
    2  B ─ B ─ B ─ B ─ B ─ B ─ B
       | / | \ | / | \ | / | \ |
    3  B ─ B ─ B ─ B ─ B ─ B ─ B
       | \ | / | \ | / | \ | / |
    4  B ─ B ─ B ─   ─ R ─ R ─ R
       | / | \ | / | \ | / | \ |
    5  R ─ R ─ R ─ R ─ R ─ R ─ R
       | \ | / | \ | / | \ | / |
    6  R ─ R ─ R ─ R ─ R ─ R ─ R
       | / | \ | / | \ | / | \ |
    7  R ─ R ─ R ─ R ─ R ─ R ─ R

... and the dreaded size 9 board:

       A   B   C   D   E   F   G   H   I   

    1  B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B
       | \ | / | \ | / | \ | / | \ | / |
    2  B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B
       | / | \ | / | \ | / | \ | / | \ |
    3  B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B
       | \ | / | \ | / | \ | / | \ | / |
    4  B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B
       | / | \ | / | \ | / | \ | / | \ |
    5  B ─ B ─ B ─ B ─   ─ R ─ R ─ R ─ R
       | \ | / | \ | / | \ | / | \ | / |
    6  R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R
       | / | \ | / | \ | / | \ | / | \ |
    7  R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R
       | \ | / | \ | / | \ | / | \ | / |
    8  R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R
       | / | \ | / | \ | / | \ | / | \ |
    9  R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R

Scary isn't it? Apart from these more standard board sizes, a player may want to play with custom board sizes against the AI. That can be done by inputting '5' at the main menu for the "just for fun" option. Here's a cute little board of 
size 3:

        A   B   C   

    1   B ─ B ─ B
        | \ | / |
    2   B ─   ─ R
        | / | \ |
    3   R ─ R ─ R

... or a monstrous one of size 21

        A   B   C   D   E   F   G   H   I   J   K   L   M   N   O   P   Q   R   S   T   U   

    1   B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B
        | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / |
    2   B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B
        | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ |
    3   B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B
        | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / |
    4   B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B
        | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ |
    5   B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B
        | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / |
    6   B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B
        | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ |
    7   B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B
        | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / |
    8   B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B
        | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ |
    9   B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B
        | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / |
    10  B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B
        | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ |
    11  B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─ B ─   ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R
        | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / |
    12  R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R
        | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ |
    13  R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R
        | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / |
    14  R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R
        | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ |
    15  R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R
        | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / |
    16  R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R
        | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ |
    17  R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R
        | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / |
    18  R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R
        | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ |
    19  R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R
        | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / |
    20  R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R
        | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ | / | \ |
    21  R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R ─ R

I get dizzy just by looking at this mess!

Couple of things to note: If the user inputs a negative sized board or if the board is deemed too small or in other ways invalid, it will be defaulted to size 5. Other interesting tidbit is that even sized boards are not deemed fair. Without delving too deep into the game theory and the proper scientific reason as to why (they may turn into infinite games or they may affect the player with fewer number of pieces due to the skewed shape of the board), these sizes are disqualified, and in their place the user is presented with a board 1 tile bigger, to avoid that. How neat!? 

Sizes of more than 200 are prohibited (who would even want that???)

Last but certainly not least, option 4 presents the user with a statistical overview of all games ever played as well as a scoreboard and other useful information. Sounds fun? Try it yourself!

NOTE: After having worked on this project for some time, I discovered Taylor Wood's nifty little negamax library (negamax is similar to minimax, the only difference being the way non-AI moves are scored). If I had to get an AI game engine up and running quickly, I'd use his library to get me started. His code is understandable, concise and exposes a useful API that could be utilized for different board games. Here's Taylor Woods' original github repo for this Clojure library:

https://github.com/taylorwood/negamax

Overall, this has been an enjoyable and pleasant experience, and I feel like my grasp on the Clojure language is far better than it was before I started this project (which isn't saying much since I'd had virtually no knowledge of Clojure to speak of beforehand, but still). Some of the code may be cumbersome and I had trouble maintaining, expanding upon it and testing, and if I were to redo this, apart from using Taylor's negamax library, I would also try to keep every function definition concise and break code up into several different maintainable units. Introducing a graphical user interface would come a long way as well, as it would improve user experience as well as make it easier to take and refine user's input. Doing it through the terminal introduces a new level of complexity when it comes to parsing inputs and user interaction. Still, I am fairly happy with how I did considering this is my very first Clojure project.
