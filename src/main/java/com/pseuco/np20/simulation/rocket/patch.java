package com.pseuco.np20.simulation.rocket;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.pseuco.np20.model.PersonInfo;
import com.pseuco.np20.model.Rectangle;
import com.pseuco.np20.model.Scenario;
import com.pseuco.np20.model.XY;
import com.pseuco.np20.simulation.common.Context;
import com.pseuco.np20.simulation.common.Person;
import com.pseuco.np20.validator.Validator;

public class patch implements Runnable, Context {

    // Ints
    private int initial_Ticks;
    private int Padding;
    private int ID;
    private int synch_ticks ;
    private int tick_at_next_synch ;

    private Patch_Monitor Monitor;
    private Rectangle Patch;
    private Rectangle Patch_with_Padding;
    private Scenario scenario;
    private Validator validator;

    // Lists for Persons on Patch & obstacles
    private List<Person> Patch_Population;  //Contains Persons on patch and on padding
    private List<Rectangle> obstacles;
    private boolean check_mon;
    private List<Patch_Monitor> remove_monitore;
    private Output_Monitor Output_Mon;
    private boolean Output ;
    private List<Thread> thread_list ;

    public patch(Patch_Monitor monitor, Output_Monitor out_mon, Rectangle p, int padding, Validator validator, int Tick,
    Scenario scen, int id, int synch_ticks_possible){
        this.Monitor = monitor;
        this.Output_Mon = out_mon;
        this.Patch = p;
        this.validator = validator;
        this.initial_Ticks = Tick;
        this.scenario = scen;
        this.Output = scenario.getTrace();
        this.Padding = padding;
        this.ID = id;
        this.synch_ticks = synch_ticks_possible;
        this.tick_at_next_synch = synch_ticks_possible;
        this.check_mon = true;

        this.Patch_Population = new LinkedList<Person>();
        this.obstacles = new LinkedList<Rectangle>();
        this.remove_monitore = new LinkedList<>();
        this.thread_list = new LinkedList<>();

        // Berechne Rectangle mit Padding, ermittle nachbarn und Personen auf dem Patch
        init_Padding(); // INIT am Ende der Klasse
    }

    @Override
    public void run() {
        for (int tick = 0; tick < initial_Ticks; tick++) {
            if (this.Output)
                Output_Mon.set_trace(get_actual_Population(), tick, this);
            Output_Mon.set_statistics(get_actual_Population());

            if (tick == tick_at_next_synch) {
                try{
                    synch(tick);
                } catch (InterruptedException e){
                    // Beenden des ganzen Programms bei interrupt
                    Rocket.interrupt_all(this.ID);
                    return;
                }
                tick_at_next_synch = tick + synch_ticks;

                // Sortiert die aktuelle liste
                Collections.sort(Patch_Population, new PersonComparator());
            }
            // we need to call this and 'onPersonTick()' (siehe Validator und slugg)
            validator.onPatchTick(tick, this.ID);

            tick(tick);
        }

        // NACH DEM Tick-PROGRAMM:
        if (this.Output)
            Output_Mon.set_trace(get_actual_Population(), this.initial_Ticks, this);
        Output_Mon.set_statistics(get_actual_Population());
        try {
            //speichern des letzten Ticks und evtl. aufwecken von schlafenden Threads
            Monitor.set_statistics(get_actual_Population(), this, this.initial_Ticks, check_mon);
        } catch (InterruptedException e){
            Rocket.interrupt_all(this.ID);
            return;
        }
     }

    private void tick(int tick) {
        // process a tick() on all persons in patch and padding

        for (Person person : Patch_Population) {
            if (Patch.contains(person.getPosition()))
                validator.onPersonTick(tick, this.ID, person.getId()); // der validator darf nur auf personen im
                                                                           // patch
                                                                           // aufgerufen werden
            person.tick();
        }

        Patch_Population.stream().forEach(Person::bustGhost);

        for (int i = 0; i < Patch_Population.size(); i++) {
            for (int j = i + 1; j < Patch_Population.size(); j++) {
                final Person iPerson = Patch_Population.get(i);
                final Person jPerson = Patch_Population.get(j);
                final XY iPosition = iPerson.getPosition();
                final XY jPosition = jPerson.getPosition();
                final int deltaX = Math.abs(iPosition.getX() - jPosition.getX());
                final int deltaY = Math.abs(iPosition.getY() - jPosition.getY());
                final int distance = deltaX + deltaY;
                if (distance <= this.scenario.getParameters().getInfectionRadius()) {
                    if (iPerson.isInfectious() && iPerson.isCoughing() && jPerson.isBreathing()) {
                        jPerson.infect();
                    }
                    if (jPerson.isInfectious() && jPerson.isCoughing() && iPerson.isBreathing()) {
                        iPerson.infect();
                    }
                }
            }
        }
    }

    // Synchronisations Funktionen .........................................................................

    /*  Erklärung zur Synchronisation
     *
     * Wir rufen im Grunde nur die unten stehenden 2 Funktionen auf, wobei set_statistics
     * die aktuelle Personenliste in den Monitor schreiben will ich auf evtl. Nachbarn wartet
     * die noch in der letzten Synchronisation stecken.
     *
     * Dabei gibt es einen Unterschied zwischen dem ersten Synchro durchlauf und dem Restlichen,
     * welcher mit check_mon gemarkert wird. Ein unterschied is das unter anderem nicht gewartet
     * werden muss zum speichern aber auch das in synch_persons auf die beendigung der Monitor
     * initialisierung gewartet wird.
     *
     * Ein normaler Ablauf in Synch_Persons läuft folgendermaßen ab:
     *      - discard Padding
     *      - Check alle Nachbarn ob diese ihre liste aktualisiert haben
     *      - erstelle eine Liste mit den Nachbarn die diese Nicht aktualisiert haben
     *      - warte nacheinannder auf diese Nachbarn -> funktion im Monitor ist hierbei "get_Population_of_Patch"
     */
    private void synch(int tick) throws InterruptedException {
        //Speichere Daten ab
        Monitor.set_statistics(get_actual_Population(), this, tick, check_mon);
        //Synchronisiere mit allen Nachbarn
        synch_persons(tick);
    }

    //discards padding and copies persons in padding from each neighbor
    private void synch_persons(int tick) throws InterruptedException {
        List<Patch_Monitor> wait_for_it = new LinkedList<>();

        //discard padding and set population of padding new (copies)
        LinkedList<Person> Persons_to_remove = new LinkedList<Person>();
        for (Person p : Patch_Population){
            if (!Patch.contains(p.getPosition())){
                Persons_to_remove.add(p);
            }
        }
        Patch_Population.removeAll(Persons_to_remove);

        /* Falls erster durchlauf:
         *      Warte darauf das maypropagate fertig ist und setze im Monitor eine referenz für alle Nachbarn
         *      "get_Population_of_Patch" checkt ob es der erste Durchlauf ist und wartet alle Nachbarn ab
         *      um die Passende liste zu bekommen
         *
         *      Anschließend lösche die nicht symmetrischen Nachbarn (siehe referenz im Monitor) und setze die
         *      erste Durchlauf bool auf false
         */
        if(check_mon){
            for (Thread t : thread_list) {
                t.join();
            }
            this.Monitor.set_init_Monitors_done();

            for (Patch_Monitor m : Monitor.get_neighbor_monitors()){
                m.get_Population_of_Patch(tick, Patch_with_Padding, this, this.Patch_Population, true, check_mon, this.remove_monitore, this.Monitor);
            }

            //Hier werden beim ersten durchlauf die nicht symmetrischen Nachbarn gefiltert
            this.Monitor.remove_monitor_neighbors(remove_monitore);
            this.check_mon = false;
            return;
        }

        // Dieser Part checkt bei allen Nachbarn ob ihre Population gespeichert wurde
        // falls man warten müsste fügt der den Nachbarn in eine Liste ein
        for (Patch_Monitor m : Monitor.get_neighbor_monitors()){
            if (!(m.get_Population_of_Patch(tick, Patch_with_Padding, this, this.Patch_Population, false, check_mon, null, null))){
                // man beachte bei "get_Population_of_Patch" die letzte variable welche angibt ob auf die synchros gewartet werden soll
                wait_for_it.add(m);
                continue;
            }
        }

        // Wait set siehe Monitor
        for (Patch_Monitor m : wait_for_it){
            m.get_Population_of_Patch(tick, Patch_with_Padding, this, this.Patch_Population, true, check_mon, null, null);
        }
    }

    //Returns all persons inside the patch and not in the padding
    private List<Person> get_actual_Population(){
        /*  An dieser Stelle könnte Theoretisch ein Data Race auftauchen aber:
         *
         *      Alle aufrufe werden für "set_trace" und "get_actual_Population" benutzt
         *
         *      Heißt während diese Funktion gecallt wird greift keiner auf
         *      die Population Liste von außen zu und da keiner die Funktion "getPopulation"
         *      aufruft und die Liste von außen bearbeitet kommt es nicht zu Data Races da nur
         *      der Patch Thread bearbeitend auf die Liste zugreift
         *
         *      Theoretisch könnte also ein Thread von außen die Referenz auf die Liste anfordern
         *      und diese bearbeiten während der Patch diese bearbeitet aber es wäre Aufgabe der aufrufenden
         *      Funktion für ein Lock zu sorgen
         *
         * Letzteres gilt im Grunde für alle bearbeitungen von "Patch_Population" in diesem Thread, falls von außen
         * über "getPopulation" bearbeitet wird, müssen alle vorkommen gelockt werden
         */
        LinkedList<Person> tmp = new LinkedList<Person>();
        for (Person p : Patch_Population){
            if (Patch.contains(p.getPosition())){
                tmp.add(p);
            }
        }
        return tmp;
    }


    // INIT DES PATCHES ...................................................................................

    private void init_Padding() {
        // Constructor sind wesentlich schneller
        Patch_with_Padding = add_Padding(Patch);
        // Finde alle Personen die auf dem Patch_with_padding liegen
        find_pop(Patch_with_Padding);

        // Suche alle Nachbar_Monitore und trage die Referenz in den eigenen Monitor
        // -> benutze dazu das Patch_with_padding und vergleiche mit Iterator über alle
        Iterator<Rectangle> Neighbor_Iter = Utils.getPatches(scenario);
        int id_counter = 0; Rectangle neighbor2 = null;
        for (; Neighbor_Iter.hasNext();){
            Rectangle neighbor = Neighbor_Iter.next();
            if (Neighbor_Iter.hasNext()){
                neighbor2 = Neighbor_Iter.next();
                if ((id_counter+1 != this.ID) && Patch_with_Padding.overlaps(neighbor2)){
                    //Check intersect teil der nachbarn mithilfe von mayPropagate
                    mayprop may_Thread = new mayprop(id_counter+1, neighbor2);
                    thread_list.add(may_Thread);
                    may_Thread.start();
                }
            }
            if ((id_counter != this.ID) && Patch_with_Padding.overlaps(neighbor)){
                //Check intersect teil der nachbarn mithilfe von mayPropagate
                mayprop may_Thread = new mayprop(id_counter, neighbor);
                thread_list.add(may_Thread);
                may_Thread.start();
            }
            id_counter = id_counter+2;
        }

        //Obstacles im Patch und Padding
        for (Rectangle r : scenario.getObstacles()) {
            if (Patch_with_Padding.overlaps(r)) {
                obstacles.add(r);
            }
        }
    }

    // Funktion nimmt ein Rectangle und addiert das Padding auf allen Seiten dazu
    private Rectangle add_Padding(Rectangle r) {
        int x_left = r.getTopLeft().getX();
        int x_right = r.getBottomRight().getX();
        int y_top = r.getTopLeft().getY();
        int y_bottom = r.getBottomRight().getY();

        if (x_left - Padding >= 0)
            x_left -= Padding;
        else
            x_left = 0;

        if (x_right + Padding <= scenario.getGrid().getBottomRight().getX())
            x_right += Padding;
        else
            x_right = scenario.getGrid().getBottomRight().getX();

        if (y_top - Padding >= 0)
            y_top -= Padding;
        else
            y_top = 0;

        if (y_bottom + Padding <= scenario.getGrid().getBottomRight().getY())
            y_bottom += Padding;
        else
            y_bottom = scenario.getGrid().getBottomRight().getY();

        XY top_left = new XY(x_left, y_top);
        XY size = new XY(x_right - x_left, y_bottom - y_top);
        return new Rectangle(top_left,size);
    }

    private void find_pop(Rectangle check_rec){
        // Sucht sich die Personen die auf dem Patch+padding sind und speichert eine Kopie davon
            int id = 0;
            for (PersonInfo p : scenario.getPopulation()){  //-> read alle personen aus scenario welche nie verändert werden
                if (check_rec.contains(p.getPosition())){
                    PersonInfo pI = new PersonInfo(p.getName() ,p.getPosition() ,p.getSeed() ,p.getInfectionState(), p.getDirection());
                    Patch_Population.add(new Person(id, this, scenario.getParameters(), pI));
                }
                id++;
           }
           Collections.sort(Patch_Population, new PersonComparator());
       }

    class PersonComparator implements Comparator<Person> {
        public int compare(Person p1, Person p2) {
            return p1.getId() - p2.getId();
        }
    }

    private class mayprop extends Thread {
        private int id_counter;
        private Rectangle neighbor;

        mayprop (int id_counter, Rectangle neighbor) {
            this.id_counter = id_counter;
            this.neighbor = neighbor;
        }
        public void run(){
            if (com.pseuco.np20.simulation.common.Utils.mayPropagateFrom(scenario, Patch, Patch_with_Padding.intersect(neighbor)))
            {
                // Dies greift auf eine synchronized Funktion auf den Monitor zu
                Monitor.set_mon(Rocket.Mon_Array[id_counter]);
            }
        }
    }

    //Implementation of Context interface ...........................................................

    //return the patch with padding rectangle
    @Override
    public Rectangle getGrid() {
        return this.Patch_with_Padding;
    }

    //return all obstacles in this patch grid
    @Override
    public List<Rectangle> getObstacles() {
        return this.obstacles;
    }

    //return Persons in this patch
    @Override
    public List<Person> getPopulation() {
        return Patch_Population;
    }
}