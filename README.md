# Final Project (Team 21)

**Please see the assignment instructions in Canvas as well as READMEs in the individual provided code packages (submodules).** 

*You must manually initially add the submodule for your Final Projct API design groups common repository.   This submodule will use a "submodule path" (mapping to a folder in the project) of <code>src/common</code>.  When the API is finalized, this submodule will be replaced with that for the class's final API.* 

Please see the Canvas pages, "Using the OwlMaps Library", "Using the Discovery Server Package", "Using the PubSubSync Library" and the "ChatApp and Final Project Resources", for documentation and usage directions.  See the individual submodule READMEs and Javadocs for additional information.   Use the "Client+Cerver" discovery server configuration mode for the application.

### IMPORTANT: Put all assignment code (except common API) under a package name with the team's NetIDs, e.g. netid1_netid2.

This will prevent name clashes when transmitting classes.   Also, don't forget to change the module name in the <code>module-info.java</code> file.

**Final Project API design group** *Group E: https://github.com/Rice-COMP-310/comp310-f21-finalproject-design-comp310-f21-finalproject-design-e*:

## List ALL Partner Names and NetIDs:
1. Yijie Huang (yh87) 
1. Cheng Peng (cp46) 

## Notes to Staff:
Final Project MS1 Notes:

#### Game Description\<Rice Questionnaire\>:

**Basic Setup**:

In each game instance, there are two teams competing against each other. Each team comprises of 2\~3 players. Players will answer 10\~20 questions (since the game is called Rice Questionnaire, we plan to make the questions about Rice!). Each question is worth different amounts. Players get to choose the next question's level of difficulty. Answering a more difficult question correctly gives you more points than answering an easy question. Ultimately, the game with the highest score wins.

**Definition of a Team & Use of Chatroom**:

The chatroom from HW08 serves as a Game Lobby in our game design. However, each chatroom comprises of two teams. While players can choose which chatroom to join when they request to join chatrooms at the connection level, the teams are assigned at random. The communications between team members and with the rival teams can be done through the chatroom.

**Introduction of a Mole & Use of Google Map**:

If time permits, when we assign a team, we would assign one player in each team the role of a "mole". For them to win the game, they need to undermine their current team and let the other team win the game. Also, if time permits, instead of counting "scores", we would award distances. Each team will start from Wong's office, and we will show the live location of each team in each map. The team that goes the farthest distance wins.

#### MS1 Implementations:
1. Players can send a room-level message that displays a custom UI by clicking the "Make Game" button in the chatroom.

2. Players can have inter-team communication: you could simply click the "Add Score" button in custom game UI (which simulates the situation in which a team answers a question correctly). Then your team's score will be updated to 10 and the info will be broadcast by the server (the game room host) to the chatroom. (You will see a text message saying "Team X has a score of 10.") Note that the "Add Score" button only serves to demonstrate inter-team communication and will be deleted later.

3. By clicking the "Send Map" button in the chatroom, a player could send a Google map to every player in the chat room.

#### MS2 Implementations:
Finished all functionalities.

#### Prose description of custom messages and UI:
**Custom UI:**
1. We have the text box combined with a "send to team" button that enables the players to send private message to his/her team members. 
2. We have a "hard" and "easy" button for each team and we will show the question after pressing the button.
3. We have a "next" button that will pop up the next question for all team members.
4. We have two textfields to show the scores of the player and the result of last question.
5. We have a map component in the middle of the UI. We will move forward one step for every 5 points we get.

**Custom messages:**
1. UpdateScoreMsg: Update the scores for each team.
2. GameWinMsg: The message sent when a team wins the game.
3. GameMsg: send the game MVC to every player.

## Application Notes:
We implemented our game on MacOS. Hence, to run our program, you may have to use the MacOS package and finish the setup as instructed on Canvas.




