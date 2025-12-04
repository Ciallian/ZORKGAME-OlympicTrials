Java Zork Adventure Game
Overview
Java Zork is a text-based adventure game inspired by classic interactive fiction. Players explore rooms, interact with NPCs, collect items, and solve puzzles to progress through the story.
The game emphasizes:

Exploration of interconnected rooms
Trading and dialogue with NPCs
Hidden items and keys to unlock new areas
Combat with enemies and strategic resource management


Features

Dynamic Rooms: Each room can contain items, NPCs, enemies, and storage containers.
Inventory System: Pick up, use, and trade items.
NPC Interaction: Talk to NPCs, trade items, and unlock secrets.
Hidden Items: Discover concealed objects using special tools.
Combat Mechanics: Attack and block against enemies.
Modular Design: Separate game logic and GUI for flexibility.
Save Load buttons
Fully functioning npc system





Requirements

Java 17+
Maven for build and dependency management


How to Run

Clone the repository:
Shellgit clone https://github.com/yourusername/java-zork.gitcd java-zorkShow more lines

Build the project:
Shellmvn clean installShow more lines

Run the game:
Shellmvn exec:java -Dexec.mainClass="game.Game"Show more lines



Gameplay

Use text commands like:
look
move north
take Shadow Key
use War Key
talk NPC
trade NPC


Explore rooms, collect items, and solve puzzles to progress.


Design Notes

GUI is optional: The game runs fully in console mode.
Loose coupling: NPCs and game logic do not depend on GUI classes.
Extensible: Add new items, NPCs, and rooms easily by extending base classes.


Future Enhancements


More complex puzzles
Combat improvements
Richer NPC dialogue trees
