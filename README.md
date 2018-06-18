# ReisenderChallenge

Das "traveler salesman problem" (Handelsreisender Problem) gehört in die Gruppe der
NP-Problemen. Das heißt um die beste Lösung zu finden, müsste man alle Lösungen
ausprobieren. Das ist aber aufgrund der immensen Größe an möglichen Lösungen
schier unmöglich (mit derzeitiger Technik). Man kann jedoch annäherungsweise
ein gutes, bzw sehr gutes Ergebnis erzielen.

# Das Problem:
Stellt euch vor ihr wollt 10 Städte bereisen. In welcher Reihenfolge ihr
die Städte besucht ist euch egal. Hauptsache ist, ihr wart in jeder Stadt ein
mal.
Um Sprittgeld zu sparen wollt ihr die kürzeste Strecke fahren und doch
alle Städte besuchen. Welche Route müsst ihr dafür nehmen?

Die Städte werden im Programm durch zufällige Punkte repräsentiert.
Die Route ersellt man durch einzelne Linien zwischen diesen Punkten.
Innerhalb der Funktion berechneRoute() wird die Route berechnet.
Dort sollt ihr eure Lösung erstellen.

# Der Ablauf:
Damit der Vergleich zwischen den einzelnen Teilnehmern vergleichbar bleibt,
werden Zufallszahlen mit einem Seed erzeugt.
Damit werden 10 unterschiedliche Städtekonstellationen erzeugt.
Diese sind wie folgt aufgeteilt:
- 6x hundert Städte
- 3x tausend Städte
- 1x dreitausend Städte
Für die Berechnung der Route existiert ein Zeitlimit. Dieses wird wie folgt
berechnte: Zeitlimit = Städteanzahl*1/22 Sekunden;
Das Berechnen aller Routen darf somit maximal 5 Minuten dauern. 
Beim Überschreiten des Zeitlimits wird das Ergebnis nicht gezählt.
Nach der Deadline werden alle eingereichten Lösungen auf dem gleichen Computer
mit dem gleichen Startseed getestet. Sollten das Ergebnis knapp ausfallen, so wird
mit weiteren Seeds ein Durchschnitt errechnet um den endgültigen Gewinner zu
ermitteln.

# Der Score:
Eure abgegebene Lösung wird mit einem Score bewertet.
Der Score setzt sich aus zwei Faktoren zusammen:
1. Wie gut ist euer gefundener Weg gegenüber einem zufällig Gewählten?
2. Wie viel Zeit von eurer Rechenzeit ist übrig.
Generell gilt:
1. Ein besserer Pfad wird besser bewertet, als wenig Rechenzeit verbraucht.
2. Je mehr Städte eine Route beinhaltet, desto mehr Punkte können erreicht werden.

Die genaue Formel findet ihr im Source Code.
Sollte nicht jede Stadt besucht, keine zusammenhängende Route gefunden oder eure Rechenzeit
überschritten sein, so wird diese Route mit 0 bewertet.
Der Score aller 10 Routen wird aufsummiert und bildet euren Endscore.

# Die Deadline:
Lösungen können bis zum 30.09.2018 23:59:59.9999 abgegeben werden.

# Der Gewinn:
Sobald ein Preis feststeht, wird er hier ergänzt. Sponsoren sind gern gesehen. :)
