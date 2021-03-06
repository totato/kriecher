package ameisenfutter;

import java.util.Random;

/**
 *
 * @author Marc und Vika
 */
public class Simulation {

    private final int gesamtAmeisen = 100;
    private Ameise[] ameisenKolonie;

    private final int futterproQuelle = 50;
    private final int futterquellenaufFeld = 10; // gemeint ist das 2D Array
    private final int feldGroesse = 500; // quadratisch
    private int duftstoffZeit = 1000;
    private final int[] nestPosition = new int[2];
    private Feld[][] feld;
    private int futterImNest = 0;

    public Simulation() {
        feldErzeugen(feldGroesse, futterquellenaufFeld);
        ameisenSpawnen(gesamtAmeisen);

    }

    public void los() {
        //   while (futterImNest < futterproQuelle * futterquellenaufFeld) {
        // Duftstoffe verschwinden mit jedem Durchlauf
        for (int i = 0; i < feld.length; i++) {
            for (int j = 0; j < feld.length; j++) {
                if (feld[i][j].getDuftstoffEinheiten() > 0) {
                    feld[i][j].setDuftstoffEinheiten(feld[i][j].getDuftstoffEinheiten() - 1);
                }

            }

        }
        // eine Runde - jede Ameise macht einen Zug
        for (int i = 0; i < ameisenKolonie.length; i++) {
            Feld aktFeld = feld[ameisenKolonie[i].getX()][ameisenKolonie[i].getY()];

            if (!ameisenKolonie[i].isTraegtFutter() && aktFeld.getFutterportion() > 0) {
                futterAufnehmen(ameisenKolonie[i]);
                duftstoffVerspruehen(aktFeld);
            } else if (ameisenKolonie[i].isTraegtFutter() && aktFeld.isNest()) {
                futterAblegen(ameisenKolonie[i]);
            } else if (ameisenKolonie[i].isTraegtFutter()) {
                nachHause(ameisenKolonie[i]);
                duftstoffVerspruehen(aktFeld);
            } else {
                futtersuche(ameisenKolonie[i]);
            }
            // }

        }
    }

    private void feldErzeugen(int groesse, int futterquellen) {
        feld = new Feld[groesse][groesse];
        // Feld wird gefüllt, erst nur 'leere' Feld Objekte
        for (int i = 0; i < groesse; i++) {
            for (int j = 0; j < groesse; j++) {
                feld[i][j] = new Feld(0, 0, false);

            }

        }
        // Nest wird in die Mitte gesetzt
        feld[groesse / 2][groesse / 2].setNest(true);
        nestPosition[0] = groesse / 2;
        nestPosition[1] = groesse / 2;
        // Futterstellen werden zufällig verteielt
        Random r = new Random();
        while (0 < futterquellen) {
            int x = r.nextInt(groesse);
            int y = r.nextInt(groesse);
            if (feld[x][y].getFutterportion() <= 0 && !feld[x][y].isNest()) {
                feld[x][y].setFutterportion(futterproQuelle);
                futterquellen--;

            }

        }

    }

    /*
     Erzeugt die Ameisen
     */
    private void ameisenSpawnen(int anzahl) {
        ameisenKolonie = new Ameise[anzahl];
        for (int i = 0; i < anzahl; i++) {
            ameisenKolonie[i] = new Ameise(false, nestPosition[0], nestPosition[1]);
        }

    }

    private void futtersuche(Ameise ameise) {
        // prüfen ob Duftpunkte in der Nähe, wenn ja geht es zu dem welches weiter vom Nest entfernt ist
        Random r = new Random();
        int x = ameise.getX();
        int y = ameise.getY();
        boolean gefunden = false;

        for (int i = ameise.getX() - 1; i <= ameise.getX() + 1; i = i + 2) { // werden nur direkt angrenzende Felder überprüft
            if (i >= 0 && i < feld.length && feld[i][y].getDuftstoffEinheiten() > 0) {
                if (feld[ameise.getX()][ameise.getY()].getDuftstoffEinheiten() <= 0) {
                    x = i;
                    gefunden = true;
                    break;
                } else if (Math.abs(i - nestPosition[0]) > Math.abs(x - nestPosition[0])) {
                    x = i;
                    gefunden = true;
                    break;
                }

            }
        }
        for (int j = ameise.getY() - 1; j <= ameise.getY() + 1; j = j + 2) {
            if (!gefunden && j >= 0 && j < feld.length && feld[x][j].getDuftstoffEinheiten() > 0) {
                if (feld[ameise.getX()][ameise.getY()].getDuftstoffEinheiten() <= 0) {
                    y = j;
                    gefunden = true;
                    break;
                } else if (Math.abs(j - nestPosition[1]) > Math.abs(y - nestPosition[1])) {
                    y = j;
                    gefunden = true;
                    break;
                }

            }

        }
        if (gefunden) {
            ameise.setX(x);
            ameise.setY(y);
        }

        if (!gefunden) { 


// wenn Kein Duftpunkt gefunden wurde 
// es wird eine zufällige Koordinate zufällig um eins erhöht oder gesenkt

            //Marc's idee des Vikas hat aber auch funktioniert
            int zahl = r.nextInt(4);
            if (zahl > 1) {

                if (zahl == 2 && ameise.getX() + 1 < feld.length) {
                    ameise.setX(ameise.getX() + 1);
                } else if (ameise.getX() - 1 >= 0) {
                    ameise.setX(ameise.getX() - 1);
                }

            } else {

                if (zahl == 0 && ameise.getY() + 1 < feld.length) {
                    ameise.setY(ameise.getY() + 1);
                } else if (ameise.getY() - 1 >= 0) {
                    ameise.setY(ameise.getY() - 1);
                }

            }

        }

    }

    private void nachHause(Ameise ameise) {

        Random r = new Random();
        if (r.nextInt(2) >= 0.5 && ameise.getX() != nestPosition[0]) { // richtung x Koordinate
            if (nestPosition[0] < ameise.getX()) {
                ameise.setX(ameise.getX() - 1);
            } else {
                ameise.setX(ameise.getX() + 1);
            }
        } else if (ameise.getY() != nestPosition[1]) { // richtung y Koordinate
            if (nestPosition[0] < ameise.getY()) {
                ameise.setY(ameise.getY() - 1);
            } else {
                ameise.setY(ameise.getY() + 1);
            }
        }

    }

    private void futterAufnehmen(Ameise ameise) {
        ameise.setTraegtFutter(true);
        feld[ameise.getX()][ameise.getY()].futterportionAbziehen();
    }

    
    private void futterAblegen(Ameise ameise) {
        ameise.setTraegtFutter(false);
        futterImNest++;
        System.out.println("Futter im Nest = " + futterImNest);
    }

    private void duftstoffVerspruehen(Feld feld) {
        feld.setDuftstoffEinheiten(duftstoffZeit);
    }

    public int getGesamtAmeisen() {
        return gesamtAmeisen;
    }

    public int getFeldGroesse() {
        return feldGroesse;
    }

    public int[] getNestPosition() {
        return nestPosition;
    }

    public Ameise[] getAmeisenKolonie() {
        return ameisenKolonie;
    }

    public Feld[][] getFeld() {
        return feld;
    }

}
