package com.pseuco.np20.simulation.rocket;

import java.util.LinkedList;
import java.util.List;

import com.pseuco.np20.model.Rectangle;
import com.pseuco.np20.simulation.common.Context;
import com.pseuco.np20.simulation.common.Person;

public class Patch_Monitor {
    // DATA:

    // Inits by Constructor
    private int ID; // War mal relevant XD
    private int Stats_save_current_tick;
    private int synched_neighbor_counter; // Zählt wie Viele nachbarn dieses Patches mit synchronisieren fertig sind, falls alle fetig sind darf er weiter laufen

    // Lists and Maps
    private List<Patch_Monitor> monitor_liste ;   // Nachbar Monitore
    private List<Person> save_Persons ;     // Personenliste aus dem Aktuellen tick
    private boolean init_Monitors_done;

    public Patch_Monitor(int id_patch) {
        this.ID = id_patch;

        // Init for Synchro
        this.synched_neighbor_counter=0;
        this.monitor_liste = new LinkedList<>();

        // Statistics required Variables
        // Tick of save
        this.Stats_save_current_tick = -10;
        // PersonLists
        this.save_Persons = new LinkedList<>();
        this.init_Monitors_done = false;
    }

    // Einfache Setter und Getter:
    public synchronized void set_mon(Patch_Monitor target_mon) {
        /* Es wird beim Aufruf dieser Funktion garantiert das dies vor dem ersten Synchro step abgeschlossen ist
         */
        if (!this.monitor_liste.contains(target_mon))
            this.monitor_liste.add(target_mon);
    }

    public synchronized List<Patch_Monitor> get_neighbor_monitors() {
        // Gibt die Monitore der nachbarn zurück
        return monitor_liste;
    }


    // WAITSET: ...............................................................................................
    /*
     * Unser Normales Waitset lädt mit "get_Population_of_Patch" die gespeicherte liste und verteilt diese an ihre Nachbarn
     * welche die Referenz ihrer NachbarMonitore im eigenen Monitor haben
     *
     * Wenn alle Nachbarn diese Liste abgerufen haben kann mit "set_statistics" eine neue Gespeichert werden und der counter auf 0
     * zurückgesetzt werden
     *
     *
     * Im Patch wurde erwähnt, dass alle Vorkommen gelockt werden müssen falls "patch_Population" von außen bearbeitet wird was hier
     * augenscheinlich gemacht wird aber nicht gelockt
     * Dies benötigt kein Lock da "get_Population_of_Patch" (selbes für "set_statistics") ausschließlich die Liste des callers bearbeitet und währendessen der caller
     * diese auch offensichtlich nicht selbst bearbeitet
     * Falls wie in Patch "get_actual_Population" erwähnt die public funktion die Population bearbeitet müsste also auch hier ein lock
     * erfolgen oder die bearbeitung in den Patch verlegt werden (neue liste -> gib copy davon zurück ...)
     */
    public synchronized boolean get_Population_of_Patch(int tick, Rectangle Patch_with_padding, Context con,
    List<Person> patch_Population, boolean wait, boolean check_mon, List<Patch_Monitor> remove_monitore, Patch_Monitor pot_neighbor)
    throws InterruptedException {

        /* Nur im ersten Durchlauf relevant um unpassende Monitore rauszufiltern.
         * Solche unpassenden Monitore können entstehen, wenn mayPropagateFrom für einen Patch
         * Nachbarn aussortiert, weil keine Informationen propagieren können. Für den Nachbarn
         * aber wird der Patch nicht aussortiert und als Nachbar gesetzt, welcher dann wiederum
         * hier entfernt wird, weil wenn in eine Richtung keine Informationen propagieren können,
         * so können sie es umgekehrt auch nicht, weil der Weg derselbe wäre und offensichtlich
         * blockiert ist.
         * TODO: eventuell zusätzlich ins WIKI?
         */

        if (check_mon){
            while (!this.init_Monitors_done) {  // Warte auf den Init abschluss des anderen Monitors -> darauf dass mayprop alle fertig sind
                wait();
            }
            if (!(this.monitor_liste.contains(pot_neighbor))) {
                remove_monitore.add(this);
                return false;
            }
        }

        // Wait signalisiert hier ob diese Funktion warten soll oder nur überprüft ob die geforderte Liste vorliegt
        if (wait){
            while (tick != this.Stats_save_current_tick || !this.init_Monitors_done) {
                wait();
            }
        } else {
            if (tick != this.Stats_save_current_tick || !this.init_Monitors_done){
                return false;
            }
        }

        // Prüfe ob dieser Tick der richtige ist
        if (tick == this.Stats_save_current_tick) {
            //für jede person checke ob diese im Patch liegt
            for (Person p : this.save_Persons){
                if (Patch_with_padding.contains(p.getPosition())){
                    patch_Population.add(p.clone(con));
                }
            }
            synched_neighbor_counter++;
            notifyAll();
            return true;
        }

        // Prüfe auf Fehler
        else
        System.out
        .println("get_Population_of_Patch is unequal Tick: " +
        tick + " and patch tick: " + this.Stats_save_current_tick);

        return false;
    }

    public synchronized void set_statistics(List<Person> population, patch patch, int tick, boolean check_mon)
             throws InterruptedException {
        // Erster Durchlauf überspringt dies, da keine liste gespeichert ist
        if (!check_mon){
            while (!(monitor_liste.size()==synched_neighbor_counter)){
                wait();
            }
            synched_neighbor_counter = 0;
        }

        // Neu setzen der Tickspeicher
        this.save_Persons = new LinkedList<>();
        this.Stats_save_current_tick = tick;

        // Clone die Population des Patches in die liste "save_Persons" um diese zu speichern
        for(Person p : population){
            this.save_Persons.add(p.clone(patch));
        }
        notifyAll();
    }


    // Nur Beim ersten Durchlauf relevant
    public synchronized void remove_monitor_neighbors(List<Patch_Monitor> remove_monitore) {
        // remove nonsymmetric monitor (oben erklärt)
        this.monitor_liste.removeAll(remove_monitore);
        notifyAll();
    }
    public synchronized void set_init_Monitors_done() {
        this.init_Monitors_done = true;
        notifyAll();
    }
}