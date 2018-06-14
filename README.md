# Raceted

Menü:

* Start -> 2 Spieler wählen das gewünschte Fahrzeug aus.
		   Zuerst Spieler 1 und dann Spieler 2

******************************

Steuerung:

Allgemeines (Möglich bei jeder Phase):
	* [ESC] -> Pausiert das Spiel
	* [T] {nur für debug} -> Wechselt von Fallen-Platzierung zum Fahren

Fahrzeug:
	* [W] -> Vorwärts
	* [S] -> Rückwärts
	* [A] -> Nach links lenken
	* [D] -> Nach rechts lenken
	* [ENTER] -> Teleport zum letzten Checkpoint
	
Stein:
	* [W] -> Vorwärts
	* [S] -> Rückwärts
	* [A] -> Nach links lenken
	* [D] -> Nach rechts lenken
	* [ENTER] -> Teleport zum letzten Checkpoint
	
Fallen-Platzierungsphase:
	* [Links-Klick] -> Platziere ausgewählte Falle (Standardmäßig Falle 1)
					-> (Im Lösch-Modus) Löscht jene Falle, die auf dem Mauszeiger liegt.
	* [Rechts-Klick gedrückt halten] -> Bewegt die Szene
	* [ENTF/DEL] -> Wechselt in den Lösch-Modus
	
******************************************
	
Beschreibung zu den Phasen:

Fahr-Phase:
	* Beide Spieler fahren die Strecke ab, zuerst Spieler 1 dann Spieler 2.
	* Der nächste Spieler ist dran, wenn entweder der jetzige Spieler stirbt, oder die Strecke fertig gefahren ist
	* Checkpoints wurden auf der ganzen Strecke verteilt, die durchgefahren werden müssen, wenn ein Spieler einen Checkpoint verpasst, dann muss er
	  wieder zum verpassten Checkpoint fahren. Nur wenn man durch alle Checkpoints durchgefahren ist, dann zählt die Strecke als fertig gefahren.
	  
Fallen-Platzierungsphase:
	* Hier kann man Fallen auf der ganzen Strecke verteilen. Hierbei kann man zwischen 3 Fallen auswählen (Hütchen, Stachel, Busch).
	* Jede Falle hat unterschiedliche Effekte:
		-> Hütchen: Blockiert den Spieler
		-> Stachel: Macht dem Spieler Schaden
		-> Busch: Verlangsamt den Spieler
	* Fallen kann man entweder über die Bilder unten auswählen, oder mit den Tasten [1],[2] oder [3].
	* Fallen kann man im Lösch-Modus wieder entfernen, diese Fallen können auch von anderen Spielern platziert worden sein.
	* Fallen kann man nicht im Spawn-Bereich setzen (Im Level ist es zwischen den beiden Rampen).