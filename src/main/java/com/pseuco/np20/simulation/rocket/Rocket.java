package com.pseuco.np20.simulation.rocket;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.pseuco.np20.model.Output;
import com.pseuco.np20.model.Rectangle;
import com.pseuco.np20.model.Scenario;
import com.pseuco.np20.model.Statistics;
import com.pseuco.np20.model.TraceEntry;
import com.pseuco.np20.simulation.common.Person;
import com.pseuco.np20.simulation.common.Simulation;
import com.pseuco.np20.validator.InsufficientPaddingException;
import com.pseuco.np20.validator.Validator;

/**
 * Your implementation shall go into this class.
 *
 * <p>
 * This class has to implement the <em>Simulation</em> interface.
 * </p>
 */
public class Rocket implements Simulation {
    private final int padding;
    private final Scenario scenario;
    private final Validator validator;

    /*  Static referenzes
     *
     * "Mon_Array" dient hier dazu die Referenzen der erstellten Monitore zu speichern um diese
     * später in der Patch Klasse zu finden falls dieser Monitor ein Nachbar ist
     *
     * Hierbei wird darauf geachtet das dieses Array vor dem erstellen der Patches initialisiert wird, indem es im
     * Constructor der Rocket Klasse erstellt wird
     * Da auch die Monitore im Constructor erstellt und gespeichert werden kann der erste Patch der gestartet wird, oder
     * an dem Lesepunkt ankommt, bereits alle Monitore abrufen
     *
     * Heißt nach dem erstellen der Rocket Klasse, ist Mon_Array ein objekt welches ausschließlich gelesen wird und nur Referenzen
     * auf Monitore Klassen weitergibt
     *
     * Sowohl Thread_list als auch check_interrupt werden in "interrupt_all" benutzt um bei einem interrupt das Programm zu beenden
     * und alle Threads anzuhalten
     * Da dies von einem Patch möglich sein muss war die static funktion notwendig um aus dem Patch ohne Rocket
     * referenz diese aufzurufen
     */
    public static Patch_Monitor[] Mon_Array;
    private static LinkedList<Thread> Thread_list;   // Zugriff nur von diesem Thread aus
    private static boolean check_interrupt;

    private LinkedList<Output_Monitor> Output_mon_List; // Tool für den Output
    private int synch_ticks ;

    /**
     * Constructs a rocket with the given parameters.
     *
     * <p>
     * You must not change the signature of this constructor.
     * </p>
     *
     * <p>
     * Throw an insufficient padding exception if and only if the padding is
     * insufficient. Hint: Depending on the parameters, some amount of padding is
     * required even if one only computes one tick concurrently. The padding is
     * insufficient if the provided padding is below this minimal required padding.
     * </p>
     *
     * @param scenario  The scenario to simulate.
     * @param padding   The padding to be used.
     * @param validator The validator to be called.
     */
    public Rocket(final Scenario scenario, final int padding, final Validator validator)
            throws InsufficientPaddingException {
        // your concurrent implementation goes here
        this.padding = padding;
        this.scenario = scenario;
        this.validator = validator;
        Thread_list = new LinkedList<>();
        this.Output_mon_List = new LinkedList<>();
        check_interrupt = false;
        init_mons(scenario.getNumberOfPatches());
        for (int i = 0; i < scenario.getNumberOfPatches(); i++) {
            Mon_Array[i] = new Patch_Monitor(i);
        }
        // berechne die Anzahl der ticks die ohne Synchro absolviert werden
        this.synch_ticks = calculate_synch_ticks();
        if (synch_ticks <= 0) {
            throw new InsufficientPaddingException(padding);
        }
    }

    // initiiert die Größe von Mon_Array
    private static void init_mons(int patch_num) {
        Mon_Array = new Patch_Monitor[patch_num];
    }

    @Override
    public Output getOutput() {
        List<TraceEntry> trace = new LinkedList<>();
        Map<String, List<Statistics>> statistics = new HashMap<>();
        boolean check = false;

        // init for Statistic Map
        for (String queryKey : this.scenario.getQueries().keySet()) {
            statistics.put(queryKey, new LinkedList<>());
        }
        int tmp = 0;
        if (scenario.getQueries().keySet().size() == 0){
            tmp++;
        }
        // Search Trace and Statistic Map/List and combine them
        for (String s : scenario.getQueries().keySet()) {
            // Iter over ticks
            for (int i = 0; i <= scenario.getTicks();) {

                // Create saves for collected Data
                List<Person> trace_save = new LinkedList<>();
                List<Statistics> statistics_save = new LinkedList<>();

                // Iter over all Monitors
                for (Output_Monitor m : this.Output_mon_List) {

                    if (!check && scenario.getTrace()) { // safe 1x die population für jeden tick
                        List<Person> var = m.get_stat_trace().get(i);
                        if (var != null && !var.isEmpty())
                            trace_save.addAll(var);
                    }
                    Map<String, List<Statistics>> stats = m.get_stat_statistics();
                    statistics_save.add(stats.get(s).get(i));
                }

                // Convert Trace Personliste into sorted PersonInfo liste
                if (!check && scenario.getTrace()) {
                    Collections.sort(trace_save, new PersonComparator());
                    trace.add(new TraceEntry(trace_save.stream().map(Person::getInfo).collect(Collectors.toList())));
                    trace_save.clear();
                }

                int save_1 = 0, save_2 = 0, save_3 = 0, save_4 = 0;
                for (Statistics stats : statistics_save) {
                    save_1 += stats.getSusceptible();
                    save_2 += stats.getInfected();
                    save_3 += stats.getInfectious();
                    save_4 += stats.getRecovered();
                }
                statistics.get(s).add(new Statistics(save_1, save_2, save_3, save_4));
                statistics_save.clear();
                i++;
            }
            check = true;
        }

        for (;tmp > 0;){
            for (int i = 0; i <= scenario.getTicks();) {

                // Create saves for collected Data
                List<Person> trace_save = new LinkedList<>();

                // Iter over all Monitors
                for (Output_Monitor m : this.Output_mon_List) {

                    if (!check && scenario.getTrace()) { // safe 1x die population für jeden tick
                        List<Person> var = m.get_stat_trace().get(i);
                        if (var != null && !var.isEmpty())
                            trace_save.addAll(var);
                    }
                }

                // Convert Trace Personliste into sorted PersonInfo liste
                if (!check && scenario.getTrace()) {
                    Collections.sort(trace_save, new PersonComparator());
                    trace.add(new TraceEntry(trace_save.stream().map(Person::getInfo).collect(Collectors.toList())));
                    trace_save.clear();
                }
                i++;
            }
            tmp--;
        }
        return new Output(this.scenario, trace, statistics);
    }

    class PersonComparator implements Comparator<Person>{
        public int compare(Person p1, Person p2) {
            return p1.getId() - p2.getId();
        }
    }

    @Override
    public void run() {
        init_Thread();
        wait_for_fin();
    }


    // END THREADS || INTERRUPT THREADS ...........................
    private void wait_for_fin() {
        // Warte darauf dass alle Patches beendet sind oder
        for (Thread t : Thread_list) {
            try {
                t.join();
            } catch (InterruptedException e) {
                // Catch die interrupt exeption die geworfen wird wenn dieser Thread dabei interrupted wird und interrupte die Patches
                e.printStackTrace();
                check_interrupt = true;
                for (Thread t1 : Thread_list) {
                    t1.interrupt();
                }
                return;
            }
        }
    }

    public synchronized static void interrupt_all(int iD) {
        if (!check_interrupt){
            check_interrupt = true;
            int i = 0;
            for (Thread t1 : Thread_list) {
                if (i != iD)
                    t1.interrupt();
                i++;
            }
        }
	}

    // THREAD START .....................................................
    private void init_Thread() {
        /*
         * Iterator for Patch List to safe the Persons for a Patch List
         */
        final Iterator<Rectangle> Patch_Iter = Utils.getPatches(scenario);
        Rectangle rectangle;
        int ID = 0;

        while (Patch_Iter.hasNext()) {
             /*
             * Init: active Patch Monitor New Iterator to search for all neighbors forall
             * Active Patches
             */
            rectangle = Patch_Iter.next();
            // Dieser Monitor speichert unseren Output um diesen mit der funktion oben zu sammeln
            Output_Monitor out_mon = new Output_Monitor(/* ID,*/ scenario);
            this.Output_mon_List.add(out_mon);

            /* Init Patch
             *
             * Mon_Array[ID]:   übergebe die passende Referenz aus dem Static Array
             * out_mon:         2. Monitor s.o.
             * rectangle        Das Rectangle aus dem Iterator
             *
             * Referenzen für Validator und Scenario um Infos zu bekommen:
             * validator
             * scenario
             *
             * Einfache INTS:
             * scenario.getTicks(), padding, ID, synch_ticks
             */
            patch active_patch = new patch(Mon_Array[ID], out_mon, rectangle, padding, validator, scenario.getTicks(), scenario,
                    ID, synch_ticks);
            Thread Patch_Thread = new Thread(active_patch); // Liste um auf Threads zu warten
            Patch_Thread.start();
            Thread_list.add(Patch_Thread);
            ID++;
        }

        // Checks
        assert scenario.getNumberOfPatches() == Mon_Array.length;
        assert scenario.getNumberOfPatches() == Thread_list.size();
    }



    //Calcluating how many synch_ticks the patches may simulate before synchronizing .....................................
    private int calculate_synch_ticks(){
        //compute highest n (ticks) we can simulate until uncertainty would be greater than the padding

        int n = 1;
        while (calculate_synch_ticks_help(n)){
            n++;
        }
        return (n-1);
    }
    private boolean calculate_synch_ticks_help(int n){
        //returns if for n ticks we can safely simulate
        //spread of uncertainty is movement_uncertainty + infection_uncertainty

        int movement_uncertainty = movement_uncertainty_help(n);
        int infection_uncertainty = infection_uncertainty_help(n);
        int uncertainty = movement_uncertainty + infection_uncertainty;
        return (uncertainty <= padding);
    }

    private int movement_uncertainty_help(int n){
        //movement uncertainty spread for n ticks
        assert (n>-1);

        return n*2; //movement uncertainty can spread 2 columns per tick

    }

    private int infection_uncertainty_help(int n){
        //infection uncertainty spread , formula taken from: https://np20.pseuco.com/t/hints-on-the-spread-of-uncertainty/954
        int incubation_time = scenario.getParameters().getIncubationTime();
        int infection_radius = scenario.getParameters().getInfectionRadius();

        return (int)(Math.ceil( (float)n / incubation_time)) * infection_radius;
    }

}
